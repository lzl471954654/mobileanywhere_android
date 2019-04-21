package io.liuzhilin.mobileanywhere.requests

import io.liuzhilin.mobileanywhere.callback.RequestCallback
import io.liuzhilin.mobileanywhere.util.CallBackParser


class LoginRequest {

    companion object{
        fun login(username : String, password : String,callBack : CallBackParser){
            val map = hashMapOf<String,String>()
            map["userAccount"] = username
            map["userPass"] = password
            val request = BaseRequests.generateGetRequest(map,BaseRequests.LOGIN_URL)
            BaseRequests.httpClient
                    .newCall(request)
                    .enqueue(callBack)
        }

        fun register(param : Map<String,String>,callBack: CallBackParser){
            val request = BaseRequests.generateGetRequest(param,BaseRequests.REGISTER_URL)
            BaseRequests.httpClient
                    .newCall(request)
                    .enqueue(callBack)
        }
    }

}