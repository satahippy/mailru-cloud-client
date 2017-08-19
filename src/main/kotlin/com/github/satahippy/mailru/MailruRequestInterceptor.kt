package com.github.satahippy.mailru

import okhttp3.Interceptor
import okhttp3.Response

class MailruRequestInterceptor : Interceptor {

    var logined = false

    lateinit var csrf: String

    lateinit var xPageId: String

    lateinit var build: String

    lateinit var user: String

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        var modifiedRequest = originalRequest.newBuilder()

        modifiedRequest = modifiedRequest
                .header("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.17 (KHTML, like Gecko) Chrome/24.0.1312.57 Safari/537.17")
        if (this.logined) {
            modifiedRequest = modifiedRequest.url(
                    originalRequest.url().newBuilder()
                            .let{
                                if (originalRequest.url().encodedPath().contains("upload")) {
                                    it
                                        .addQueryParameter("cloud_domain", "2")
                                        .addQueryParameter("x-email", user)
                                } else {
                                    it
                                        .addQueryParameter("api", "2")
                                        .addQueryParameter("token", csrf)
                                        .addQueryParameter("x-page-id", xPageId)
                                        .addQueryParameter("build", build)
                                        .addQueryParameter("email", user)
                                        .addQueryParameter("x-email", user)
                                }
                            }
                            .build()
            )
        }

        return chain.proceed(modifiedRequest.build())
    }


}