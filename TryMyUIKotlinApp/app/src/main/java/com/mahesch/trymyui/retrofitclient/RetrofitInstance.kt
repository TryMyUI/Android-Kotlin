package com.mahesch.trymyui.retrofitclient

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

class RetrofitInstance() {

    companion object{

        var httpBuilder: OkHttpClient.Builder? = null
        var client: OkHttpClient? = null

        private fun getUnsafeOkHttpClientForPost():OkHttpClient?{
            var myClient: OkHttpClient? = null

            val httpBuilder =
                OkHttpClient.Builder().addInterceptor { chain ->
                    val original = chain.request()
                    // Customize the request
                    val request =
                        original.newBuilder()
                         //   .header("Accept", "application/json")
					      // .header("Authorization", "auth-token")
                            .method(original.method(), original.body())
                            .build()
                    // Customize or return the response
                    chain.proceed(request)
                }

            try {

                // Create a trust manager that does not validate certificate chains
                val trustAllCerts =
                    arrayOf<TrustManager>(
                        object : X509TrustManager {
                            override fun checkClientTrusted(
                                chain: Array<X509Certificate>,
                                authType: String
                            ) {
                            }

                            override fun checkServerTrusted(
                                chain: Array<X509Certificate>,
                                authType: String
                            ) {
                            }

                            override fun getAcceptedIssuers(): Array<X509Certificate?> {
                                return arrayOfNulls(0)
                            }
                        }
                    )

                val sslContext: SSLContext = SSLContext.getInstance("SSL")

                sslContext.init(null,trustAllCerts,java.security.SecureRandom())

                val sslSocketFactory =sslContext.socketFactory

                httpBuilder.sslSocketFactory(sslSocketFactory)

                httpBuilder.hostnameVerifier { hostname, session ->  true }

                val interceptor = HttpLoggingInterceptor()
                interceptor.level = HttpLoggingInterceptor.Level.BODY

                myClient = httpBuilder.connectTimeout(60,TimeUnit.MINUTES)
                    .readTimeout(60,TimeUnit.MINUTES)
                    .writeTimeout(60,TimeUnit.MINUTES)
                    .addInterceptor(interceptor).build()

            }
            catch (e: Exception){
                throw RuntimeException(e)
            }

            return myClient
        }

        private fun getUnsafeOkHttpClient(): OkHttpClient? {
            try {
                val trustAllCerts = arrayOf<TrustManager>(
                    object :  X509TrustManager{
                        override fun checkClientTrusted(
                            chain: Array<out X509Certificate>?,
                            authType: String?
                        ) {
                          //  TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                        }

                        override fun checkServerTrusted(
                            chain: Array<out X509Certificate>?,
                            authType: String?
                        ) {
                          //  TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                        }

                        override fun getAcceptedIssuers(): Array<X509Certificate?> {
                          //  TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                            return arrayOfNulls(0)
                        }
                    }
                )

                val sslContext = SSLContext.getInstance("SSL")
                sslContext.init(null,trustAllCerts,java.security.SecureRandom())

                val sslSocketFactory = sslContext.socketFactory

                httpBuilder?.sslSocketFactory(sslSocketFactory)
                httpBuilder?.hostnameVerifier { hostname, session ->  true }

                client = httpBuilder?.connectTimeout(60,TimeUnit.MINUTES)
                    ?.readTimeout(60,TimeUnit.MINUTES)
                    ?.writeTimeout(60,TimeUnit.MINUTES)?.build()


            }
            catch (e: java.lang.Exception){
                throw java.lang.RuntimeException(e)
            }

            return client
        }

        // this service is USE only for GET methods and some POST methods which having single parameters to pass
        fun getService() : ApiService.ApiInterface {

            apiService()

            httpBuilder = OkHttpClient.Builder().addInterceptor { chain ->
                val original = chain.request()

                val request =original.newBuilder()
                    .method(original.method(),original.body())
                    .build()

                chain.proceed(request)
            }

            getUnsafeOkHttpClient()

            val restAdapterType = Retrofit.Builder()
                .client(client)
                .baseUrl(ApiService.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            return restAdapterType.create(ApiService.ApiInterface::class.java)


        }

        fun getPostApiService() : ApiService.ApiInterface {

            apiService()

            val okHttpClient = getUnsafeOkHttpClientForPost()

            val retrofit =Retrofit.Builder()
                .baseUrl(ApiService.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build()

            return retrofit.create(ApiService.ApiInterface::class.java)

        }

        private fun apiService(){
            ApiService.BASE_URL = "https://beta.trymyui.com"
        }

    }






}
