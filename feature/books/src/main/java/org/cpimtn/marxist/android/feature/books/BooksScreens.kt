package org.cpimtn.marxist.android.feature.books

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
) {
    val bottomPadding = WindowInsets.navigationBars.asPaddingValues()

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
            )
        }
    }
}

@Composable
fun BookCard(
    book: Book,
    downloadState: BookDownloadUiState,
    onDownloadClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val ext = MarxistReaderTheme.colors

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerLow),
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

        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = book.title,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                minLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = book.date,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(modifier = Modifier.height(10.dp))
            BookDownloadButton(
                state = downloadState,
                onClick = onDownloadClick,
            )
        }
    }
}

@Composable
private fun BookDownloadButton(
    state: BookDownloadUiState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val ext = MarxistReaderTheme.colors

    Surface(
        onClick = onClick,
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
