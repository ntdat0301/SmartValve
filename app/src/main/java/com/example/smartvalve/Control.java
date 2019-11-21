package com.example.smartvalve;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static com.example.smartvalve.ThietBiAdapter.Id_Thiet_Bi;
import static com.example.smartvalve.ThietBiAdapter.name_device_pulic;

public class Control extends AppCompatActivity {
    public DatabaseReference mDatabase,myRef2;
    TextView txtvan1,txtvan2,txtvan3,txtNameDevice;
    ImageButton btnalarm;
    Button btnOnVan1,btnOffVan1,btnOnVan2,btnOffVan2,btnOnVan3,btnOffVan3;
    ProgressDialog progressBar;

    /*-------Phần bổ trợ thêm cho nut Back-----------------*/
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
    /*----------------------------------------------------*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control);

        /*-------------Phần tạo Nut back--------------------*/
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);// hiển thị nút Up ở Home icon

        txtNameDevice=findViewById(R.id.textView_Name);
         txtvan1=findViewById(R.id.txtVan1);
         txtvan2=findViewById(R.id.txtVan2);
         txtvan3=findViewById(R.id.txtVan3);
         btnalarm=findViewById(R.id.btnAlarm);
         btnOnVan1=findViewById(R.id.btn_Van1_On);
         btnOffVan1=findViewById(R.id.btn_Van1_Off);
         btnOnVan2=findViewById(R.id.btn_Van2_On);
         btnOffVan2=findViewById(R.id.btn_Van2_Off);
         btnOnVan3=findViewById(R.id.btn_Van3_On);
         btnOffVan3=findViewById(R.id.btn_Van3_Off);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        myRef2 = mDatabase.child("Device").child(Id_Thiet_Bi);

        txtNameDevice.setText("Device:"+name_device_pulic);

        UpdateStatus("VAN1",txtvan1);
        UpdateStatus("VAN2",txtvan2);
        UpdateStatus("VAN3",txtvan3);

        /*--------------Bắt sự kiện với button---------------------*/
        btnOnVan1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Thaydoi(txtvan1,"Status","VAN1","ON");
                Toast.makeText(Control.this, "Đã bật Van 1", Toast.LENGTH_SHORT).show();
            }
        });
        btnOffVan1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Thaydoi(txtvan1,"Status","VAN1","OFF");
                Toast.makeText(Control.this, "Đã tắt Van 1", Toast.LENGTH_SHORT).show();
            }
        });
        btnOnVan2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Thaydoi(txtvan2,"Status","VAN2","ON");
                Toast.makeText(Control.this, "Đã bật Van 2", Toast.LENGTH_SHORT).show();
            }
        });
        btnOffVan2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Thaydoi(txtvan2,"Status","VAN2","OFF");
                Toast.makeText(Control.this, "Đã tắt Van 2", Toast.LENGTH_SHORT).show();
            }
        });
        btnOnVan3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Thaydoi(txtvan3,"Status","VAN3","ON");
                Toast.makeText(Control.this, "Đã bật Van 3", Toast.LENGTH_SHORT).show();
            }
        });
        btnOffVan3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Thaydoi(txtvan1,"Status","VAN3","OFF");
                Toast.makeText(Control.this, "Đã tắt Van 3", Toast.LENGTH_SHORT).show();
            }
        });
        btnalarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RunXuLy();
                Intent intent= new Intent(Control.this,Alarm.class);
                startActivity(intent);
            }
        });

    }

    public void Thaydoi(final TextView txt,final String mien,final String tenvan ,final String giatri)
    {

        myRef2.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                mDatabase.child("Device/" + Id_Thiet_Bi +"/"+tenvan+"/"+mien).setValue(giatri);
                txt.setText(giatri);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                //String sss= dataSnapshot.getValue().toString();
                if (dataSnapshot.getKey().equals(mien))
                {
                    mDatabase.child("Device/" + Id_Thiet_Bi+"/" +tenvan+"/"+mien).setValue(giatri);
                    txt.setText(giatri);
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    /*----------------------------------------------------------------------*/
    public void UpdateStatus(final String tenvan, final TextView txtvan)
    {
        myRef2.child(tenvan).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Device_Details device_details= dataSnapshot.getValue(Device_Details.class);
                txtvan.setText(device_details.Status);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(Control.this, "Failed to load post.",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
    /*-----------------------------------------------------------*/

    public void RunXuLy() {
        // Khởi tạo progressBar với đối là Context
        progressBar = new ProgressDialog(Control.this);
        // Cho phép hủy progressBar nếu ấn nút Back
        progressBar.setCancelable(true);
        // Đặt tiêu đề cho ProgressBar
        progressBar.setMessage("ALARM...");
        // Đặt giao diện cho ProgressBar
        progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        // Đặt giá trị đầu tiên, đây là giá trị thể hiện mức độ hoàn thành công
        // việc có thang từ 0 - > 100
        // do hiện tại công việc chưa hoàn thành được chút nào nên ta đặt là 0
        progressBar.setProgress(0);
        // Đặt cho giá trị lớn nhất thể hiện mức độ hoàn thành công việc là 100
        progressBar.setMax(100);
        // Hiện ProgressBar
        progressBar.show();
        // Tạo một luồng xử lý công việc.
        new Control.MyThread().start();
    }
    class MyThread extends Thread {

        @Override
        public void run() {
            // xử lý công việc cần hoàn thành
            for (int i = 0; i < 30; i++) {
                // Tạm dừng 1s, thực tế thì chỗ này là xử lý công việc
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                // tính xem công việc đã hoàn thành bao nhiêu phần trăm và đưa lên progressbar
                progressBar.setProgress((i * 100) / 30);
            }
            // đóng brogressbar.
            progressBar.dismiss();
        }
    }
}
