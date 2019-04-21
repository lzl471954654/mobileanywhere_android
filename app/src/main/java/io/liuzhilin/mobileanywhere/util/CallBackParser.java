package io.liuzhilin.mobileanywhere.util;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import io.liuzhilin.mobileanywhere.callback.RequestCallback;
import io.liuzhilin.mobileanywhere.exception.AccessDenyException;
import io.liuzhilin.mobileanywhere.exception.ParseNetworkDataException;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class CallBackParser implements Callback {

    public CallBackParser(RequestCallback requestCallback) {
        this.requestCallback = requestCallback;
    }

    private RequestCallback requestCallback;

    @Override
    public void onFailure(Call call, IOException e) {
        requestCallback.failed(e);
    }

    @Override
    public void onResponse(Call call, Response response) throws IOException {
        try{
            String data = response.body().string();
            JSONObject object = new JSONObject(data);
            int code = object.getInt("code");
            String raw = object.getString("data");
            if (code >= 0){
                requestCallback.success(raw);
            }else {
                Exception exception = new Exception("Unknown Exception");
                switch (code) {
                    case -5:{
                        exception = new AccessDenyException(object.getString("msg"));
                        break;
                    }case -1:{

                        break;
                    }default:{
                        exception = new Exception("Unknown Exception");
                        break;
                    }
                }
                requestCallback.failed(exception);
            }
        }catch (NullPointerException e){
            e.printStackTrace();
            requestCallback.failed(e);
        } catch (JSONException e) {
            e.printStackTrace();
            requestCallback.failed(new ParseNetworkDataException(e));
        }

    }
}
