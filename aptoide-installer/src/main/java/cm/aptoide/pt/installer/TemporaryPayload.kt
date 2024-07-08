package cm.aptoide.pt.installer

import com.google.gson.Gson

data class TemporaryPayload(
  val isInCatappult: Boolean?,
  val isAab: Boolean,
  val hasObb: Boolean,
  val trustedBadge: String?,
) {
  override fun toString(): String = Gson().toJson(this)

  companion object {
    fun String.fromString(): TemporaryPayload =
      Gson().fromJson(this, TemporaryPayload::class.java)
  }
}
