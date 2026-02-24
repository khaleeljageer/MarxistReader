package org.cpimtn.marxist.android.data.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.cpimtn.marxist.android.data.source.local.datastore.SyncStatusRepositoryImpl
import org.cpimtn.marxist.android.domain.repository.SyncStatusRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class SyncStatusModule {

    @Binds
    @Singleton
    abstract fun bindSyncStatusRepository(impl: SyncStatusRepositoryImpl): SyncStatusRepository
}
