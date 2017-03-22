package com.l24o.vyatich.modules.connectreceiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import com.google.gson.Gson
import com.l24o.vyatich.Constants
import com.l24o.vyatich.VyatichApplication
import com.l24o.vyatich.data.rest.VyatichInterceptor
import com.l24o.vyatich.data.rest.datasource.TaskDataSource
import com.l24o.vyatich.data.rest.repositories.RealmRepository
import com.l24o.vyatich.data.rest.repositories.TaskRepository
import io.realm.Realm
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * @author Alexander Popov on 26/01/2017.
 */
class ConnectionChangeReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, p1: Intent?) {
        val client = OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC))
                .addInterceptor(VyatichInterceptor())
                .build()
        val adapter = Retrofit.Builder()
                .baseUrl(Constants.API_MAIN_ENDPOINT_URL)
                .client(client)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(Gson()))
                .build()


        val taskDataSource: TaskDataSource = adapter.create(
                TaskDataSource::class.java)

        val taskRepo = TaskRepository(taskDataSource)
        val realmRepo = RealmRepository(Realm.getDefaultInstance())
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = cm.activeNetworkInfo
        if (activeNetwork?.isConnectedOrConnecting ?: false) {
            realmRepo
                    .fetchSyncTasks()
                    .subscribe({
                        tasks ->
                        for (task in tasks) {
                            taskRepo.endTask(task.id)
                                    .subscribe({
                                        realmRepo.updateTask(task)
                                    }, Throwable::printStackTrace)
                        }
                    })

        }
    }
}
