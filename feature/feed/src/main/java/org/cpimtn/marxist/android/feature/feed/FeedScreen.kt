package org.cpimtn.marxist.android.feature.feed

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import org.cpimtn.marxist.android.domain.model.SyncStatus
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun FeedScreen(
    viewModel: FeedViewModel = hiltViewModel()
) {
    val uiState by viewModel.feedUiState.collectAsState()
    val syncStatus by viewModel.syncStatus.collectAsState()

    when (val state = uiState) {
        is FeedUiState.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        is FeedUiState.Success -> {
            Column(modifier = Modifier.fillMaxSize()) {
                SyncStatusBanner(syncStatus = syncStatus, onRetry = { viewModel.refresh() })
                Button(
                    onClick = { viewModel.refresh() },
                    modifier = Modifier.padding(8.dp)
                ) {
                    Text("Refresh")
                }
                LazyColumn(modifier = Modifier.padding(horizontal = 16.dp)) {
                    items(state.posts) { post ->
                        Text(
                            text = post.title,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                }
            }
        }
        is FeedUiState.Empty -> {
            Column(modifier = Modifier.fillMaxSize()) {
                SyncStatusBanner(syncStatus = syncStatus, onRetry = { viewModel.refresh() })
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "No posts yet. Sync when online.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Button(
                            onClick = { viewModel.refresh() },
                            modifier = Modifier.padding(top = 16.dp)
                        ) {
                            Text("Retry")
                        }
                    }
                }
            }
        }
        is FeedUiState.Error -> {
            Column(modifier = Modifier.fillMaxSize()) {
                SyncStatusBanner(syncStatus = syncStatus, onRetry = { viewModel.refresh() })
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = state.message,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.error
                        )
                        Button(
                            onClick = { viewModel.refresh() },
                            modifier = Modifier.padding(top = 16.dp)
                        ) {
                            Text("Retry")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SyncStatusBanner(
    syncStatus: SyncStatus,
    onRetry: () -> Unit,
) {
    val lastSynced = syncStatus.lastSyncedAtMillis
    val message = when {
        syncStatus.isSyncing -> "Syncing…"
        syncStatus.lastError != null -> "Sync failed: ${syncStatus.lastError}"
        lastSynced != null -> "Last synced: ${formatSyncTime(lastSynced)}"
        else -> null
    } ?: return

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = when {
            syncStatus.isSyncing -> MaterialTheme.colorScheme.surfaceVariant
            syncStatus.hasError -> MaterialTheme.colorScheme.errorContainer
            else -> MaterialTheme.colorScheme.surfaceVariant
        },
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = message,
                style = MaterialTheme.typography.bodySmall,
                color = when {
                    syncStatus.hasError -> MaterialTheme.colorScheme.onErrorContainer
                    else -> MaterialTheme.colorScheme.onSurfaceVariant
                },
            )
            if (syncStatus.hasError) {
                Button(onClick = onRetry) {
                    Text("Retry")
                }
            }
        }
    }
}

private fun formatSyncTime(millis: Long): String {
    val now = System.currentTimeMillis()
    val diffMinutes = (now - millis) / (60 * 1000)
    return when {
        diffMinutes < 1 -> "Just now"
        diffMinutes < 60 -> "${diffMinutes} min ago"
        else -> SimpleDateFormat("MMM d, h:mm a", Locale.getDefault()).format(Date(millis))
    }
}
