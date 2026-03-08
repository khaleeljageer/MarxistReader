package org.cpimtn.marxist.android.feature.search

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.outlined.CloudOff
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.SearchOff
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.NorthWest
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import org.cpimtn.marxist.android.domain.model.CategoryWithCount
import org.cpimtn.marxist.android.domain.model.TimelineMonth
import org.cpimtn.marxist.android.ui.theme.MarxistExtendedColors
import org.cpimtn.marxist.android.ui.theme.MarxistReaderTheme
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items


//@Composable
//fun SearchScreen(
//    modifier: Modifier = Modifier,
//    onArticleClick: (postId: Int) -> Unit = {},
//    onCategoryClick: (categoryId: Int) -> Unit = {},
//    viewModel: SearchViewModel = hiltViewModel(),
//) {
//    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
//    val ext = MarxistReaderTheme.colors
//    val keyboardController = LocalSoftwareKeyboardController.current
//    val focusManager = LocalFocusManager.current
//
//    // Query lives in the composable — ViewModel only gets notified on submit
//    var query by rememberSaveable { mutableStateOf("") }
//    // Focus is transient UI state — don't survive process death
//    var isFocused by remember { mutableStateOf(false) }
//
//    LazyColumn(
//        modifier = modifier
//            .fillMaxSize()
//            .padding(horizontal = 16.dp),
//    ) {
//        // ── Search Bar ──
//        item(key = "search_bar") {
//            Spacer(modifier = Modifier.height(12.dp))
//            SearchBar(
//                query = query,
//                onQueryChange = {
//                    query = it
//                    viewModel.onQueryChanged(it)
//                },
//                isFocused = isFocused,
//                onFocusChange = { isFocused = it },
//                onSubmit = {
//                    viewModel.onSearchSubmit(query)
//                    keyboardController?.hide()
//                    focusManager.clearFocus()
//                },
//                onClear = {
//                    query = ""
//                    viewModel.onQueryChanged("")
//                },
//                onBack = {
//                    query = ""
//                    viewModel.onQueryChanged("")
//                    keyboardController?.hide()
//                    focusManager.clearFocus()
//                },
//                colors = ext,
//            )
//        }
//
//        // ── Recent Searches ──
//        if (uiState.recentSearches.isNotEmpty() && query.isBlank()) {
//            item(key = "recent_header") {
//                Spacer(modifier = Modifier.height(24.dp))
//                SectionHeader(
//                    title = stringResource(R.string.search_recent_title),
//                    action = stringResource(R.string.search_recent_clear),
//                    onActionClick = viewModel::clearRecentSearches,
//                )
//                Spacer(modifier = Modifier.height(4.dp))
//            }
//
//            items(
//                items = uiState.recentSearches,
//                key = { "recent_$it" },
//            ) { term ->
//                RecentSearchItem(
//                    term = term,
//                    onClick = {
//                        query = term
//                        viewModel.onSearchSubmit(term)
//                        keyboardController?.hide()
//                        focusManager.clearFocus()
//                    },
//                    onFillClick = {
//                        // Fill search bar without submitting
//                        query = term
//                        viewModel.onQueryChanged(term)
//                    },
//                    colors = ext,
//                )
//            }
//        }
//
//        // ── Categories ──
//        if (uiState.categoriesWithCount.isNotEmpty() && query.isBlank()) {
//            item(key = "cat_header") {
//                Spacer(modifier = Modifier.height(24.dp))
//                SectionHeader(
//                    title = stringResource(R.string.search_categories_title),
//                )
//                Spacer(modifier = Modifier.height(12.dp))
//            }
//
//            item(key = "cat_grid") {
//                CategoryGrid(
//                    categories = uiState.categoriesWithCount,
//                    onCategoryClick = onCategoryClick,
//                    colors = ext,
//                )
//                Spacer(modifier = Modifier.height(8.dp))
//            }
//        }
//
//        // ── Timeline ──
//        if (uiState.timelineMonths.isNotEmpty() && query.isBlank()) {
//            item(key = "timeline_header") {
//                Spacer(modifier = Modifier.height(24.dp))
//                SectionHeader(
//                    title = stringResource(R.string.search_timeline_title),
//                )
//                Spacer(modifier = Modifier.height(12.dp))
//            }
//
//            item(key = "timeline_chips") {
//                TimelineRow(
//                    months = uiState.timelineMonths,
//                    selectedMonth = uiState.selectedMonth,
//                    onMonthClick = viewModel::onTimelineMonthClick,
//                    colors = ext,
//                )
//                Spacer(modifier = Modifier.height(32.dp))
//            }
//        }
//
//        // ── Search Results (when query is active) ──
//        // TODO: Add search results section when SearchViewModel
//        //       supports FTS query results
//    }
//}



