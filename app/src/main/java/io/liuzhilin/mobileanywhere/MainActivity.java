package io.liuzhilin.mobileanywhere;

import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
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
        //PYQActivity.startActivity(this);
        //MapActivity.Companion.startActivity(this);
        /*Intent intent=new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY,1);
        //好使
        intent.putExtra(MediaStore.EXTRA_SIZE_LIMIT,10485760L);
        intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT,10);
        startActivityForResult(intent,5);*/
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        System.out.println("111");
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 5){
            Uri uri = data.getData();
            System.out.println(uri);
        }
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
