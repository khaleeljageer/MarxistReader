package com.marxist.android.ui.base

import com.marxist.android.model.Quote

interface QuoteClickListener {
    fun quoteClickListener(
        quote: Quote, type: Int
    )
}