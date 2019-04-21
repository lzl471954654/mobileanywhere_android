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
    public static final String LOGIN_URL = BASE_URL + "/login";
    public static final String REGISTER_URL = BASE_URL + "/register";


    public static Request generateGetRequest(Map<String,String> param,String url){
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
