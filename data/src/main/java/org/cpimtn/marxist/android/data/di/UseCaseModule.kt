package org.cpimtn.marxist.android.data.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.cpimtn.marxist.android.domain.repository.CategoryRepository
import org.cpimtn.marxist.android.domain.repository.PostRepository
import org.cpimtn.marxist.android.domain.repository.SavedPostRepository
import org.cpimtn.marxist.android.domain.repository.SettingsRepository
import org.cpimtn.marxist.android.domain.repository.SyncStatusRepository
import org.cpimtn.marxist.android.domain.repository.TagRepository
import org.cpimtn.marxist.android.domain.usecase.GetCategoriesFlowUseCase
import org.cpimtn.marxist.android.domain.usecase.GetFeedItemsFlowUseCase
import org.cpimtn.marxist.android.domain.usecase.GetPostsFlowUseCase
import org.cpimtn.marxist.android.domain.usecase.GetSavedPostIdsFlowUseCase
import org.cpimtn.marxist.android.domain.usecase.GetSavedPostsFlowUseCase
import org.cpimtn.marxist.android.domain.usecase.GetSettingsFlowUseCase
import org.cpimtn.marxist.android.domain.usecase.GetSyncStatusUseCase
import org.cpimtn.marxist.android.domain.usecase.GetTagsFlowUseCase
import org.cpimtn.marxist.android.domain.usecase.GetWelcomeCompletedUseCase
import org.cpimtn.marxist.android.domain.usecase.SavePostUseCase
import org.cpimtn.marxist.android.domain.usecase.SetFontSizeUseCase
import org.cpimtn.marxist.android.domain.usecase.SetLanguageUseCase
import org.cpimtn.marxist.android.domain.usecase.SetPushNotificationsUseCase
import org.cpimtn.marxist.android.domain.usecase.SetThemeUseCase
import org.cpimtn.marxist.android.domain.usecase.SetWelcomeCompletedUseCase
import org.cpimtn.marxist.android.domain.usecase.SyncPostsUseCase
import org.cpimtn.marxist.android.domain.usecase.SyncTaxonomyUseCase
import org.cpimtn.marxist.android.domain.usecase.UnsavePostUseCase
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

    @Provides
    @Singleton
    fun provideGetCategoriesFlowUseCase(categoryRepository: CategoryRepository): GetCategoriesFlowUseCase =
        GetCategoriesFlowUseCase(categoryRepository)

    @Provides
    @Singleton
    fun provideGetTagsFlowUseCase(tagRepository: TagRepository): GetTagsFlowUseCase =
        GetTagsFlowUseCase(tagRepository)

    @Provides
    @Singleton
    fun provideGetFeedItemsFlowUseCase(
        getPostsFlowUseCase: GetPostsFlowUseCase,
        getCategoriesFlowUseCase: GetCategoriesFlowUseCase,
        getTagsFlowUseCase: GetTagsFlowUseCase,
    ): GetFeedItemsFlowUseCase =
        GetFeedItemsFlowUseCase(getPostsFlowUseCase, getCategoriesFlowUseCase, getTagsFlowUseCase)

    @Provides
    @Singleton
    fun provideGetSavedPostIdsFlowUseCase(savedPostRepository: SavedPostRepository): GetSavedPostIdsFlowUseCase =
        GetSavedPostIdsFlowUseCase(savedPostRepository)

    @Provides
    @Singleton
    fun provideSavePostUseCase(savedPostRepository: SavedPostRepository): SavePostUseCase =
        SavePostUseCase(savedPostRepository)

    @Provides
    @Singleton
    fun provideUnsavePostUseCase(savedPostRepository: SavedPostRepository): UnsavePostUseCase =
        UnsavePostUseCase(savedPostRepository)

    @Provides
    @Singleton
    fun provideGetSavedPostsFlowUseCase(
        getPostsFlowUseCase: GetPostsFlowUseCase,
        getSavedPostIdsFlowUseCase: GetSavedPostIdsFlowUseCase,
        getCategoriesFlowUseCase: GetCategoriesFlowUseCase,
        getTagsFlowUseCase: GetTagsFlowUseCase,
    ): GetSavedPostsFlowUseCase =
        GetSavedPostsFlowUseCase(
            getPostsFlowUseCase, getSavedPostIdsFlowUseCase,
            getCategoriesFlowUseCase, getTagsFlowUseCase
        )

    @Provides
    @Singleton
    fun provideSyncTaxonomyUseCase(
        categoryRepository: CategoryRepository,
        tagRepository: TagRepository,
    ): SyncTaxonomyUseCase = SyncTaxonomyUseCase(categoryRepository, tagRepository)

    @Provides
    @Singleton
    fun provideGetSettingsFlowUseCase(settingsRepository: SettingsRepository): GetSettingsFlowUseCase =
        GetSettingsFlowUseCase(settingsRepository)

    @Provides
    @Singleton
    fun provideSetThemeUseCase(settingsRepository: SettingsRepository): SetThemeUseCase =
        SetThemeUseCase(settingsRepository)

    @Provides
    @Singleton
    fun provideSetFontSizeUseCase(settingsRepository: SettingsRepository): SetFontSizeUseCase =
        SetFontSizeUseCase(settingsRepository)

    @Provides
    @Singleton
    fun provideSetLanguageUseCase(settingsRepository: SettingsRepository): SetLanguageUseCase =
        SetLanguageUseCase(settingsRepository)

    @Provides
    @Singleton
    fun provideSetPushNotificationsUseCase(settingsRepository: SettingsRepository): SetPushNotificationsUseCase =
        SetPushNotificationsUseCase(settingsRepository)

    @Provides
    @Singleton
    fun provideGetWelcomeCompletedUseCase(settingsRepository: SettingsRepository): GetWelcomeCompletedUseCase =
        GetWelcomeCompletedUseCase(settingsRepository)

    @Provides
    @Singleton
    fun provideSetWelcomeCompletedUseCase(settingsRepository: SettingsRepository): SetWelcomeCompletedUseCase =
        SetWelcomeCompletedUseCase(settingsRepository)
}
