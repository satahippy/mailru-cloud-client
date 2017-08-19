package com.github.satahippy.mailru

import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.*
import java.util.regex.Pattern
import javax.xml.bind.DatatypeConverter

class Cloud(inner: CloudApi) : CloudApi by inner {

    private lateinit var requestInterceptor: MailruRequestInterceptor

    private lateinit var uploadUrl: String

    companion object Factory {
        fun instance(): Cloud {
            val requestInterceptor = MailruRequestInterceptor()

            val client = OkHttpClient.Builder()
                    .cookieJar(MailruCookieJar())
                    .addInterceptor(requestInterceptor)
                    .build()

            val retrofit = Retrofit.Builder()
                    .client(client)
                    .baseUrl("https://cloud.mail.ru/api/v2/")
                    .addConverterFactory(FormUrlEncodedConverterFactory())
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()

            val instance = Cloud(retrofit.create(CloudApi::class.java))
            instance.requestInterceptor = requestInterceptor
            return instance
        }
    }

    fun login(username: String, password: String) {
        val html = internalLogin(InternalLoginRequest(
                page = "https://cloud.mail.ru/?from=promo",
                FailPage = "",
                Domain = "mail.ru",
                Login = username,
                Password = password,
                new_auth_form = 1
        )).execute().body() ?: throw MailruException("Can't receive login page")

        requestInterceptor.csrf = searchOnLoginPage("csrf", html) ?: throw MailruException("Can't extract csrf from login page")
        requestInterceptor.xPageId = searchOnLoginPage("x-page-id", html) ?: throw MailruException("Can't extract x-page-id from login page")
        requestInterceptor.build = searchOnLoginPage("BUILD", html) ?: throw MailruException("Can't extract BUILD from login page")
        uploadUrl = searchUploadUrlOnLoginPage(html) ?: throw MailruException("Can't extract upload url from login page")
        requestInterceptor.user = username
        requestInterceptor.logined = true
    }

    private fun searchOnLoginPage(parameter: String, html: String): String? {
        val matcher = Pattern.compile(""""$parameter":"(.*?)"""").matcher(html)
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
}

interface CloudApi {
    @POST("http://auth.mail.ru/cgi-bin/auth?lang=ru_RU&from=authpopup")
    fun internalLogin(@Body request: InternalLoginRequest): Call<String>

    @PUT
    fun internalUploadFile(@Url url: String, @Body bytes: RequestBody): Call<String>

    @GET("folder?sort={\"type\":\"name\",\"order\":\"asc\"}&offset=0&limit=500")
    fun getFolder(@Query("home") home: String = "/"): Call<MailruResponse<FolderOrFile>>

    @POST("folder/add?conflict=rename")
    fun addFolder(@Query("home") home: String): Call<MailruResponse<String>>

    @POST("file/add")
    fun addFile(
            @Query("home") home: String,
            @Query("hash") hash: String,
            @Query("size") size: Int
    ): Call<MailruResponse<String>>

    @POST("file/remove")
    fun removeFile(@Query("home") home: String): Call<MailruResponse<String>>
}