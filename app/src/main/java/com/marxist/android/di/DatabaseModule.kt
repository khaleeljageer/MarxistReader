package com.marxist.android.di

import android.content.Context
import com.marxist.android.database.AppDatabase
import org.koin.dsl.module

val roomModule = module {
    single { getRoom(get()) }
}

fun getRoom(context: Context): AppDatabase {
    return AppDatabase.getAppDatabase(context)
}