@Composable
fun SearchDiscovery(
    state: SearchUiState.Discovery,
    query: String,
    onQueryChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onClearSearch: () -> Unit,
    onRecentSearchClick: (String) -> Unit,
    onClearRecentSearches: () -> Unit,
    onTimelineMonthClick: (String) -> Unit,
    onCategoryClick: (Int) -> Unit,
    onArticleClick: (postId: Int) -> Unit,
) {
    var isFocused by androidx.compose.runtime.mutableStateOf(false)
    val ext = MarxistReaderTheme.colors

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
    ) {
        item(key = "search_bar") {
            Spacer(modifier = Modifier.height(12.dp))
            SearchBar(
                query = query,
                onQueryChange = onQueryChange,
                isFocused = isFocused,
                onFocusChange = { isFocused = it },
                onSubmit = onSubmit,
                onClear = { onQueryChange("") },
                onBack = onClearSearch,
                colors = ext,
            )
        }

        if (state.recentSearches.isNotEmpty() && query.isBlank()) {
            item(key = "recent_header") {
                Spacer(modifier = Modifier.height(24.dp))
                SectionHeader(
                    title = stringResource(R.string.search_recent_title),
                    action = stringResource(R.string.search_recent_clear),
                    onActionClick = onClearRecentSearches,
                )
                Spacer(modifier = Modifier.height(4.dp))
            }
            items(
                items = state.recentSearches,
                key = { "recent_$it" },
            ) { term ->
                RecentSearchItem(
                    term = term,
                    onClick = { onRecentSearchClick(term) },
                    onFillClick = { onQueryChange(term) },
                    colors = ext,
                )
            }
        }

        if (state.categories.isNotEmpty() && query.isBlank()) {
            item(key = "cat_header") {
                Spacer(modifier = Modifier.height(24.dp))
                SectionHeader(
                    title = stringResource(R.string.search_categories_title),
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
            item(key = "cat_grid") {
                CategoryGridUi(
                    categories = state.categories,
                    onCategoryClick = onCategoryClick,
                    colors = ext,
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        if (state.timelineMonths.isNotEmpty() && query.isBlank()) {
            item(key = "timeline_header") {
                Spacer(modifier = Modifier.height(24.dp))
                SectionHeader(
                    title = stringResource(R.string.search_timeline_title),
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
            item(key = "timeline_chips") {
                TimelineRowUi(
                    months = state.timelineMonths,
                    selectedMonth = state.selectedMonth,
                    onMonthClick = onTimelineMonthClick,
                    colors = ext,
                )
                Spacer(modifier = Modifier.height(32.dp))
            }
        }

        if (state.suggestions.isNotEmpty()) {
            item(key = "suggestions_header") {
                Spacer(modifier = Modifier.height(16.dp))
                SectionHeader(title = stringResource(R.string.search_hint))
                Spacer(modifier = Modifier.height(8.dp))
            }
            items(
                items = state.suggestions,
                key = { "sug_${it.postId}" },
            ) { item ->
                SearchResultRow(
                    item = item,
                    isSaved = false,
                    onArticleClick = { onArticleClick(item.postId) },
                    modifier = Modifier.padding(vertical = 6.dp),
                )
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
    onClear: () -> Unit,
    onBack: () -> Unit,
    colors: MarxistExtendedColors,
) {
    val focusRequester = remember { FocusRequester() }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Back arrow — visible when focused
        AnimatedVisibility(
            visible = isFocused,
            enter = fadeIn(),
            exit = fadeOut(),
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.size(40.dp),
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        // Search field
        Row(
            modifier = Modifier
                .weight(1f)
                .focusRequester(focusRequester)
                .onFocusChanged { onFocusChange(it.isFocused) }
                .background(
                    color = colors.searchFieldBg,
                    shape = RoundedCornerShape(12.dp),
                )
                .border(
                    width = 1.dp,
                    color = if (isFocused) colors.searchFieldBorderFocused
                    else colors.searchFieldBorder,
                    shape = RoundedCornerShape(12.dp),
                )
                .padding(start = 14.dp, end = 6.dp, top = 4.dp, bottom = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = Icons.Rounded.Search,
                contentDescription = null,
                tint = if (isFocused) MaterialTheme.colorScheme.primary
                else colors.searchPlaceholder,
                modifier = Modifier.size(22.dp),
            )
            Spacer(modifier = Modifier.width(8.dp))

            BasicTextField(
                value = query,
                onValueChange = onQueryChange,
                textStyle = MaterialTheme.typography.bodyLarge.copy(
                    color = MaterialTheme.colorScheme.onSurface,
                ),
                cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(
                    onSearch = { onSubmit() },
                ),
                modifier = Modifier.weight(1f),
                decorationBox = { innerTextField ->
                    Box(
                        modifier = Modifier.padding(vertical = 10.dp),
                    ) {
                        if (query.isEmpty()) {
                            Text(
                                text = stringResource(R.string.search_placeholder),
                                style = MaterialTheme.typography.bodyLarge,
                                color = colors.searchPlaceholder,
                            )
                        }
                        innerTextField()
                    }
                },
            )

            // Clear button — visible when query is not empty
            AnimatedVisibility(
                visible = query.isNotEmpty(),
                enter = fadeIn(),
                exit = fadeOut(),
            ) {
                IconButton(
                    onClick = onClear,
                    modifier = Modifier.size(36.dp),
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Close,
                        contentDescription = stringResource(R.string.search_clear_content_desc),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp),
                    )
                }
            }
        }
    }
}

/** Search bar with back button always visible (e.g. on results screen). */
@Composable
fun SearchBarWithBack(
    query: String,
    onBack: () -> Unit,
    onQueryChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onClear: () -> Unit,
    colors: MarxistExtendedColors,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = onBack, modifier = Modifier.size(40.dp)) {
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Row(
            modifier = Modifier
                .weight(1f)
                .background(
                    color = colors.searchFieldBg,
                    shape = RoundedCornerShape(12.dp),
                )
                .border(
                    width = 1.dp,
                    color = colors.searchFieldBorder,
                    shape = RoundedCornerShape(12.dp),
                )
                .padding(start = 14.dp, end = 6.dp, top = 4.dp, bottom = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = Icons.Rounded.Search,
                contentDescription = null,
                tint = colors.searchPlaceholder,
                modifier = Modifier.size(22.dp),
            )
            Spacer(modifier = Modifier.width(8.dp))
            BasicTextField(
                value = query,
                onValueChange = onQueryChange,
                textStyle = MaterialTheme.typography.bodyLarge.copy(
                    color = MaterialTheme.colorScheme.onSurface,
                ),
                cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = { onSubmit() }),
                modifier = Modifier.weight(1f),
                decorationBox = { innerTextField ->
                    Box(modifier = Modifier.padding(vertical = 10.dp)) {
                        if (query.isEmpty()) {
                            Text(
                                text = stringResource(R.string.search_placeholder),
                                style = MaterialTheme.typography.bodyLarge,
                                color = colors.searchPlaceholder,
                            )
                        }
                        innerTextField()
                    }
                },
            )
            AnimatedVisibility(
                visible = query.isNotEmpty(),
                enter = fadeIn(),
                exit = fadeOut(),
            ) {
                IconButton(onClick = onClear, modifier = Modifier.size(36.dp)) {
                    Icon(
                        imageVector = Icons.Rounded.Close,
                        contentDescription = stringResource(R.string.search_clear_content_desc),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp),
                    )
                }
            }
        }
    }
}

@Composable
fun SearchResultRow(
    item: SearchItemUi,
    isSaved: Boolean,
    onArticleClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val ext = MarxistReaderTheme.colors
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onArticleClick)
            .padding(horizontal = 0.dp, vertical = 12.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (item.categoryLabel.isNotEmpty()) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = ext.categoryBadgeBg,
                ) {
                    Text(
                        text = item.categoryLabel,
                        style = MaterialTheme.typography.labelMedium,
                        color = ext.categoryBadgeText,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp),
                    )
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Outlined.Schedule,
                    contentDescription = null,
                    modifier = Modifier.size(13.dp),
                    tint = ext.articleTimestamp,
                )
                Spacer(modifier = Modifier.width(3.dp))
                Text(
                    text = item.formattedDate,
                    style = MaterialTheme.typography.labelSmall,
                    color = ext.articleTimestamp,
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = item.title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis,
        )
        if (item.excerpt.isNotBlank()) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = item.excerpt,
                style = MaterialTheme.typography.bodySmall,
                color = ext.articleExcerpt,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (item.tagLabels.isNotEmpty()) {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.weight(1f, fill = false),
                ) {
                    items(items = item.tagLabels.take(3), key = { it }) { tagName ->
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = ext.tagChipBg,
                            border = BorderStroke(1.dp, ext.tagChipBorder),
                        ) {
                            Text(
                                text = tagName,
                                style = MaterialTheme.typography.labelSmall,
                                color = ext.tagChipText,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                            )
                        }
                    }
                }
            } else {
                Spacer(modifier = Modifier.weight(1f))
            }
            Text(
                text = item.readTime,
                style = MaterialTheme.typography.labelSmall,
                color = ext.articleTimestamp,
            )
        }
    }
}

