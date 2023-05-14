package com.example.th2android;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.th2android.model.Item;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class UpdateDeleteActivity extends AppCompatActivity implements View.OnClickListener {
    public Spinner sp;
    private EditText eTitle,ePrice,eDate;
    ImageView img;
    private final static int galleryPick=1;
    private StorageReference imgRef;
    private Uri imageUri;
    private Button btUpdate,btCancel,btRemove;
    private DatabaseReference userRef;
    private String id="",downloadImgUrl;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_delete);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        initView();
        id=getIntent().getStringExtra("id");
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userId = user.getUid();
        userRef= FirebaseDatabase.getInstance().getReference().child("users").child(userId).child("notelist").child(id);
        displayWork();
        btUpdate.setOnClickListener(this);
        btCancel.setOnClickListener(this);
        btRemove.setOnClickListener(this);
        eDate.setOnClickListener(this);
        img.setOnClickListener(this);
    }

    private void displayWork() {
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String title=snapshot.child("title").getValue().toString();
                    String category=snapshot.child("category").getValue().toString();
                    String price=snapshot.child("price").getValue().toString();
                    String date=snapshot.child("date").getValue().toString();
                    String image=snapshot.child("image").getValue().toString();
                    eTitle.setText(title);
                    ePrice.setText(price);
                    eDate.setText(date);
                    Picasso.get().load(image).into(img);
                    int p=0;
                    for(int i=0;i<sp.getCount();i++){
                        if(sp.getItemAtPosition(i).toString().equalsIgnoreCase(category)){
                            p=i;
                            break;
                        }
                    }
                    sp.setSelection(p);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "No Data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initView() {
        sp=findViewById(R.id.spCategory);
        eTitle=findViewById(R.id.tvTitle);
        ePrice=findViewById(R.id.tvPrice);
        eDate=findViewById(R.id.tvDate);
        btUpdate=findViewById(R.id.btUpdate);
        btCancel=findViewById(R.id.btCancel);
        btRemove=findViewById(R.id.btRemove);
        img=findViewById(R.id.img);
        sp.setAdapter(new ArrayAdapter<String>(this,R.layout.item_spinner,getResources().getStringArray(R.array.category)));
    }

    @Override
    public void onClick(View view) {
        if(view==eDate){
            final Calendar c= Calendar.getInstance();
            int year=c.get(Calendar.YEAR);
            int month=c.get(Calendar.MONTH);
            int day=c.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog dialog=new DatePickerDialog(UpdateDeleteActivity.this, new DatePickerDialog.OnDateSetListener() {
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
            storeWork();
        }
        if(view==btRemove){
            AlertDialog.Builder builder=new AlertDialog.Builder(view.getContext());
            builder.setTitle("Thông báo xoa");
            builder.setMessage("Bạn có chắc chắn muốn xóa "+eTitle.getText().toString()+" không?");
            builder.setIcon(R.drawable.remove);
            builder.setPositiveButton("Có", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    deleteWork();
                }
            });
            builder.setNegativeButton("Không", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });
            AlertDialog dialog=builder.create();
            dialog.show();
        }
        if(view==img){
            openGallery();
        }
    }

    private void openGallery(){
        Intent intent=new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent,galleryPick);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==galleryPick && resultCode==RESULT_OK && data!=null){
            imageUri=data.getData();
            img.setImageURI(imageUri);
        }
    }

    private void storeWork() {

        imgRef= FirebaseStorage.getInstance().getReference().child("note image");
        StorageReference filePath=imgRef.child(
                imageUri.getLastPathSegment()+" "+id);
        final UploadTask uploadTask=filePath.putFile(imageUri);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                String message=e.toString();
                Toast.makeText(UpdateDeleteActivity.this,
                        "error: "+message, Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(UpdateDeleteActivity.this,
                        "up ảnh thành công!", Toast.LENGTH_SHORT).show();
                Task<Uri> uriTask=uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if(!task.isSuccessful()){
                            throw task.getException();
                        }
                        downloadImgUrl=filePath.getDownloadUrl().toString();
                        return filePath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        downloadImgUrl=task.getResult().toString();
                        Toast.makeText(UpdateDeleteActivity.this,
                                "Lưu Url ảnh thành công!", Toast.LENGTH_SHORT).show();
                        editWork();
                    }
                });
            }
        });
    }

    private void editWork() {
        String t=eTitle.getText().toString();
        String p=ePrice.getText().toString();
        String category=sp.getSelectedItem().toString();
        String d=eDate.getText().toString();
        if(t.isEmpty()){
            Toast.makeText(this, "Chưa viết title", Toast.LENGTH_SHORT).show();
        }else if(p.isEmpty() || !p.matches("\\d+")) {
            Toast.makeText(this, "Chưa đúng giá tiền", Toast.LENGTH_SHORT).show();
        }else if(d.isEmpty()) {
            Toast.makeText(this, "Chưa chọn ngày giờ", Toast.LENGTH_SHORT).show();
        }else{
            HashMap<String,Object> item=new HashMap<>();
            item.put("id",id);
            item.put("title",t);
            item.put("category",category);
            item.put("price",p);
            item.put("date",d);
            item.put("image",downloadImgUrl);
            userRef.updateChildren(item)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    // Create the NotificationChannel with a unique ID and name
                                    NotificationChannel channel = new NotificationChannel("Channel1", "Channel 1", NotificationManager.IMPORTANCE_HIGH);
                                    // Set the channel description (optional)
                                    channel.setDescription("My Channel Description");
                                    // Register the channel with the system
                                    NotificationManager notificationManager = getSystemService(NotificationManager.class);
                                    notificationManager.createNotificationChannel(channel);
                                }
                                NotificationCompat.Builder builder = new NotificationCompat.Builder(UpdateDeleteActivity.this, "Channel1")
                                        .setSmallIcon(R.drawable.ic_notifications)
                                        .setContentTitle("Sửa thành công!")
                                        .setContentText("Bạn đã sửa một ghi chú!")
                                        .setCategory(NotificationCompat.CATEGORY_ALARM)
                                        .setPriority(NotificationCompat.PRIORITY_HIGH);
                                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(UpdateDeleteActivity.this);
                                notificationManager.notify(1, builder.build());
                                Intent intent1=new Intent(UpdateDeleteActivity.this,
                                        MainActivity.class);
                                startActivity(intent1);
                                finish();
                            }else{
                                Toast.makeText(UpdateDeleteActivity.this,
                                        "Sửa không thành công!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(UpdateDeleteActivity.this,
                                    "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void deleteWork() {
        userRef.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    // Create the NotificationChannel with a unique ID and name
                    NotificationChannel channel = new NotificationChannel("Channel2", "Channel 2", NotificationManager.IMPORTANCE_HIGH);
                    // Set the channel description (optional)
                    channel.setDescription("My Channel Description");
                    // Register the channel with the system
                    NotificationManager notificationManager = getSystemService(NotificationManager.class);
                    notificationManager.createNotificationChannel(channel);
                }
                NotificationCompat.Builder builder = new NotificationCompat.Builder(UpdateDeleteActivity.this, "Channel2")
                        .setSmallIcon(R.drawable.ic_notifications)
                        .setContentTitle("Xóa thành công!")
                        .setContentText("Bạn đã xóa một ghi chú!")
                        .setCategory(NotificationCompat.CATEGORY_ALARM)
                        .setPriority(NotificationCompat.PRIORITY_HIGH);
                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(UpdateDeleteActivity.this);
                notificationManager.notify(1, builder.build());
                Intent intent=new Intent(UpdateDeleteActivity.this,
                        MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}