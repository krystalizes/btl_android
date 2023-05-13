package com.example.th2android;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.example.th2android.adapter.ViewPagerAdapter;
import com.example.th2android.fragment.FragmentHistory;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;
    private ViewPager viewPager;
    private TextView tv;

    FirebaseAuth mAuth;
    private DatabaseReference userRef;
    private DrawerLayout drawer;

    ActionBarDrawerToggle toggle;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bottomNavigationView=findViewById(R.id.bottom_nav);
        viewPager=findViewById(R.id.viewPager);
        fab=findViewById(R.id.fab);
        drawer=findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.bringToFront();
        View headerView = navigationView.getHeaderView(0);
        tv = headerView.findViewById(R.id.tv);
        FirebaseUser user = mAuth.getInstance().getCurrentUser();
        String userId = user.getUid();
        String useremail=user.getEmail();
        tv.setText(useremail);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_logout:
                        mAuth = FirebaseAuth.getInstance();
                        mAuth.getInstance().signOut();
                        Intent intent=new Intent(getApplicationContext(), LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                        break;
                    case R.id.nav_delete:
                        AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this);
                        builder.setTitle("Thông báo xóa");
                        builder.setMessage("Bạn có chắc chắn muốn cóa tài khoản với email "+useremail+" khong?");
                        builder.setIcon(R.drawable.remove);
                        builder.setPositiveButton("Có", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                userRef= FirebaseDatabase.getInstance().getReference().child("users").child(userId);
                                userRef.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        user.delete()
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                                                // Create the NotificationChannel with a unique ID and name
                                                                NotificationChannel channel = new NotificationChannel("Channel4", "Channel 4", NotificationManager.IMPORTANCE_HIGH);
                                                                // Set the channel description (optional)
                                                                channel.setDescription("My Channel Description");
                                                                // Register the channel with the system
                                                                NotificationManager notificationManager = getSystemService(NotificationManager.class);
                                                                notificationManager.createNotificationChannel(channel);
                                                            }
                                                            NotificationCompat.Builder builder = new NotificationCompat.Builder(MainActivity.this, "Channel4")
                                                                    .setSmallIcon(R.drawable.ic_notifications)
                                                                    .setContentTitle("Xóa tài khoản thành công!")
                                                                    .setContentText("Bạn đã xóa tài khoản của bạn, vui lòng đăng kí tài khoản mới nếu muốn tiếp tục sử dụng")
                                                                    .setCategory(NotificationCompat.CATEGORY_ALARM)
                                                                    .setPriority(NotificationCompat.PRIORITY_HIGH);
                                                            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(MainActivity.this);
                                                            notificationManager.notify(1, builder.build());
                                                            Intent intent2=new Intent(MainActivity.this,LoginActivity.class);
                                                            startActivity(intent2);
                                                            finish();
                                                        } else {
                                                            Toast.makeText(MainActivity.this,
                                                                    "Xóa tài khoản không thành công", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                    }
                                });

                            }
                        });
                        builder.setNegativeButton("Không", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        });
                        AlertDialog dialog=builder.create();
                        dialog.show();
                        break;
                    case R.id.nav_today:
                        viewPager.setCurrentItem(0);
                        break;
                    case R.id.nav_note:
                        viewPager.setCurrentItem(1);
                        break;
                    case R.id.nav_statistic:
                        viewPager.setCurrentItem(2);
                        break;
                    case R.id.nav_settings:
                        break;
                    case R.id.nav_share:
                        break;
                    case R.id.nav_about:
                        break;
                }
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        });
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Danh sách");
        toggle = new ActionBarDrawerToggle(this, drawer,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        drawer.addDrawerListener(toggle);
        toggle.syncState();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainActivity.this,AddActivity.class);
                startActivity(intent);
            }
        });
        ViewPagerAdapter adapter=new ViewPagerAdapter(getSupportFragmentManager(), FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch(position){
                    case 0:bottomNavigationView.getMenu().findItem(R.id.mHome).setChecked(true);
                        break;
                    case 1:bottomNavigationView.getMenu().findItem(R.id.mHistory).setChecked(true);
                        break;
                    case 2:bottomNavigationView.getMenu().findItem(R.id.mSearch).setChecked(true);
                        break;
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()){
                    case R.id.mHome:viewPager.setCurrentItem(0);
                        break;
                    case R.id.mHistory:viewPager.setCurrentItem(1);
                        break;
                    case R.id.mSearch:viewPager.setCurrentItem(2);
                        break;
                }
                return true;
            }
        });

    }
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (toggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


}