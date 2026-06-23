package com.aptoide.android.aptoidegames.editorial

import cm.aptoide.pt.feature_editorial.data.EditorialRepository
import cm.aptoide.pt.feature_editorial.domain.Article
import cm.aptoide.pt.feature_editorial.domain.ArticleMeta

internal class AGEditorialRepository(
  private val editorialRepository: EditorialRepository,
) : EditorialRepository {

  override suspend fun getLatestArticle(): List<ArticleMeta> =
    editorialRepository.getLatestArticle().filterOutAppArena()

  override suspend fun getArticle(editorialUrl: String): Article =
    editorialRepository.getArticle(editorialUrl)

  override suspend fun getArticlesMeta(
    editorialWidgetUrl: String,
    subtype: String?,
  ): List<ArticleMeta> =
    editorialRepository.getArticlesMeta(editorialWidgetUrl, subtype).filterOutAppArena()

  override suspend fun getRelatedArticlesMeta(packageName: String): List<ArticleMeta> =
    editorialRepository.getRelatedArticlesMeta(packageName).filterOutAppArena()
}
