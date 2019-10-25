package com.marxist.android.model

import org.simpleframework.xml.Element
import org.simpleframework.xml.Root

@Root(name = "item", strict = false)
data class FeedItem(
    @field:Element(name = "title", required = true)
    var title: String? = null,

    @field:Element(name = "link", required = true)
    var link: String? = null,

    @field:Element(name = "pubDate", required = true)
    var pubDate: String? = null,

    @field:Element(name = "description", required = true)
    var description: String? = null,

    @field:Element(name = "encoded", required = true)
    var content: String? = null,

    @field:Element(name = "enclosure", required = false)
    var enclosure: Enclosure? = null
)