@Composable
private fun CategoryGridUi(
    categories: List<CategoryUi>,
    onCategoryClick: (Int) -> Unit,
    colors: MarxistExtendedColors,
) {
    val columns = remember(categories) { categories.chunked(3) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        for (columnItems in columns) {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.width(152.dp),
            ) {
                for (cat in columnItems) {
                    CategoryUiCard(
                        category = cat,
                        onClick = { onCategoryClick(cat.id) },
                        colors = colors,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
        }
    }
}

@Composable
private fun CategoryUiCard(
    category: CategoryUi,
    onClick: () -> Unit,
    colors: MarxistExtendedColors,
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
            .clip(shape = RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(12.dp),
    ) {
        Text(
            modifier = Modifier.basicMarquee(),
            text = category.name,
            style = MaterialTheme.typography.titleSmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = stringResource(R.string.search_articles_count, category.articleCount),
            style = MaterialTheme.typography.bodySmall,
            color = colors.searchSuggestionText,
        )
    }
}

@Composable
private fun TimelineRowUi(
    months: List<TimelineMonthUi>,
    selectedMonth: String?,
    onMonthClick: (String) -> Unit,
    colors: MarxistExtendedColors,
) {
    val columns = remember(months) { months.chunked(3) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        for (columnItems in columns) {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                for (month in columnItems) {
                    TimelineChip(
                        label = month.label,
                        selected = month.label == selectedMonth,
                        onClick = { onMonthClick(month.label) },
                        colors = colors,
                    )
                }
            }
        }
    }
}

@Composable
private fun SectionHeader(
    title: String,
    action: String? = null,
    onActionClick: (() -> Unit)? = null,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            color = MarxistReaderTheme.colors.sectionHeader,
        )
        if (action != null && onActionClick != null) {
            TextButton(onClick = onActionClick) {
                Text(
                    text = action,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        }
    }
}

@Composable
private fun RecentSearchItem(
    term: String,
    onClick: () -> Unit,
    onFillClick: () -> Unit,
    colors: MarxistExtendedColors,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = Icons.Outlined.Schedule,
            contentDescription = null,
            tint = colors.searchSuggestionIcon,
            modifier = Modifier.size(18.dp),
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = term,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f),
        )
        // NorthWest arrow — "fill into search bar" affordance
        IconButton(
            onClick = onFillClick,
            modifier = Modifier.size(36.dp),
        ) {
            Icon(
                imageVector = Icons.Rounded.NorthWest,
                contentDescription = null,
                tint = colors.searchSuggestionIcon,
                modifier = Modifier.size(16.dp),
            )
        }
    }
}

