package cm.aptoide.pt.feature_editorial.presentation

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.compose.rememberNavController
import cm.aptoide.pt.aptoide_ui.AptoideAsyncImage
import cm.aptoide.pt.aptoide_ui.textformatter.TextFormatter
import cm.aptoide.pt.aptoide_ui.theme.AppTheme
import cm.aptoide.pt.aptoide_ui.toolbar.TopBar
import cm.aptoide.pt.aptoide_ui.video.YoutubePlayer
import cm.aptoide.pt.feature_editorial.data.model.Media
import cm.aptoide.pt.feature_editorial.domain.Paragraph

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun EditorialViewScreen(viewModel: EditorialViewModel) {
  val uiState by viewModel.uiState.collectAsState()
  val navController = rememberNavController()

  Scaffold(
    topBar = {
      TopBar(
        title = "Editorial",
        onBackPressed = { navController.popBackStack() }
      )
    },
    modifier = Modifier
      .fillMaxWidth()
      .fillMaxHeight()
  ) {
    val state = uiState
    if (state is EditorialUiState.Loading) {
      Text("loading")
    } else if (state is EditorialUiState.Idle) {
      Column(
        modifier = Modifier
          .fillMaxHeight()
          .verticalScroll(rememberScrollState())
          .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 64.dp)
      ) {
        Box(modifier = Modifier.padding(bottom = 16.dp)) {
          AptoideAsyncImage(
            data = state.article.image,
            contentDescription = "Background Image",
            placeholder = ColorPainter(AppTheme.colors.placeholderColor),
            modifier = Modifier
              .height(200.dp)
              .fillMaxWidth()
              .clip(RoundedCornerShape(24.dp))
          )
          Card(
            elevation = 0.dp,
            modifier = Modifier
              .padding(start = 16.dp, top = 12.dp)
              .wrapContentWidth()
              .height(30.dp)
              .clip(RoundedCornerShape(16.dp))
              .background(color = AppTheme.colors.editorialLabelColor)
          ) {
            Text(
              text = state.article.caption.uppercase(),
              style = AppTheme.typography.button_S,
              color = Color.White,
              textAlign = TextAlign.Center,
              modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 8.dp)
            )
          }
        }
        Text(
          text = state.article.title,
          style = AppTheme.typography.medium_L,
          modifier = Modifier.padding(bottom = 12.dp)
        )

        state.article.content.forEach {
          ContentView(it)
        }

      }
    }
  }
}

@Composable
fun ContentView(content: Paragraph) {
  Column {
    content.title?.let {
      Text(
        it,
        style = AppTheme.typography.medium_L,
        modifier = Modifier.padding(top = 16.dp, bottom = 12.dp)
      )
    }
    content.message?.let {
      Text(
        text = it,
        style = AppTheme.typography.regular_S
      )
    }

    val media = try {
      content.media.first()
    } catch (e: NoSuchElementException) {
      Media("", "", "", "")
    }

    if (media.type == "image") {
      media.image?.let {
        AptoideAsyncImage(
          data = it,
          contentDescription = "Background Image",
          placeholder = ColorPainter(AppTheme.colors.placeholderColor),
          modifier = Modifier
            .height(192.dp)
            .padding(top = 24.dp, bottom = 24.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
        )
      }
    } else if (media.type == "video_webview") {
      media.url?.let { VideoView(it) }
    }

    if (content.app != null) {
      AppBannerView(content.app.icon, content.app.name, content.app.pRating.avgRating)
    }
  }
}

@Composable
private fun AppBannerView(
  icon: String,
  name: String,
  rating: Double,
) {
  Card(
    modifier = Modifier
      .padding(top = 10.dp)
      .height(80.dp), backgroundColor = AppTheme.colors.background, elevation = 0.dp
  ) {
    Row(verticalAlignment = Alignment.CenterVertically) {
      AptoideAsyncImage(
        data = icon,
        contentDescription = "App Icon",
        placeholder = ColorPainter(AppTheme.colors.placeholderColor),
        modifier = Modifier
          .padding(top = 16.dp, bottom = 16.dp, start = 16.dp, end = 16.dp)
          .height(48.dp)
          .width(48.dp)
          .clip(RoundedCornerShape(16.dp))
      )
      Column(
        modifier = Modifier
          .padding(top = 21.dp, bottom = 18.dp)
          .weight(1f)
      ) {
        Text(
          text = name,
          modifier = Modifier.padding(bottom = 4.dp),
          overflow = TextOverflow.Ellipsis,
          style = AppTheme.typography.medium_S
        )
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier
            .height(16.dp)
            .fillMaxWidth()
        ) {
          Image(
            imageVector = Icons.Filled.Star,
            colorFilter = ColorFilter.tint(AppTheme.colors.imageIconBackground),
            contentDescription = "App Stats rating",
            modifier = Modifier
              .padding(end = 4.dp)
              .width(12.dp)
              .height(12.dp)
          )
          Text(
            text = TextFormatter.formatDecimal(rating),
            style = AppTheme.typography.medium_XS, textAlign = TextAlign.Center
          )
        }
      }
      Button(
        onClick = { TODO() },
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
          .height(40.dp)
          .width(88.dp)
      ) {
        Text(
          "INSTALL", maxLines = 1, style = AppTheme.typography.button_M,
          color = Color.White
        )
      }
    }
  }
}

@Composable
private fun VideoView(videoUrl: String) {
  Column(
    Modifier
      .height(232.dp)
      .padding(top = 24.dp, bottom = 24.dp)
      .fillMaxWidth(),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center
  ) {

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
