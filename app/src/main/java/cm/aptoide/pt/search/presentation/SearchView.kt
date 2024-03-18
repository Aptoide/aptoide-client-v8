package cm.aptoide.pt.search.presentation

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import cm.aptoide.pt.appview.buildAppViewRoute
import cm.aptoide.pt.aptoide_ui.AptoideAsyncImage
import cm.aptoide.pt.aptoide_ui.animations.staticComposable
import cm.aptoide.pt.aptoide_ui.textformatter.TextFormatter
import cm.aptoide.pt.extensions.PreviewAll
import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.feature_apps.data.randomApp
import cm.aptoide.pt.feature_search.domain.model.SearchSuggestionType
import cm.aptoide.pt.feature_search.presentation.SearchUiState
import cm.aptoide.pt.feature_search.presentation.SearchViewModel
import cm.aptoide.pt.feature_search.utils.isValidSearch
import cm.aptoide.pt.theme.AppTheme
import cm.aptoide.pt.theme.AptoideTheme

const val searchRoute = "search"

@OptIn(ExperimentalComposeUiApi::class)
fun NavGraphBuilder.searchScreen(
  navigate: (String) -> Unit,
) = staticComposable(
  searchRoute,
) {
  val searchViewModel: SearchViewModel = hiltViewModel()
  val uiState by searchViewModel.uiState.collectAsState()

  var searchValue by remember { mutableStateOf("") }
  val focusManager = LocalFocusManager.current
  val keyboardController = LocalSoftwareKeyboardController.current

  MainSearchView(
    uiState = uiState,
    searchValue = searchValue,
    onSelectSearchSuggestion = { suggestion ->
      focusManager.clearFocus()
      keyboardController?.hide()
      searchValue = suggestion
      searchViewModel.onSelectSearchSuggestion(suggestion)
    },
    onRemoveSuggestion = { searchViewModel.onRemoveSearchSuggestion(it) },
    onSearchValueChanged = {
      searchValue = it
      searchViewModel.onSearchInputValueChanged(it)
    },
    onSearchQueryClick = {
      if (searchValue.isValidSearch()) {
        searchValue = searchValue.trim()
        focusManager.clearFocus()
        keyboardController?.hide()
        searchViewModel.searchApp(searchValue)
      }
    },
    onItemClick = {
      navigate(buildAppViewRoute(it.packageName))
    }
  )
}

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun MainSearchView(
  uiState: SearchUiState,
  searchValue: String,
  onSelectSearchSuggestion: (String) -> Unit,
  onRemoveSuggestion: (String) -> Unit,
  onSearchValueChanged: (String) -> Unit,
  onSearchQueryClick: () -> Unit,
  onItemClick: (App) -> Unit,
) {
  Scaffold(topBar = {
    SearchAppBar(
      query = searchValue,
      onSearchQueryChanged = onSearchValueChanged,
      onSearchQueryClick = onSearchQueryClick,
    )
  }) {
    when (uiState) {
      is SearchUiState.Suggestions -> {
        if (uiState.searchSuggestions.suggestionType == SearchSuggestionType.AUTO_COMPLETE) {
          AutoCompleteSearchSuggestions(
            suggestions = uiState.searchSuggestions.suggestionsList,
            onSelectSearchSuggestion = onSelectSearchSuggestion
          )
        } else {
          SearchSuggestions(
            suggestionType = uiState.searchSuggestions.suggestionType,
            suggestions = uiState.searchSuggestions.suggestionsList,
            onSelectSearchSuggestion = onSelectSearchSuggestion,
            onRemoveSuggestion = onRemoveSuggestion
          )
        }
      }

      is SearchUiState.Results -> {
        SearchResultsView(uiState.searchResults, onItemClick)
      }

      else -> {}
    }
  }
}

@Composable
fun SearchResultsView(
  searchResults: List<App>,
  onItemClick: (App) -> Unit,
) {
  LazyColumn(modifier = Modifier.padding(top = 24.dp)) {
    items(searchResults) { searchResult ->
      SearchResultItem(searchApp = searchResult, onItemClick)
    }
  }
}

