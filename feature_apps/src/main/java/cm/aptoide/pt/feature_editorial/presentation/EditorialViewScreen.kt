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
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cm.aptoide.pt.feature_apps.R
import cm.aptoide.pt.feature_editorial.data.network.ContentJSON
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
fun ContentView(content: ContentJSON) {
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
      Media("", "", "")
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
    }
  }
}
