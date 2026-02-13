package org.cpimtn.marxist.core.config

/**
 * Centralized app configuration. Network and sync layers use these constants.
 */
object AppConfig {

    object Network {
        const val BASE_URL = "https://marxist.cpimtn.org/wp-json/wp/v2/"
        const val CONNECT_TIMEOUT_SEC = 60L
        const val READ_TIMEOUT_SEC = 60L
        const val WRITE_TIMEOUT_SEC = 60L
    }

    object Sync {
        const val DEFAULT_PER_PAGE = 30
    }
}
