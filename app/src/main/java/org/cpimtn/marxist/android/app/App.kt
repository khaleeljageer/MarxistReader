package org.cpimtn.marxist.android.app

import android.content.Context
import android.content.Intent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.jskaleel.epub.reader.ReaderActivityContract
import org.cpimtn.marxist.android.domain.model.HelpTopic
import org.cpimtn.marxist.android.feature.feeddetails.ArticleDetailRoute
import org.cpimtn.marxist.android.feature.welcome.WelcomeScreen
import org.cpimtn.marxist.android.ui.common.help.HelpBottomSheet
import org.cpimtn.marxist.navigation.MainAppState
import org.cpimtn.marxist.navigation.MainScreen
import org.cpimtn.marxist.navigation.Route
import org.cpimtn.marxist.navigation.Screen
import org.cpimtn.marxist.navigation.TopLevelDestination

/**
 * Root composable: root decides welcome vs main from DataStore. Welcome shown only once.
 */
@Composable
fun App(
    windowSizeClass: WindowSizeClass,
    darkTheme: Boolean,
    startTab: String? = null,
    onStartTabHandled: () -> Unit = {},
) {
    val navController = rememberNavController()
    val context = LocalContext.current

    NavHost(
        navController = navController,
        startDestination = Route.Root.name,
        enterTransition = { fadeIn(animationSpec = tween(300)) },
        exitTransition = { fadeOut(animationSpec = tween(300)) },
    ) {
        composable(Route.Root.name) {
            val rootViewModel: RootViewModel = hiltViewModel()
            val destination by rootViewModel.destination.collectAsState(initial = null)

            LaunchedEffect(destination) {
                destination?.let { route ->
                    navController.navigate(route) {
                        popUpTo(Route.Root.name) { inclusive = true }
                    }
                }
            }
        }

        composable(Route.Welcome.name) {
            WelcomeScreen(
                onContinueClicked = {
                    navController.navigate(Route.Main.name) {
                        popUpTo(Route.Welcome.name) { inclusive = true }
                    }
                }
            )
        }

        composable(Route.Main.name) {
            val openBookViewModel: OpenBookViewModel = hiltViewModel()
            LaunchedEffect(Unit) {
                openBookViewModel.readerReady.collect { readerId ->
                    context.launchReaderActivity(readerId)
                }
            }

            MainScreen(
                windowSizeClass = windowSizeClass,
                darkTheme = darkTheme
            ) { appState, modifier ->
                // A notification tap can request a tab (e.g. "new book" → Books). appState.navController
                // is only available inside this slot, so select the tab here, then clear the request.
                LaunchedEffect(startTab) {
                    when (startTab) {
                        Screen.Books.route -> {
                            appState.navigateToTopLevelDestination(TopLevelDestination.BOOKS)
                        }
                    }
                    if (startTab != null) onStartTabHandled()
                }
                MainScreensNavHost(
                    appState = appState,
                    modifier = modifier,
                    goToArticleDetails = {
                        navController.navigate(Route.ArticleDetail.createRoute(it)) {
                            launchSingleTop = true
                        }
                    },
                    openBook = { bookId ->
                        openBookViewModel.openBook(bookId)
                    }
                )

                HelpHost(appState = appState)
            }
        }

        composable(
            route = Route.ArticleDetail.name,
            arguments = Route.ArticleDetail.arguments,
        ) {
            ArticleDetailRoute(
                onBackClick = { navController.popBackStack() },
            )
        }
    }
}

/**
 * Hosts contextual help for the main tab screens. Auto-shows the current tab's help on first visit
 * and registers [MainAppState.helpCallback] so the top bar / search bar help icon can reopen it.
 */
@Composable
private fun HelpHost(appState: MainAppState) {
    val helpViewModel: HelpViewModel = hiltViewModel()
    val topic = appState.currentTopLevelDestination?.toHelpTopic()

    LaunchedEffect(topic) {
        topic?.let(helpViewModel::onScreenShown)
    }
    SideEffect {
        appState.helpCallback = topic?.let { t -> { helpViewModel.onHelpClicked(t) } }
    }

    val visibleTopic by helpViewModel.visibleTopic.collectAsState()
    visibleTopic?.let { activeTopic ->
        HelpBottomSheet(
            topic = activeTopic,
            onDismiss = helpViewModel::dismiss,
        )
    }
}

private fun TopLevelDestination.toHelpTopic(): HelpTopic = when (this) {
    TopLevelDestination.FEED -> HelpTopic.FEED
    TopLevelDestination.BOOKS -> HelpTopic.BOOKS
    TopLevelDestination.SEARCH -> HelpTopic.SEARCH
    TopLevelDestination.SAVED -> HelpTopic.SAVED
    TopLevelDestination.SETTINGS -> HelpTopic.SETTINGS
}

fun Context.launchReaderActivity(readerId: Long) {
    val intent = ReaderActivityContract().createIntent(
        context = this,
        input = ReaderActivityContract.Arguments(
            bookId = readerId
        )
    )
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    this.startActivity(intent)
}