package cm.aptoide.pt.app_games.search.presentation

import android.view.KeyEvent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import cm.aptoide.pt.app_games.R
import cm.aptoide.pt.app_games.R.string
import cm.aptoide.pt.app_games.appview.LoadingView
import cm.aptoide.pt.app_games.appview.buildAppViewRoute
import cm.aptoide.pt.app_games.feature_apps.presentation.AppItem
import cm.aptoide.pt.app_games.feature_apps.presentation.LargeAppItem
import cm.aptoide.pt.app_games.home.EmptyView
import cm.aptoide.pt.app_games.home.GenericErrorView
import cm.aptoide.pt.app_games.home.NoConnectionView
import cm.aptoide.pt.app_games.installer.presentation.InstallViewShort
import cm.aptoide.pt.app_games.search.SearchType
import cm.aptoide.pt.app_games.theme.AppTheme
import cm.aptoide.pt.app_games.theme.gray4
import cm.aptoide.pt.app_games.theme.gray5
import cm.aptoide.pt.app_games.theme.gray6
import cm.aptoide.pt.aptoide_ui.animations.staticComposable
import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.feature_search.domain.model.SearchSuggestionType
import cm.aptoide.pt.feature_search.presentation.SearchUiState
import cm.aptoide.pt.feature_search.presentation.SearchUiState.Error
import cm.aptoide.pt.feature_search.presentation.SearchUiState.FirstLoading
import cm.aptoide.pt.feature_search.presentation.SearchUiState.NoConnection
import cm.aptoide.pt.feature_search.presentation.SearchUiState.Results
import cm.aptoide.pt.feature_search.presentation.SearchUiState.ResultsLoading
import cm.aptoide.pt.feature_search.presentation.SearchUiState.Suggestions
import cm.aptoide.pt.feature_search.presentation.SearchViewModel
import cm.aptoide.pt.feature_search.utils.fixQuery
import cm.aptoide.pt.feature_search.utils.isValidSearch

const val searchRoute = "search"
@OptIn(ExperimentalComposeUiApi::class) fun NavGraphBuilder.searchScreen(
  navigate: (String) -> Unit,
) = staticComposable(
  searchRoute,
  // TODO deepLinks = listOf(navDeepLink { uriPattern = BuildConfig.DEEP_LINK_SCHEMA + searchRoute })
) {
  val searchViewModel: SearchViewModel = hiltViewModel()
  val uiState by searchViewModel.uiState.collectAsState()

  var searchValue by rememberSaveable { mutableStateOf("") }
  val focusManager = LocalFocusManager.current
  val keyboardController = LocalSoftwareKeyboardController.current

  SearchView(
    uiState = uiState,
    searchValue = searchValue,
    onSelectSearchSuggestion = { suggestion, searchType ->
      focusManager.clearFocus()
      keyboardController?.hide()
      System.out.println(
        "Analytics Search Made Event - Inserted Keyword=$searchValue - Search Keyword=$suggestion - Search Type = ${searchType.type}"
      )
      //TODO Real Analytics?
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
        System.out.println(
          "Analytics Search Made Event - Inserted Keyword=$searchValue - Search Keyword=$searchValue - Search Type = ${SearchType.MANUAL.type}"
        )
        //TODO Real Analytics?
        searchViewModel.searchApp(searchValue)
      }
    },
    onItemClick = {
      System.out.println(
        "Analytics App Promo Click - Package Name=${it.packageName} - Has APPC Billing=${it.isAppCoins} - Search Keyword = $searchValue"
      )
      //TODO Real Analytics?
      navigate(
        buildAppViewRoute(
          packageName = it.packageName,
          //Might want search keyword
        )
      )
    },
    //TODO InstallBottomSheet appcoins wallet (onItemInstallStarted)
  )
}

