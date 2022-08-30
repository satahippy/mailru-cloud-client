package com.github.satahippy.mailru

import okhttp3.Interceptor
import okhttp3.Response

class MailruRequestInterceptor : Interceptor {

    var logined = false

    lateinit var csrf: String

    lateinit var xPageId: String

    lateinit var build: String

    lateinit var email: String

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        var modifiedRequest = originalRequest.newBuilder()

        modifiedRequest = modifiedRequest
                .header("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.108 YaBrowser/19.12.3.332 (beta) Yowser/2.5 Safari/537.36")
        if (this.logined) {
            modifiedRequest
                    .header("X-CSRF-Token", csrf)
            modifiedRequest = modifiedRequest.url(
                    originalRequest.url.newBuilder()
                            .let {
                                if (originalRequest.url.encodedPath.contains("upload")) {
                                    it
                                            .addQueryParameter("cloud_domain", "2")
                                            .addQueryParameter("x-email", email)
                                } else {
                                    it
                                            .addQueryParameter("api", "2")
                                            .addQueryParameter("x-page-id", xPageId)
                                            .addQueryParameter("build", build)
                                            .addQueryParameter("email", email)
                                            .addQueryParameter("x-email", email)
                                }
                            }
                            .build()
            )
        }

        return chain.proceed(modifiedRequest.build())
    }


}
