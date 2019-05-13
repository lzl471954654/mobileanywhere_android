import com.google.gson.Gson
import com.google.gson.JsonParser
import io.liuzhilin.mobileanywhere.callback.RequestCallback
import io.liuzhilin.mobileanywhere.util.GitHubUploadModule.UploadData
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import java.lang.Exception
import java.util.concurrent.TimeUnit


private const val ROOT_URL = "https://api.github.com"

private const val username = "puredeepblue"
private const val token = "deepblue@bobbydoor"
private const val repo = "media_storage"

fun getAuthencationString(username: String, token: String): String {
    return android.util.Base64.encodeToString("$username:$token".toByteArray(charset("UTF-8")),android.util.Base64.DEFAULT).trim()
}

fun sendToGitHub(fileName : String,base64Data : String,callback: RequestCallback){
    val pathName = fileName
    val url = createFileAPIURL(username,repo,pathName)
    val uploadData = UploadData()
    uploadData.message = "upload a file named $pathName"
    uploadData.path = pathName
    uploadData.content = base64Data
    val bodyData = Gson().toJson(uploadData)
    val requestBody = RequestBody.create(MediaType.parse("application/json"),bodyData)
    val request = Request.Builder()
            .url(url)
            .addHeader("Authorization"," Basic ${getAuthencationString(username,token)}")
            .put(requestBody)
            .build()
    val call = client.newCall(request)
    val response = call.execute()
    val responseData = response.body()?.string()
    println("code is ${response.code()}")
    println("reponseData is :\n$responseData")
    if (response.code() == 201){
        callback.success(getDownloadURL(responseData!!))
    }
    else{
        callback.failed(Exception("send data error , http status code : ${response.code()}"))
    }
}

fun createFileAPIURL(owner: String, repo: String, path: String): String {
    return "$ROOT_URL/repos/$owner/$repo/contents/$path"
}

fun getDownloadURL( data : String) = JsonParser()
        .parse(data).asJsonObject.get("content")
        .asJsonObject.get("download_url").asString



val client = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(120, TimeUnit.SECONDS)
        .build()
