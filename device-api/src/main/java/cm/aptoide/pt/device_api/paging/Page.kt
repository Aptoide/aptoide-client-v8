package cm.aptoide.pt.device_api.paging

/**
 * One page of a cursor-paginated device API list. [nextCursor] is the opaque server token for
 * the following page, null on the last one. Cursors are tied to the sort they were issued
 * for and must never be parsed or built by the client.
 */
data class Page<T>(
  val items: List<T>,
  val nextCursor: String?,
) {
  val hasMore: Boolean get() = nextCursor != null

  fun <R> map(transform: (T) -> R): Page<R> = Page(items.map(transform), nextCursor)
}
