package cm.aptoide.pt.feature_search.presentation

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import cm.aptoide.pt.aptoide_ui.AptoideAsyncImage
import cm.aptoide.pt.aptoide_ui.theme.AppTheme
import cm.aptoide.pt.aptoide_ui.theme.AptoideTheme
import cm.aptoide.pt.extensions.PreviewAll
import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.feature_apps.data.emptyApp
import cm.aptoide.pt.feature_appview.presentation.AppViewScreen
import cm.aptoide.pt.feature_search.domain.model.SearchSuggestionType
import kotlin.math.round

@Composable
fun SearchScreen(searchViewModel: SearchViewModel = hiltViewModel()) {
  val uiState by searchViewModel.uiState.collectAsState()
  val searchValue by remember { mutableStateOf("") }
  val navController = rememberNavController()
  NavigationGraph(
    navController = navController,
    uiState = uiState,
    searchViewModel = searchViewModel,
    searchValue = searchValue
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
  onSearchQueryClick: (String) -> Unit,
  onSearchFocus: (Boolean) -> Unit,
  navController: NavHostController,
) {

  Scaffold(topBar = {
    SearchAppBar(
      query = searchValue,
      onSearchQueryChanged = onSearchValueChanged,
      onSearchQueryClick = onSearchQueryClick,
      onSearchFocus = onSearchFocus,
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
        SearchResultsView(uiState.searchResults, navController)
      }

      else -> {}
    }
  }
}

@Composable
fun SearchResultsView(
  searchResults: List<App>,
  navController: NavHostController,
) {
  LazyColumn(modifier = Modifier.padding(top = 24.dp)) {
    items(searchResults) { searchResult ->
      SearchResultItem(searchApp = searchResult, navController)
    }
  }
}

@Composable
fun SearchResultItem(
  searchApp: App,
  navController: NavHostController,
) {
  Row(
    horizontalArrangement = Arrangement.spacedBy(9.dp),
    modifier = Modifier
      .clickable { navController.navigate("appView/${searchApp.packageName}") }
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
        rating = searchApp.rating.avgRating,
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
  rating: Double,
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
      text = (round(rating * 100) / 100).toString(),
      style = MaterialTheme.typography.caption,
    )
  }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SearchAppBar(
  query: String,
  onSearchQueryChanged: (String) -> Unit,
  onSearchQueryClick: (String) -> Unit,
  onSearchFocus: (Boolean) -> Unit,
) {
  val focusManager = LocalFocusManager.current
  val keyboardController = LocalSoftwareKeyboardController.current
  var isFocused by remember { mutableStateOf(false) }
  TopAppBar(title = {
    OutlinedTextField(
      modifier = Modifier
        .fillMaxWidth()
        .defaultMinSize(minHeight = 40.dp)
        .onFocusChanged {
          isFocused = it.isFocused
          onSearchFocus(it.isFocused)
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
      keyboardActions = KeyboardActions(
        onSearch = {
          focusManager.clearFocus()
          onSearchQueryClick(query)
        },
        onDone = {
          keyboardController?.hide()
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
private fun NavigationGraph(
  navController: NavHostController,
  uiState: SearchUiState,
  searchViewModel: SearchViewModel,
  searchValue: String,
) {
  NavHost(
    navController = navController,
    startDestination = "search"
  ) {
    composable(
      "search"
    ) {
      MainSearchView(
        uiState = uiState,
        searchValue = searchValue,
        onSelectSearchSuggestion = { searchViewModel.onSelectSearchSuggestion(it) },
        onRemoveSuggestion = { searchViewModel.onRemoveSearchSuggestion(it) },
        onSearchValueChanged = { searchViewModel.onSearchInputValueChanged(it) },
        onSearchQueryClick = { searchViewModel.searchApp(it) },
        onSearchFocus = { searchViewModel.updateSearchAppBarState(it) },
        navController
      )
    }
    composable("appview/{packageName}") {
      val packageName = it.arguments?.getString("packageName")
      AppViewScreen(
        packageName = packageName,
        navigateBack = { navController.popBackStack() }
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
      onSearchFocus = {},
      navController = rememberNavController()
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
      searchResults = listOf(
        emptyApp.copy(name = "Lords Mobile", malware = "TRUSTED"),
        emptyApp.copy(name = "Clash of Lords", malware = "TRUSTED"),
        emptyApp.copy(name = "Lord of The Rings"),
        emptyApp.copy(name = "Lord the savior"),
        emptyApp.copy(name = "Lords and peasants", malware = "TRUSTED"),
        emptyApp.copy(name = "Rags to Lords"),
        emptyApp.copy(name = "Lords toys", malware = "TRUSTED"),
        emptyApp.copy(name = "50 shades for the Lord"),
        emptyApp.copy(name = "Lords and Ladies uninhibited"),
      )
    ),
  )
}
