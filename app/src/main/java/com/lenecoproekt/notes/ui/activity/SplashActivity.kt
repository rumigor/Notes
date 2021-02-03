package com.lenecoproekt.notes.ui.activity

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.ViewModelProvider
import com.lenecoproekt.notes.databinding.ActivitySplashBinding
import com.lenecoproekt.notes.ui.base.BaseActivity
import com.lenecoproekt.notes.viewmodel.SplashViewModel
import org.koin.android.ext.android.inject


private const val START_DELAY = 1000L

class SplashActivity : BaseActivity<Boolean?, SplashViewState>() {

    override val viewModel: SplashViewModel by inject()
    override val ui: ActivitySplashBinding by lazy {ActivitySplashBinding.inflate(layoutInflater)}


    override fun onResume() {
        super.onResume()
        Handler(Looper.getMainLooper()).postDelayed({ viewModel.requestUser() }, START_DELAY)
    }

    override fun renderData(data: Boolean?) {
        data?.takeIf{ it }?.let {
            startMainActivity()
        }
    }


    private fun startMainActivity() {
        startActivity(MainActivity.getStartIntent(this))
        finish()
    }
}
