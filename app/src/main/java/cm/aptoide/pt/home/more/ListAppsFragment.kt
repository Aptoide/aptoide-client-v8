package cm.aptoide.pt.home.more

import android.graphics.Rect
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.*
import cm.aptoide.analytics.implementation.navigation.ScreenTagHistory
import cm.aptoide.pt.R
import cm.aptoide.pt.dataprovider.ws.v7.store.StoreContext
import cm.aptoide.pt.store.view.StoreTabGridRecyclerFragment
import cm.aptoide.pt.utils.AptoideUtils
import cm.aptoide.pt.view.Translator
import cm.aptoide.pt.view.fragment.NavigationTrackFragment
import kotlinx.android.synthetic.main.fragment_list_apps.*
import kotlinx.android.synthetic.main.partial_view_progress_bar.*
import kotlinx.android.synthetic.main.toolbar.*

abstract class ListAppsFragment<T, V : RecyclerView.ViewHolder> : NavigationTrackFragment(),
    ListAppsView<T> {

  protected var adapter: ListAppsAdapter<T, V>? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setHasOptionsMenu(true)
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

    setupToolbar()
  }

  override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
    super.onCreateOptionsMenu(menu, inflater)
    inflater.inflate(R.menu.menu_empty, menu)
  }

  override fun getHistoryTracker(): ScreenTagHistory? {
    return ScreenTagHistory.Builder.build(this.javaClass
        .simpleName, "", StoreContext.home.name)
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
    adapter?.apply {
      apps_list.visibility = View.VISIBLE
      error_view.visibility = View.GONE
      progress_bar.visibility = View.GONE
      addToAdapter(apps)
    }
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
   * Specifies what is the spacing between items.
   * Note that this does not apply spacing to the container, for that see [getContainerPaddingDp]
   *
   * By default the span count is 8 dp. You may override this.
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

  private fun getPixels(dp: Int): Int {
    return AptoideUtils.ScreenU.getPixelsForDip(dp, view?.resources)
  }

  private fun getPixels(dp: Rect): Rect {
    return Rect(getPixels(dp.left), getPixels(dp.top), getPixels(dp.right), getPixels(dp.bottom))
  }

}