@Composable
fun SearchView(
  uiState: SearchUiState,
  searchValue: String,
  onSelectSearchSuggestion: (String, SearchType) -> Unit,
  onRemoveSuggestion: (String) -> Unit,
  onSearchValueChanged: (String) -> Unit,
  onSearchQueryClick: () -> Unit,
  onItemClick: (App) -> Unit,
) {
  //TODO ContextTouchListener (might not be needed)
  Column {
    SearchAppBar(
      query = searchValue,
      onSearchQueryChanged = onSearchValueChanged,
      onSearchQueryClick = onSearchQueryClick,
    )

    when (uiState) {
      is NoConnection -> NoConnectionView(onRetryClick = onSearchQueryClick)
      is Error -> GenericErrorView(onRetryClick = onSearchQueryClick)
      is FirstLoading, ResultsLoading -> LoadingView()
      is Suggestions -> {
        // TODO buzz implementation after migration
        if (uiState.searchSuggestions.suggestionType == SearchSuggestionType.AUTO_COMPLETE) {
          AutoCompleteSearchSuggestions(
            suggestions = uiState.searchSuggestions.suggestionsList,
            popularSearch = uiState.searchSuggestions.popularSearchList,
            onSelectSearchSuggestion = onSelectSearchSuggestion
          )
        } else {
          SearchSuggestions(
            searchHistory = uiState.searchSuggestions.suggestionsList,
            popularSearch = uiState.searchSuggestions.popularSearchList,
            onSelectSearchSuggestion = onSelectSearchSuggestion,
            onRemoveSuggestion = onRemoveSuggestion
          )
        }
      }

      is Results -> {
        if (uiState.searchResults.isEmpty()) {
          EmptyView(text = stringResource(string.search_empty_body, searchValue))
        } else {
          SearchResultsView(
            searchResults = uiState.searchResults,
            searchValue = searchValue,
            onItemClick = onItemClick,
            //onItemInstallStarted
          )
        }
      }
    }
  }
}

@Composable
fun SearchAppBar(
  query: String,
  onSearchQueryChanged: (String) -> Unit,
  onSearchQueryClick: () -> Unit,
) {
  var isFocused by remember { mutableStateOf(false) }
  val placeholderText = stringResource(R.string.search_bar_label)
  val searchLabel = stringResource(R.string.search_item_talkback)
  val clearSearchLabel = stringResource(R.string.search_clear_talkback)

  OutlinedTextField(
    modifier = Modifier
      .padding(all = 16.dp)
      .fillMaxWidth()
      .defaultMinSize(minHeight = 40.dp)
      .onFocusChanged(onFocusChanged = { isFocused = it.isFocused })
      .onKeyEvent {
        if (it.nativeKeyEvent.keyCode == KeyEvent.KEYCODE_ENTER) {
          onSearchQueryClick()
        }
        true
      }
      .semantics {
        contentDescription = placeholderText
        customActions = if (isFocused && query.isNotEmpty()) {
          listOf(
            CustomAccessibilityAction(
              label = searchLabel,
              action = {
                onSearchQueryClick()
                true
              }
            ),
            CustomAccessibilityAction(
              label = clearSearchLabel,
              action = {
                onSearchQueryChanged("")
                true
              }
            )
          )
        } else {
          emptyList()
        }
      },
    shape = RoundedCornerShape(16.dp),
    value = query,
    onValueChange = { onSearchQueryChanged(it.fixQuery()) },
    placeholder = {
      Text(
        modifier = Modifier.clearAndSetSemantics { },
        text = if (query.isNotEmpty()) "" else placeholderText,
        style = AppTheme.typography.bodyCopySmall,
        color = gray6,
        overflow = TextOverflow.Ellipsis
      )
    },
    textStyle = AppTheme.typography.bodyCopySmall,
    singleLine = true,
    maxLines = 1,
    trailingIcon = {
      if (!isFocused) {
        IconButton(
          onClick = { onSearchQueryChanged("") },
        ) {
          Icon(
            imageVector = Icons.Default.Search,
            contentDescription = null,
            tint = gray5
          )
        }
      } else {
        if (query.isNotEmpty()) {
          IconButton(
            modifier = Modifier.semantics {
              onClick(label = clearSearchLabel) {
                false //Return false in order to perform the already defined onClick
              }
            },
            onClick = { onSearchQueryChanged("") }
          ) {
            Icon(
              imageVector = Icons.Default.Close,
              contentDescription = clearSearchLabel,
              tint = gray5
            )
          }
        }
      }
    },
    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
    keyboardActions = KeyboardActions(onSearch = { onSearchQueryClick() }),
    colors = TextFieldDefaults.outlinedTextFieldColors(
      backgroundColor = Color.Transparent,
      cursorColor = gray6,
      textColor = AppTheme.colors.searchBarTextColor,
      focusedBorderColor = gray4,
      unfocusedBorderColor = gray4
    )
  )
}

