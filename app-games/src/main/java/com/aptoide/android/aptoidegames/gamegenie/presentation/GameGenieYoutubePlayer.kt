package com.aptoide.android.aptoidegames.gamegenie.presentation

import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import android.widget.FrameLayout
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import cm.aptoide.pt.extensions.runPreviewable
import com.aptoide.android.aptoidegames.R
import com.aptoide.android.aptoidegames.drawables.icons.getLeftArrow
import com.aptoide.android.aptoidegames.theme.Palette
import com.aptoide.android.aptoidegames.videos.presentation.VideoSettingsViewModel
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerCallback
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.options.IFramePlayerOptions
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.utils.YouTubePlayerTracker
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView

@Composable
fun GameGenieYoutubePlayer(
  modifier: Modifier,
  videoId: String,
  playerCache: MutableMap<String, YouTubePlayerView>,
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
    val lifecycleOwner = LocalLifecycleOwner.current
    val youtubePlayerTracker = remember { YouTubePlayerTracker() }
    var youtubePlayer: YouTubePlayer? by remember { mutableStateOf(null) }

    val videoSettingsViewModel = hiltViewModel<VideoSettingsViewModel>()
    val shouldMute by videoSettingsViewModel.uiState.collectAsState()

    var showVideo by remember { mutableStateOf(true) }
    var showFullscreen by remember { mutableStateOf(false) }
    var userPaused by remember { mutableStateOf(false) }

    LaunchedEffect(videoId) {
      showVideo = true
      showFullscreen = false
      userPaused = false
    }

    val youtubePlayerView = rememberYoutubePlayerView(
      videoId = videoId,
      youtubePlayerTracker = youtubePlayerTracker,
      startMuted = shouldMute,
      playerCache = playerCache
    )

    DisposableEffect(lifecycleOwner) {
      val observer = LifecycleEventObserver { _, event ->
        when (event) {
          Lifecycle.Event.ON_STOP -> {
            //This condition is used to fix an issue where the video appears to
            //restart when the app goes to background and returns to foreground
            if (youtubePlayerTracker.currentSecond > 0f)
              youtubePlayer?.seekTo(youtubePlayerTracker.currentSecond)
          }

          else -> {}
        }
      }

      lifecycleOwner.lifecycle.addObserver(observer)

      onDispose {
        lifecycleOwner.lifecycle.removeObserver(observer)
        //youtubePlayerView?.release()
      }
    }

    youtubePlayerView?.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
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
          PlayerConstants.PlayerState.VIDEO_CUED -> showVideo = true

          PlayerConstants.PlayerState.PAUSED -> {
            if (showFullscreen) {
              userPaused = true
            } else {
              youtubePlayer?.hidePlayerControls()
              youtubePlayer?.hideVideoTitle()
            }
            showVideo = true
          }

          PlayerConstants.PlayerState.PLAYING -> {
            if (showFullscreen) {
              userPaused = false
            } else {
              youtubePlayer?.hidePlayerControls()
              youtubePlayer?.hideVideoTitle()
            }
            showVideo = true
          }

          PlayerConstants.PlayerState.ENDED -> {
            youtubePlayer?.cueVideo(videoId, 0f)
          }

          else -> {}
        }
      }
    })
    val show = youtubePlayerView != null && showVideo
    AnimatedVisibility(
      visible = show,
      enter = fadeIn(),
    ) {
      GameGenieYoutubePlayerContent(
        modifier = modifier,
        youtubePlayerView = youtubePlayerView, //check on visible already
        showFullscreen = showFullscreen,
        toggleFullscreen = { showFullscreen = !showFullscreen },
        contentDescription = contentDesc
      )
    }
  }
)

@Composable
private fun GameGenieYoutubePlayerContent(
  modifier: Modifier,
  youtubePlayerView: YouTubePlayerView,
  showFullscreen: Boolean,
  toggleFullscreen: () -> Unit,
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
              youTubePlayer.showVideoTitle()
              youTubePlayer.showPlayerControls()
            }
          })
        }
      )

      IconButton(
        onClick = { toggleFullscreen() },
        modifier = Modifier
          .align(Alignment.BottomEnd)
          .padding(12.dp)
          .background(Color.Black.copy(alpha = 0.5f), shape = CircleShape)
          .clip(CircleShape)
          .size(36.dp)
      ) {
        Icon(
          imageVector = Icons.Filled.Fullscreen,
          contentDescription = "Toggle fullscreen",
          tint = Color.White
        )
      }
    }
  } else {
    Dialog(
      onDismissRequest = {
        toggleFullscreen()
      },
      properties = DialogProperties(
        dismissOnClickOutside = false,
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

@Composable
fun rememberYoutubePlayerView(
  videoId: String,
  youtubePlayerTracker: YouTubePlayerTracker,
  startMuted: Boolean,
  playerCache: MutableMap<String, YouTubePlayerView>,
): YouTubePlayerView {
  val context = LocalContext.current
  val lifecycle = LocalLifecycleOwner.current.lifecycle
  val configuration = LocalConfiguration.current

  return remember(videoId) {
    playerCache.getOrPut(videoId) {
      val youtubePlayerView = YouTubePlayerView(context)
      lifecycle.addObserver(youtubePlayerView)
      youtubePlayerView.enableAutomaticInitialization = false

      youtubePlayerView.layoutParams = FrameLayout.LayoutParams(
        LayoutParams.MATCH_PARENT,
        LayoutParams.MATCH_PARENT
      ).apply { gravity = Gravity.CENTER }

      val youtubeListener = object : AbstractYouTubePlayerListener() {
        override fun onReady(youTubePlayer: YouTubePlayer) {
          youTubePlayer.addListener(youtubePlayerTracker)
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
          langPref(configuration.locales.get(0)?.language ?: "en")
          if (startMuted) mute(1)
        }.build()

      youtubePlayerView.initialize(youtubeListener, iFramePlayerOptions)
      youtubePlayerView
    }
  }
}

fun View?.removeSelf() {
  this ?: return
  val parentView = parent as? ViewGroup ?: return
  parentView.removeView(this)
}
