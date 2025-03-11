package com.aptoide.android.aptoidegames.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import cm.aptoide.pt.extensions.runPreviewable
import cm.aptoide.pt.feature_flags.domain.FeatureFlags
import cm.aptoide.pt.install_manager.environment.NetworkConnection
import com.aptoide.android.aptoidegames.apkfy.presentation.InjectionsProvider
import com.aptoide.android.aptoidegames.network.presentation.rememberNetworkState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class InjectionsProvider @Inject constructor(
  val featureFlags: FeatureFlags,
) : ViewModel()

@Composable
fun rememberShouldShowVideos(bundleTag: String): Boolean = runPreviewable(
  preview = { Random.nextBoolean() },
  real = {
    val vm = hiltViewModel<InjectionsProvider>()

    val homeAnalytics = rememberHomeAnalytics()
    val networkState = rememberNetworkState()
    val unmeteredConnection by remember(networkState) {
      derivedStateOf { networkState == NetworkConnection.State.UNMETERED }
    }

    var ecVideoVariant: String? by remember { mutableStateOf(null) }
    var shouldShowECVideo: Boolean? by remember { mutableStateOf(null) }

    val showVideos by remember(shouldShowECVideo, unmeteredConnection) {
      derivedStateOf {
        bundleTag == "apps-group-editors-choice" && shouldShowECVideo == true && unmeteredConnection
      }
    }

    LaunchedEffect(Unit) {
      ecVideoVariant = vm.featureFlags.getFlagAsString("ab_test_ec_videos_jan_29")?.also {
        //Flag fetched but user the A/B test has not activated yet
        homeAnalytics.setECVideoFlagProperty("n-a")
      }
      shouldShowECVideo = when (vm.featureFlags.getFlagAsString("ab_test_ec_videos_jan_29")) {
        "group_a" -> false
        "group_b" -> true
        else -> null
      }
    }

    LaunchedEffect(shouldShowECVideo, unmeteredConnection) {
      if (bundleTag == "apps-group-editors-choice" && shouldShowECVideo != null && unmeteredConnection) {
        homeAnalytics.sendECVideosFlagReady()

        ecVideoVariant?.let {
          homeAnalytics.setECVideoFlagProperty(it)
        }
      }
    }

    showVideos
  }
)
