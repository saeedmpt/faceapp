package com.saeedmpt.chatapp.net

import com.readystatesoftware.chuck.ChuckInterceptor
import com.saeedmpt.chatapp.component.Global
import com.saeedmpt.chatapp.model.api.*
import com.saeedmpt.chatapp.utility.MyConstants
import com.saeedmpt.chatapp.utility.PaperBook
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.io.IOException
import java.security.SecureRandom
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

class ApiService {
    interface DataApi {


        @Multipart
        @POST(MyConstants.route_upload)
        fun profileUpload(
            @Part body: MultipartBody.Part,
            @Part(MyConstants.param_filter) filterParam: RequestBody,
            @Part(MyConstants.param_firebase_token) notificationToken: RequestBody,
        ): Call<UploadApi>

        @FormUrlEncoded
        @POST(MyConstants.route_firebase)
        fun profileFcmUpdate(
            @Field(MyConstants.param_firebase_token) token: String,
        ): Call<ResponseBody>
    }


    private fun provideOkHttpClient(token: String): OkHttpClient {
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY
        return OkHttpClient.Builder()
            .connectTimeout(20, TimeUnit.SECONDS)
            .writeTimeout(20, TimeUnit.SECONDS)
            .readTimeout(25, TimeUnit.SECONDS)
            .addInterceptor { chain ->
                val url = chain
                    .request()
                    .url()
                    .newBuilder()
                    //.addQueryParameter("os", "1")//android=1 , ios=0
                    .addQueryParameter("deviceId", Global.getDeviceId())
                chain.proceed(chain.request().newBuilder().url(url.build()).build())
            }
            .addInterceptor(logging)
            .addInterceptor(TokenInterceptor(token))
            .addInterceptor(ChuckInterceptor(Global.context))
            .build()
    }

    class TokenInterceptor(private val token: String) : Interceptor {
        @Throws(IOException::class)
        override fun intercept(chain: Interceptor.Chain): Response {

            //rewrite the request to add bearer token
            val newRequest: Request.Builder = chain.request().newBuilder()
                .header("Authorization", "Bearer $token")
            /*if (!Global.SOCKET_ID.isNullOrEmpty()) {
                newRequest.header("X-Socket-Id", Global.SOCKET_ID)
            }*/

            return chain.proceed(newRequest.build())
        }
    }

    fun getApi(): DataApi {
        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .client(provideOkHttpClient(PaperBook.token))
            //.client(getUnsafeOkHttpClient().build())
            .baseUrl(MyConstants.BASE_URL)
            .build().create(DataApi::class.java)
    }

    private fun getUnsafeOkHttpClient(): OkHttpClient.Builder =
        try {
            // Create a trust manager that does not validate certificate chains
            val trustAllCerts: Array<TrustManager> = arrayOf(
                object : X509TrustManager {
                    @Throws(CertificateException::class)
                    override fun checkClientTrusted(
                        chain: Array<X509Certificate?>?,
                        authType: String?,
                    ) = Unit

                    @Throws(CertificateException::class)
                    override fun checkServerTrusted(
                        chain: Array<X509Certificate?>?,
                        authType: String?,
                    ) = Unit

                    override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
                }
            )
            // Install the all-trusting trust manager
            val sslContext: SSLContext = SSLContext.getInstance("SSL")
            sslContext.init(null, trustAllCerts, SecureRandom())
            // Create an ssl socket factory with our all-trusting manager
            val sslSocketFactory: SSLSocketFactory = sslContext.socketFactory
            val builder = OkHttpClient.Builder()
            builder.sslSocketFactory(
                sslSocketFactory,
                trustAllCerts[0] as X509TrustManager
            )
            builder.hostnameVerifier { _, _ -> true }
            builder
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
}