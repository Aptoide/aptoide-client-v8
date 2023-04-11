package cm.aptoide.pt.feature_categories.data.network.model

import androidx.annotation.Keep

@Keep
data class CategoryJson(
  val id: Long,
  val name: String,
  val title: String,
  val icon: String? = null,
  val graphic: String? = null,
  val background: String? = null,
  var added: String? = null,
  var modified: String? = null,
  val parent: Parent? = null,
  val foreign: Foreign? = null,
  val stats: Stats? = null
)

@Keep
data class Parent(
  val id: Long,
  val name: String,
  val title: String,
  val icon: String? = null,
  val graphic: String? = null,
  val background: String? = null,
)

@Keep
data class Foreign(
  val id: Long,
  val name: String,
  val title: String
)

@Keep
data class Stats(
  val groups: Int,
  val items: Int
)
