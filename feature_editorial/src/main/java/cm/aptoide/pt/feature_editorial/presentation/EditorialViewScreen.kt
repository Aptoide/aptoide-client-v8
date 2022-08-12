package cm.aptoide.pt.feature_editorial.presentation

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import cm.aptoide.pt.aptoide_ui.video.YoutubePlayer
import cm.aptoide.pt.feature_editorial.R
import cm.aptoide.pt.feature_editorial.data.ArticleContent
import cm.aptoide.pt.feature_editorial.data.network.Media
import cm.aptoide.pt.theme.AptoideTheme
import coil.compose.rememberImagePainter
import coil.transform.RoundedCornersTransformation

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun EditorialViewScreen(viewModel: EditorialViewModel) {
  val uiState by viewModel.uiState.collectAsState()
  AptoideTheme {
    Scaffold(
      modifier = Modifier
        .fillMaxWidth()
        .fillMaxHeight()
    ) {
      if (uiState.isLoading) {
        Text("loading")
      } else {
        Column(modifier = Modifier
          .fillMaxHeight()
          .verticalScroll(rememberScrollState())
          .padding(start = 8.dp, end = 8.dp, top = 8.dp, bottom = 64.dp)) {
          Box {
            Image(
              painter = rememberImagePainter(uiState.article?.image,
                builder = {
                  placeholder(R.drawable.ic_placeholder)
                  transformations(RoundedCornersTransformation())
                }),
              contentDescription = "Background Image",
              modifier = Modifier
                .height(168.dp)
                .fillMaxWidth()
            )
            uiState.article?.subtype?.label?.let { it -> Text(text = it) }
          }
          uiState.article?.let { it -> Text(it.title) }

          uiState.article?.content?.forEach { it ->
            ContentView(it)
          }

        }
      }
    }
  }
}

@Composable
fun ContentView(content: ArticleContent) {
  Column(modifier = Modifier.padding(top = 8.dp)) {
    content.title?.let {
      Text(it,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(top = 8.dp))
    }
    content.message?.let { Text(it, modifier = Modifier.padding(top = 8.dp)) }


    val media = try {
      content.media.first()
    } catch (e: NoSuchElementException) {
      Media("", "", "", "")
    }

    if (media.type == "image") {
      Image(
        painter = rememberImagePainter(media.image,
          builder = {
            placeholder(R.drawable.ic_placeholder)
            transformations(RoundedCornersTransformation())
          }),
        contentDescription = "Background Image",
        modifier = Modifier
          .height(168.dp)
          .padding(top = 8.dp)
          .fillMaxWidth()
      )
    } else if (media.type == "video_webview") {
      VideoView(media.url)
    }

    if (content.app != null) {
      AppBannerView(content.app.icon, content.app.name, content.app.stats.prating.avg)
    }
  }
}

@Composable
private fun AppBannerView(icon: String, name: String, rating: Double) {
  Row(modifier = Modifier.padding(top = 8.dp)) {
    Image(
      painter = rememberImagePainter(icon,
        builder = {
          placeholder(R.drawable.ic_placeholder)
          transformations(RoundedCornersTransformation(16f))
        }),
      contentDescription = "App Icon",
      modifier = Modifier
        .height(48.dp)
        .width(48.dp),
    )
    Column(modifier = Modifier.padding(start = 8.dp)) {
      Text(name)
      Text("" + rating)
    }
  }
}

@Composable
private fun VideoView(videoUrl: String) {
  Column(Modifier
    .height(232.dp)
    .padding(top = 8.dp)
    .fillMaxWidth(),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center) {

    AndroidView(
      modifier = Modifier.fillMaxSize(),
      factory = { context ->
        YoutubePlayer(context).apply {
          //can potentially set listeners here.
        }
      },
      update = { view ->
        view.loadVideo(videoUrl, false)
      }
    )
  }
}
