package io.liuzhilin.mobileanywhere.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.liuzhilin.mobileanywhere.R;
import io.liuzhilin.mobileanywhere.adapter.BlogCommentsAdapter;
import io.liuzhilin.mobileanywhere.bean.Blog;
import io.liuzhilin.mobileanywhere.bean.BlogComment;
import io.liuzhilin.mobileanywhere.bean.User;
import io.liuzhilin.mobileanywhere.callback.GetBlogCallBack;
import io.liuzhilin.mobileanywhere.callback.GetPointCallBack;
import io.liuzhilin.mobileanywhere.callback.RequestCallback;
import io.liuzhilin.mobileanywhere.manager.UserCacheManager;
import io.liuzhilin.mobileanywhere.requests.RequestCenter;
import io.liuzhilin.mobileanywhere.ui.pyq.PYQViewModel;
import io.liuzhilin.mobileanywhere.util.GsonUtils;

public class BlogCommentsBottomSheetDialogFragment extends BottomSheetDialogFragment implements GetBlogCallBack {

    private View root;
    private PYQViewModel viewModel;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private Blog blog;
    private FloatingActionButton add;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_blog_comment,container,false);
        this.root = root;
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        add = root.findViewById(R.id.add_button);
        progressBar = root.findViewById(R.id.progressBar);
        recyclerView = root.findViewById(R.id.comment_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        initListener();
        loadingBlogCommentsData(blog);
    }


    @Override
    public Blog getBlogData() {
        return blog;
    }

    private void initListener(){
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShareMenuFragment fragment = new ShareMenuFragment();
                fragment.setGetPointCallBack(new GetPointCallBack() {
                    @Override
                    public String getPointData() {
                        return blog.getBlogPointId();
                    }
                });
                fragment.setGetBlogCallBack(BlogCommentsBottomSheetDialogFragment.this);
                fragment.setBlog(false);
                fragment.show(getChildFragmentManager(),"ShareMenu");



            }
        });
    }

    public void loadingBlogCommentsData(Blog blog){
        progressBar.setVisibility(View.VISIBLE);
        viewModel.getBlogCommentsByBlogId(blog, new RequestCallback() {
            @Override
            public void success(String json) {
                handler.post(()->{ loadingCommentsDataSuccess(json);});
            }

            @Override
            public void failed(Exception e) {
                handler.post(()->{
                    progressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(getContext(),"加载评论数据失败",Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    public void loadingCommentsDataSuccess(String json){
        try {
            JSONObject object = new JSONObject(json);
            JSONArray array = object.getJSONArray("data");
            List<BlogComment> blogComments = GsonUtils.gson.fromJson(array.toString(),new TypeToken<List<BlogComment>>(){}.getType());
            loadingUserData(blogComments);
        }catch (JSONException e){
            e.printStackTrace();
            Toast.makeText(getContext(),"解析数据失败！",Toast.LENGTH_LONG).show();
        }
    }

    public void loadingUserData(List<BlogComment> blogComments){
        viewModel.getUserDataByComments(blogComments, new RequestCallback() {
            @Override
            public void success(String json) {
                try{
                    JSONObject object = new JSONObject(json);
                    JSONArray array = object.getJSONArray("data");
                    List<User> list = GsonUtils.gson.fromJson(array.toString(),new TypeToken<List<User>>(){}.getType());
                    handler.post(()->{
                        UserCacheManager.addUser(list);
                        showData(blogComments);
                        progressBar.setVisibility(View.GONE);
                    });
                }catch (JSONException e){
                    e.printStackTrace();
                }
            }

            @Override
            public void failed(Exception e) {
                handler.post(()->{  Toast.makeText(getContext(),"暂无评论",Toast.LENGTH_LONG).show(); });
            }
        });
    }

    public void setBlog(Blog blog) {
        this.blog = blog;
    }

    private void showData(List<BlogComment> blogComments){
        recyclerView.setAdapter(new BlogCommentsAdapter(getContext(),blogComments));
    }

    public void setViewModel(PYQViewModel viewModel) {
        this.viewModel = viewModel;
    }


    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(){

    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.viewModel = null;
        handler.removeCallbacksAndMessages(null);
    }
}
