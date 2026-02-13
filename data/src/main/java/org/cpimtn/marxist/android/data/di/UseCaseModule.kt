package org.cpimtn.marxist.android.data.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.cpimtn.marxist.android.domain.repository.PostRepository
import org.cpimtn.marxist.android.domain.repository.SyncStatusRepository
import org.cpimtn.marxist.android.domain.usecase.GetPostsFlowUseCase
import org.cpimtn.marxist.android.domain.usecase.GetSyncStatusUseCase
import org.cpimtn.marxist.android.domain.usecase.SyncPostsUseCase
import org.cpimtn.marxist.core.config.AppConfig
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Provides
    @Singleton
    fun provideGetPostsFlowUseCase(postRepository: PostRepository): GetPostsFlowUseCase =
        GetPostsFlowUseCase(postRepository)

    @Provides
    @Singleton
    fun provideSyncPostsUseCase(
        postRepository: PostRepository,
        syncStatusRepository: SyncStatusRepository,
    ): SyncPostsUseCase = SyncPostsUseCase(
        postRepository,
        syncStatusRepository,
        AppConfig.Sync.DEFAULT_PER_PAGE,
    )

    @Provides
    @Singleton
    fun provideGetSyncStatusUseCase(syncStatusRepository: SyncStatusRepository): GetSyncStatusUseCase =
        GetSyncStatusUseCase(syncStatusRepository)
}
