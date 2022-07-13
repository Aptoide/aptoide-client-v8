package cm.aptoide.pt.feature_editorial.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class AptoideEditorialRepository : EditorialRepository {
  override fun getLatestArticle(): Flow<EditorialRepository.EditorialResult> = flow {
    emit(EditorialRepository.EditorialResult.Success(Article("Teste - Editorial",
      ArticleType.GAME_OF_THE_WEEK,
      "Artigo do mês jogo da semana para os melhores jogadores do mundo a jogar League of Legends Wild Rift.",
      "")))
  }
}