@Composable
fun SearchResultItem(
  searchApp: App,
  onItemClick: (App) -> Unit,
) {
  Row(
    horizontalArrangement = Arrangement.spacedBy(9.dp),
    modifier = Modifier
      .clickable { onItemClick(searchApp) }
      .padding(horizontal = 16.dp, vertical = 8.dp)
      .fillMaxWidth()
      .height(64.dp)
  ) {
    AptoideAsyncImage(
      data = searchApp.icon,
      contentDescription = "App icon",
      placeholder = ColorPainter(AppTheme.colors.placeholderColor),
      modifier = Modifier
        .size(64.dp)
        .clip(RoundedCornerShape(16.dp)),
    )
    Column(modifier = Modifier.weight(1f)) {
      Text(
        text = searchApp.name,
        maxLines = 1,
        fontSize = MaterialTheme.typography.subtitle2.fontSize,
        overflow = TextOverflow.Ellipsis
      )
      RatingSearchView(
        pRating = searchApp.pRating.avgRating,
        modifier = Modifier
      )
      Spacer(modifier = Modifier.weight(1f))
      Text(
        text = searchApp.downloads.toString() + " downloads",
        maxLines = 1,
        fontSize = MaterialTheme.typography.overline.fontSize
      )
    }
    if (searchApp.malware == "TRUSTED") {
      MalwareBadgeView()
    }
  }
}

@Composable
fun MalwareBadgeView() {
  Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
    Text(
      text = "Trusted",
      color = AppTheme.colors.trustedColor,
      fontSize = MaterialTheme.typography.caption.fontSize
    )
    Image(
      imageVector = AppTheme.icons.TrustedIcon,
      contentDescription = "Trusted icon",
      modifier = Modifier.size(16.dp)
    )
  }
}

@Composable
fun RatingSearchView(
  pRating: Double,
  modifier: Modifier,
) {
  Row(
    modifier = modifier,
    horizontalArrangement = Arrangement.spacedBy(4.dp),
    verticalAlignment = CenterVertically
  ) {
    Image(
      imageVector = Icons.Filled.Star,
      colorFilter = ColorFilter.tint(AppTheme.colors.iconColor),
      contentDescription = "Rating icon",
      modifier = Modifier.size(12.dp)
    )
    Text(
      text = if (pRating == 0.0) "--" else TextFormatter.formatDecimal(pRating),
      style = MaterialTheme.typography.caption,
    )
  }
}

@Composable
fun SearchAppBar(
  query: String,
  onSearchQueryChanged: (String) -> Unit,
  onSearchQueryClick: () -> Unit,
) {
  var isFocused by remember { mutableStateOf(false) }
  TopAppBar(title = {
    OutlinedTextField(
      modifier = Modifier
        .fillMaxWidth()
        .defaultMinSize(minHeight = 40.dp)
        .onFocusChanged { isFocused = it.isFocused },
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
        if (isFocused) {
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
        } else {
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
        }
      },
      keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
      keyboardActions = KeyboardActions(onSearch = { onSearchQueryClick() }),
      colors = TextFieldDefaults.textFieldColors(
        backgroundColor = Color.Transparent,
        cursorColor = Color.White.copy(alpha = ContentAlpha.medium)
      )
    )
  })
}

@Composable
fun AutoCompleteSearchSuggestions(
  suggestions: List<String>,
  onSelectSearchSuggestion: (String) -> Unit,
) {
  LazyColumn(
    modifier = Modifier.padding(top = 26.dp),
    verticalArrangement = Arrangement.spacedBy(20.dp)
  ) {
    items(suggestions) { suggestion ->
      AutoCompleteSearchSuggestionItem(item = suggestion, onSelectSearchSuggestion)
    }
  }
}

@Composable
fun AutoCompleteSearchSuggestionItem(
  item: String,
  onSelectSearchSuggestion: (String) -> Unit,
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(start = 26.dp)
  ) {
    Image(
      imageVector = Icons.Filled.Search,
      colorFilter = ColorFilter.tint(AppTheme.colors.onBackground),
      contentDescription = "Auto-complete icon",
      modifier = Modifier
        .size(24.dp, 24.dp)
    )
    Text(
      modifier = Modifier
        .padding(start = 12.dp, end = 16.dp)
        .clickable(onClick = { onSelectSearchSuggestion(item) }),
      text = item,
      fontSize = MaterialTheme.typography.body2.fontSize,
    )
  }
}

