package org.cpimtn.marxist.android.feature.more

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun MoreScreen(
    viewModel: MoreViewModel = hiltViewModel()
) {
    Text("More Screen")
}