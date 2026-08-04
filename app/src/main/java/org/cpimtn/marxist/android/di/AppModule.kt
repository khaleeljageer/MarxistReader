package org.cpimtn.marxist.android.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.cpimtn.marxist.android.BuildConfig
import org.cpimtn.marxist.android.domain.AppVersionProvider
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun appVersionProvider(): AppVersionProvider = object : AppVersionProvider {
        override fun getVersion(): String = BuildConfig.VERSION_NAME + "_" + BuildConfig.VERSION_CODE
    }
}
