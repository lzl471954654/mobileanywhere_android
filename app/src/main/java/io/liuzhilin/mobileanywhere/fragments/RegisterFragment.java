package io.liuzhilin.mobileanywhere.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import io.liuzhilin.mobileanywhere.MapActivity;
import io.liuzhilin.mobileanywhere.R;
import io.liuzhilin.mobileanywhere.bean.User;
import io.liuzhilin.mobileanywhere.manager.UserCacheManager;
import io.liuzhilin.mobileanywhere.ui.register.RegisterViewModel;
import io.liuzhilin.mobileanywhere.util.DIgestUtils;
import io.liuzhilin.mobileanywhere.util.GsonUtils;

public class RegisterFragment extends Fragment {

    public static final int REG_SUCEESS = 1;
    public static final int REG_FAILED = 0;


    private RegisterViewModel viewModel = new RegisterViewModel();

    private View root;
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
        try{
            JSONObject object = new JSONObject(data);
            JSONObject u = object.getJSONObject("data");
            User user = GsonUtils.gson.fromJson(u.toString(),User.class);
            UserCacheManager.setCurrentUser(user);
            progressBar.setVisibility(View.INVISIBLE);
            Toast.makeText(getContext(),data,Toast.LENGTH_LONG).show();
            MapActivity.Companion.startActivity(getContext());
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    private void regFailed(Exception e){
        progressBar.setVisibility(View.INVISIBLE);
        Toast.makeText(getContext(),e.getMessage(),Toast.LENGTH_LONG).show();
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.activity_register,container,false);
        initView();
        initClickListener();
        initWatcher();
        return root;
    }

    public void initClickListener(){
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                viewModel.registerToServer(collectRegisterInfo(),handler);
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
        username = root.findViewById(R.id.username);
        password = root.findViewById(R.id.password);
        email = root.findViewById(R.id.email);
        nickname = root.findViewById(R.id.nickname);
        register = root.findViewById(R.id.login);
        progressBar = root.findViewById(R.id.loading);
    }
}
