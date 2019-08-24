package cm.aptoide.pt.home.more.base

import android.graphics.Rect
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.*
import cm.aptoide.analytics.implementation.navigation.ScreenTagHistory
import cm.aptoide.aptoideviews.errors.ErrorView
import cm.aptoide.pt.R
import cm.aptoide.pt.dataprovider.ws.v7.store.StoreContext
import cm.aptoide.pt.store.view.StoreTabGridRecyclerFragment
import cm.aptoide.pt.utils.AptoideUtils
import cm.aptoide.pt.view.Translator
import cm.aptoide.pt.view.app.Application
import cm.aptoide.pt.view.fragment.NavigationTrackFragment
import com.jakewharton.rxbinding.support.v4.widget.RxSwipeRefreshLayout
import kotlinx.android.synthetic.main.fragment_list_apps.*
import kotlinx.android.synthetic.main.partial_view_progress_bar.*
import kotlinx.android.synthetic.main.toolbar.*
import rx.Observable
import rx.subjects.PublishSubject

/**
 * This fragment is responsible for drawing a grid list of apps. It's the equivalent of the old
 * ListAppsFragment but with the current architecture.
 *
 * You can implement it by extending this class ([ListAppsFragment]) and specifying how the
 * viewholder(s) is(are) built on [createViewHolder]. The ViewHolder(s) must extend
 * [ListAppsViewHolder]. You can interact with this fragment by implementing the [ListAppsView]
 * interface.
 *
 * You can also customize how the items are laid out in the grid, as well as spacing by overriding
 * [getSpanCount], [getItemSpacingDp], [getContainerPaddingDp]
 *
 * Note: If you're intending to use a Presenter, you can also extend [ListAppsPresenter] to help you
 * avoid implementing common functionalities.
 *
 * @param <T: Application> The type of application that represents an item, see [Application]
 * @param <V: ListAppsViewHolder<T>> The ViewHolder that represents the item, see [ListAppsViewHolder]
 */
