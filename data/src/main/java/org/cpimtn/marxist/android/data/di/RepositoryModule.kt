package org.cpimtn.marxist.android.data.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.cpimtn.marxist.android.data.repository.CategoryRepositoryImpl
import org.cpimtn.marxist.android.data.repository.PostRepositoryImpl
import org.cpimtn.marxist.android.data.datastore.UserSettingsRepositoryImpl
import org.cpimtn.marxist.android.data.repository.SavedPostRepositoryImpl
import org.cpimtn.marxist.android.data.repository.TagRepositoryImpl
import org.cpimtn.marxist.android.domain.repository.CategoryRepository
import org.cpimtn.marxist.android.domain.repository.PostRepository
import org.cpimtn.marxist.android.domain.repository.SavedPostRepository
import org.cpimtn.marxist.android.domain.repository.SettingsRepository
import org.cpimtn.marxist.android.domain.repository.TagRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindPostRepository(impl: PostRepositoryImpl): PostRepository

    @Binds
    @Singleton
    abstract fun bindCategoryRepository(impl: CategoryRepositoryImpl): CategoryRepository

    @Binds
    @Singleton
    abstract fun bindTagRepository(impl: TagRepositoryImpl): TagRepository

    @Binds
    @Singleton
    abstract fun bindSavedPostRepository(impl: SavedPostRepositoryImpl): SavedPostRepository

    @Binds
    @Singleton
    abstract fun bindSettingsRepository(impl: UserSettingsRepositoryImpl): SettingsRepository
}
