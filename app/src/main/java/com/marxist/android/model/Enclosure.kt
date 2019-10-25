package com.marxist.android.model

import org.simpleframework.xml.Attribute
import org.simpleframework.xml.Root

@Root(name = "enclosure", strict = false)
data class Enclosure(
    @field:Attribute(name = "url")
    var audioUrl: String? = null,
    @field:Attribute(name = "type")
    var type: String? = null
)