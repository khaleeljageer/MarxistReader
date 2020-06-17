package com.marxist.android.di

import android.content.Context
import android.content.SharedPreferences
import com.marxist.android.database.AppDatabase
import com.marxist.android.utils.AppPreference
import org.koin.dsl.module

val roomModule = module {
    single { getRoom(get()) }
    single { getPreference(get()) }

}

fun getPreference(context: Context): SharedPreferences {
    return AppPreference.customPrefs(context)
}

fun getRoom(context: Context): AppDatabase {
    return AppDatabase.getAppDatabase(context)
}
