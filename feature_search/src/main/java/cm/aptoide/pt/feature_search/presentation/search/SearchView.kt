package cm.aptoide.pt.feature_search.presentation.search

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel

@Preview
@Composable
fun SearchScreen(searchViewModel: SearchViewModel = hiltViewModel()) {

  // UiState of the HomeScreen
  val uiState by searchViewModel.uiState.collectAsState()


  Column(
    modifier = Modifier
      .fillMaxSize()
      .wrapContentSize(Alignment.Center)
  ) {
    Text(text = "Apps")
  }
  SearchSuggestions(uiState.searchSuggestions)
}

@Composable
fun SearchSuggestions(suggestions: List<String>) {
  LazyColumn {
    items(suggestions) { suggestion ->
      SearchSuggestionItem(item = suggestion)
    }
  }
}

@Composable
fun SearchSuggestionItem(item: String) {
  Text(text = item)
}
