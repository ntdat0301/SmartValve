package com.example.smartvalve;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
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

import java.util.ArrayList;

import static com.example.smartvalve.MainActivity.usernameUSER;

public class AllDevices extends AppCompatActivity {
    public DatabaseReference mDatabase;
    ImageButton btnRefresh;
    ImageButton btnAdd;

    public String userN;
    public String namedevice="";

    public ListView LVdevice;
    public static ArrayList<ThietBi2> mangThietBi;
    public ThietBiAdapter adapter;

    public static ArrayList<String> mID=new ArrayList<>();



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
        setContentView(R.layout.activity_all_devices);

        /*-------------Phần tạo Nut back--------------------*/
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);// hiển thị nút Up ở Home icon

        /*------------ANH XẠ---------------------*/
        LVdevice =(ListView) findViewById(R.id.lvMyDevices);
        btnAdd= findViewById(R.id.buttonAddDevice);
        btnRefresh = findViewById(R.id.buttonRefresh);

        mangThietBi = new ArrayList<ThietBi2>();
        //--------------------------------thiết lập apdapter cho nó-------------------------------
        adapter = new ThietBiAdapter(AllDevices.this, R.layout.linedevice,mangThietBi);
        LVdevice.setAdapter(adapter);

        //---------------------Thêm thông tin vào dòng listview----------------------------------
        GetTenThietBi();

        //---------------------------Bắt sự kiện cho button thêm device--------------------------
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(AllDevices.this, AddDevice.class);
                startActivity(intent);
            }
        });

        /* -------Click vào button Refresh thì load lại các thiết bị của mình--------------*/
        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(AllDevices.this, "Refresh device", Toast.LENGTH_SHORT).show();
            }
        });
        //------------------------------------------------------------------------------------
    }


    public void GetTenThietBi()
    {
        userN=usernameUSER.substring(0,(usernameUSER).length()-10);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        //final DatabaseReference myRef = mDatabase.child("User/"+userN);
        mDatabase.child("User/"+userN).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                namedevice=dataSnapshot.child("name").getValue().toString();
                //Toast.makeText(AllDevices.this, namedevice, Toast.LENGTH_SHORT).show();
                mangThietBi.add(new ThietBi2(namedevice));
                String ididid=dataSnapshot.getKey();
                mID.add(ididid);
                adapter.notifyDataSetChanged();

            }
            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                namedevice=dataSnapshot.child("name").getValue().toString();

                String key= dataSnapshot.getKey();
                int index= mID.indexOf(key);

                mangThietBi.set(index,new ThietBi2(namedevice));

                adapter.notifyDataSetChanged();
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
}
