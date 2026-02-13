package org.cpimtn.marxist.android.data.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.cpimtn.marxist.android.domain.repository.PostRepository
import org.cpimtn.marxist.android.domain.usecase.GetPostsFlowUseCase
import org.cpimtn.marxist.android.domain.usecase.SyncPostsUseCase
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
    fun provideSyncPostsUseCase(postRepository: PostRepository): SyncPostsUseCase =
        SyncPostsUseCase(postRepository)
}
