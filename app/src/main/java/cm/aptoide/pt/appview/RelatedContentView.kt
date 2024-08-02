package cm.aptoide.pt.appview

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cm.aptoide.pt.editorial.RelatedEditorialViewCard
import cm.aptoide.pt.feature_editorial.presentation.relatedEditorialsCardViewModel

@Composable
fun RelatedContentView(
  packageName: String,
  onRelatedContentClick: (String) -> Unit,

) {
  val editorialsMetaViewModel = relatedEditorialsCardViewModel(packageName = packageName)
  val uiState by editorialsMetaViewModel.uiState.collectAsState()
  Column(
    modifier = Modifier.padding(top = 24.dp)
  ) {
    uiState?.forEach { editorialMeta ->
      RelatedEditorialViewCard(editorialMeta, onRelatedContentClick)
    }
  }
}
