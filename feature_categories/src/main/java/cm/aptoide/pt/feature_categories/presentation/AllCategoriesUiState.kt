package cm.aptoide.pt.feature_categories.presentation

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import cm.aptoide.pt.feature_categories.domain.Category
import cm.aptoide.pt.feature_categories.domain.randomCategory
import kotlin.random.Random
import kotlin.random.nextInt

sealed class AllCategoriesUiState {
  data class Idle(val categories: List<Category>) : AllCategoriesUiState()
  object Loading : AllCategoriesUiState()
  object NoConnection : AllCategoriesUiState()
  object Error : AllCategoriesUiState()
}

class AllCategoriesUiStateProvider : PreviewParameterProvider<AllCategoriesUiState> {
  override val values: Sequence<AllCategoriesUiState> = sequenceOf(
    AllCategoriesUiState.Idle(List(Random.nextInt(1..20)) { randomCategory }),
    AllCategoriesUiState.Loading,
    AllCategoriesUiState.NoConnection,
    AllCategoriesUiState.Error
  )
}
