package org.cpimtn.marxist.android.feature.more

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

@Composable
fun MoreScreen(
    viewModel: MoreViewModel = hiltViewModel()
) {
    Text(stringResource(R.string.more_screen_title))
}