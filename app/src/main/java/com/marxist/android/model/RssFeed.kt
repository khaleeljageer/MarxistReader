package com.marxist.android.model

import androidx.room.Dao
import org.simpleframework.xml.Attribute
import org.simpleframework.xml.Element
import org.simpleframework.xml.Root

@Root(name = "rss", strict = false)
data class RssFeed (
    @field:Attribute(name = "version")
    var version: String? = null,

    @field:Element(name = "channel")
    var channel: RssChannel? = null
)