package com.github.kwertyXS.birthdayCheckerMobile.db

import android.app.Application
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface WorkerEntryPoint {
    fun dao(): Dao
    fun application(): Application
}
