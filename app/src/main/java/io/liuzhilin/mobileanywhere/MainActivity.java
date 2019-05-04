package io.liuzhilin.mobileanywhere;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import io.liuzhilin.mobileanywhere.fragments.LoginFragment;
import io.liuzhilin.mobileanywhere.fragments.RegisterFragment;
import io.liuzhilin.mobileanywhere.ui.login.LoginActivity;

public class MainActivity extends AppCompatActivity {

    private FrameLayout content;
    private TextView login;
    private TextView register;

    private LoginFragment loginFragment;
    private RegisterFragment registerFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        initView();
        initFragment();
        initListener();
        PYQActivity.startActivity(this);
    }

    private void initListener(){
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideFragment(getRegisterFragment());
                showFragment(getLoginFragment());

                register.setTextSize(TypedValue.COMPLEX_UNIT_SP,16);
                register.setTextColor(getResources().getColor(R.color.secondText));
                login.setTextSize(TypedValue.COMPLEX_UNIT_SP,24);
                login.setTextColor(getResources().getColor(R.color.textPrimary));
            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideFragment(getLoginFragment());
                showFragment(getRegisterFragment());
                login.setTextSize(TypedValue.COMPLEX_UNIT_SP,16);
                login.setTextColor(getResources().getColor(R.color.secondText));
                register.setTextSize(TypedValue.COMPLEX_UNIT_SP,24);
                register.setTextColor(getResources().getColor(R.color.textPrimary));
            }
        });
    }

    private void initFragment(){
        loginFragment = getLoginFragment();
        registerFragment = getRegisterFragment();
        addFragment(getRegisterFragment());
        addFragment(getLoginFragment());
        hideFragment(getRegisterFragment());
        showFragment(getLoginFragment());
    }

    private void addFragment(Fragment fragment){
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragmentContent,fragment)
                .commit();
    }

    private void showFragment(Fragment fragment){
        getSupportFragmentManager()
                .beginTransaction()
                .show(fragment)
                .commit();
    }

    private void hideFragment(Fragment fragment){
        getSupportFragmentManager()
                .beginTransaction()
                .hide(fragment)
                .commit();
    }

    private void initView(){
        content = findViewById(R.id.fragmentContent);
        login = findViewById(R.id.password_login_button);
        register = findViewById(R.id.register_button);
    }

    public LoginFragment getLoginFragment() {
        return loginFragment == null ? new LoginFragment() : loginFragment;
    }

    public RegisterFragment getRegisterFragment() {
        return registerFragment == null ? new RegisterFragment() : registerFragment;
    }
}
