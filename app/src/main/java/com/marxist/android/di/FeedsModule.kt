package com.marxist.android.di

import com.marxist.android.ui.fragments.feeds.FeedsViewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val feedsModule = module {
    viewModel { FeedsViewModel(get(), get(), get()) }
}