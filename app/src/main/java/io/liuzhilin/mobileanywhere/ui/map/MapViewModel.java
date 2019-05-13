package io.liuzhilin.mobileanywhere.ui.map;

import android.os.Handler;

import java.util.HashMap;
import java.util.Map;

import io.liuzhilin.mobileanywhere.callback.RequestCallback;
import io.liuzhilin.mobileanywhere.requests.RequestCenter;
import io.liuzhilin.mobileanywhere.util.CallBackParser;

public class MapViewModel {

    private Handler handler;

    public MapViewModel(Handler handler){
        this.handler = handler;
    }

    public void getAllPoint(RequestCallback callback){
        RequestCenter.Companion.requestGet(RequestCenter.GET_ALL_POINT,null,new CallBackParser(callback));
    }

    public void getPointById(String pointId,RequestCallback callback){
        Map<String,String> map = new HashMap<>();
        map.put("pointId",pointId);
        RequestCenter.Companion.requestGet(RequestCenter.GET_POINT_BY_ID,map,new CallBackParser(callback));
    }

    public void destory(){
        handler = null;
    }

}
