package org.cpimtn.marxist.android.data.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.cpimtn.marxist.android.data.source.remote.BookRemoteDataSource
import org.cpimtn.marxist.android.data.source.remote.BookRemoteDataSourceImpl
import org.cpimtn.marxist.android.data.source.remote.CategoryRemoteDataSource
import org.cpimtn.marxist.android.data.source.remote.CategoryRemoteDataSourceImpl
import org.cpimtn.marxist.android.data.source.remote.PostRemoteDataSource
import org.cpimtn.marxist.android.data.source.remote.PostRemoteDataSourceImpl
import org.cpimtn.marxist.android.data.source.remote.TagRemoteDataSource
import org.cpimtn.marxist.android.data.source.remote.TagRemoteDataSourceImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataSourceModule {

    @Binds
    @Singleton
    abstract fun bindPostRemoteDataSource(impl: PostRemoteDataSourceImpl): PostRemoteDataSource

    @Binds
    @Singleton
    abstract fun bindCategoryRemoteDataSource(impl: CategoryRemoteDataSourceImpl): CategoryRemoteDataSource

    @Binds
    @Singleton
    abstract fun bindTagRemoteDataSource(impl: TagRemoteDataSourceImpl): TagRemoteDataSource

    @Binds
    @Singleton
    abstract fun bindBookRemoteDataSource(impl: BookRemoteDataSourceImpl): BookRemoteDataSource
}
