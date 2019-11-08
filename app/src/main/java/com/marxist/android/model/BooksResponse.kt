package com.marxist.android.model

import com.marxist.android.database.entities.LocalBooks

data class BooksResponse(val books: MutableList<LocalBooks>)