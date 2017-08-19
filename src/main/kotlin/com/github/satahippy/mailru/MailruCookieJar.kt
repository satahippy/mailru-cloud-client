package com.github.satahippy.mailru

import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl
import java.util.ArrayList

class MailruCookieJar : CookieJar {

    val cookies = ArrayList<Cookie>()

    override fun loadForRequest(url: HttpUrl?): MutableList<Cookie> {
        return cookies
    }

    override fun saveFromResponse(url: HttpUrl?, cookiesFromResponse: MutableList<Cookie>?) {
        if (cookiesFromResponse != null) {
            cookies.addAll(cookiesFromResponse)
        }
    }
}