package io.liuzhilin.mobileanywhere.ui.login;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.os.Handler;
import android.util.Log;
import android.util.Patterns;

import io.liuzhilin.mobileanywhere.callback.RequestCallback;
import io.liuzhilin.mobileanywhere.data.LoginRepository;
import io.liuzhilin.mobileanywhere.data.Result;
import io.liuzhilin.mobileanywhere.data.model.LoggedInUser;
import io.liuzhilin.mobileanywhere.R;
import io.liuzhilin.mobileanywhere.requests.LoginRequest;
import io.liuzhilin.mobileanywhere.util.CallBackParser;
import io.liuzhilin.mobileanywhere.util.DIgestUtils;

public class LoginViewModel extends ViewModel {

    private MutableLiveData<LoginFormState> loginFormState = new MutableLiveData<>();
    private MutableLiveData<LoginResult> loginResult = new MutableLiveData<>();
    private LoginRepository loginRepository;

    LoginViewModel(LoginRepository loginRepository) {
        this.loginRepository = loginRepository;
    }

    LiveData<LoginFormState> getLoginFormState() {
        return loginFormState;
    }

    LiveData<LoginResult> getLoginResult() {
        return loginResult;
    }

    public void login(String username, String password) {
        // can be launched in a separate asynchronous job
        Result<LoggedInUser> result = loginRepository.login(username, password);

        if (result instanceof Result.Success) {
            LoggedInUser data = ((Result.Success<LoggedInUser>) result).getData();
            loginResult.setValue(new LoginResult(new LoggedInUserView(data.getDisplayName())));
        } else {
            loginResult.setValue(new LoginResult(R.string.login_failed));
        }
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

    public void loginDataChanged(String username, String password) {
        if (!isUserNameValid(username)) {
            loginFormState.setValue(new LoginFormState(R.string.invalid_username, null));
        } else if (!isPasswordValid(password)) {
            loginFormState.setValue(new LoginFormState(null, R.string.invalid_password));
        } else {
            loginFormState.setValue(new LoginFormState(true));
        }
    }

    // A placeholder username validation check
    private boolean isUserNameValid(String username) {
        if (username == null) {
            return false;
        }
        if (username.contains("@")) {
            return Patterns.EMAIL_ADDRESS.matcher(username).matches();
        } else {
            return !username.trim().isEmpty();
        }
    }

    // A placeholder password validation check
    private boolean isPasswordValid(String password) {
        return password != null && password.trim().length() > 5;
    }
}
