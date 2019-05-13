package io.liuzhilin.mobileanywhere.ui.pyq;


import android.os.Handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.liuzhilin.mobileanywhere.bean.Blog;
import io.liuzhilin.mobileanywhere.bean.BlogComment;
import io.liuzhilin.mobileanywhere.bean.Point;
import io.liuzhilin.mobileanywhere.callback.RequestCallback;
import io.liuzhilin.mobileanywhere.requests.RequestCenter;
import io.liuzhilin.mobileanywhere.util.CallBackParser;
import io.liuzhilin.mobileanywhere.util.GsonUtils;

public class PYQViewModel {

    private Handler handler;

    public PYQViewModel(Handler handler){
        this.handler = handler;
    }


    public void getBlogDataByPoint(Point point, RequestCallback callback){
        Map<String,String> map = new HashMap<>();
        map.put("pointId",point.getLongitude()+":"+point.getLatitude());
        RequestCenter.Companion.requestGet(RequestCenter.GET_BLOG_BY_POINT,map,new CallBackParser(callback));
    }

    public void getUserData(List<Blog> blogs,RequestCallback callback){
        List<String> list = new ArrayList<>();
        for (Blog blog : blogs) {
            list.add(blog.getBlogOwner());
        }
        Map<String,String> map = new HashMap<>();
        map.put("accounts", GsonUtils.gson.toJson(list));
        RequestCenter.Companion.requestPost(RequestCenter.GET_USERS,map,new CallBackParser(callback));
    }

    public void getUserDataByComments(List<BlogComment> blogs, RequestCallback callback){
        List<String> list = new ArrayList<>();
        for (BlogComment blog : blogs) {
            list.add(blog.getBlogCommentsOwner());
        }
        Map<String,String> map = new HashMap<>();
        map.put("accounts", GsonUtils.gson.toJson(list));
        RequestCenter.Companion.requestPost(RequestCenter.GET_USERS,map,new CallBackParser(callback));
    }

    public void getBlogCommentsByBlogId(Blog blog,RequestCallback callback){
        Map<String,String> map = new HashMap<>();
        map.put("blogId",blog.getBlogId());
        RequestCenter.Companion.requestGet(RequestCenter.GET_COMMENT_BY_BLOG_ID,map,new CallBackParser(callback));
    }

    public void destory(){
        handler = null;
    }

}
