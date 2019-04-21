package io.liuzhilin.mobileanywhere.callback;

public interface RequestCallback {

    void success(String json);

    void failed(Exception e);

}
