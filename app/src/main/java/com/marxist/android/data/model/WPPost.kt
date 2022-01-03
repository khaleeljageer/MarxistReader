package com.marxist.android.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class WPPost(
    val id: Int,
    val title: Rendered,
    val content: Rendered,
    val link: String,
    val date: String,
    var audioUrl: String = ""
) : Parcelable
