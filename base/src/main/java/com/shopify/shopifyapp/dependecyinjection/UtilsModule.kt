package com.shopify.shopifyapp.dependecyinjection

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import com.shopify.buy3.GraphClient
import com.shopify.shopifyapp.MyApplication
import com.shopify.shopifyapp.R
import com.shopify.shopifyapp.dbconnection.database.AppDatabase
import com.shopify.shopifyapp.repositories.Repository
import com.shopify.shopifyapp.utils.ApiCallInterface
import com.shopify.shopifyapp.utils.Constant
import com.shopify.shopifyapp.utils.Urls
import com.shopify.shopifyapp.utils.ViewModelFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton
import dagger.Module
import dagger.Provides
import me.jessyan.retrofiturlmanager.RetrofitUrlManager
import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.URI

@Module
class UtilsModule(private val context: Context) {

    internal val requestHeader: OkHttpClient
        @Provides
        @Singleton
        get() {
            val httpClient = RetrofitUrlManager.getInstance().with(OkHttpClient.Builder())
            httpClient.addInterceptor { chain ->
                val original = chain.request()
                val request = original.newBuilder().build()
                Log.i("OkHttp", " " + request.url())
                Log.i("OkHttp", " " + request.headers())
                Log.i("OkHttp", " " + request.method())
                chain.proceed(request)
            }
                    .connectTimeout(100, TimeUnit.SECONDS)
                    .writeTimeout(100, TimeUnit.SECONDS)
                    .readTimeout(300, TimeUnit.SECONDS)

            return httpClient.build()
        }

    @Provides
    @Singleton
    internal fun provideGson(): Gson {
        val builder = GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
        return builder.setLenient().create()
    }

    @Provides
    @Singleton
    internal fun provideRetrofit(gson: Gson, okHttpClient: OkHttpClient): Retrofit {


        return Retrofit.Builder()
                .baseUrl(Urls.BASE_URL)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
    }

    @Provides
    @Singleton
    internal fun getApiCallInterface(retrofit: Retrofit): ApiCallInterface {
        return retrofit.create(ApiCallInterface::class.java)
    }

    @Provides
    @Singleton
    internal fun getRepository(apiCallInterface: ApiCallInterface, appDatabase: AppDatabase): Repository {
        return Repository(apiCallInterface, appDatabase)
    }

    @Provides
    @Singleton
    internal fun getViewModelFactory(myRepository: Repository): ViewModelProvider.Factory {
        return ViewModelFactory(myRepository)
    }

    @Provides
    @Singleton
    internal fun provideContext(): Context {
        return context
    }

    @Provides
    @Singleton
    internal fun getAppDatabase(context: Context): AppDatabase {
        return Room.databaseBuilder(context, AppDatabase::class.java, "MageNative").fallbackToDestructiveMigration().build()
    }

}
