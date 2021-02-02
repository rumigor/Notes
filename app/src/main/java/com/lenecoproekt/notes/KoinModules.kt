package com.lenecoproekt.notes

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.lenecoproekt.notes.data.FireStoreProvider
import com.lenecoproekt.notes.data.RemoteDataProvider
import com.lenecoproekt.notes.model.Repository
import com.lenecoproekt.notes.viewmodel.MainViewModel
import com.lenecoproekt.notes.viewmodel.NoteViewModel
import com.lenecoproekt.notes.viewmodel.SplashViewModel
import org.koin.dsl.bind
import org.koin.dsl.module

val appModule = module {
    single { FirebaseAuth.getInstance() }
    single { FirebaseFirestore.getInstance() }
    single { FireStoreProvider(get(), get()) } bind RemoteDataProvider::class
    single { Repository(get()) }
}

val splashModule = module {
    factory { SplashViewModel(get()) }
}

val mainModule = module {
    factory { MainViewModel(get()) }
}

val noteModule = module {
    factory { NoteViewModel(get()) }
}