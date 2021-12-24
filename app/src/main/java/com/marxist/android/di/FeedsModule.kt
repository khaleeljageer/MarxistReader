package com.marxist.android.di

import com.marxist.android.ui.activities.search.SearchViewModel
import com.marxist.android.ui.fragments.feeds.FeedsViewModel
import com.marxist.android.viewmodel.BookListViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val feedsModule = module {
    viewModel { FeedsViewModel(get(), get()) }
    viewModel { BookListViewModel(get(), get()) }
    viewModel { SearchViewModel(get()) }
}