package com.skuuzie.xpass.data.local.di

import android.content.Context
import androidx.room.Room
import com.skuuzie.xpass.data.local.database.AppDatabase
import com.skuuzie.xpass.data.local.database.CredentialDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {

    @Provides
    fun provideCredentialDao(appDatabase: AppDatabase): CredentialDao {
        return appDatabase.credentialDao()
    }

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext ctx: Context): AppDatabase {
        return Room.databaseBuilder(
            ctx,
            AppDatabase::class.java,
            "xpass"
        ).build()
    }
}