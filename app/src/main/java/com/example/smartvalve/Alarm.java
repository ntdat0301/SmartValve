package com.example.smartvalve;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;
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

import java.util.ArrayList;

import static com.example.smartvalve.ThietBiAdapter.Id_Thiet_Bi;

public class Alarm extends AppCompatActivity {
    ArrayList<String> arrayGio= new ArrayList<String>();
    ArrayList<String>arrayPhut= new ArrayList<String>();
    Spinner hourv1,minutev1,hourv2,minutev2,hourv3,minutev3;
    Button btnSet_On_V1,btnSet_On_V2,btnSet_On_V3,btnSet_Off_V1,btnSet_Off_V2,btnSet_Off_V3;
    Switch swV1,swV2,swV3;
    TextView txt_STS_V1,txt_Bat_V1,txt_Tat_V1,txt_STS_V2,txt_Bat_V2,txt_Tat_V2,txt_STS_V3,txt_Bat_V3,txt_Tat_V3;

    public DatabaseReference mDatabase,myRef2;

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
        setContentView(R.layout.activity_alarm);

        /*-------------Phần tạo Nut back--------------------*/
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);// hiển thị nút Up ở Home icon
        /*------------Ánh xạ------------------------------------*/
        hourv1=findViewById(R.id.spnHV1);
        hourv2=findViewById(R.id.spnHV2);
        hourv3=findViewById(R.id.spnHV3);
        minutev1=findViewById(R.id.spnMV1);
        minutev2=findViewById(R.id.spnMV2);
        minutev3=findViewById(R.id.spnMV3);

        swV1=findViewById(R.id.switchV1);
        swV2=findViewById(R.id.switchV2);
        swV3=findViewById(R.id.switchV3);

        txt_STS_V1=findViewById(R.id.txtSts_Alarm_1);
        txt_STS_V2=findViewById(R.id.txtSts_Alarm_2);
        txt_STS_V3=findViewById(R.id.txtSts_Alarm_3);

        txt_Bat_V1=findViewById(R.id.txt_Alarm_Hour_On_1);
        txt_Bat_V2=findViewById(R.id.txt_Alarm_Hour_On_2);
        txt_Bat_V3=findViewById(R.id.txt_Alarm_Hour_On_3);

        txt_Tat_V1=findViewById(R.id.txt_Alarm_Hour_Off_1);
        txt_Tat_V2=findViewById(R.id.txt_Alarm_Hour_Off_2);
        txt_Tat_V3=findViewById(R.id.txt_Alarm_Hour_Off_3);

        btnSet_Off_V1=findViewById(R.id.btn_Alarm_1_Off);
        btnSet_Off_V2=findViewById(R.id.btn_Alarm_2_Off);
        btnSet_Off_V3=findViewById(R.id.btn_Alarm_3_Off);

        btnSet_On_V1=findViewById(R.id.btn_Alarm_1_On);
        btnSet_On_V2=findViewById(R.id.btn_Alarm_2_On);
        btnSet_On_V3=findViewById(R.id.btn_Alarm_3_On);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        myRef2 = mDatabase.child("Device").child(Id_Thiet_Bi);


        /*-------------------------------------------------*/
        Spinergiophut(hourv1,minutev1);
        Spinergiophut(hourv2,minutev2);
        Spinergiophut(hourv3,minutev3);

        /*-------------------------------------------------*/
        UpdateHourandMinute("VAN1",txt_STS_V1,txt_Bat_V1,txt_Tat_V1,swV1);
        UpdateHourandMinute("VAN2",txt_STS_V2,txt_Bat_V2,txt_Tat_V2,swV2);
        UpdateHourandMinute("VAN3",txt_STS_V3,txt_Bat_V3,txt_Tat_V3,swV3);

        /*------------Bắt sự kiện cho spiner-------------------------------------*/
        btnSet_On_V1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Thaydoi("VAN1",hourv1.getSelectedItem().toString(),minutev1.getSelectedItem().
                        toString(),"Hour_B","Minute_B");

                Toast.makeText(Alarm.this, "Sẽ bật van 1 lúc " + hourv1.getSelectedItem()
                        .toString()+" giờ:"+minutev1.getSelectedItem().
                        toString()+" phút", Toast.LENGTH_SHORT).show();
            }
        });
        btnSet_On_V2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Thaydoi("VAN2",hourv2.getSelectedItem().toString(),minutev2.getSelectedItem().
                        toString(),"Hour_B","Minute_B");
                Toast.makeText(Alarm.this, "Sẽ bật van 2 lúc " + hourv3.getSelectedItem()
                        .toString()+" giờ:"+minutev2.getSelectedItem().
                        toString()+" phút", Toast.LENGTH_SHORT).show();
            }
        });
        btnSet_On_V3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Thaydoi("VAN3",hourv3.getSelectedItem().toString(),minutev3.getSelectedItem().
                        toString(),"Hour_B","Minute_B");
                Toast.makeText(Alarm.this, "Sẽ bật van 3 lúc " + hourv3.getSelectedItem()
                        .toString()+" giờ:"+minutev3.getSelectedItem().
                        toString()+" phút", Toast.LENGTH_SHORT).show();
            }
        });
        btnSet_Off_V1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Thaydoi("VAN1",hourv1.getSelectedItem().toString(),minutev1.getSelectedItem().
                        toString(),"Hour_T","Minute_T");

                Toast.makeText(Alarm.this, "Sẽ tắt van 1 lúc " + hourv1.getSelectedItem()
                        .toString()+" giờ:"+minutev1.getSelectedItem().
                        toString()+" phút", Toast.LENGTH_SHORT).show();
            }
        });
        btnSet_Off_V2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Thaydoi("VAN2",hourv2.getSelectedItem().toString(),minutev2.getSelectedItem().
                        toString(),"Hour_T","Minute_T");

                Toast.makeText(Alarm.this, "Sẽ tắt van 2 lúc " + hourv2.getSelectedItem()
                        .toString()+" giờ:"+minutev2.getSelectedItem().
                        toString()+" phút", Toast.LENGTH_SHORT).show();
            }
        });
        btnSet_Off_V3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Thaydoi("VAN3",hourv3.getSelectedItem().toString(),minutev3.getSelectedItem().
                        toString(),"Hour_T","Minute_T");

                Toast.makeText(Alarm.this, "Sẽ bật van 3 lúc " + hourv3.getSelectedItem()
                        .toString()+" giờ:"+minutev3.getSelectedItem().
                        toString()+" phút", Toast.LENGTH_SHORT).show();
            }
        });
        swV1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if ( swV1.isChecked() == true)
                { mDatabase.child("Device" + "/" + Id_Thiet_Bi +"/VAN1/stsSW").setValue("ON");}
                else
                    mDatabase.child("Device" + "/" + Id_Thiet_Bi +"/VAN1/stsSW").setValue("OFF");
            }
        });
        swV2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if ( swV2.isChecked() == true)
                { mDatabase.child("Device" + "/" + Id_Thiet_Bi +"/VAN2/stsSW").setValue("ON");}
                else
                    mDatabase.child("Device" + "/" + Id_Thiet_Bi +"/VAN2/stsSW").setValue("OFF");
            }
        });
        swV3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if ( swV3.isChecked() == true)
                { mDatabase.child("Device" + "/" + Id_Thiet_Bi +"/VAN3/stsSW").setValue("ON");}
                else
                    mDatabase.child("Device" + "/" + Id_Thiet_Bi +"/VAN3/stsSW").setValue("OFF");
            }
        });



    }
