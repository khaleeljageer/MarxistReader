package org.cpimtn.marxist.android.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import org.cpimtn.marxist.android.ui.theme.MarxistReaderTheme

/**
 * Single article row placeholder for loading skeletons.
 * Used by feed and saved screens.
 */
@Composable
fun ArticleSkeleton(
    modifier: Modifier = Modifier,
) {
    val ext = MarxistReaderTheme.colors
    Column(modifier = modifier) {
        Box(
            modifier = Modifier
                .width(80.dp)
                .height(20.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(ext.shimmerBase),
        )
        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(16.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(ext.shimmerBase),
        )
        Spacer(modifier = Modifier.height(6.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .height(16.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(ext.shimmerBase),
        )
        Spacer(modifier = Modifier.height(6.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .height(12.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(ext.shimmerBase),
        )
        Spacer(modifier = Modifier.height(10.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            repeat(2) {
                Box(
                    modifier = Modifier
                        .width(60.dp)
                        .height(18.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(ext.shimmerBase),
                )
            }
        }
    }
}
