package com.github.kwertyXS.birthdayCheckerMobile

import android.app.Application
import com.github.kwertyXS.birthdayCheckerMobile.api.Repository
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class App : Application() {
    @Inject lateinit var repository: Repository
}
