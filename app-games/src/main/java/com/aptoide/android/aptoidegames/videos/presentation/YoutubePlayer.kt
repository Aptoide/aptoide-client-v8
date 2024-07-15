package com.aptoide.android.aptoidegames.videos.presentation

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import cm.aptoide.pt.extensions.isActiveNetworkMetered
import cm.aptoide.pt.extensions.runPreviewable
import com.aptoide.android.aptoidegames.R
import com.aptoide.android.aptoidegames.drawables.icons.getLeftArrow
import com.aptoide.android.aptoidegames.drawables.icons.getMuted
import com.aptoide.android.aptoidegames.drawables.icons.getUnmuted
import com.aptoide.android.aptoidegames.theme.Palette
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerCallback
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.options.IFramePlayerOptions
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.utils.YouTubePlayerTracker
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView

@Composable
fun AppViewYoutubePlayer(
  modifier: Modifier,
  videoId: String,
  shouldPause: Boolean = false,
  contentDesc: String = "",
) = runPreviewable(
  preview = {
    Box(
      modifier = modifier.background(Color.Gray),
      contentAlignment = Alignment.Center
    ) {
      Text(text = "Youtube player")
    }
  },
  real = {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val youtubePlayerTracker = remember { YouTubePlayerTracker() }
    var youtubePlayer: YouTubePlayer? by remember { mutableStateOf(null) }

    val videoSettingsViewModel = hiltViewModel<VideoSettingsViewModel>()
    val shouldMute by videoSettingsViewModel.uiState.collectAsState()

    var showFullscreen by remember { mutableStateOf(false) }
    var userPaused by remember { mutableStateOf(false) }

    val meteredConnection = isActiveNetworkMetered()
    val autoplayAllowed = !meteredConnection
    val shouldAutoplay by remember { derivedStateOf { !userPaused && autoplayAllowed } }

    val youtubePlayerView = remember {
      buildYoutubePlayerView(
        context = context,
        lifecycle = lifecycleOwner.lifecycle,
        videoId = videoId,
        youtubePlayerTracker = youtubePlayerTracker,
        startMuted = shouldMute,
        autoplay = autoplayAllowed
      )
    }

    LaunchedEffect(shouldPause) {
      if (shouldPause) {
        youtubePlayer?.pause()
      } else {
        if (shouldAutoplay) {
          youtubePlayer?.play()
        }
      }
    }

    DisposableEffect(lifecycleOwner) {
      val observer = LifecycleEventObserver { _, event ->
        when (event) {
          Lifecycle.Event.ON_STOP -> {
            //This condition is used to fix an issue where the video appears to
            //restart when the app goes to background and returns to foreground
            if (youtubePlayerTracker.currentSecond > 0f)
              youtubePlayer?.seekTo(youtubePlayerTracker.currentSecond)
          }

          Lifecycle.Event.ON_RESUME -> {
            if (shouldAutoplay) {
              youtubePlayer?.play()
            }
          }

          else -> {}
        }
      }

      lifecycleOwner.lifecycle.addObserver(observer)

      onDispose {
        lifecycleOwner.lifecycle.removeObserver(observer)
        youtubePlayerView.release()
      }
    }

    youtubePlayerView.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
      override fun onReady(youTubePlayer: YouTubePlayer) {
        youtubePlayer = youTubePlayer
      }

      override fun onMuteChanged(
        youTubePlayer: YouTubePlayer,
        isMuted: Boolean,
      ) {
        videoSettingsViewModel.setShouldMute(isMuted)
      }

      override fun onStateChange(
        youTubePlayer: YouTubePlayer,
        state: PlayerConstants.PlayerState,
      ) {
        when (state) {
          PlayerConstants.PlayerState.PAUSED -> {
            if (showFullscreen) {
              userPaused = true
            }
          }

          PlayerConstants.PlayerState.PLAYING -> {
            if (showFullscreen) {
              userPaused = false
            }
          }

          else -> {}
        }
      }
    })

    AppViewYoutubePlayerContent(
      modifier = modifier,
      youtubePlayerView = youtubePlayerView,
      youtubePlayer = youtubePlayer,
      showFullscreen = showFullscreen,
      toggleFullscreen = { showFullscreen = !showFullscreen },
      shouldMute = shouldMute,
      contentDescription = contentDesc
    )
  }
)

