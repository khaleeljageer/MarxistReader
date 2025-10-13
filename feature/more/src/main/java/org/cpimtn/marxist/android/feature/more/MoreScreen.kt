package org.cpimtn.marxist.android.feature.more

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun MoreScreen(
    navigateToAbout: () -> Unit,
    navigateToDonate: () -> Unit,
    navigateToThemeSettings: () -> Unit
) {
    Column {
        Text("More Screen")
        Button(onClick = navigateToAbout) {
            Text("Go to About")
        }
        Button(onClick = navigateToDonate) {
            Text("Go to Donate")
        }
        Button(onClick = navigateToThemeSettings) {
            Text("Go to Theme Settings")
        }
    }
}
