package com.lenecoproekt.notes.viewmodel

import com.lenecoproekt.notes.data.NoAuthException
import com.lenecoproekt.notes.model.Repository
import com.lenecoproekt.notes.ui.base.BaseViewModel
import kotlinx.coroutines.launch

class SplashViewModel(val repository: Repository) : BaseViewModel<Boolean>() {

    fun requestUser() {
        launch {
            repository.getCurrentUser()?.let {
                setData(true)
            } ?: setError(NoAuthException())
        }
    }
}