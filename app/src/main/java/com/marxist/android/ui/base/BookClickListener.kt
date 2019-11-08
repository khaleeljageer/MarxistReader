package com.marxist.android.ui.base

import com.marxist.android.database.entities.LocalBooks

interface BookClickListener {
    fun bookItemClickListener(adapterPosition: Int, book: LocalBooks)
}