package com.github.satahippy.mailru

import com.google.gson.GsonBuilder
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.Type
import java.net.URLEncoder

class FormUrlEncodedConverterFactory : Converter.Factory() {

    override fun requestBodyConverter(
            type: Type,
            parameterAnnotations: Array<Annotation>,
            methodAnnotations: Array<Annotation>,
            retrofit: Retrofit
    ): Converter<*, RequestBody>? {
        if (!(type is Class<*>) || !FormUrlEncodedRequest::class.java.isAssignableFrom(type)) {
            return null
        }
        return FormUrlEncodedConverter.INSTANCE
    }
}

class FormUrlEncodedConverter : Converter<FormUrlEncodedRequest, RequestBody> {

    companion object {
        val MEDIA_TYPE = "application/x-www-form-urlencoded".toMediaTypeOrNull()
        val INSTANCE = FormUrlEncodedConverter()
    }

    override fun convert(value: FormUrlEncodedRequest): RequestBody {
        var urlencoded = ""
        val fieldSet = GsonBuilder().create().toJsonTree(value).getAsJsonObject().entrySet()
        for (entry in fieldSet) {
            val entryKeyString = URLEncoder.encode(entry.key, "utf-8")
            val entryValueString = URLEncoder.encode(entry.value.getAsJsonPrimitive().getAsString(), "utf-8")
            urlencoded += "${entryKeyString}=${entryValueString}&"
        }
        return RequestBody.create(MEDIA_TYPE, urlencoded)
    }
}

interface FormUrlEncodedRequest