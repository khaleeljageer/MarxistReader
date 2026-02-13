package org.cpimtn.marxist.android.feature.books

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun BooksScreen(
    viewModel: BooksViewModel = hiltViewModel()
) {
    Text("Books Screen")
}