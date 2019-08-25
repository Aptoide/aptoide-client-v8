package cm.aptoide.pt.home.more.appcoins

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cm.aptoide.pt.R
import cm.aptoide.pt.home.bundles.apps.RewardApp
import cm.aptoide.pt.home.more.base.ListAppsFragment
import java.text.DecimalFormat
import javax.inject.Inject

class EarnAppcListFragment : ListAppsFragment<RewardApp, EarnAppcListViewHolder>(),
    EarnAppcListView {

  @Inject
  lateinit var presenter: EarnAppcListPresenter

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    getFragmentComponent(savedInstanceState).inject(this)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    presenter.present()
  }


  override fun getItemSizeWidth(): Int {
    return 168
  }

  override fun getItemSizeHeight(): Int {
    return 158
  }

  override fun createViewHolder(): (ViewGroup, Int) -> EarnAppcListViewHolder {
    return { parent, viewType ->
      EarnAppcListViewHolder(
          LayoutInflater.from(parent.context).inflate(R.layout.earn_appcoins_item, parent,
              false), DecimalFormat("0.00"))
    }
  }

}