@Composable
private fun AppViewYoutubePlayerContent(
  modifier: Modifier,
  youtubePlayerView: YouTubePlayerView,
  youtubePlayer: YouTubePlayer?,
  showFullscreen: Boolean,
  toggleFullscreen: () -> Unit,
  shouldMute: Boolean,
  contentDescription: String,
) {
  if (!showFullscreen) {
    Box {
      AndroidView(
        modifier = modifier,
        factory = {
          youtubePlayerView.removeSelf()
          youtubePlayerView.toggleAccessibility(false)
          youtubePlayerView
        },
        update = {
          youtubePlayerView.getYouTubePlayerWhenReady(object : YouTubePlayerCallback {
            override fun onYouTubePlayer(youTubePlayer: YouTubePlayer) {
              youTubePlayer.hideVideoTitle()
              youTubePlayer.hidePlayerControls()
            }
          })
        }
      )
      Box(
        modifier = modifier.clickable {
          toggleFullscreen()
        }
      )
      IconButton(
        modifier = Modifier
          .padding(top = 4.dp, end = 16.dp)
          .clip(CircleShape)
          .size(32.dp)
          .background(Palette.GreyLight.copy(alpha = 0.4f))
          .align(Alignment.TopEnd),
        onClick = {
          if (shouldMute) youtubePlayer?.unMute() else youtubePlayer?.mute()
        }
      ) {
        Icon(
          imageVector = if (shouldMute) getMuted(Palette.White) else getUnmuted(Palette.White),
          contentDescription = if (shouldMute) "Muted" else "Unmuted",
          tint = Color.Unspecified
        )
      }
    }
  } else {
    Dialog(
      onDismissRequest = {
        toggleFullscreen()
      },
      properties = DialogProperties(
        usePlatformDefaultWidth = false
      )
    ) {
      Box(
        modifier = Modifier
          .fillMaxSize()
          .background(Palette.Black)
      ) {
        AndroidView(
          modifier = Modifier.fillMaxSize(),
          factory = {
            youtubePlayerView.contentDescription = contentDescription
            youtubePlayerView.toggleAccessibility(true)
            youtubePlayerView
          },
          update = {
            youtubePlayerView.getYouTubePlayerWhenReady(object : YouTubePlayerCallback {
              override fun onYouTubePlayer(youTubePlayer: YouTubePlayer) {
                youTubePlayer.showPlayerControls()
                youTubePlayer.showVideoTitle()
              }
            })
          }
        )
        Image(
          imageVector = getLeftArrow(Palette.Primary, Palette.Black),
          contentDescription = stringResource(id = R.string.button_back_title),
          contentScale = ContentScale.Crop,
          modifier = Modifier
            .clickable { toggleFullscreen() }
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .size(32.dp)
        )
      }
    }
  }
}

fun buildYoutubePlayerView(
  context: Context,
  lifecycle: Lifecycle,
  videoId: String,
  youtubePlayerTracker: YouTubePlayerTracker,
  startMuted: Boolean,
  autoplay: Boolean,
): YouTubePlayerView {
  val youtubePlayerView = YouTubePlayerView(context)
  lifecycle.addObserver(youtubePlayerView)
  youtubePlayerView.enableAutomaticInitialization = false

  val youtubeListener = object : AbstractYouTubePlayerListener() {
    override fun onReady(youTubePlayer: YouTubePlayer) {
      youTubePlayer.addListener(youtubePlayerTracker)

      if (autoplay)
        youTubePlayer.loadVideo(videoId, youtubePlayerTracker.currentSecond)
      else
        youTubePlayer.cueVideo(videoId, youtubePlayerTracker.currentSecond)
    }
  }

  val iFramePlayerOptions = IFramePlayerOptions.Builder()
    .apply {
      rel(0)
      controls(1)
      ivLoadPolicy(3)
      ccLoadPolicy(1)
      modestBranding(0)
      langPref(context.resources?.configuration?.locales?.get(0)?.language ?: "en")
      if (startMuted) mute(1)
    }.build()

  youtubePlayerView.initialize(youtubeListener, iFramePlayerOptions)

  return youtubePlayerView
}

fun View?.removeSelf() {
  this ?: return
  val parentView = parent as? ViewGroup ?: return
  parentView.removeView(this)
}
