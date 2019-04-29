package io.liuzhilin.mobileanywhere.ui.register;

import android.os.Handler;
import android.util.Log;
import android.util.Patterns;

import java.util.Map;

import io.liuzhilin.mobileanywhere.RegisterActivity;
import io.liuzhilin.mobileanywhere.callback.RequestCallback;
import io.liuzhilin.mobileanywhere.requests.LoginRequest;
import io.liuzhilin.mobileanywhere.ui.login.LoginActivity;
import io.liuzhilin.mobileanywhere.util.CallBackParser;
import io.liuzhilin.mobileanywhere.util.DIgestUtils;

public class RegisterViewModel {


    public void loginToServer(Map<String,String> params, final Handler handler){
        LoginRequest.Companion.register(params,new CallBackParser(new RequestCallback() {
            @Override
            public void success(String json) {
                handler.obtainMessage(RegisterActivity.REG_SUCEESS,json).sendToTarget();
            }

            @Override
            public void failed(Exception e) {
                handler.obtainMessage(RegisterActivity.REG_FAILED,e).sendToTarget();
            }
        }));
    }

    public void loginToServer(String username, String password , final Handler handler){
        password = DIgestUtils.sha1(password+"mobile");
        Log.e("loginToServer:",password);
        LoginRequest.Companion.login(username, password,new CallBackParser(new RequestCallback() {
            @Override
            public void success(String json) {
                handler.obtainMessage(LoginActivity.LOGIN_SUCEESS,json).sendToTarget();
            }

            @Override
            public void failed(Exception e) {
                handler.obtainMessage(LoginActivity.LOGIN_FAILED,e).sendToTarget();
            }
        }));
    }

    public boolean checkFormValid(String username,String password,String email,String nickname){
        return isUserNameValid(username) && isPasswordValid(password) && isUserNameValid(email) && isUserNameValid(nickname);
    }

    // A placeholder username validation check
    public boolean isUserNameValid(String username) {
        if (username == null) {
            return false;
        }
        if (username.contains("@")) {
            return Patterns.EMAIL_ADDRESS.matcher(username).matches();
        } else {
            return !username.trim().isEmpty();
        }
    }

    public boolean isEmailValid(String email){
        if (email == null){
            return false;
        }
        if (email.contains("@")){
            return Patterns.EMAIL_ADDRESS.matcher(email).matches();
        }else {
            return false;
        }
    }

    // A placeholder password validation check
    public boolean isPasswordValid(String password) {
        return password != null && password.trim().length() > 5;
    }
}
