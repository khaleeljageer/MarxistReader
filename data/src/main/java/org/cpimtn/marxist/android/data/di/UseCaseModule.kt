package org.cpimtn.marxist.android.data.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.cpimtn.marxist.android.domain.repository.BookRepository
import org.cpimtn.marxist.android.domain.repository.CategoryRepository
import org.cpimtn.marxist.android.domain.repository.PostRepository
import org.cpimtn.marxist.android.domain.repository.RecentSearchRepository
import org.cpimtn.marxist.android.domain.repository.SavedPostRepository
import org.cpimtn.marxist.android.domain.repository.SearchRepository
import org.cpimtn.marxist.android.domain.repository.SettingsRepository
import org.cpimtn.marxist.android.domain.repository.SyncStatusRepository
import org.cpimtn.marxist.android.domain.repository.TagRepository
import org.cpimtn.marxist.android.domain.usecase.AddRecentSearchUseCase
import org.cpimtn.marxist.android.domain.usecase.ClearRecentSearchesUseCase
import org.cpimtn.marxist.android.domain.usecase.DownloadBookUseCase
import org.cpimtn.marxist.android.domain.usecase.GetBookFilePathUseCase
import org.cpimtn.marxist.android.domain.usecase.GetBookReaderIdUseCase
import org.cpimtn.marxist.android.domain.usecase.GetBooksFlowUseCase
import org.cpimtn.marxist.android.domain.usecase.GetCategoriesFlowUseCase
import org.cpimtn.marxist.android.domain.usecase.GetCategoriesWithCountFlowUseCase
import org.cpimtn.marxist.android.domain.usecase.GetDownloadedBookIdsUseCase
import org.cpimtn.marxist.android.domain.usecase.GetFeedItemByIdFlowUseCase
import org.cpimtn.marxist.android.domain.usecase.GetFeedItemsFlowUseCase
import org.cpimtn.marxist.android.domain.usecase.GetPostByIdFlowUseCase
import org.cpimtn.marxist.android.domain.usecase.GetPostsByCategoryFlowUseCase
import org.cpimtn.marxist.android.domain.usecase.GetPostsByMonthFlowUseCase
import org.cpimtn.marxist.android.domain.usecase.GetPostsFlowUseCase
import org.cpimtn.marxist.android.domain.usecase.GetRecentSearchesFlowUseCase
import org.cpimtn.marxist.android.domain.usecase.GetSavedPostIdsFlowUseCase
import org.cpimtn.marxist.android.domain.usecase.GetSavedPostsFlowUseCase
import org.cpimtn.marxist.android.domain.usecase.GetSearchResultsFlowUseCase
import org.cpimtn.marxist.android.domain.usecase.GetSearchSuggestionsFlowUseCase
import org.cpimtn.marxist.android.domain.usecase.GetSeenHelpTopicsUseCase
import org.cpimtn.marxist.android.domain.usecase.GetSettingsFlowUseCase
import org.cpimtn.marxist.android.domain.usecase.GetSyncStatusUseCase
import org.cpimtn.marxist.android.domain.usecase.GetTagsFlowUseCase
import org.cpimtn.marxist.android.domain.usecase.GetTimelineMonthsFlowUseCase
import org.cpimtn.marxist.android.domain.usecase.GetWelcomeCompletedUseCase
import org.cpimtn.marxist.android.domain.usecase.MarkHelpSeenUseCase
import org.cpimtn.marxist.android.domain.usecase.SaveBookReaderIdUseCase
import org.cpimtn.marxist.android.domain.usecase.SavePostUseCase
import org.cpimtn.marxist.android.domain.usecase.SetFontSizeUseCase
import org.cpimtn.marxist.android.domain.usecase.SetHelpIconVisibleUseCase
import org.cpimtn.marxist.android.domain.usecase.SetLanguageUseCase
import org.cpimtn.marxist.android.domain.usecase.SetPushNotificationsUseCase
import org.cpimtn.marxist.android.domain.usecase.SetThemeUseCase
import org.cpimtn.marxist.android.domain.usecase.SetWelcomeCompletedUseCase
import org.cpimtn.marxist.android.domain.usecase.SyncBooksUseCase
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
    fun provideSetHelpIconVisibleUseCase(settingsRepository: SettingsRepository): SetHelpIconVisibleUseCase =
        SetHelpIconVisibleUseCase(settingsRepository)

    @Provides
    @Singleton
    fun provideGetWelcomeCompletedUseCase(settingsRepository: SettingsRepository): GetWelcomeCompletedUseCase =
        GetWelcomeCompletedUseCase(settingsRepository)

    @Provides
    @Singleton
    fun provideSetWelcomeCompletedUseCase(settingsRepository: SettingsRepository): SetWelcomeCompletedUseCase =
        SetWelcomeCompletedUseCase(settingsRepository)

    @Provides
    @Singleton
    fun provideGetSeenHelpTopicsUseCase(settingsRepository: SettingsRepository): GetSeenHelpTopicsUseCase =
        GetSeenHelpTopicsUseCase(settingsRepository)

    @Provides
    @Singleton
    fun provideMarkHelpSeenUseCase(settingsRepository: SettingsRepository): MarkHelpSeenUseCase =
        MarkHelpSeenUseCase(settingsRepository)

    @Provides
    @Singleton
    fun provideGetPostByIdFlowUseCase(postRepository: PostRepository): GetPostByIdFlowUseCase =
        GetPostByIdFlowUseCase(postRepository)

    @Provides
    @Singleton
    fun provideGetFeedItemByIdFlowUseCase(
        getPostByIdFlowUseCase: GetPostByIdFlowUseCase,
        getCategoriesFlowUseCase: GetCategoriesFlowUseCase,
        getTagsFlowUseCase: GetTagsFlowUseCase,
    ): GetFeedItemByIdFlowUseCase =
        GetFeedItemByIdFlowUseCase(
            getPostByIdFlowUseCase,
            getCategoriesFlowUseCase,
            getTagsFlowUseCase
        )

    @Provides
    @Singleton
    fun provideGetCategoriesWithCountFlowUseCase(
        getCategoriesFlowUseCase: GetCategoriesFlowUseCase,
    ): GetCategoriesWithCountFlowUseCase =
        GetCategoriesWithCountFlowUseCase(getCategoriesFlowUseCase)

    @Provides
    @Singleton
    fun provideGetTimelineMonthsFlowUseCase(): GetTimelineMonthsFlowUseCase =
        GetTimelineMonthsFlowUseCase()

    @Provides
    @Singleton
    fun provideGetPostsByCategoryFlowUseCase(
        getCategoriesFlowUseCase: GetCategoriesFlowUseCase,
        getTagsFlowUseCase: GetTagsFlowUseCase,
    ): GetPostsByCategoryFlowUseCase =
        GetPostsByCategoryFlowUseCase(getCategoriesFlowUseCase, getTagsFlowUseCase)

    @Provides
    @Singleton
    fun provideGetPostsByMonthFlowUseCase(
        getCategoriesFlowUseCase: GetCategoriesFlowUseCase,
        getTagsFlowUseCase: GetTagsFlowUseCase,
    ): GetPostsByMonthFlowUseCase =
        GetPostsByMonthFlowUseCase(getCategoriesFlowUseCase, getTagsFlowUseCase)

    @Provides
    @Singleton
    fun provideGetRecentSearchesFlowUseCase(recentSearchRepository: RecentSearchRepository): GetRecentSearchesFlowUseCase =
        GetRecentSearchesFlowUseCase(recentSearchRepository)

    @Provides
    @Singleton
    fun provideAddRecentSearchUseCase(recentSearchRepository: RecentSearchRepository): AddRecentSearchUseCase =
        AddRecentSearchUseCase(recentSearchRepository)

    @Provides
    @Singleton
    fun provideClearRecentSearchesUseCase(recentSearchRepository: RecentSearchRepository): ClearRecentSearchesUseCase =
        ClearRecentSearchesUseCase(recentSearchRepository)

    @Provides
    @Singleton
    fun provideGetSearchResultsFlowUseCase(
        searchRepository: SearchRepository,
        categoryRepository: CategoryRepository,
        tagRepository: TagRepository,
    ): GetSearchResultsFlowUseCase =
        GetSearchResultsFlowUseCase(searchRepository, categoryRepository, tagRepository)

    @Provides
    @Singleton
    fun provideGetSearchSuggestionsFlowUseCase(
        searchRepository: SearchRepository,
        categoryRepository: CategoryRepository,
        tagRepository: TagRepository,
    ): GetSearchSuggestionsFlowUseCase =
        GetSearchSuggestionsFlowUseCase(searchRepository, categoryRepository, tagRepository)

    @Provides
    @Singleton
    fun provideGetBooksFlowUseCase(bookRepository: BookRepository): GetBooksFlowUseCase =
        GetBooksFlowUseCase(bookRepository)

    @Provides
    @Singleton
    fun provideSyncBooksUseCase(bookRepository: BookRepository): SyncBooksUseCase =
        SyncBooksUseCase(bookRepository)

    @Provides
    @Singleton
    fun provideDownloadBookUseCase(bookRepository: BookRepository): DownloadBookUseCase =
        DownloadBookUseCase(bookRepository)

    @Provides
    @Singleton
    fun provideGetDownloadedBookIdsUseCase(bookRepository: BookRepository): GetDownloadedBookIdsUseCase =
        GetDownloadedBookIdsUseCase(bookRepository)

    @Provides
    @Singleton
    fun provideGetBookFilePathUseCase(bookRepository: BookRepository): GetBookFilePathUseCase =
        GetBookFilePathUseCase(bookRepository)

    @Provides
    @Singleton
    fun provideGetBookReaderIdUseCase(bookRepository: BookRepository): GetBookReaderIdUseCase =
        GetBookReaderIdUseCase(bookRepository)

    @Provides
    @Singleton
    fun provideSaveBookReaderIdUseCase(bookRepository: BookRepository): SaveBookReaderIdUseCase =
        SaveBookReaderIdUseCase(bookRepository)
}
