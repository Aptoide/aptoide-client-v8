package cm.aptoide.pt.device_api.paging

/**
 * One page of a cursor-paginated device-API list. [nextCursor] is the opaque
 * server token for the following page (null ⇒ last page). Cursors are minted by
 * the server — never parse or fabricate them; a foreign/expired cursor is a 4xx,
 * restart from page one.
 */
data class Page<T>(
  val items: List<T>,
  val nextCursor: String?,
) {
  val hasMore: Boolean get() = nextCursor != null

  fun <R> map(transform: (T) -> R): Page<R> = Page(items.map(transform), nextCursor)

  companion object {
    /** A terminal single page (used by the v7 default impls that don't paginate). */
    fun <T> terminal(items: List<T>): Page<T> = Page(items, nextCursor = null)
  }
}