@Composable
private fun CategoryGrid(
    categories: List<CategoryWithCount>,
    onCategoryClick: (categoryId: Int) -> Unit,
    colors: MarxistExtendedColors,
) {
    val columns = remember(categories) { categories.chunked(3) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        for (columnItems in columns) {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.width(152.dp),
            ) {
                for (item in columnItems) {
                    CategoryCard(
                        categoryWithCount = item,
                        onClick = { onCategoryClick(item.category.id) },
                        colors = colors,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
        }
    }
}

@Composable
private fun CategoryCard(
    categoryWithCount: CategoryWithCount,
    onClick: () -> Unit,
    colors: MarxistExtendedColors,
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
            .clip(shape = RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
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
            text = stringResource(
                R.string.search_articles_count,
                categoryWithCount.postCount,
            ),
            style = MaterialTheme.typography.bodySmall,
            color = colors.searchSuggestionText,
        )
    }
}

@Composable
private fun TimelineRow(
    months: List<TimelineMonth>,
    selectedMonth: String?,
    onMonthClick: (String) -> Unit,
    colors: MarxistExtendedColors,
) {
    val columns = remember(months) { months.chunked(3) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        for (columnItems in columns) {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier,
            ) {
                for (month in columnItems) {
                    TimelineChip(
                        label = month.label,
                        selected = month.label == selectedMonth,
                        onClick = { onMonthClick(month.label) },
                        colors = colors,
                    )
                }
            }
        }
    }
}

