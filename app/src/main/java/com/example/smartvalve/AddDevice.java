package com.example.smartvalve;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

import java.util.ArrayList;

import static com.example.smartvalve.MainActivity.usernameUSER;

public class AddDevice extends AppCompatActivity {
    public DatabaseReference reff,mDatame,mData;
    public String namedevice="";
    EditText edtID,edtNameDevice;
    Button btnAdd;
    ID_name id_name;
    ArrayList<String> OldDevice= new ArrayList<>();
    public String username;
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
        setContentView(R.layout.activity_add_device);

        /*-------------Phần tạo Nut back--------------------*/
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);// hiển thị nút Up ở Home icon

        edtID= findViewById(R.id.editID);
        edtNameDevice=findViewById(R.id.edit_NameDevice);
        btnAdd=findViewById(R.id.btnAddDevice);

        id_name=new ID_name();

        username=usernameUSER.substring(0,(usernameUSER).length()-10);

        mDatame= FirebaseDatabase.getInstance().getReference();
        reff=mDatame.child("User/"+username);

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mData= mDatame.child("Device");
                FindArray();
                KiemTra();
            }
        });
    }

    private void KiemTra() {
        Boolean test=false;
        if (OldDevice.size()==0) Toast.makeText(this, "Không có thiết bị nào có sẵn," +
                                         " vui lòng bật thiết bị trước", Toast.LENGTH_SHORT).show();
        else
            {
                //Toast.makeText(this, OldDevice.get(0), Toast.LENGTH_SHORT).show();
                for (int k=0;k <OldDevice.size();k++)
                {

                    if (OldDevice.get(k).equals(id_name.getID()))
                        {
                            test=true;
                        }
                    if (test==true)
                        {
                            UpdateData(id_name.getID());
                            Toast.makeText(this, "Đã thêm thiết bị", Toast.LENGTH_SHORT).show();
                            test=false;
                            /*-----------------------*/
                            String edt=edtID.getText().toString().trim();
                            String name=edtNameDevice.getText().toString().trim();
                            id_name.setID(edt);
                            id_name.setName(name);
                            reff.child(edt).setValue(id_name);
                            /*------------------------*/
                            Intent intent= new Intent(AddDevice.this,AllDevices.class);
                            startActivity(intent);
                        }
                    else
                        Toast.makeText(this, "Thiết bị chưa được cài đặt. Hãy bật thiết bị của bạn trước", Toast.LENGTH_SHORT).show();
                }
            }
    }

    private void FindArray() {
        //Chuong trinh proceesbar
        mData.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                OldDevice.add(dataSnapshot.getKey());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void UpdateData(final String idid) {
        //final DatabaseReference mDatame= FirebaseDatabase.getInstance().getReference();
        //DatabaseReference mData= mDatame.child("Device/"+idid);
        mData.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                mDatame.child("Device/"+idid+"/"+"name").setValue(idid);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                mDatame.child("Device/"+idid+"/"+"name").setValue(idid);
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
