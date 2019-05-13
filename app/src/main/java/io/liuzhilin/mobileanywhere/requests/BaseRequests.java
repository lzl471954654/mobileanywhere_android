package io.liuzhilin.mobileanywhere.requests;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class BaseRequests {

    public static final OkHttpClient httpClient = new OkHttpClient.Builder()
            .connectTimeout(20, TimeUnit.SECONDS)
            .readTimeout(60,TimeUnit.SECONDS)
            .build();

    private static final String BASE_URL = "http://dev.server.liuzhilin.io:10000";
    public static final String BASE_BLOG_COM_URL = BASE_URL + "/blogComment";
    public static final String BASE_POINT_URL = BASE_URL + "/point";
    public static final String BASE_BLOG_URL = BASE_URL + "/blog";
    public static final String LOGIN_URL = BASE_URL + "/login";
    public static final String REGISTER_URL = BASE_URL + "/register";

    public static final String GET_USER_BY_ID = BASE_URL + "/user/getByAccount";
    public static final String GET_USERS_BY_IDS = BASE_URL + "/user/getByAccounts";

    public static final String GET_BLOG_COM_ALL_BY_ID = BASE_BLOG_COM_URL + "/getAllByBlogId";
    public static final String DEL_BLOG_COM_BY_ID = BASE_BLOG_COM_URL + "/delete";
    public static final String ADD_BLOG_COM = BASE_BLOG_COM_URL + "/add";

    public static final String GET_BLOG_BY_POINT = BASE_BLOG_URL + "/getByPoint";
    public static final String SEND_BLOG = BASE_BLOG_URL + "/add";

    public static final String GET_ALL_POINT = BASE_POINT_URL + "/findAll";
    public static final String GET_POINT_BY_ID = BASE_POINT_URL + "/findById";


    public static Request generateGetRequest(Map<String,String> param,String url){
        if (param != null){
            StringBuilder builder = new StringBuilder(64);
            builder.append("?");
            for (Map.Entry<String, String> entry : param.entrySet()) {
                builder.append(entry.getKey())
                        .append("=")
                        .append(entry.getValue())
                        .append("&");
            }
            builder.deleteCharAt(builder.length()-1);
            url += builder.toString();
        }
        return new Request.Builder()
                .url(url)
                .get()
                .build();
    }

    public static Request generatePostRequest(Map<String,String> param, String url){
        FormBody.Builder formBody = new FormBody.Builder();
        for (Map.Entry<String, String> entry : param.entrySet()) {
            formBody.add(entry.getKey(),entry.getValue());
        }
        return new Request.Builder()
                .post(formBody.build())
                .url(url)
                .build();
    }

}