@Composable
private fun TimelineChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    colors: MarxistExtendedColors,
) {
    val borderColor = if (selected) {
        colors.searchFieldBorderFocused
    } else {
        colors.searchFieldBorder
    }
    val bgColor = if (selected) {
        colors.searchFieldBorderFocused.copy(alpha = 0.12f)
    } else {
        colors.searchFieldBg
    }

    Text(
        text = label,
        style = MaterialTheme.typography.labelLarge,
        color = if (selected) MaterialTheme.colorScheme.primary
        else MaterialTheme.colorScheme.onSurface,
        modifier = Modifier
            .fillMaxWidth()
            .background(bgColor, RoundedCornerShape(20.dp))
            .border(1.dp, borderColor, RoundedCornerShape(20.dp))
            .clip(RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 10.dp),
    )
}

@Composable
fun SearchLoadingSkeleton(
    modifier: Modifier = Modifier,
) {
    val shimmer = MarxistReaderTheme.colors.shimmerBase

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        // ── Search Bar Skeleton ──
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(shimmer),
        )

        Spacer(modifier = Modifier.height(24.dp))

        // ── Recent Searches Header ──
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Box(
                modifier = Modifier
                    .width(120.dp)
                    .height(14.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(shimmer),
            )
            Box(
                modifier = Modifier
                    .width(40.dp)
                    .height(14.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(shimmer),
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // ── Recent Search Items (3) ──
        repeat(3) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp),
            ) {
                Box(
                    modifier = Modifier
                        .width(18.dp)
                        .height(18.dp)
                        .clip(RoundedCornerShape(9.dp))
                        .background(shimmer),
                )
                Spacer(modifier = Modifier.width(12.dp))
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(14.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(shimmer),
                )
                Spacer(modifier = Modifier.width(12.dp))
                Box(
                    modifier = Modifier
                        .width(16.dp)
                        .height(16.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(shimmer),
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ── Categories Header ──
        Box(
            modifier = Modifier
                .width(100.dp)
                .height(14.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(shimmer),
        )

        Spacer(modifier = Modifier.height(12.dp))

        // ── Category Grid (2 columns × 3 rows) ──
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            repeat(2) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f),
                ) {
                    repeat(3) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(shimmer),
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ── Timeline Header ──
        Box(
            modifier = Modifier
                .width(80.dp)
                .height(14.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(shimmer),
        )

        Spacer(modifier = Modifier.height(12.dp))

        // ── Timeline Chips ──
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            repeat(4) {
                Box(
                    modifier = Modifier
                        .width(80.dp)
                        .height(36.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(shimmer),
                )
            }
        }
    }
}

@Composable
fun NoResultsMessage(
    query: String,
    modifier: Modifier = Modifier,
) {
    val ext = MarxistReaderTheme.colors

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = ext.settingsIconBg,
                modifier = Modifier.size(64.dp),
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Outlined.SearchOff,
                        contentDescription = null,
                        modifier = Modifier.size(28.dp),
                        tint = ext.settingsIconTint,
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(R.string.search_no_results),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = stringResource(R.string.search_no_results_for, query),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = stringResource(R.string.search_no_results_hint),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
fun SearchErrorMessage(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.errorContainer,
                modifier = Modifier.size(64.dp),
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Outlined.CloudOff,
                        contentDescription = null,
                        modifier = Modifier.size(28.dp),
                        tint = MaterialTheme.colorScheme.onErrorContainer,
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(R.string.search_error_title),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = message,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(20.dp))

            Surface(
                onClick = onRetry,
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.primary,
            ) {
                Text(
                    text = stringResource(R.string.search_retry),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.padding(
                        horizontal = 24.dp,
                        vertical = 10.dp,
                    ),
                )
            }
        }
    }
}
