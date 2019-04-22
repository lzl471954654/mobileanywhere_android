package io.liuzhilin.mobileanywhere;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;

import io.liuzhilin.mobileanywhere.ui.register.RegisterViewModel;
import io.liuzhilin.mobileanywhere.util.DIgestUtils;

public class RegisterActivity extends AppCompatActivity {

    public static final int REG_SUCEESS = 1;
    public static final int REG_FAILED = 0;


    private RegisterViewModel viewModel = new RegisterViewModel();

    private EditText username;
    private EditText password;
    private EditText email;
    private EditText nickname;
    private Button register;
    private ProgressBar progressBar;


    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case REG_SUCEESS:{
                    regSuccess((String)msg.obj);
                    break;
                }
                case REG_FAILED:{
                    regFailed((Exception)msg.obj);
                    break;
                }
            }
        }
    };

    private void regSuccess(String data){
        progressBar.setVisibility(View.INVISIBLE);
        Toast.makeText(this,data,Toast.LENGTH_LONG).show();
        MapActivity.Companion.startActivity(this);
    }

    private void regFailed(Exception e){
        progressBar.setVisibility(View.INVISIBLE);
        Toast.makeText(this,e.getMessage(),Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initView();
        initWatcher();
        initClickListener();
    }

    public void initClickListener(){
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                viewModel.loginToServer(collectRegisterInfo(),handler);
            }
        });
    }

    private Map<String,String> collectRegisterInfo(){
        String username = this.username.getText().toString();
        String password = this.password.getText().toString();
        String nickname = this.nickname.getText().toString();
        String email = this.nickname.getText().toString();
        password = DIgestUtils.sha1(password+"mobile");
        Map<String,String> map = new HashMap<>();
        map.put("userAccount",username);
        map.put("userPass",password);
        map.put("userNickname",nickname);
        map.put("userEmail",email);
        return map;
    }

    public void initWatcher(){
        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                inputDataChanged();
            }
        };
        username.addTextChangedListener(afterTextChangedListener);
        password.addTextChangedListener(afterTextChangedListener);
        email.addTextChangedListener(afterTextChangedListener);
        nickname.addTextChangedListener(afterTextChangedListener);
    }

    private void inputDataChanged(){
        String username = this.username.getText().toString();
        String password = this.password.getText().toString();
        String nickname = this.nickname.getText().toString();
        String email = this.email.getText().toString();
        if (viewModel.checkFormValid(username,password,email,nickname)){
            register.setEnabled(true);
            return;
        }
        if (!viewModel.isUserNameValid(username)){
            this.username.setError(getResources().getString(R.string.invalid_username));
        }
        if (!viewModel.isPasswordValid(password)){
            this.password.setError(getResources().getString(R.string.invalid_password));
        }
        if (!viewModel.isEmailValid(email)){
            this.email.setError("邮箱不合法");
        }
        if (!viewModel.isUserNameValid(nickname)){
            this.nickname.setError("昵称不合法");
        }
    }

    public void initView(){
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        email = findViewById(R.id.email);
        nickname = findViewById(R.id.nickname);
        register = findViewById(R.id.login);
        progressBar = findViewById(R.id.loading);
    }



    public static void startActivity(Context context){
        Intent intent = new Intent(context,RegisterActivity.class);
        context.startActivity(intent);
    }
}
