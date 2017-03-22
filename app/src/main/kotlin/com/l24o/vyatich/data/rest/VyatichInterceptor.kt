package com.l24o.vyatich.data.rest

import com.l24o.vyatich.Constants
import com.pixplicity.easyprefs.library.Prefs
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

class VyatichInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain?): Response {
        try {
            val request: Request?
            val token = Prefs.getString(Constants.TOKEN, null)
            if (token != null) {
                request = chain!!
                        .request()!!
                        .newBuilder()
                        .addHeader("Cookie", token)
                        .build()
            } else {
                request = chain!!.request()
            }
            val response = chain.proceed(request)
            val header = response.header("Set-Cookie")
            if (header != null && header.isNotEmpty()) {
                Prefs.putString(Constants.TOKEN, header.split(";")[0])
            }
            return response
        } catch(e: Exception) {
            throw IOException("Не получилось установить связь сервером", e)
        }
    }
}