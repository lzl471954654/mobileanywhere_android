package io.liuzhilin.mobileanywhere.requests

import android.util.Log
import io.liuzhilin.mobileanywhere.util.CallBackParser

class RequestCenter {

    companion object{


        fun requestGet(requestName : String, param : Map<String,String>?,callBackParser: CallBackParser){
            val url = requestMap[requestName]
            val request = BaseRequests.generateGetRequest(param,url)
            Log.e("RequestCenter",url)
            BaseRequests.httpClient
                    .newCall(request)
                    .enqueue(callBackParser)
        }

        fun requestPost(requestName : String, param : Map<String,String>,callBackParser: CallBackParser){
            val url = requestMap[requestName]
            Log.e("RequestCenter",url)
            val request = BaseRequests.generatePostRequest(param,url)
            BaseRequests.httpClient
                    .newCall(request)
                    .enqueue(callBackParser)
        }


        private val requestMap : HashMap<String,String> = hashMapOf()


        public const val ADD_BLOG = "addBlog"
        public const val DEL_BLOG = "delBlog"
        public const val GET_BLOG_BY_POINT = "getBlogByPoint"
        public const val ADD_COMMENT = "addComment"
        public const val DEL_COMMENT = "delComment"
        public const val GET_COMMENT_BY_BLOG_ID = "getCommentByBlogId"

        public const val GET_ALL_POINT = "getAllPoint"
        public const val GET_POINT_BY_ID = "getPointById"

        public const val LOGIN = "login"
        public const val REGISTER = "register"

        public const val GET_USER = "getUser"
        public const val GET_USERS = "getUsers"

        public const val SEND_BLOG = "sendBlog"

        init {
            requestMap[SEND_BLOG] = BaseRequests.SEND_BLOG

            requestMap[GET_USER] = BaseRequests.GET_USER_BY_ID
            requestMap[GET_USERS] = BaseRequests.GET_USERS_BY_IDS

            requestMap[LOGIN] = BaseRequests.LOGIN_URL
            requestMap[REGISTER] = BaseRequests.REGISTER_URL

            requestMap[ADD_COMMENT] = BaseRequests.ADD_BLOG_COM
            requestMap[GET_COMMENT_BY_BLOG_ID] = BaseRequests.GET_BLOG_COM_ALL_BY_ID

            requestMap[GET_ALL_POINT] = BaseRequests.GET_ALL_POINT
            requestMap[GET_POINT_BY_ID] = BaseRequests.GET_POINT_BY_ID

            requestMap[GET_BLOG_BY_POINT] = BaseRequests.GET_BLOG_BY_POINT

        }
    }
}