abstract class ListAppsFragment<T : Application, V : ListAppsViewHolder<T>> :
    NavigationTrackFragment(), ListAppsView<T> {

  protected lateinit var adapter: ListAppsAdapter<T, V>

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setHasOptionsMenu(true)
    adapter = ListAppsAdapter(createViewHolder())
  }

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                            savedInstanceState: Bundle?): View? {
    return inflater.inflate(R.layout.fragment_list_apps, container, false)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    apps_list.addItemDecoration(object : RecyclerView.ItemDecoration() {
      override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView,
                                  state: RecyclerView.State?) {
        val marginBetweenItems = getPixels(getItemSpacingDp())

        val position = parent.getChildAdapterPosition(view)
        val row: Int = position / getSpanCount()

        val marginLeft = if (position % getSpanCount() != 0) marginBetweenItems else 0
        val marginTop = if (row != 0) marginBetweenItems else 0

        outRect.set(marginLeft, marginTop, 0, 0)
      }
    })
    apps_list.layoutManager = GridLayoutManager(view.context, getSpanCount())
    apps_list.adapter = adapter
    swipe_container.setColorSchemeResources(R.color.default_progress_bar_color,
        R.color.default_color, R.color.default_progress_bar_color, R.color.default_color)

    val padding = getPixels(getContainerPaddingDp())
    apps_list.setPadding(padding.left, padding.top, padding.right, padding.bottom)

    adapter.setItemFillWidth(shouldItemFillWidth())
    adapter.setItemRatioSize(getItemSizeRatio())

    setupToolbar()
  }

  override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
    super.onCreateOptionsMenu(menu, inflater)
    inflater.inflate(R.menu.menu_empty, menu)
  }

  /**
   * Specifies what is the span (column) count of the list
   *
   * By default the span count is 3. You may override this.
   */
  protected open fun getSpanCount(): Int {
    return 3
  }

  /**
   * Specifies what the spacing between items is.
   * Note that this does not apply spacing to the container edges, for that see [getContainerPaddingDp]
   *
   * By default the item spacing is 8 dp. You may override this.
   */
  protected open fun getItemSpacingDp(): Int {
    return 8
  }

  /**
   * Specifies the padding of the list container.
   *
   * By default the padding is 8 dp in every direction. You may override this.
   */
  protected open fun getContainerPaddingDp(): Rect {
    return Rect(8, 8, 8, 8)
  }

  /**
   * Specifies what is the desired ratio size of the item viewholder (width:height). The ratio must
   * be bigger than 0, or it will be ignored.
   *
   * This only has any effect if [shouldItemFillWidth] is set to true.
   *
   * By default this is set to 0, which means there's no ratio applied. You may override this.
   */
  protected open fun getItemSizeRatio(): Double {
    return 0.0
  }

  /**
   * Specifies that the item viewholder should fill the available space width wise.
   * This is especially useful to make sure the margins are consistent independently of the screen
   * size.
   *
   * Note that this will cause the original viewholder size to be disregarded.
   *
   * By default this is set to false. You may override this
   */
  protected open fun shouldItemFillWidth(): Boolean {
    return false
  }

  /**
   * Specifies how the viewholder for this list is built
   */
  abstract fun createViewHolder(): (ViewGroup, Int) -> V

  override fun getHistoryTracker(): ScreenTagHistory? {
    return ScreenTagHistory.Builder.build(javaClass.simpleName, "", StoreContext.home.name)
  }

  private fun setupToolbar() {
    val appCompatActivity = activity as AppCompatActivity
    if (arguments?.getBoolean(StoreTabGridRecyclerFragment.BundleCons.TOOLBAR, true) == true) {
      appCompatActivity.setSupportActionBar(toolbar)
      appCompatActivity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
    } else {
      action_bar.visibility = View.GONE
    }
  }

  override fun setToolbarInfo(title: String) {
    toolbar.title = Translator.translate(title, context, "")
    toolbar.setLogo(R.drawable.logo_toolbar)
  }

  override fun showLoading() {
    apps_list.visibility = View.GONE
    error_view.visibility = View.GONE
    progress_bar.visibility = View.VISIBLE
  }

  override fun showApps(apps: List<T>) {
    showResultsVisibility()
    adapter.setData(apps)
    apps_list.scheduleLayoutAnimation()
  }

  override fun addApps(apps: List<T>) {
    showResultsVisibility()
    adapter.addData(apps)
  }

  override fun showGenericError() {
    error_view.setError(ErrorView.Error.GENERIC)
    showErrorVisibility()
  }

  override fun showNoNetworkError() {
    error_view.setError(ErrorView.Error.NO_NETWORK)
    showErrorVisibility()
  }

  private fun showErrorVisibility() {
    error_view.visibility = View.VISIBLE
    apps_list.visibility = View.GONE
    progress_bar.visibility = View.GONE
    swipe_container.isRefreshing = false
  }

  private fun showResultsVisibility() {
    apps_list.visibility = View.VISIBLE
    error_view.visibility = View.GONE
    progress_bar.visibility = View.GONE
    swipe_container.isRefreshing = false
  }

  override fun refreshEvents(): Observable<Void> {
    return RxSwipeRefreshLayout.refreshes(swipe_container)
  }

  override fun errorRetryClick(): Observable<Void> {
    return error_view.retryClick()
  }

  override fun getItemClickEvents(): PublishSubject<ListAppsClickEvent<T>> {
    return adapter.getClickListener()
  }

  private fun getPixels(dp: Int): Int {
    return AptoideUtils.ScreenU.getPixelsForDip(dp, view?.resources)
  }

  private fun getPixels(dp: Rect): Rect {
    return Rect(getPixels(dp.left), getPixels(dp.top), getPixels(dp.right), getPixels(dp.bottom))
  }

}

