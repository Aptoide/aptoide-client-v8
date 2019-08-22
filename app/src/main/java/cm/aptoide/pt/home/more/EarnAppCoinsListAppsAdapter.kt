package cm.aptoide.pt.home.more

import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import cm.aptoide.pt.R
import cm.aptoide.pt.home.bundles.apps.RewardApp
import rx.subjects.PublishSubject
import java.text.DecimalFormat

class EarnAppCoinsListAppsAdapter(private val decimalFormatter: DecimalFormat,
                                  private val appClickedEvents: PublishSubject<ListAppsEvent>) :
    RecyclerView.Adapter<EarnAppCoinsListAppViewHolder>() {


  private var appList: List<RewardApp> = emptyList()

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EarnAppCoinsListAppViewHolder {
    val view = LayoutInflater.from(parent.context)
        .inflate(R.layout.earn_appcoins_item, parent, false)
//    view.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, view.measuredHeight)
    return EarnAppCoinsListAppViewHolder(view, appClickedEvents, decimalFormatter)
  }

  override fun onBindViewHolder(holder: EarnAppCoinsListAppViewHolder, position: Int) {
    holder.setApp(appList[position])
  }

  override fun getItemCount(): Int {
    return appList.size
  }

  fun updateBundle(apps: List<RewardApp>) {
    appList = apps
  }

}