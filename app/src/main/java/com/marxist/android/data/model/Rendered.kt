package com.marxist.android.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Rendered(
    val rendered: String
) : Parcelable