/*-------------Chương trình con giành cho spiner giờ và phút ---------------------*/
    public void Spinergiophut(final Spinner spinner_Gio,final Spinner spinner_Phut)
        {
            for (int k=0;k<=24;k++)
            {
                String a=String.valueOf(k);
                arrayGio.add(a);
            }
            ArrayAdapter arrayAdapter2=new ArrayAdapter<String>(this, R.layout.spinner_con, R.id.text,arrayGio);
            spinner_Gio.setAdapter(arrayAdapter2);

            /*-----------------------Thêm phút vào spinnerPhut----------------------------*/
            for (int l=0;l<=59;l++)
            {
                String b= String.valueOf(l);
                arrayPhut.add(b);
            }
            ArrayAdapter arrayAdapter3=new ArrayAdapter<String>(this, R.layout.spinner_con, R.id.text,arrayPhut);
            spinner_Phut.setAdapter(arrayAdapter3);
        }
    /*----------------Cập nhật giữ liệu lên------------------------------*/
    public void Thaydoi(final String van,final String gio,final String phut,final String mienHour,final String mienPhut)
    {

        myRef2.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                mDatabase.child("Device" + "/" + Id_Thiet_Bi +"/"+van+"/"+mienHour).setValue(gio);
                mDatabase.child("Device" + "/" + Id_Thiet_Bi +"/"+van+"/"+mienPhut).setValue(phut);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                //String sss= dataSnapshot.getValue().toString();
                if (dataSnapshot.getKey().equals(mienHour))
                {
                    mDatabase.child("Device" + "/" + Id_Thiet_Bi+"/" +van+"/"+mienHour).setValue(gio);
                }
                if (dataSnapshot.getKey().equals(mienPhut))
                {
                    mDatabase.child("Device" + "/" + Id_Thiet_Bi+"/" +van+"/"+mienPhut).setValue(phut);
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
    public void UpdateHourandMinute(final String tenvan,final TextView txtsts,final TextView H_S,
                                    final  TextView H_T,final Switch OnOff)
    {
        final DatabaseReference myRef2 = mDatabase.child("Device").child(Id_Thiet_Bi);
        myRef2.child(tenvan).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Device_Details user= dataSnapshot.getValue(Device_Details.class);
                txtsts.setText(user.stsSW);
                H_S.setText(user.Hour_B+" : "+user.Minute_B);
                H_T.setText(user.Hour_T+" : "+user.Minute_T);
                if (user.stsSW.equals("ON"))
                    {
                        OnOff.setChecked(true);}
                else
                    {
                        OnOff.setChecked(false);
                    }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(Alarm.this, "Failed to load post.",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
    /*-----------------------------------------------------------*/

}
