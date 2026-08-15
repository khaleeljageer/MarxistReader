package org.cpimtn.marxist.android.app

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.util.Log
import androidx.core.net.toUri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.play.core.ktx.launchReview
import com.google.android.play.core.ktx.requestReview
import com.google.android.play.core.review.ReviewManagerFactory

/**
 * Raises Play's in-app review prompt once the user has read enough articles.
 *
 * Play decides on its own whether the dialog is actually shown (it is heavily quota-limited) and
 * never reports whether a review was left, so the prompt is marked as offered as soon as the flow
 * has been attempted. A failure — no Play Services, a sideloaded build — counts as an attempt too,
 * otherwise every launch would retry a call that can never succeed on that device.
 */
@Composable
fun ReviewHost() {
    val viewModel: ReviewViewModel = hiltViewModel()
    val shouldPrompt by viewModel.shouldPrompt.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(shouldPrompt) {
        if (!shouldPrompt) return@LaunchedEffect
        val activity = context.findActivity() ?: return@LaunchedEffect
        try {
            val manager = ReviewManagerFactory.create(context)
            manager.launchReview(activity, manager.requestReview())
        } catch (e: Exception) {
            Log.w("ReviewHost", "In-app review flow unavailable", e)
        } finally {
            viewModel.onPromptAttempted()
        }
    }
}

/**
 * Opens the app's Play Store listing. Used by the Settings entry rather than the in-app review
 * flow, which silently does nothing once Play's quota is exhausted — a tap the user made
 * deliberately has to visibly go somewhere.
 */
fun Context.openPlayStoreListing() {
    val marketIntent = Intent(Intent.ACTION_VIEW, "market://details?id=$packageName".toUri())
        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    try {
        startActivity(marketIntent)
    } catch (_: ActivityNotFoundException) {
        startActivity(
            Intent(
                Intent.ACTION_VIEW,
                "https://play.google.com/store/apps/details?id=$packageName".toUri(),
            ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
        )
    }
}

private fun Context.findActivity(): Activity? {
    var context = this
    while (context is ContextWrapper) {
        if (context is Activity) return context
        context = context.baseContext
    }
    return null
}
