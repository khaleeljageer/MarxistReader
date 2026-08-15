package org.cpimtn.marxist.android.feature.books

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import org.cpimtn.marxist.android.domain.model.Book
import org.cpimtn.marxist.android.ui.theme.MarxistReaderTheme

@Composable
fun BooksContent(
    books: List<Book>,
    downloadStates: Map<String, BookDownloadUiState>,
    onDownloadClick: (Book) -> Unit,
    onDeleteClick: (Book) -> Unit,
    onOpenClick: (Book) -> Unit,
) {
    val bottomPadding = WindowInsets.navigationBars.asPaddingValues()

    // Held here rather than per card so the dialog survives the grid recycling its items.
    var pendingDeletion by remember { mutableStateOf<Book?>(null) }

    pendingDeletion?.let { book ->
        AlertDialog(
            onDismissRequest = { pendingDeletion = null },
            title = { Text(stringResource(R.string.books_delete_title)) },
            text = { Text(stringResource(R.string.books_delete_message, book.title)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDeleteClick(book)
                        pendingDeletion = null
                    },
                ) {
                    Text(stringResource(R.string.books_delete_confirm))
                }
            },
            dismissButton = {
                TextButton(onClick = { pendingDeletion = null }) {
                    Text(stringResource(R.string.books_delete_cancel))
                }
            },
        )
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = 16.dp,
            end = 16.dp,
            top = 12.dp,
            bottom = bottomPadding.calculateBottomPadding() + 16.dp,
        ),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items(items = books, key = { it.id }) { book ->
            BookCard(
                book = book,
                downloadState = downloadStates[book.id] ?: BookDownloadUiState.NotDownloaded,
                onDownloadClick = { onDownloadClick(book) },
                onDeleteClick = { pendingDeletion = book },
                onOpenClick = { onOpenClick(book) },
            )
        }
    }
}

@Composable
fun BookCard(
    book: Book,
    downloadState: BookDownloadUiState,
    onDownloadClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onOpenClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val ext = MarxistReaderTheme.colors

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerLow)
            .clickable(
                enabled = downloadState == BookDownloadUiState.Downloaded,
                onClick = onOpenClick,
            ),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(3f / 4f)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(ext.bookCoverGradientStart, ext.bookCoverGradientEnd),
                    ),
                ),
        ) {
            Text(
                text = book.year,
                style = MaterialTheme.typography.displayLarge.copy(fontWeight = FontWeight.Black),
                color = ext.bookYearText,
                maxLines = 1,
                overflow = TextOverflow.Clip,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(start = 6.dp),
            )
            Surface(
                shape = RoundedCornerShape(8.dp),
                shadowElevation = 4.dp,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
            ) {
                AsyncImage(
                    model = book.imageUrl,
                    contentDescription = book.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }

        // Only a downloaded book has anything to delete, so the row splits just for that state.
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            BookDownloadButton(
                state = downloadState,
                onClick = onDownloadClick,
                onOpen = onOpenClick,
                modifier = Modifier.weight(1f),
            )
            if (downloadState == BookDownloadUiState.Downloaded) {
                BookDeleteButton(onClick = onDeleteClick)
            }
        }
    }
}

@Composable
private fun BookDeleteButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val ext = MarxistReaderTheme.colors

    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(10.dp),
        color = ext.bookDownloadBg,
        modifier = modifier,
    ) {
        Icon(
            imageVector = Icons.Outlined.Delete,
            contentDescription = stringResource(R.string.books_delete),
            tint = ext.bookDownloadText,
            modifier = Modifier
                .padding(vertical = 8.dp, horizontal = 10.dp)
                .size(16.dp),
        )
    }
}

@Composable
private fun BookDownloadButton(
    state: BookDownloadUiState,
    onClick: () -> Unit,
    onOpen: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val ext = MarxistReaderTheme.colors

    Surface(
        // Once downloaded the button reads "open", so the whole surface has to open the book —
        // hanging it off the label alone leaves the icon and the padding around it dead.
        onClick = if (state == BookDownloadUiState.Downloaded) onOpen else onClick,
        enabled = state != BookDownloadUiState.Downloading,
        shape = RoundedCornerShape(10.dp),
        color = ext.bookDownloadBg,
        modifier = modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            when (state) {
                BookDownloadUiState.Downloading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.size(14.dp),
                        strokeWidth = 2.dp,
                        color = ext.bookDownloadText,
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = stringResource(R.string.books_downloading),
                        style = MaterialTheme.typography.labelMedium,
                        color = ext.bookDownloadText,
                    )
                }

                BookDownloadUiState.Downloaded -> {
                    Icon(
                        imageVector = Icons.Filled.CheckCircle,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = ext.bookDownloadText,
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = stringResource(R.string.books_downloaded),
                        style = MaterialTheme.typography.labelMedium,
                        color = ext.bookDownloadText,
                    )
                }

                BookDownloadUiState.Failed -> {
                    Icon(
                        imageVector = Icons.Outlined.Refresh,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = ext.bookDownloadText,
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = stringResource(R.string.books_retry),
                        style = MaterialTheme.typography.labelMedium,
                        color = ext.bookDownloadText,
                    )
                }

                BookDownloadUiState.NotDownloaded -> {
                    Icon(
                        imageVector = Icons.Outlined.Download,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = ext.bookDownloadText,
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = stringResource(R.string.books_download),
                        style = MaterialTheme.typography.labelMedium,
                        color = ext.bookDownloadText,
                    )
                }
            }
        }
    }
}

@Composable
fun BooksLoadingSkeleton() {
    val ext = MarxistReaderTheme.colors

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        userScrollEnabled = false,
    ) {
        items(count = 4) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surfaceContainerLow),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(3f / 4f)
                        .background(ext.shimmerBase),
                )
                Column(modifier = Modifier.padding(12.dp)) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(14.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(ext.shimmerBase),
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.6f)
                            .height(14.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(ext.shimmerBase),
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(32.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(ext.shimmerBase),
                    )
                }
            }
        }
    }
}
