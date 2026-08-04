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
        const val DEFAULT_PER_PAGE = 50
    }

    /**
     * The public website every article originates from. Permalinks are
     * `https://marxist.cpimtn.org/<slug>/`, so [articleUrl] can rebuild the canonical source
     * link from a stored post slug without an extra API field.
     */
    object Site {
        const val BASE_URL = "https://marxist.cpimtn.org/"
        const val DISPLAY_NAME = "marxist.cpimtn.org"
        const val PRIVACY_POLICY_URL = "${BASE_URL}privacy-policy"
        const val TERMS_CONDITIONS_URL = "${BASE_URL}terms-conditions"

        const val CONTACT_URL = "${BASE_URL}contact/"

        fun articleUrl(slug: String): String = "$BASE_URL${slug.trim('/')}/"
    }

    object Books {
        const val CATALOG_URL =
            "https://raw.githubusercontent.com/tamilmarxist/MarxistTamilEbooks/refs/heads/master/booksdb.json"
    }
}
