package cm.aptoide.pt.aptoide_ui.video.domain

import android.content.Context

class YoutubeUrlExtractor (private val context: Context) {

/*  fun extractYoutubeUrl(videoUrl: String){
    object : YouTubeExtractor(this) {//need context
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

          }
          i++
        }
      }
    }.extract(youtubeLink)

  }*/
}