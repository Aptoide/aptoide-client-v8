package com.aptoide.android.aptoidegames.editorial

import cm.aptoide.pt.feature_editorial.domain.ArticleMeta

private const val APPARENA_MESSAGE_PREFIX = "apparena-"

internal fun List<ArticleMeta>.filterOutAppArena(): List<ArticleMeta> =
  filterNot { it.caption.startsWith(APPARENA_MESSAGE_PREFIX) }
