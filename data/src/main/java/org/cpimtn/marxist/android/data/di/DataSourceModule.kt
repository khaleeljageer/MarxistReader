package org.cpimtn.marxist.android.data.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.cpimtn.marxist.android.data.source.remote.PostRemoteDataSource
import org.cpimtn.marxist.android.data.source.remote.PostRemoteDataSourceImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataSourceModule {

    @Binds
    @Singleton
    abstract fun bindPostRemoteDataSource(impl: PostRemoteDataSourceImpl): PostRemoteDataSource
}
