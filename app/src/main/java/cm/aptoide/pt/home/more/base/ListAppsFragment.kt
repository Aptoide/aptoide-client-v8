package cm.aptoide.pt.home.more.base

import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.Dimension
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.Group
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.GridLayoutManager
import cm.aptoide.analytics.implementation.navigation.ScreenTagHistory
import cm.aptoide.aptoideviews.errors.ErrorView
import cm.aptoide.aptoideviews.recyclerview.GridRecyclerView
import cm.aptoide.pt.R
import cm.aptoide.pt.dataprovider.ws.v7.store.StoreContext
import cm.aptoide.pt.store.view.StoreTabGridRecyclerFragment
import cm.aptoide.pt.utils.AptoideUtils
import cm.aptoide.pt.view.Translator
import cm.aptoide.pt.view.app.Application
import cm.aptoide.pt.view.fragment.NavigationTrackFragment
import com.jakewharton.rxbinding.support.v4.widget.RxSwipeRefreshLayout
import com.jakewharton.rxbinding.support.v7.widget.RxRecyclerView
import kotlinx.android.synthetic.main.fragment_list_apps.action_bar
import kotlinx.android.synthetic.main.fragment_list_apps.apps_list
import kotlinx.android.synthetic.main.fragment_list_apps.bundle_description
import kotlinx.android.synthetic.main.fragment_list_apps.bundle_header_group
import kotlinx.android.synthetic.main.fragment_list_apps.bundle_image
import kotlinx.android.synthetic.main.fragment_list_apps.bundle_title_1
import kotlinx.android.synthetic.main.fragment_list_apps.bundle_title_2
import kotlinx.android.synthetic.main.fragment_list_apps.error_view
import kotlinx.android.synthetic.main.fragment_list_apps.eskills_title
import kotlinx.android.synthetic.main.fragment_list_apps.swipe_container
import kotlinx.android.synthetic.main.partial_view_progress_bar.progress_bar
import kotlinx.android.synthetic.main.toolbar.toolbar
import rx.Observable
import rx.subjects.PublishSubject

/**
 * This fragment is responsible for drawing a grid list of apps. It's the equivalent of the old
 * ListAppsFragment but with the current architecture.
 *
 * You can implement it by extending this class ([ListAppsFragment]) and specifying how the
 * ViewHolder(s) is(are) built on [createViewHolder]. The ViewHolder(s) must extend
 * [ListAppsViewHolder]. You can interact with this fragment by implementing the [ListAppsView]
 * interface.
 *
 * This fragment uses [GridRecyclerView] with an adaptive grid layout. This means that it will
 * adjust the number of items per row, padding and item size automatically. However, you still need
 * to specify the target ViewHolder size with [getItemSizeWidth] and [getItemSizeHeight]. You can
 * also customize preferential padding and item spacing by overriding [getItemSpacingDp] and
 * [getContainerPaddingDp]. Also note that this adaptive grid layout only supports ViewHolders of
 * the same size.
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
  private lateinit var headerClickListener: PublishSubject<Void>

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

    apps_list.layoutManager = GridLayoutManager(view.context, 3)
    apps_list.setAdaptiveLayout(getItemSizeWidth(), getItemSizeHeight(),
        getAdapterStrategy())
    apps_list.setIntendedItemSpacing(getItemSpacingDp())
    val padding = getPixels(getContainerPaddingDp())
    apps_list.setPadding(padding.left, padding.top, padding.right, padding.bottom)
    apps_list.adapter = adapter
    setupHeaderListener()
    setupToolbar()
  }

  open fun setupHeaderListener() {
    headerClickListener = PublishSubject.create()
    bundle_header_group.setAllOnClickListener(View.OnClickListener { headerClickListener.onNext(null) })
  }

  override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
    super.onCreateOptionsMenu(menu, inflater)
    inflater.inflate(R.menu.menu_empty, menu)
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
   * Specifies what is the target width of the item viewholder
   */
  @Dimension(unit = Dimension.DP)
  abstract fun getItemSizeWidth(): Int

  /**
   * Specifies what is the target height of the item viewholder
   */
  @Dimension(unit = Dimension.DP)
  abstract fun getItemSizeHeight(): Int


  abstract fun getAdapterStrategy(): GridRecyclerView.AdaptStrategy

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

  override fun setupEskillsView() {
    toolbar.logo = null
    toolbar.setBackgroundColor(ResourcesCompat.getColor(resources, R.color.grey_900, null))
    swipe_container?.setBackgroundColor(ResourcesCompat.getColor(resources, R.color.grey_dark, null))
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

  override fun showHeader() {
    bundle_image.visibility = View.VISIBLE
    bundle_title_1.visibility = View.VISIBLE
    bundle_title_2.visibility = View.VISIBLE
    bundle_description.visibility = View.VISIBLE
    eskills_title.visibility = View.VISIBLE
  }

  override fun headerClicks(): Observable<Void> {
    return headerClickListener
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

  open fun showErrorVisibility() {
    error_view.visibility = View.VISIBLE
    apps_list.visibility = View.GONE
    progress_bar.visibility = View.GONE
    swipe_container.isRefreshing = false
  }

  open fun showResultsVisibility() {
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

  override fun onBottomReached(): Observable<Void> {
    return RxRecyclerView.scrollEvents(apps_list)
        .map { apps_list.isEndReached(2) }
        .distinctUntilChanged()
        .filter { isEnd -> isEnd }.map { null }
  }

  private fun getPixels(dp: Int): Int {
    return AptoideUtils.ScreenU.getPixelsForDip(dp, view?.resources)
  }

  private fun getPixels(dp: Rect): Rect {
    return Rect(getPixels(dp.left), getPixels(dp.top), getPixels(dp.right), getPixels(dp.bottom))
  }

}

fun Group.setAllOnClickListener(listener: View.OnClickListener?) {
  referencedIds.forEach { id ->
    rootView.findViewById<View>(id).setOnClickListener(listener)
  }
}
