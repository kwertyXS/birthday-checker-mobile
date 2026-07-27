package com.github.kwertyXS.birthdayCheckerMobile.db

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DBModule {

    @Provides
    @Singleton
    fun provideDb(@ApplicationContext ctx: Context): MainDB =
        Room.databaseBuilder(ctx, MainDB::class.java, "main.db")
            .fallbackToDestructiveMigration(false)
            .build()

    @Provides
    fun provideDao(db: MainDB): Dao = db.dao
}
