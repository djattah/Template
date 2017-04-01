package com.l24o.vyatich.modules.signin

import com.google.gson.Gson
import com.l24o.vyatich.Constants
import com.l24o.vyatich.common.mvp.RxPresenter
import com.l24o.vyatich.data.realm.models.RealmUser
import com.l24o.vyatich.data.rest.VyatichInterceptor
import com.l24o.vyatich.data.rest.datasource.AuthDataSource
import com.l24o.vyatich.data.rest.datasource.UserDataSource
import com.l24o.vyatich.data.rest.models.User
import com.l24o.vyatich.data.rest.repositories.AuthRepository
import com.l24o.vyatich.data.rest.repositories.RealmRepository
import com.l24o.vyatich.data.rest.repositories.UserRepository
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

    var authRepo: AuthRepository
    var realmRepo: RealmRepository
    var userRepo: UserRepository

    init {
        val client = OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
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
        userRepo = UserRepository(adapter.create(
                UserDataSource::class.java))
        realmRepo = RealmRepository(Realm.getDefaultInstance())

    }

    override fun onViewAttached() {
        super.onViewAttached()

        //view?.setLoadingVisible(true)
        /*subscriptions += prevRealmRepo.hasUser()
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
        subscriptions += authRepo.authenticate(login, password)
                .concatMap {
                    authResponse ->
                    userRepo.getUser()
                }
                .subscribe(
                        {
                            result ->
                            view?.saveLoginData(login, password)
                            realmRepo.clearRealmUser()
                            realmRepo.saveUser(RealmUser(login, password))
                            view?.setLoadingVisible(false)
                            view?.navigateToTasks()
                        },
                        { error ->
                            view?.showMessage(error.parsedMessage())
                            view?.setLoadingVisible(false)
                            if (!realmRepo.fetchUsers().isEmpty())
                                view?.navigateToTasks()
                            else
                                view?.showMessage(error.toString())
                        }
                )
    }
}