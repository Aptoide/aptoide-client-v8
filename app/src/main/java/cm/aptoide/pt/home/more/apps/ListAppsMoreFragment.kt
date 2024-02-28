package cm.aptoide.pt.home.more.apps

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cm.aptoide.aptoideviews.recyclerview.GridRecyclerView
import cm.aptoide.pt.R
import cm.aptoide.pt.home.more.base.ListAppsFragment
import cm.aptoide.pt.home.more.base.ListAppsView
import cm.aptoide.pt.view.app.Application
import java.text.DecimalFormat
import javax.inject.Inject

open class ListAppsMoreFragment : ListAppsFragment<Application, ListAppsMoreViewHolder>(),
    ListAppsView<Application> {

  @Inject
  lateinit var presenter: ListAppsMorePresenter

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    getFragmentComponent(savedInstanceState).inject(this)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    presenter.present()
  }

  override fun getItemSizeWidth(): Int {
    return 104
  }

  override fun getItemSizeHeight(): Int {
    return 158
  }

  override fun getAdapterStrategy(): GridRecyclerView.AdaptStrategy {
    return GridRecyclerView.AdaptStrategy.SCALE_KEEP_ASPECT_RATIO
  }

  override fun createViewHolder(): (ViewGroup, Int) -> ListAppsMoreViewHolder {
    return { parent, _ ->
      ListAppsMoreViewHolder(
          LayoutInflater.from(parent.context).inflate(R.layout.app_home_item, parent,
              false), DecimalFormat("0.0"))
    }
  }
}
