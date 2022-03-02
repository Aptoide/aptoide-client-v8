package cm.aptoide.pt.feature_search.presentation.search

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import cm.aptoide.pt.feature_search.R

@Preview
@Composable
fun SearchScreen(searchViewModel: SearchViewModel = hiltViewModel()) {

  // UiState of the HomeScreen
  val uiState by searchViewModel.uiState.collectAsState()

  Searchview(
    uiState = uiState,
    onSelectSearchSuggestion = { searchViewModel.onSelectSearchSuggestion(it) },
    onRemoveSuggestion = { searchViewModel.onRemoveSearchSuggestion(it) },
    onSearchValueChanged = { searchViewModel.onSearchInputValueChanged(it) }
  )
}

@Composable
fun Searchview(
  uiState: SearchUiState,
  onSelectSearchSuggestion: (String) -> Unit,
  onRemoveSuggestion: (String) -> Unit,
  onSearchValueChanged: (String) -> Unit
) {
  Scaffold(topBar = {
    TopAppBar(title = {
//      Row(
//        modifier = Modifier
//          .fillMaxWidth()
//          .wrapContentWidth(align = Alignment.CenterHorizontally)
//      ) {
//        TextField(value = uiState.searchTextInput, onValueChange = onSearchValueChanged, label = {
//          Text(
//            text = "Search for Apps and Games"
//          )
//        })
//      }

      DefaultSearchAppBar("text", {}, onSearchValueChanged)
    })
  }) {
    SearchSuggestions(
      uiState.searchSuggestionType.name,
      uiState.searchSuggestions,
      onSelectSearchSuggestion,
      onRemoveSuggestion
    )
  }
}

@Composable
fun DefaultSearchAppBar(
  title: String,
  onSearchClicked: (String) -> Unit,
  onSearchQueryChanged: (String) -> Unit
) {

  TopAppBar(
    title = {
      Text(
        text = title
      )
    },
    actions = {
      IconButton(onClick = { onSearchClicked(title) }
      ) {
        Icon(
          imageVector = Icons.Filled.Search,
          contentDescription = "Search Icon",
          tint = Color.White
        )
      }
    }
  )
}

@Composable
fun SearchAppBar(
  query: String,
  onSearchQueryChanged: (String) -> Unit,
  onSearchClicked: (String) -> Unit
) {
  Surface(
    modifier = Modifier
      .fillMaxWidth()
      .height(56.dp),
    elevation = AppBarDefaults.TopAppBarElevation,
    color = MaterialTheme.colors.primary
  ) {
    TextField(
      modifier = Modifier.fillMaxWidth(),
      value = query,
      onValueChange = {
        onSearchQueryChanged(it)
      },
      placeholder = {
        Text(
          modifier = Modifier.alpha(ContentAlpha.medium),
          text = "Search apps and games !",
          color = Color.White
        )
      },
      textStyle = TextStyle(fontSize = MaterialTheme.typography.subtitle1.fontSize),
      singleLine = true,
      leadingIcon = {
        IconButton(
          modifier = Modifier.alpha(ContentAlpha.medium),
          onClick = {}) {
          Icon(
            imageVector = Icons.Default.Search,
            contentDescription = "Search Icon",
            tint = Color.White
          )
        }
      },
      trailingIcon = {
        IconButton(
          onClick = {
            if (query.isNotEmpty()) {
              onSearchQueryChanged("")
            } else {
              onSearchQueryChanged("Sporting")
            }
          }) {
          Icon(
            imageVector = Icons.Default.Close,
            contentDescription = "Close search Icon",
            tint = Color.White
          )
        }
      },
      keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
      keyboardActions = KeyboardActions(
        onSearch = {
          onSearchClicked(query)
        }
      ), colors =
      TextFieldDefaults.textFieldColors(
        backgroundColor = Color.Transparent,
        cursorColor = Color.White.copy(alpha = ContentAlpha.medium)
      )
    )
  }
}

@Composable
fun SearchSuggestions(
  title: String,
  suggestions: List<String>,
  onSelectSearchSuggestion: (String) -> Unit,
  onRemoveSuggestion: (String) -> Unit
) {
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
        SearchSuggestionItem(item = suggestion, onSelectSearchSuggestion)
      }
    }
  }
}

@Composable
fun SearchSuggestionItem(item: String, onSelectSearchSuggestion: (String) -> Unit) {
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
      modifier = Modifier
        .padding(start = 16.dp, end = 16.dp)
        .clickable(onClick = { onSelectSearchSuggestion(item) }),
      text = item
    )
  }
}


@Composable
@Preview
fun DefaultSearchAppBarPreview() {
  DefaultSearchAppBar(title = "sporting", onSearchClicked = {}, onSearchQueryChanged = {})
}

@Composable
@Preview
fun SearchAppBarPreview() {
  SearchAppBar(
    query = "SPOOOOORTINggggggggggggggggggggggggG",
    onSearchQueryChanged = {},
    onSearchClicked = {})
}
