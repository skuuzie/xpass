package com.skuuzie.xpass.data.local.di

import com.skuuzie.xpass.data.local.CredentialRepository
import com.skuuzie.xpass.data.local.DefaultCredentialRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface LocalDataModule {

    @Singleton
    @Binds
    fun bindsCredentialRepository(
        credentialRepository: DefaultCredentialRepository
    ): CredentialRepository
}