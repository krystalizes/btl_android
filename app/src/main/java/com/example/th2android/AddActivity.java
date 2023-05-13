package com.example.th2android;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class AddActivity extends AppCompatActivity implements View.OnClickListener{
    public Spinner sp;
    private EditText eTitle,ePrice,eDate;
    private Button btUpdate,btCancel;
    private DatabaseReference userRef;
    private String saveCurDate,saveCurTime, randomKey;
    private String t,category,p,d;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        initView();
        btUpdate.setOnClickListener(this);
        btCancel.setOnClickListener(this);
        eDate.setOnClickListener(this);
    }

    private void initView() {
        sp=findViewById(R.id.spCategory);
        eTitle=findViewById(R.id.tvTitle);
        ePrice=findViewById(R.id.tvPrice);
        eDate=findViewById(R.id.tvDate);
        btUpdate=findViewById(R.id.btUpdate);
        btCancel=findViewById(R.id.btCancel);
        sp.setAdapter(new ArrayAdapter<String>(this,R.layout.item_spinner,getResources().getStringArray(R.array.category)));
    }

    @Override
    public void onClick(View view) {
        if(view==eDate){
            final Calendar c= Calendar.getInstance();
            int year=c.get(Calendar.YEAR);
            int month=c.get(Calendar.MONTH);
            int day=c.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog dialog=new DatePickerDialog(AddActivity.this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int y, int m, int d) {
                    String date="";
                    if(m>8){
                        date=d+"/"+(m+1)+"/"+y;
                    }else{
                        date=d+"/0"+(m+1)+"/"+y;
                    }
                    eDate.setText(date);
                }
            },year,month,day);
            dialog.show();
        }
        if(view==btCancel){
            finish();
        }
        if(view==btUpdate){
            validateData();
        }
    }

    private void validateData() {
        t=eTitle.getText().toString();
        p=ePrice.getText().toString();
        category=sp.getSelectedItem().toString();
        d=eDate.getText().toString();
        if(t.isEmpty()){
            Toast.makeText(this, "Chưa viết title", Toast.LENGTH_SHORT).show();
        }else if(p.isEmpty() || !p.matches("\\d+")) {
            Toast.makeText(this, "Chưa đúng giá tiền", Toast.LENGTH_SHORT).show();
        }else if(d.isEmpty()) {
            Toast.makeText(this, "Chưa chọn ngày giờ", Toast.LENGTH_SHORT).show();
        }else{
            saveWorktoDatabas();
        }
    }


    private void saveWorktoDatabas(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userId = user.getUid();
        Calendar c=Calendar.getInstance();
        SimpleDateFormat curDate=new SimpleDateFormat("dd-MM-yyyy");
        saveCurDate=curDate.format(c.getTime());
        SimpleDateFormat curTime=new SimpleDateFormat("HH:mm:ss");
        saveCurTime=curTime.format(c.getTime());
        randomKey="note created at "+saveCurDate+"-"+saveCurTime;
        HashMap<String,Object> item=new HashMap<>();
        item.put("id",randomKey);
        item.put("title",t);
        item.put("category",category);
        item.put("price",p);
        item.put("date",d);
        userRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId).child("notelist");
        userRef.child(randomKey).updateChildren(item)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                // Create the NotificationChannel with a unique ID and name
                                NotificationChannel channel = new NotificationChannel("Channel3", "Channel 3", NotificationManager.IMPORTANCE_HIGH);
                                // Set the channel description (optional)
                                channel.setDescription("My Channel Description");
                                // Register the channel with the system
                                NotificationManager notificationManager = getSystemService(NotificationManager.class);
                                notificationManager.createNotificationChannel(channel);
                            }
                            NotificationCompat.Builder builder = new NotificationCompat.Builder(AddActivity.this, "Channel3")
                                    .setSmallIcon(R.drawable.ic_notifications)
                                    .setContentTitle("Thêm thành công!")
                                    .setContentText("Bạn đã thêm một ghi chú mới vào danh sách!")
                                    .setCategory(NotificationCompat.CATEGORY_ALARM)
                                    .setPriority(NotificationCompat.PRIORITY_HIGH);
                            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(AddActivity.this);
                            notificationManager.notify(1, builder.build());
                            Intent intent1=new Intent(AddActivity.this,
                                    MainActivity.class);
                            startActivity(intent1);
                        }else{
                            Toast.makeText(AddActivity.this,
                                    "Thêm không thành công!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}