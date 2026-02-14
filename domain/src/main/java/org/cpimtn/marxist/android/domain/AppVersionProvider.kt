package org.cpimtn.marxist.android.domain

/**
 * Provides the application version string (e.g. from BuildConfig).
 * Implemented by the app module; feature modules use this to avoid depending on app.
 */
interface AppVersionProvider {
    fun getVersion(): String
}
