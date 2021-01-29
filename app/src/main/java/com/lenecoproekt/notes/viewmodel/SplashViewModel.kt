package com.lenecoproekt.notes.viewmodel

import com.lenecoproekt.notes.data.NoAuthException
import com.lenecoproekt.notes.model.Repository
import com.lenecoproekt.notes.ui.activity.SplashViewState
import com.lenecoproekt.notes.ui.base.BaseViewModel

class SplashViewModel(private val repository: Repository = Repository) : BaseViewModel<Boolean?, SplashViewState>() {

    fun requestUser() {
        repository.getCurrentUser().observeForever {user->
            viewStateLiveData.value = user?.let {
                SplashViewState(isAuth = true)
            } ?: SplashViewState(error = NoAuthException())
        }
    }
}