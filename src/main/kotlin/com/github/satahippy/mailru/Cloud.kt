package com.github.satahippy.mailru

import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.*
import java.util.regex.Pattern
import javax.xml.bind.DatatypeConverter

class Cloud(inner: CloudApi, val cookieJar: MailruCookieJar) : CloudApi by inner {

    private lateinit var requestInterceptor: MailruRequestInterceptor

    private lateinit var uploadUrl: String

    private lateinit var downloadUrl: String

    companion object Factory {
        fun instance(): Cloud {
            val requestInterceptor = MailruRequestInterceptor()
            val cookieJar = MailruCookieJar()

            val client = OkHttpClient.Builder()
                    .cookieJar(cookieJar)
                    .addInterceptor(requestInterceptor)
                    .build()

            val retrofit = Retrofit.Builder()
                    .client(client)
                    .baseUrl("https://cloud.mail.ru/api/v2/")
                    .addConverterFactory(FormUrlEncodedConverterFactory())
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()

            val instance = Cloud(retrofit.create(CloudApi::class.java), cookieJar)
            instance.requestInterceptor = requestInterceptor
            return instance
        }
    }

    fun login(username: String, password: String) {
        // изначальный запрос нужен чтобы получить act_token
        init().execute()
        val actToken = cookieJar.getActToken()
                ?: throw MailruException("Act token is not found in cookies")

        val html = internalLogin(InternalLoginRequest(
                Login = username,
                Password = password,
                act_token = actToken,
                page = "https://cloud.mail.ru/?from=login&from-page=promo&from-promo=blue-2018",
                Domain = "mail.ru",
                FromAccount = "opener=account&twoSteps=1",
                new_auth_form = 1,
                saveauth = 1,
                lang = "ru_RU"
        )).execute().body() ?: throw MailruException("Can't receive login page")

        requestInterceptor.csrf = searchOnLoginPage("csrf", html)
                ?: throw MailruException("Can't extract csrf from login page")
        requestInterceptor.xPageId = searchOnLoginPage("x-page-id", html)
                ?: throw MailruException("Can't extract x-page-id from login page")
        requestInterceptor.build = searchOnLoginPage("BUILD", html)
                ?: throw MailruException("Can't extract BUILD from login page")
        uploadUrl = searchUploadUrlOnLoginPage(html)
                ?: throw MailruException("Can't extract upload url from login page")
        downloadUrl = searchDownloadUrlOnLoginPage(html)
                ?: throw MailruException("Can't extract download url from login page")
        requestInterceptor.email = username + "@mail.ru"
        requestInterceptor.logined = true
    }

    private fun searchOnLoginPage(parameter: String, html: String): String? {
        val matcher = Pattern.compile(""""$parameter":\s*"(.*?)"""").matcher(html)
        if (!matcher.find()) {
            return null
        }
        return matcher.group(1)
    }

    private fun searchUploadUrlOnLoginPage(html: String): String? {
        val matcher = Pattern.compile(""""([^"]+mail.ru/upload/)"""").matcher(html)
        if (!matcher.find()) {
            return null
        }
        return matcher.group(1)
    }

    private fun searchDownloadUrlOnLoginPage(html: String): String? {
        val matcher = Pattern.compile(""""([^"]+mail.ru/attach/)"""").matcher(html)
        if (!matcher.find()) {
            return null
        }
        return matcher.group(1)
    }

    fun uploadFile(home: String, data: ByteArray): Call<MailruResponse<String>> {
        val size = data.size
        val hash = uploadFileHash(data)
        return addFile(home, hash, size)
    }

    private fun uploadFileHash(data: ByteArray): String {
        if (data.size < 21) {
            return String.format("%-40s", DatatypeConverter.printHexBinary(data))
                    .replace(" ", "0")
        }
        return internalUploadFile(
                uploadUrl,
                RequestBody.create(MediaType.parse("application/octet-stream"), data)
        ).execute().body() ?: throw MailruException("Can't upload file")
    }

    fun downloadFile(home: String): Call<ResponseBody> {
        val downloadFileUrl = downloadUrl + home
        return internalDownloadFile(downloadFileUrl)
    }
}

interface CloudApi {

    @GET("https://account.mail.ru/login/")
    fun init(): Call<String>

    @POST("https://auth.mail.ru/cgi-bin/auth")
    fun internalLogin(@Body request: InternalLoginRequest): Call<String>

    @PUT
    fun internalUploadFile(@Url url: String, @Body bytes: RequestBody): Call<String>

    @GET
    fun internalDownloadFile(@Url url: String): Call<ResponseBody>

    @GET("folder")
    fun getFolder(
            @Query("home") home: String,
            @Query("sort") sort: Sort = Sort(type = "name", order = "asc"),
            @Query("offset") offset: Int = 0,
            @Query("limit") limit: Int = 500
    ): Call<MailruResponse<FolderOrFile>>

    @POST("folder/add")
    fun addFolder(
            @Query("home") home: String,
            @Query("conflict") conflict: String = "rename"
    ): Call<MailruResponse<String>>

    @POST("file")
    fun getFile(
            @Query("home") home: String
    ): Call<MailruResponse<FolderOrFile>>

    @POST("file/add")
    fun addFile(
            @Query("home") home: String,
            @Query("hash") hash: String,
            @Query("size") size: Int
    ): Call<MailruResponse<String>>

    @POST("file/remove")
    fun removeFile(
            @Query("home") home: String
    ): Call<MailruResponse<String>>
}
