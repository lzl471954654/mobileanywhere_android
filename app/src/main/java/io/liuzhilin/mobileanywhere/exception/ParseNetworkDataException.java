package io.liuzhilin.mobileanywhere.exception;

import org.json.JSONException;

public class ParseNetworkDataException extends RuntimeException {

    public ParseNetworkDataException(JSONException e){
        super(e);
    }

}
