package com.github.satahippy.mailru

import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl
import java.util.ArrayList

class MailruCookieJar : CookieJar {

    val cookies = ArrayList<Cookie>()

    override fun loadForRequest(url: HttpUrl): List<Cookie> {
        return cookies
    }

    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
        this.cookies.addAll(cookies)
    }

    fun getActToken(): String? {
        return cookies.find { it.name == "act" }?.value
    }
}
