package cm.aptoide.pt.aptoide_ui.video.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import cm.aptoide.pt.aptoide_ui.video.domain.YoutubeUrlExtractor
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AptoideVideoPlayerViewModel @Inject constructor(
  private val savedStateHandle: SavedStateHandle,
  private val youtubeUrlExtractor: YoutubeUrlExtractor,
  val player: Player,
) : ViewModel() {

  init {
    player.prepare()
    player.addMediaItem(MediaItem.fromUri("https://www.youtube.com/watch?v=JX1fwti2LI4"))
  }

  override fun onCleared() {
    super.onCleared()
    player.release()
  }
}