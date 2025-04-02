package com.talhaatif.tickojet.data.remote.client


import com.talhaatif.tickojet.BuildConfig
import com.talhaatif.tickojet.data.remote.api.BookingService
import com.talhaatif.tickojet.data.remote.api.EventService
import com.talhaatif.tickojet.data.remote.api.ProfileService
import com.talhaatif.tickojet.data.remote.api.SearchService
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object ApiClient {


    private const val BASE_URL = BuildConfig.API_BASE_URL





    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val request = chain.request().newBuilder().build()
            chain.proceed(request)
        }
        .build()

    val bookingInstance: BookingService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(BookingService::class.java)
    }

    val instance: EventService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(EventService::class.java)
    }

    val profileInstance: ProfileService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ProfileService::class.java)
    }

    val searchInstance: SearchService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(SearchService::class.java)
    }
}