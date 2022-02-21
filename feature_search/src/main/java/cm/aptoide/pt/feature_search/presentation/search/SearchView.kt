package cm.aptoide.pt.feature_search.presentation.search

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import cm.aptoide.pt.feature_search.R

@Preview
@Composable
fun SearchScreen(searchViewModel: SearchViewModel = hiltViewModel()) {

  // UiState of the HomeScreen
  val uiState by searchViewModel.uiState.collectAsState()

  SearchSuggestions(uiState.searchSuggestionType.name, uiState.searchSuggestions)
}

@Composable
fun SearchSuggestions(title: String, suggestions: List<String>) {
  Column(
    modifier = Modifier
      .fillMaxSize()
  ) {
    Text(
      modifier = Modifier.padding(start = 16.dp, top = 16.dp, end = 16.dp),
      text = title
    )
    LazyColumn {
      items(suggestions) { suggestion ->
        SearchSuggestionItem(item = suggestion)
      }
    }
  }
}

@Composable
fun SearchSuggestionItem(item: String) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
  ) {
    Image(
      modifier = Modifier
        .size(21.dp, 18.dp)
        .wrapContentHeight(CenterVertically),
      painter = painterResource(id = R.drawable.ic_search_history_icon),
      contentDescription = "Suggestion icon"
    )
    Text(
      modifier = Modifier.padding(start = 16.dp, end = 16.dp),
      text = item
    )
  }
}