@Composable
fun SearchSuggestions(
  suggestionType: SearchSuggestionType,
  suggestions: List<String>,
  onSelectSearchSuggestion: (String) -> Unit,
  onRemoveSuggestion: (String) -> Unit,
) {
  Column(
    modifier = Modifier
      .fillMaxSize()
  ) {
    Text(
      modifier = Modifier.padding(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 21.dp),
      text = suggestionType.title
    )
    LazyColumn {
      items(suggestions) { suggestion ->
        SearchSuggestionItem(
          item = suggestion,
          onSelectSearchSuggestion,
          suggestionType,
          onRemoveSuggestion
        )
      }
    }
  }
}

@Composable
fun SearchSuggestionItem(
  item: String,
  onSelectSearchSuggestion: (String) -> Unit,
  suggestionType: SearchSuggestionType,
  onRemoveSuggestion: (String) -> Unit,
) {
  Row(
    modifier = Modifier
      .padding(bottom = 24.dp, start = 16.dp)
      .fillMaxWidth(),
    verticalAlignment = CenterVertically
  ) {
    Image(
      imageVector = Icons.Filled.History,
      colorFilter = ColorFilter.tint(AppTheme.colors.secondaryGrey),
      contentDescription = "Suggestion icon",
      modifier = Modifier
        .size(24.dp, 24.dp)
    )
    Text(
      modifier = Modifier
        .padding(start = 8.dp)
        .weight(1f)
        .clickable(onClick = { onSelectSearchSuggestion(item) }),
      text = item,
      fontSize = MaterialTheme.typography.body2.fontSize
    )

    if (suggestionType == SearchSuggestionType.SEARCH_HISTORY) {
      Icon(
        imageVector = Icons.Default.Close,
        contentDescription = "Remove suggestion icon",
        modifier = Modifier
          .clickable(onClick = {
            onRemoveSuggestion(item)
          })
          .padding(start = 8.dp, end = 38.dp)
          .size(12.dp)
      )
    }
  }
}

@Composable
@PreviewAll
fun SearchScreenPreview(
  @PreviewParameter(SearchUiStateProvider::class)
  state: Pair<String, SearchUiState>,
) {
  AptoideTheme(darkTheme = isSystemInDarkTheme()) {
    MainSearchView(
      uiState = state.second,
      searchValue = state.first,
      onSelectSearchSuggestion = {},
      onRemoveSuggestion = {},
      onSearchValueChanged = {},
      onSearchQueryClick = {},
      onItemClick = {}
    )
  }
}

class SearchUiStateProvider : PreviewParameterProvider<Pair<String, SearchUiState>> {
  override val values: Sequence<Pair<String, SearchUiState>> = sequenceOf(
    "lord" to SearchUiState.FirstLoading,
    "lord" to SearchUiState.ResultsLoading,
    "lord" to SearchUiState.NoConnection,
    "lard" to SearchUiState.Error,
    "lord" to SearchUiState.Suggestions(
      searchSuggestions = cm.aptoide.pt.feature_search.domain.model.SearchSuggestions(
        suggestionType = SearchSuggestionType.SEARCH_HISTORY,
        suggestionsList = listOf(
          "Lords Mobile",
          "Clash of Lords",
          "Lord of The Rings"
        ),
        popularSearchList = listOf(
          "Lords and peasants",
          "Rags to Lords",
          "Lord the savior"
        )
      )
    ),
    "lord" to SearchUiState.Suggestions(
      searchSuggestions = cm.aptoide.pt.feature_search.domain.model.SearchSuggestions(
        suggestionType = SearchSuggestionType.AUTO_COMPLETE,
        suggestionsList = listOf(
          "Lord of The Rings",
          "Lord the savior",
          "Lords Mobile"
        ),
        popularSearchList = listOf(
          "Lords and peasants",
          "Rags to Lords",
          "Lord the savior"
        )
      )
    ),
    "lord" to SearchUiState.Suggestions(
      searchSuggestions = cm.aptoide.pt.feature_search.domain.model.SearchSuggestions(
        suggestionType = SearchSuggestionType.TOP_APTOIDE_SEARCH,
        suggestionsList = emptyList(),
        popularSearchList = emptyList()
      )
    ),
    "lord" to SearchUiState.Results(
      searchResults = List(9) { randomApp }
    ),
  )
}
