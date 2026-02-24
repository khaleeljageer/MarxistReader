package org.cpimtn.marxist.android.feature.search

import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import org.cpimtn.marxist.android.domain.model.CategoryWithCount
import org.cpimtn.marxist.android.ui.theme.MarxistReaderTheme

@Composable
fun SearchScreen(
    viewModel: SearchViewModel = hiltViewModel(),
    modifier: Modifier = Modifier,
) {
    var query by rememberSaveable { mutableStateOf("") }
    var isFocused by rememberSaveable { mutableStateOf(false) }
    val colors = MarxistReaderTheme.colors
    val recentSearches by viewModel.recentSearches.collectAsState()
    val categoriesWithCount by viewModel.categoriesWithCount.collectAsState()
    val timelineMonths by viewModel.timelineMonths.collectAsState()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
    ) {
        item {
            Spacer(modifier = Modifier.height(24.dp))
            SearchBar(
                query = query,
                onQueryChange = { query = it },
                isFocused = isFocused,
                onFocusChange = { isFocused = it },
                onSubmit = { viewModel.onSearchSubmit(query) },
                placeholderRes = R.string.search_placeholder,
                colors = colors,
            )
        }

        item {
            if (recentSearches.isNotEmpty()) {
                Spacer(modifier = Modifier.height(24.dp))
                RecentSearchesSection(
                    recentSearches = recentSearches,
                    onItemClick = viewModel::onRecentSearchClick,
                    onClearClick = viewModel::clearRecentSearches,
                    colors = colors,
                )
            }
        }

        item {
            if (categoriesWithCount.isNotEmpty()) {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = stringResource(R.string.search_categories_title),
                    style = MaterialTheme.typography.titleMedium,
                    color = colors.sectionHeader,
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
        }

        if (categoriesWithCount.isNotEmpty()) {
            item {
                val columns = categoriesWithCount.chunked(3)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState(0)),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    for (columnItems in columns) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.width(160.dp),
                        ) {
                            for (item in columnItems) {
                                CategoryCard(
                                    categoryWithCount = item,
                                    colors = colors,
                                    modifier = Modifier.fillMaxWidth(),
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        item {
            if (timelineMonths.isNotEmpty()) {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = stringResource(R.string.search_timeline_title),
                    style = MaterialTheme.typography.titleMedium,
                    color = colors.sectionHeader,
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
        }

        if (timelineMonths.isNotEmpty()) {
            item {
                val columns = timelineMonths.chunked(2)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState(0)),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    for (columnItems in columns) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier,
                        ) {
                            for (item in columnItems) {
                                TimelineChip(
                                    label = item.label,
                                    selected = false,
                                    onClick = { },
                                    colors = colors,
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}


@Composable
private fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    isFocused: Boolean,
    onFocusChange: (Boolean) -> Unit,
    onSubmit: () -> Unit,
    placeholderRes: Int,
    colors: org.cpimtn.marxist.android.ui.theme.MarxistExtendedColors,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .onFocusChanged { onFocusChange(it.isFocused) }
            .background(
                color = colors.searchFieldBg,
                shape = RoundedCornerShape(12.dp),
            )
            .border(
                width = 1.dp,
                color = if (isFocused) colors.searchFieldBorderFocused else colors.searchFieldBorder,
                shape = RoundedCornerShape(12.dp),
            )
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = Icons.Rounded.Search,
            contentDescription = null,
            tint = colors.searchPlaceholder,
            modifier = Modifier.size(24.dp),
        )
        Spacer(modifier = Modifier.width(12.dp))
        BasicTextField(
            value = query,
            onValueChange = onQueryChange,
            textStyle = MaterialTheme.typography.bodyLarge.copy(
                color = MaterialTheme.colorScheme.onSurface,
            ),
            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
            singleLine = true,
            modifier = Modifier.weight(1f),
            decorationBox = { innerTextField ->
                Box {
                    if (query.isEmpty()) {
                        Text(
                            text = stringResource(placeholderRes),
                            style = MaterialTheme.typography.bodyLarge,
                            color = colors.searchPlaceholder,
                        )
                    }
                    innerTextField()
                }
            },
        )
    }
}

@Composable
private fun RecentSearchesSection(
    recentSearches: List<String>,
    onItemClick: (String) -> Unit,
    onClearClick: () -> Unit,
    colors: org.cpimtn.marxist.android.ui.theme.MarxistExtendedColors,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(R.string.search_recent_title),
                style = MaterialTheme.typography.titleMedium,
                color = colors.sectionHeader,
            )
            TextButton(onClick = onClearClick) {
                Text(
                    text = stringResource(R.string.search_recent_clear),
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        recentSearches.forEach { term ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onItemClick(term) }
                    .padding(vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = Icons.Outlined.Schedule,
                    contentDescription = stringResource(R.string.search_recent_content_desc),
                    tint = colors.searchSuggestionIcon,
                    modifier = Modifier.size(20.dp),
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = term,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f),
                )
                Icon(
                    imageVector = Icons.Rounded.KeyboardArrowUp,
                    contentDescription = null,
                    tint = colors.searchSuggestionIcon,
                    modifier = Modifier.size(20.dp),
                )
            }
        }
    }
}

@Composable
private fun CategoryCard(
    categoryWithCount: CategoryWithCount,
    colors: org.cpimtn.marxist.android.ui.theme.MarxistExtendedColors,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = colors.searchFieldBg,
                shape = RoundedCornerShape(12.dp),
            )
            .border(
                width = 1.dp,
                color = colors.searchFieldBorder,
                shape = RoundedCornerShape(12.dp),
            )
            .padding(12.dp),
    ) {
        Text(
            modifier = Modifier.basicMarquee(),
            text = categoryWithCount.category.name,
            style = MaterialTheme.typography.titleSmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = stringResource(R.string.search_articles_count, categoryWithCount.postCount),
            style = MaterialTheme.typography.bodySmall,
            color = colors.searchSuggestionText,
        )
    }
}

@Composable
private fun TimelineChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    colors: org.cpimtn.marxist.android.ui.theme.MarxistExtendedColors,
) {
    val borderColor = if (selected) colors.searchFieldBorderFocused else colors.searchFieldBorder
    val bgColor =
        if (selected) colors.searchFieldBorderFocused.copy(alpha = 0.12f) else colors.searchFieldBg
    Text(
        text = label,
        style = MaterialTheme.typography.labelLarge,
        color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
        modifier = Modifier
            .background(bgColor, RoundedCornerShape(20.dp))
            .border(1.dp, borderColor, RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 10.dp),
    )
}
