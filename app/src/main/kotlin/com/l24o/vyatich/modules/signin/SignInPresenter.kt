package com.l24o.vyatich.modules.signin

import com.google.gson.Gson
import com.l24o.vyatich.Constants
import com.l24o.vyatich.common.mvp.RxPresenter
import com.l24o.vyatich.data.rest.VyatichInterceptor
import com.l24o.vyatich.data.rest.datasource.AuthDataSource
import com.l24o.vyatich.data.rest.repositories.AuthRepository
import com.l24o.vyatich.data.rest.repositories.RealmRepository
import com.l24o.vyatich.extensions.parsedMessage
import io.realm.Realm
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import rx.lang.kotlin.plusAssign
import java.util.concurrent.TimeUnit


class SignInPresenter(view: ISignInView) : RxPresenter<ISignInView>(view), ISignInPresenter {

    lateinit var authRepo: AuthRepository
    lateinit var realmRepo: RealmRepository

    init {
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


        authRepo = AuthRepository(adapter.create(
                AuthDataSource::class.java))
        realmRepo = RealmRepository(Realm.getDefaultInstance())

    }

    override fun onViewAttached() {
        super.onViewAttached()

        view?.setLoadingVisible(true)
        /*subscriptions += realmRepo.hasUser()
                .subscribe({
                    result ->
                    view?.setLoadingVisible(false)
                    if (result > 0) {
                        view?.navigateToTasks()
                    }
                }, {
                    error ->
                    view?.setLoadingVisible(false)
                    view?.showMessage(error.parsedMessage())
                })*/
    }

    override fun onSignInClick(login: String, password: String) {
        authenticate(login, password)
    }

    private fun authenticate(login: String, password: String) {
        view?.setLoadingVisible(true)

        if (login == SignInActivity.TEST_USER_ID && password == SignInActivity.TEST_PASSWORD) {
            view?.navigateToTasks()
            view?.setLoadingVisible(false)
        }
        // выпилил password, тк его нет в бд
        /*subscriptions += authRepo.authenticate(login)
                .subscribe(
                        {
                            result ->
                            view?.navigateToTasks()
                            view?.setLoadingVisible(false)
                        },
                        { error ->
                            view?.showMessage(error.parsedMessage())
                            view?.setLoadingVisible(false)
                        }
                )*/
    }
}