@Composable
fun SearchSuggestions(
  searchHistory: List<String>,
  popularSearch: List<String>,
  onSelectSearchSuggestion: (String, SearchType) -> Unit,
  onRemoveSuggestion: (String) -> Unit,
) {

  LazyColumn(
    modifier = Modifier.fillMaxSize(),
  ) {
    if (searchHistory.isNotEmpty()) {
      item {
        SearchSuggestionHeader(stringResource(id = R.string.search_recent_title))
      }
      items(searchHistory) { suggestion ->
        SearchHistoryItem(
          item = suggestion,
          onSelectSearchSuggestion = onSelectSearchSuggestion,
          onRemoveSuggestion = onRemoveSuggestion
        )
      }
    }
    if (popularSearch.isNotEmpty()) {
      item {
        SearchSuggestionHeader(
          stringResource(id = R.string.search_popular_title),
          if (searchHistory.isNotEmpty()) {
            Modifier.padding(top = 8.dp)
          } else {
            Modifier
          }
        )
      }
      items(popularSearch) { suggestion ->
        PopularSearchItem(
          item = suggestion,
          onSelectSearchSuggestion = onSelectSearchSuggestion
        )
      }
    }
  }
}

@Composable
fun AutoCompleteSearchSuggestions(
  suggestions: List<String>,
  popularSearch: List<String>,
  onSelectSearchSuggestion: (String, SearchType) -> Unit,
) {
  LazyColumn(
    modifier = Modifier.fillMaxSize()
  ) {
    if (suggestions.isNotEmpty()) {
      item {
        SearchSuggestionHeader(stringResource(id = R.string.search_suggested_title))
      }
      items(suggestions) { suggestion ->
        AutoCompleteSearchSuggestionItem(item = suggestion, onSelectSearchSuggestion)
      }
    }
    if (popularSearch.isNotEmpty()) {
      item {
        SearchSuggestionHeader(
          title = stringResource(id = R.string.search_popular_title),
          modifier = if (suggestions.isNotEmpty()) {
            Modifier.padding(top = 8.dp)
          } else {
            Modifier
          }
        )
      }
      items(popularSearch) { suggestion ->
        PopularSearchItem(
          item = suggestion,
          onSelectSearchSuggestion = onSelectSearchSuggestion
        )
      }
    }
  }
}

@Composable
fun SearchSuggestionHeader(
  title: String,
  modifier: Modifier = Modifier,
) {
  Text(
    modifier = modifier
      .padding(start = 16.dp, end = 16.dp, bottom = 10.dp)
      .semantics { },
    text = title,
    style = AppTheme.typography.headlineTitleTextSecondary,
    color = AppTheme.colors.searchSuggestionHeaderTextColor
  )
}

