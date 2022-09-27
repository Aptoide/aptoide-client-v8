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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.compose.rememberNavController
import cm.aptoide.pt.aptoide_ui.textformatter.TextFormatter
import cm.aptoide.pt.aptoide_ui.video.YoutubePlayer
import cm.aptoide.pt.feature_editorial.R
import cm.aptoide.pt.feature_editorial.data.ArticleContent
import cm.aptoide.pt.feature_editorial.data.network.Media
import cm.aptoide.pt.theme.AppTheme
import cm.aptoide.pt.theme.AptoideTheme
import coil.compose.rememberImagePainter
import coil.transform.RoundedCornersTransformation

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun EditorialViewScreen(viewModel: EditorialViewModel) {
  val uiState by viewModel.uiState.collectAsState()
  val navController = rememberNavController()

  AptoideTheme {
    Scaffold(
      topBar = {
        TopAppBar(
          title = { Text(text = "Editorial", textAlign = TextAlign.Center) },
          backgroundColor = AppTheme.colors.background,
          navigationIcon = {
            IconButton(
              modifier = Modifier.alpha(ContentAlpha.medium),
              onClick = { navController.popBackStack() }) {
              Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "AppViewBack",
                tint = Color.White
              )
            }
          },
        )
      },
      modifier = Modifier
        .fillMaxWidth()
        .fillMaxHeight()
    ) {
      if (uiState.isLoading) {
        Text("loading")
      } else {
        Column(
          modifier = Modifier
            .fillMaxHeight()
            .verticalScroll(rememberScrollState())
            .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 64.dp)
        ) {
          Box(modifier = Modifier.padding(bottom = 20.dp)) {
            Image(
              painter = rememberImagePainter(uiState.article?.image,
                builder = {
                  placeholder(R.drawable.ic_placeholder)
                  transformations(RoundedCornersTransformation(16f))
                }),
              contentDescription = "Background Image",
              modifier = Modifier
                .height(200.dp)
                .fillMaxWidth()
            )
            uiState.article?.subtype?.label?.let { it ->

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
                  text = it,
                  fontSize = MaterialTheme.typography.overline.fontSize,
                  color = Color.White,
                  textAlign = TextAlign.Center,
                  modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 8.dp)
                )
              }
            }
          }
          uiState.article?.let { it ->
            Text(
              text = it.title,
              fontSize = MaterialTheme.typography.h6.fontSize,
              modifier = Modifier.padding(bottom = 10.dp)
            )
          }

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
  Column {
    content.title?.let {
      Text(
        it,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(top = 10.dp)
      )
    }
    content.message?.let {
      Text(
        text = it,
        modifier = Modifier.padding(top = 10.dp),
        fontSize = MaterialTheme.typography.body2.fontSize
      )
    }


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
            transformations(RoundedCornersTransformation(16f))
          }),
        contentDescription = "Background Image",
        modifier = Modifier
          .height(192.dp)
          .padding(top = 24.dp)
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
  Card(
    modifier = Modifier
      .padding(top = 10.dp)
      .height(80.dp)
  ) {
    Row(verticalAlignment = Alignment.CenterVertically) {
      Image(
        painter = rememberImagePainter(icon,
          builder = {
            placeholder(R.drawable.ic_placeholder)
            transformations(RoundedCornersTransformation(16f))
          }),
        contentDescription = "App Icon",
        modifier = Modifier
          .padding(top = 16.dp, bottom = 16.dp, start = 16.dp, end = 16.dp)
          .height(48.dp)
          .width(48.dp)
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
          fontSize = MaterialTheme.typography.body2.fontSize
        )
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier
            .height(16.dp)
            .fillMaxWidth()
        ) {
          Image(
            painter = rememberImagePainter(
              R.drawable.ic_icon_star,
              builder = {
                placeholder(R.drawable.ic_icon_star)
                transformations(RoundedCornersTransformation())
              }),
            contentDescription = "App Stats rating",
            modifier = Modifier
              .padding(end = 4.dp)
              .width(12.dp)
              .height(12.dp)
          )
          Text(
            text = TextFormatter.formatDecimal(rating),
            fontSize = MaterialTheme.typography.caption.fontSize, textAlign = TextAlign.Center
          )
        }
      }
      Button(
        onClick = { TODO() },
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
          .padding(end = 16.dp)
          .height(40.dp)
          .width(88.dp)
      ) {
        Text(
          "INSTALL", maxLines = 1, fontSize = MaterialTheme.typography.button.fontSize,
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
      .padding(top = 10.dp)
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
