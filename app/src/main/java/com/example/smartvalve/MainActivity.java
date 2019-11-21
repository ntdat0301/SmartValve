package com.example.smartvalve;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    public DatabaseReference mDatabase;
    public static String username;
    public static String usernameUSER;
    String USERNAME_KEY = "user";
    String PASSWORD_KEY = "pass";
    String CHECK_BOX;
    ProgressDialog progressBar;
    Button btnLogin;
    EditText edtUser;
    EditText edtPass;
    TextView txtRegister;
    TextView txtForgetPass;
    CheckBox checkBox;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        btnLogin = (Button) findViewById(R.id.buttonLogin);
        edtUser = (EditText) findViewById(R.id.editID);
        edtPass = (EditText) findViewById(R.id.editPassword);
        txtRegister = (TextView) findViewById(R.id.tvRegister);
        txtForgetPass = (TextView) findViewById(R.id.tvForgot);
        checkBox = (CheckBox) findViewById(R.id.cbRemember);

        // Không cho ứng dụng khác lấy tài khoản với mật khẩu của mình
        sharedPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);

        //Mới vào kiểm tra xem thử tài khoản vào mật khẩu ok chưa. Nếu ok thì vào màn hình thiết bị luôn
        //Khi tắt ứng dụng thì nó sẽ đổ lại dữ liệu mà đã lưu trước đó
        edtUser.setText(sharedPreferences.getString(USERNAME_KEY, ""));
        edtPass.setText(sharedPreferences.getString(PASSWORD_KEY, ""));
        checkBox.setChecked(sharedPreferences.getBoolean(CHECK_BOX, false));
        Login_func();

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Login_func();
            }
        });

        txtForgetPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Quên mật khẩu", Toast.LENGTH_SHORT).show();
            }
        });

        txtRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(Login.this, "Register", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, Register.class);
                startActivity(intent);
            }
        });
    }

    public void Login_func() {
        final String email = edtUser.getText().toString();

        String password = edtPass.getText().toString();
        if (("".equals(email) || ("".equals(password)))) {
            Toast.makeText(this, "Xin mời nhập tài khoản và mật khẩu", Toast.LENGTH_SHORT).show();
        } else {
            RunLogin();
            Task<AuthResult> authResultTask = mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                username="";
                                String USERNAMEthunghiem=edtUser.getText().toString();
                                for( int t=0;t<(USERNAMEthunghiem.length()-10);t++)
                                {
                                    username=username+USERNAMEthunghiem.charAt(t);
                                }

                                Toast.makeText(MainActivity.this, "Login Completely!", Toast.LENGTH_SHORT).show();
                                if (checkBox.isChecked()) {
                                    editor = sharedPreferences.edit();
                                    editor.putString(USERNAME_KEY, edtUser.getText().toString().trim());
                                    editor.putString(PASSWORD_KEY, edtPass.getText().toString().trim());
                                    editor.putBoolean(CHECK_BOX, true);
                                    editor.commit();
                                } else {
                                    editor.remove(USERNAME_KEY);
                                    editor.remove(PASSWORD_KEY);
                                    editor.putBoolean(CHECK_BOX, false);
                                    editor.commit();
                                }
        usernameUSER=email;
        Intent intent = new Intent(MainActivity.this, AllDevices.class);
        intent.setAction(Intent.ACTION_VIEW);
        startActivity(intent);
                            } else {

                                Toast.makeText(MainActivity.this, "Login Failed!", Toast.LENGTH_SHORT).show();
                            }

                            // ...
                        }
                    });

        }
    }
    public void RunLogin() {
        // Khởi tạo progressBar với đối là Context
        progressBar = new ProgressDialog(MainActivity.this);
        // Cho phép hủy progressBar nếu ấn nút Back
        progressBar.setCancelable(true);
        // Đặt tiêu đề cho ProgressBar
        progressBar.setMessage("LOGIN...");
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
        new MyThread().start();
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