@Composable
fun PopularSearchItem(
  item: String,
  onSelectSearchSuggestion: (String, SearchType) -> Unit,
) {
  Row(
    modifier = Modifier
      .clickable(
        onClick = { onSelectSearchSuggestion(item, SearchType.POPULAR) },
        onClickLabel = stringResource(R.string.search_item_talkback)
      )
      .minimumInteractiveComponentSize()
      .fillMaxWidth()
      .padding(horizontal = 16.dp),
    verticalAlignment = Companion.CenterVertically
  ) {
    Image(
      modifier = Modifier
        .size(24.dp, 24.dp)
        .wrapContentHeight(Companion.CenterVertically),
      imageVector = AppTheme.icons.PlanetSearch,
      contentDescription = null
    )
    Text(
      modifier = Modifier
        .padding(start = 8.dp)
        .weight(1f)
        .wrapContentHeight(Companion.CenterVertically),
      text = item,
      maxLines = 1,
      overflow = TextOverflow.Ellipsis,
      style = AppTheme.typography.bodyCopyXS,
    )
  }
}

@Composable
fun SearchHistoryItem(
  item: String,
  onSelectSearchSuggestion: (String, SearchType) -> Unit,
  onRemoveSuggestion: (String) -> Unit,
) {
  val removeSearchLabel = stringResource(R.string.search_remove_from_recent_talkback)

  Row(
    modifier = Modifier
      .clickable(
        onClick = { onSelectSearchSuggestion(item, SearchType.RECENT) },
        onClickLabel = stringResource(R.string.search_item_talkback)
      )
      .minimumInteractiveComponentSize()
      .fillMaxWidth(),
    verticalAlignment = Companion.CenterVertically
  ) {
    Image(
      modifier = Modifier
        .padding(start = 16.dp)
        .size(24.dp, 24.dp)
        .wrapContentHeight(Companion.CenterVertically),
      imageVector = AppTheme.icons.HistoryOutlined,
      contentDescription = null
    )
    Text(
      modifier = Modifier
        .padding(start = 8.dp)
        .weight(1f)
        .wrapContentHeight(Companion.CenterVertically),
      text = item,
      maxLines = 1,
      overflow = TextOverflow.Ellipsis,
      style = AppTheme.typography.bodyCopyXS,
    )
    Icon(
      imageVector = Icons.Default.Close,
      tint = gray4,
      contentDescription = null,
      modifier = Modifier
        .semantics {
          role = Role.Button
          contentDescription = removeSearchLabel
        }
        .padding(start = 8.dp, end = 6.dp)
        .clickable(onClick = { onRemoveSuggestion(item) })
        .minimumInteractiveComponentSize()
        .size(12.dp)
    )
  }
}

@Composable
fun AutoCompleteSearchSuggestionItem(
  item: String,
  onSelectSearchSuggestion: (String, SearchType) -> Unit,
) {
  Row(
    modifier = Modifier
      .clickable(
        onClick = { onSelectSearchSuggestion(item, SearchType.AUTO_COMPLETE) },
        onClickLabel = stringResource(string.search_item_talkback)
      )
      .minimumInteractiveComponentSize()
      .fillMaxWidth()
      .padding(horizontal = 16.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Image(
      modifier = Modifier
        .size(24.dp, 24.dp)
        .wrapContentHeight(Alignment.CenterVertically),
      imageVector = AppTheme.icons.AutoCompleteSuggestion,
      contentDescription = null
    )
    Text(
      modifier = Modifier
        .padding(start = 8.dp),
      text = item,
      maxLines = 1,
      overflow = TextOverflow.Ellipsis,
      style = AppTheme.typography.bodyCopyXS,
    )
  }
}

@Composable fun SearchResultsView(
  searchResults: List<App>,
  searchValue: String,
  onItemClick: (App) -> Unit,
) {
  LazyColumn(
    modifier = Modifier.padding(start = 16.dp, end = 16.dp),
  ) {
    itemsIndexed(
      items = searchResults,
    ) { index, app ->
      val installViewShort: @Composable () -> Unit = {
        InstallViewShort() //TODO onItemInstallStarted - InstallButton
      }
      if (index == 0 && app.name.lowercase() == searchValue.lowercase()) {
        LargeAppItem(
          app = app,
          onClick = { onItemClick(app) }
        ) {
          installViewShort()
        }
      } else {
        AppItem(
          app = app,
          onClick = { onItemClick(app) },
        ) {
          installViewShort()
        }
      }
    }
  }
}