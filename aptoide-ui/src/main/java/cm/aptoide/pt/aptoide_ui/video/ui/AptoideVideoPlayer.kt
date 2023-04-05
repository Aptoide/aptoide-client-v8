package cm.aptoide.pt.aptoide_ui.video.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.media3.ui.PlayerView

@Composable
fun AptoideVideoPlayer(url: String) {
  val viewModel = hiltViewModel<AptoideVideoPlayerViewModel>()

  var lifecycle by remember {
    mutableStateOf(Lifecycle.Event.ON_CREATE)
  }

  val lifecycleOwner = LocalLifecycleOwner.current

  DisposableEffect(lifecycleOwner) {
    val observer = LifecycleEventObserver { _, event ->
      lifecycle = event
    }
    lifecycleOwner.lifecycle.addObserver(observer)

    onDispose {
      lifecycleOwner.lifecycle.removeObserver(observer)
    }
  }

  Column(modifier = Modifier
    .fillMaxSize()
    .padding(horizontal = 16.dp)) {

    AndroidView(
      factory = { context ->
        PlayerView(context).also {
          it.player = viewModel.player
          //xml we would have a ref, but on compose we dont have or want to save a ref. So we need it as a state
          //keeping this player ref outside here could cause leaks
          //in the future we might have a player for compose
        }
      },
      update = {
        //called when a state inside here changes
        when (lifecycle) {
          Lifecycle.Event.ON_PAUSE -> {
            it.onPause()
            it.player?.pause()
          }
          Lifecycle.Event.ON_RESUME -> {
            it.onResume()
          }
          else -> Unit
        }

      },
      modifier = Modifier
        .fillMaxWidth()
        .aspectRatio(16 / 9f)
    )
    Spacer(modifier = Modifier.height(8.dp))

    Text("sporting videos")
  }
}

/*
fun extractYoutubeUrl(videoUrl: String, context: Context){
  object : YouTubeExtractor(context) {
    fun onExtractionComplete(ytFiles: SparseArray<YtFile>?, vMeta: VideoMeta) {
      //mainProgressBar.setVisibility(View.GONE)
      if (ytFiles == null) {
        // Something went wrong we got no urls. Always check this.
        finish()
        return
      }
      // Iterate over itags
      var i = 0
      var itag: Int
      while (i < ytFiles.size()) {
        itag = ytFiles.keyAt(i)
        // ytFile represents one file with its url and meta data
        val ytFile: YtFile = ytFiles[itag]

        // Just add videos in a decent format => height -1 = audio
        if (ytFile.getFormat().getHeight() === -1 || ytFile.getFormat().getHeight() >= 360) {
          //addButtonToMainLayout(vMeta.getTitle(), ytFile)
          //call callback for video url
          callback(yt.file.url)
        }
        i++
      }
    }
  }.extract(youtubeLink)

}*/
