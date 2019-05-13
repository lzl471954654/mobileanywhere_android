package io.liuzhilin.mobileanywhere;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.liuzhilin.mobileanywhere.adapter.PYQAdapter;
import io.liuzhilin.mobileanywhere.bean.Blog;
import io.liuzhilin.mobileanywhere.bean.Point;
import io.liuzhilin.mobileanywhere.bean.User;
import io.liuzhilin.mobileanywhere.callback.GetPointCallBack;
import io.liuzhilin.mobileanywhere.callback.OnBlogClickListener;
import io.liuzhilin.mobileanywhere.callback.RequestCallback;
import io.liuzhilin.mobileanywhere.fragments.BlogCommentsBottomSheetDialogFragment;
import io.liuzhilin.mobileanywhere.fragments.ShareMenuFragment;
import io.liuzhilin.mobileanywhere.manager.UserCacheManager;
import io.liuzhilin.mobileanywhere.requests.RequestCenter;
import io.liuzhilin.mobileanywhere.ui.pyq.PYQViewModel;
import io.liuzhilin.mobileanywhere.util.CallBackParser;
import io.liuzhilin.mobileanywhere.util.GsonUtils;
import io.liuzhilin.mobileanywhere.util.PointUtils;

public class PYQActivity extends AppCompatActivity implements OnBlogClickListener {

    private Point point;
    private PYQViewModel viewModel;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private FloatingActionButton add;
    private FloatingActionButton refresh;

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {

        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pyq);
        getPointData();
        initViewModel();
        initView();
        loadingData();
    }

    private void initView(){
        add = findViewById(R.id.add_button);
        refresh = findViewById(R.id.refresh_button);
        progressBar = findViewById(R.id.progressBar);
        recyclerView = findViewById(R.id.pyq_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        getSupportActionBar().setTitle("查看附近");
        getSupportActionBar().hide();

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShareMenuFragment shareMenuFragment = new ShareMenuFragment();
                shareMenuFragment.setGetPointCallBack(new GetPointCallBack() {
                    @Override
                    public String getPointData() {
                        return point.getLongitude()+":"+point.getLatitude();
                    }
                });
                shareMenuFragment.show(getSupportFragmentManager(),"ShareMenu");
            }
        });

        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingData();
            }
        });

    }

    private void loadingData(){
        progressBar.setVisibility(View.VISIBLE);
        viewModel.getBlogDataByPoint(point, new RequestCallback() {
            @Override
            public void success(String json) {
                handler.post(()->{
                    loadingSuccess(json);
                });
            }

            @Override
            public void failed(Exception e) {
                handler.post(()->{
                    Toast.makeText(PYQActivity.this,"加载数据失败",Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    private void loadingSuccess(String json){
        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray jsonArray = jsonObject.getJSONArray("data");
            List<Blog> blogs = GsonUtils.gson.fromJson(jsonArray.toString(),new TypeToken<List<Blog>>(){}.getType());
            getUserData(blogs);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getUserData(List<Blog> blogs){
        viewModel.getUserData(blogs, new RequestCallback() {
            @Override
            public void success(String json) {
                try {
                    JSONObject object = new JSONObject(json);
                    JSONArray array = object.getJSONArray("data");
                    List<User> list = GsonUtils.gson.fromJson(array.toString(),new TypeToken<List<User>>(){}.getType());
                    handler.post(()->{
                        UserCacheManager.addUser(list);
                        showData(blogs);
                        progressBar.setVisibility(View.GONE);
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void failed(Exception e) {
                handler.post(()->{ Toast.makeText(PYQActivity.this,"加载用户数据失败",Toast.LENGTH_LONG).show();});
            }
        });
    }


    private void showData(List<Blog> list){
        PYQAdapter adapter = new PYQAdapter(this,list,this::onBlogClick);
        recyclerView.setAdapter(adapter);
    }

    private void initViewModel(){
        viewModel = new PYQViewModel(handler);
    }

    private void getPointData(){
        String pointId = getIntent().getStringExtra("pointId");
        point = PointUtils.getPointByPointId(pointId);
    }

    @Override
    public void onBlogClick(Blog blog) {
        showBlogComments(blog);
    }

    private void showBlogComments(Blog blog){
        BlogCommentsBottomSheetDialogFragment fragment = new BlogCommentsBottomSheetDialogFragment();
        fragment.setViewModel(viewModel);
        fragment.setBlog(blog);
        fragment.show(getSupportFragmentManager(),"blogComments");
    }

    public static void startActivity(Context context){
        Intent intent = new Intent(context,PYQActivity.class);
        context.startActivity(intent);
    }

    public static void startActivityWithParam(Context context, Map<String,String> map){
        Intent intent = new Intent(context,PYQActivity.class);
        for (Map.Entry<String, String> entry : map.entrySet()) {
            intent.putExtra(entry.getKey(),entry.getValue());
        }
        context.startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
        viewModel.destory();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }


}
