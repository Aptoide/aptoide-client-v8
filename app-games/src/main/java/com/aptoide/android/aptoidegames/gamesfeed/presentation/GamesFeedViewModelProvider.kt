package com.aptoide.android.aptoidegames.gamesfeed.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import kotlin.reflect.KFunction0

@Composable
fun rememberGamesFeedViewModel(): Pair<GamesFeedUiState, KFunction0<Unit>> {
  val viewModel = hiltViewModel<GamesFeedViewModel>()
  val uiState by viewModel.uiState.collectAsState()
  return uiState to viewModel::loadGamesFeed
}

@Composable
fun rememberGamesFeedVisibility(): Boolean? {
  val vm = hiltViewModel<GamesFeedVisibilityViewModel>()
  val uiState by vm.shouldShowGamesFeed.collectAsState()
  return uiState
}
