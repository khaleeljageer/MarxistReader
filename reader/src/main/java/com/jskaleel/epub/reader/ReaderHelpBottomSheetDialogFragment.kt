package com.jskaleel.epub.reader

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.TouchApp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jskaleel.epub.R
import com.jskaleel.epub.utils.compose.ComposeBottomSheetDialogFragment

/**
 * "How to use the reader" help, shown as a Compose bottom sheet themed with the reader's warm
 * palette (via [ComposeBottomSheetDialogFragment]'s AppTheme wrapper). Auto-shown once on the first
 * book open and re-openable from the reader toolbar's help icon.
 */
class ReaderHelpBottomSheetDialogFragment : ComposeBottomSheetDialogFragment(isScrollable = true) {

    @Composable
    override fun Content() {
        ReaderHelpContent()
    }
}

private data class ReaderHelpTip(
    val painter: Painter,
    val title: String,
    val description: String,
)

@Composable
private fun ReaderHelpContent() {
    val tips = listOf(
        ReaderHelpTip(
            painter = rememberVectorPainter(Icons.Outlined.TouchApp),
            title = stringResource(R.string.reader_help_pages_title),
            description = stringResource(R.string.reader_help_pages_desc),
        ),
        ReaderHelpTip(
            painter = painterResource(R.drawable.ic_outline_menu_24),
            title = stringResource(R.string.reader_help_toc_title),
            description = stringResource(R.string.reader_help_toc_desc),
        ),
        ReaderHelpTip(
            painter = painterResource(R.drawable.ic_baseline_bookmark_24),
            title = stringResource(R.string.reader_help_bookmark_title),
            description = stringResource(R.string.reader_help_bookmark_desc),
        ),
        ReaderHelpTip(
            painter = painterResource(R.drawable.ic_baseline_headphones_24),
            title = stringResource(R.string.reader_help_tts_title),
            description = stringResource(R.string.reader_help_tts_desc),
        ),
        ReaderHelpTip(
            painter = painterResource(R.drawable.ic_outline_format_size_24),
            title = stringResource(R.string.reader_help_settings_title),
            description = stringResource(R.string.reader_help_settings_desc),
        ),
        ReaderHelpTip(
            painter = painterResource(R.drawable.ic_baseline_search_24),
            title = stringResource(R.string.reader_help_search_title),
            description = stringResource(R.string.reader_help_search_desc),
        ),
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 24.dp)
            .padding(top = 20.dp, bottom = 24.dp),
    ) {
        Text(
            text = stringResource(R.string.reader_help_title),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Spacer(modifier = Modifier.height(20.dp))
        tips.forEachIndexed { index, tip ->
            if (index > 0) Spacer(modifier = Modifier.height(18.dp))
            ReaderHelpTipRow(tip)
        }
    }
}

@Composable
private fun ReaderHelpTipRow(tip: ReaderHelpTip) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        androidx.compose.material3.Surface(
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.primaryContainer,
            modifier = Modifier.size(44.dp),
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    painter = tip.painter,
                    contentDescription = null,
                    modifier = Modifier.size(22.dp),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            }
        }
        Column(modifier = Modifier.padding(top = 2.dp)) {
            Text(
                text = tip.title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = tip.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
