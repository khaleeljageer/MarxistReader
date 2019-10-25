package com.marxist.android.model

import org.simpleframework.xml.Element
import org.simpleframework.xml.ElementList
import org.simpleframework.xml.Root

@Root(name = "channel", strict = false)
data class RssChannel(
    @field:Element
    var title: String? = null,

    @field:ElementList(name = "item", inline = true)
    var itemList: List<FeedItem>? = null
)