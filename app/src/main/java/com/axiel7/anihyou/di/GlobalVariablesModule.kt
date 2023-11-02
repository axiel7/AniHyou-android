package com.axiel7.anihyou.di

import com.axiel7.anihyou.common.GlobalVariables
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object GlobalVariablesModule {

    @Singleton
    @Provides
    fun provideGlobalVariables(): GlobalVariables {
        return GlobalVariables()
    }

}