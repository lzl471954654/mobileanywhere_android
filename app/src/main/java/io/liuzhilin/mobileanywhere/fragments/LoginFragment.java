package io.liuzhilin.mobileanywhere.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import io.liuzhilin.mobileanywhere.MapActivity;
import io.liuzhilin.mobileanywhere.R;
import io.liuzhilin.mobileanywhere.bean.User;
import io.liuzhilin.mobileanywhere.manager.UserCacheManager;
import io.liuzhilin.mobileanywhere.ui.register.RegisterViewModel;
import io.liuzhilin.mobileanywhere.util.GsonUtils;

public class LoginFragment extends Fragment {
    public static final int LOGIN_SUCEESS = 1;
    public static final int LOGIN_FAILED = 0;


    private View root;

    private RegisterViewModel registerViewModel = new RegisterViewModel();
    private EditText usernameEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private ProgressBar loadingProgressBar;

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case LOGIN_SUCEESS:{
                    loginSuccess((String)msg.obj);
                    break;
                }
                case LOGIN_FAILED:{
                    loginFailed((Exception)msg.obj);
                    break;
                }
            }
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.activity_login,container,false);
        initView();
        return root;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }

    private void initView(){
        usernameEditText = root.findViewById(R.id.username);
        passwordEditText = root.findViewById(R.id.password);
        loginButton = root.findViewById(R.id.login);
        loadingProgressBar = root.findViewById(R.id.loading);

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
        usernameEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.addTextChangedListener(afterTextChangedListener);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingProgressBar.setVisibility(View.VISIBLE);
                registerViewModel.loginToServer(
                        usernameEditText.getText().toString(),
                        passwordEditText.getText().toString(),
                        handler
                );
            }
        });
    }

    private void loginSuccess(String data){
        try {
            JSONObject object = new JSONObject(data);
            JSONObject u = object.getJSONObject("data");
            User user = GsonUtils.gson.fromJson(u.toString(),User.class);
            UserCacheManager.setCurrentUser(user);
            loadingProgressBar.setVisibility(View.INVISIBLE);
            Toast.makeText(this.getContext(),data,Toast.LENGTH_LONG).show();
            MapActivity.Companion.startActivity(getContext());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void loginFailed(Exception e){
        loadingProgressBar.setVisibility(View.INVISIBLE);
        Toast.makeText(this.getContext(),e.getMessage(),Toast.LENGTH_LONG).show();
    }

    private void inputDataChanged(){
        String username = this.usernameEditText.getText().toString();
        String password = this.passwordEditText.getText().toString();
        if (registerViewModel.isUserNameValid(username) && registerViewModel.isPasswordValid(password)){
            loginButton.setEnabled(true);
            return;
        }
        if (!registerViewModel.isUserNameValid(username)){
            this.usernameEditText.setError(getResources().getString(R.string.invalid_username));
        }
        if (!registerViewModel.isPasswordValid(password)){
            this.passwordEditText.setError(getResources().getString(R.string.invalid_password));
        }
        loginButton.setEnabled(false);
    }
}
