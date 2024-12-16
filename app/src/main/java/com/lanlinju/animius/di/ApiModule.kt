package com.laqoome.laqoo.di

import com.laqoome.laqoo.data.remote.api.laqooApi
import com.laqoome.laqoo.data.remote.api.laqooApiImpl
import com.laqoome.laqoo.data.remote.dandanplay.DandanplayDanmakuProvider
import com.laqoome.laqoo.data.remote.dandanplay.DanmakuProvider
import com.laqoome.laqoo.data.repository.laqooRepositoryImpl
import com.laqoome.laqoo.data.repository.DanmakuRepositoryImpl
import com.laqoome.laqoo.domain.repository.laqooRepository
import com.laqoome.laqoo.domain.repository.DanmakuRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ApiModule {
    @Singleton
    @Binds
    abstract fun provideslaqooApi(laqooApiImpl: laqooApiImpl): laqooApi

    @Singleton
    @Binds
    abstract fun provideslaqooRepository(laqooRepositoryImpl: laqooRepositoryImpl): laqooRepository

    @Singleton
    @Binds
    abstract fun provideDandanplayProvider(dandanplayDanmakuProvider: DandanplayDanmakuProvider): DanmakuProvider

    @Singleton
    @Binds
    abstract fun provideDanmakuRepository(danmakuRepositoryImpl: DanmakuRepositoryImpl): DanmakuRepository
}