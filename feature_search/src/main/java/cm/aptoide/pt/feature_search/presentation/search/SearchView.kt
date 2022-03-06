package cm.aptoide.pt.feature_search.presentation.search

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import cm.aptoide.pt.feature_search.R
import cm.aptoide.pt.feature_search.domain.model.SearchSuggestion

@Preview
@Composable
fun SearchScreen(searchViewModel: SearchViewModel = hiltViewModel()) {

  val uiState by searchViewModel.uiState.collectAsState()

  MainSearchView(
    uiState = uiState,
    onSelectSearchSuggestion = { searchViewModel.onSelectSearchSuggestion(it) },
    onRemoveSuggestion = { searchViewModel.onRemoveSearchSuggestion(it) },
    onSearchValueChanged = { searchViewModel.onSearchInputValueChanged(it) },
    onSearchQueryClick = { searchViewModel.searchApp(it) },
    onSearchFocus = { searchViewModel.updateSearchAppBarState(it) }
  )
}

@Composable
fun MainSearchView(
  uiState: SearchUiState,
  onSelectSearchSuggestion: (String) -> Unit,
  onRemoveSuggestion: (String) -> Unit,
  onSearchValueChanged: (String) -> Unit,
  onSearchQueryClick: (String) -> Unit,
  onSearchFocus: (SearchAppBarState) -> Unit
) {

  Scaffold(topBar = {
    SearchAppBar(
      query = uiState.searchTextInput,
      onSearchQueryChanged = onSearchValueChanged,
      onSearchQueryClick = onSearchQueryClick,
      onSearchFocus = onSearchFocus, uiState.searchAppBarState
    )
  }) {

    if (uiState.searchAppBarState == SearchAppBarState.CLOSED) {

      SearchSuggestions(
        title = uiState.searchSuggestions.suggestionType.title,
        suggestions = uiState.searchSuggestions.suggestionsList,
        onSelectSearchSuggestion = onSelectSearchSuggestion,
        onRemoveSuggestion = onRemoveSuggestion
      )
    } else {
      AutoCompleteSearchSuggestions(
        uiState.searchSuggestions.suggestionsList,
        onSelectSearchSuggestion = onSelectSearchSuggestion
      )
    }
  }
}

@Composable
fun SearchAppBar(
  query: String,
  onSearchQueryChanged: (String) -> Unit,
  onSearchQueryClick: (String) -> Unit,
  onSearchFocus: (SearchAppBarState) -> Unit,
  searchAppBarState: SearchAppBarState
) {

  TopAppBar(title = {
    OutlinedTextField(
      modifier = Modifier
        .fillMaxWidth()
        .defaultMinSize(minHeight = 40.dp)
        .onFocusChanged {
          if (it.isFocused) {
            onSearchFocus(SearchAppBarState.OPENED)
          } else {
            onSearchFocus(SearchAppBarState.CLOSED)
          }
        },
      shape = RoundedCornerShape(16.dp),
      value = query,
      onValueChange = {
        onSearchQueryChanged(it)
      },
      placeholder = {
        Text(
          modifier = Modifier
            .alpha(ContentAlpha.medium)
            .wrapContentHeight(),
          text = "Search apps and games",
          color = Color.White,
          fontSize = MaterialTheme.typography.button.fontSize
        )
      },
      textStyle = TextStyle(fontSize = MaterialTheme.typography.body2.fontSize),
      singleLine = true,
      leadingIcon = {
        IconButton(
          modifier = Modifier.alpha(ContentAlpha.medium),
          onClick = {}) {
          Icon(
            imageVector = Icons.Default.ArrowBack,
            contentDescription = "Search Icon",
            tint = Color.White
          )
        }
      },
      trailingIcon = {
        if (searchAppBarState == SearchAppBarState.CLOSED) {
          IconButton(
            onClick = {
              onSearchQueryChanged("")
            }) {
            Icon(
              imageVector = Icons.Default.Search,
              contentDescription = "Search icon",
              tint = Color.White
            )
          }
        } else {
          if (query.isNotEmpty()) {
            IconButton(
              onClick = {
                onSearchQueryChanged("")
              }) {
              Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Clear search icon",
                tint = Color.White
              )
            }
          }
        }
      },
      keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
      keyboardActions = KeyboardActions(
        onSearch = {
          onSearchQueryClick(query)
        }
      ), colors =
      TextFieldDefaults.textFieldColors(
        backgroundColor = Color.Transparent,
        cursorColor = Color.White.copy(alpha = ContentAlpha.medium)
      )
    )
  })

}

@Composable
fun AutoCompleteSearchSuggestions(
  suggestions: List<SearchSuggestion>,
  onSelectSearchSuggestion: (String) -> Unit
) {
  LazyColumn(
    modifier = Modifier.padding(top = 26.dp),
    verticalArrangement = Arrangement.spacedBy(20.dp)
  ) {
    items(suggestions) { suggestion ->
      AutoCompleteSearchSuggestionItem(item = suggestion.appName, onSelectSearchSuggestion)
    }
  }
}

@Composable
fun AutoCompleteSearchSuggestionItem(item: String, onSelectSearchSuggestion: (String) -> Unit) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(start = 26.dp)
  ) {
    Image(
      modifier = Modifier
        .size(21.dp, 18.dp)
        .wrapContentHeight(CenterVertically),
      painter = painterResource(id = R.drawable.ic_search),
      contentDescription = "Auto-complete icon"
    )
    Text(
      modifier = Modifier
        .padding(start = 12.dp, end = 16.dp)
        .clickable(onClick = { onSelectSearchSuggestion(item) }),
      text = item,
      fontSize = MaterialTheme.typography.body1.fontSize,
    )
  }
}


@Composable
fun SearchSuggestions(
  title: String,
  suggestions: List<SearchSuggestion>,
  onSelectSearchSuggestion: (String) -> Unit,
  onRemoveSuggestion: (String) -> Unit
) {
  Column(
    modifier = Modifier
      .fillMaxSize()
  ) {
    Text(
      modifier = Modifier.padding(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 21.dp),
      text = title
    )
    LazyColumn {
      items(suggestions) { suggestion ->
        SearchSuggestionItem(item = suggestion.appName, onSelectSearchSuggestion)
      }
    }
  }
}

@Composable
fun SearchSuggestionItem(item: String, onSelectSearchSuggestion: (String) -> Unit) {
  Row(
    modifier = Modifier
      .padding(bottom = 24.dp, start = 16.dp)
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
      modifier = Modifier
        .padding(start = 12.dp, end = 16.dp)
        .wrapContentHeight(CenterVertically)
        .clickable(onClick = { onSelectSearchSuggestion(item) }),
      text = item,
      fontSize = MaterialTheme.typography.body1.fontSize
    )
  }
}


@Composable
@Preview
fun SearchAppBarPreview() {
  SearchAppBar(
    query = "facebook",
    onSearchQueryChanged = {},
    onSearchQueryClick = {}, onSearchFocus = {}, SearchAppBarState.CLOSED
  )
}
