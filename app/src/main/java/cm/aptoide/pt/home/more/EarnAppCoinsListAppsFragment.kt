package cm.aptoide.pt.home.more

import android.graphics.Rect
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cm.aptoide.analytics.implementation.navigation.ScreenTagHistory
import cm.aptoide.pt.R
import cm.aptoide.pt.dataprovider.ws.v7.store.StoreContext
import cm.aptoide.pt.home.bundles.apps.RewardApp
import cm.aptoide.pt.store.view.StoreTabGridRecyclerFragment
import cm.aptoide.pt.utils.AptoideUtils
import cm.aptoide.pt.view.Translator
import cm.aptoide.pt.view.fragment.NavigationTrackFragment
import kotlinx.android.synthetic.main.bundle_earn_appcoins.view.*
import kotlinx.android.synthetic.main.fragment_list_apps.*
import kotlinx.android.synthetic.main.partial_view_progress_bar.*
import kotlinx.android.synthetic.main.toolbar.*
import rx.subjects.PublishSubject
import java.text.DecimalFormat
import javax.inject.Inject

class EarnAppCoinsListAppsFragment : NavigationTrackFragment(), EarnAppCoinsListAppsView {

  @Inject lateinit var presenter: EarnAppCoinsListPresenter

  private val uiEventsListener = PublishSubject.create<ListAppsEvent>()
  private val adapter = EarnAppCoinsListAppsAdapter(DecimalFormat("0.00"), uiEventsListener)

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    getFragmentComponent(savedInstanceState).inject(this)

    apps_list.addItemDecoration(object : RecyclerView.ItemDecoration() {
      override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView,
                                  state: RecyclerView.State?) {
        val margin = AptoideUtils.ScreenU.getPixelsForDip(8, view.resources)
        val marginBottom = AptoideUtils.ScreenU.getPixelsForDip(4, view.resources)
        outRect.set(margin, margin, 0, marginBottom)
      }
    })
    apps_list.layoutManager = GridLayoutManager(view.context, 2)
    apps_list.adapter = adapter
    list_apps_swipe_refresh.setColorSchemeResources(R.color.default_progress_bar_color,
        R.color.default_color, R.color.default_progress_bar_color, R.color.default_color)
    setupToolbar()

    presenter.present()
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

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                            savedInstanceState: Bundle?): View? {
    return inflater.inflate(R.layout.fragment_list_apps, container, false)
  }

  override fun showLoading() {
    apps_list.visibility = View.GONE
    error_view.visibility = View.GONE
    progress_bar.visibility = View.VISIBLE
  }

  override fun showApps(apps: List<RewardApp>) {
    apps_list.visibility = View.VISIBLE
    error_view.visibility = View.GONE
    progress_bar.visibility = View.GONE
    adapter.updateBundle(apps)
  }
}