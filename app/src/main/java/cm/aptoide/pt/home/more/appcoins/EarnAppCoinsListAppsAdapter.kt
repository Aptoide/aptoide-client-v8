package cm.aptoide.pt.home.more.appcoins

import android.view.LayoutInflater
import android.view.ViewGroup
import cm.aptoide.pt.R
import cm.aptoide.pt.home.bundles.apps.RewardApp
import cm.aptoide.pt.home.more.ListAppsAdapter
import cm.aptoide.pt.home.more.ListAppsEvent
import rx.subjects.PublishSubject
import java.text.DecimalFormat

class EarnAppCoinsListAppsAdapter(private val decimalFormatter: DecimalFormat,
                                  private val appClickedEvents: PublishSubject<ListAppsEvent<RewardApp>>) :
    ListAppsAdapter<RewardApp, EarnAppCoinsListAppViewHolder>() {


  private var appList: ArrayList<RewardApp> = arrayListOf()

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EarnAppCoinsListAppViewHolder {
    return EarnAppCoinsListAppViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.earn_appcoins_more_item, parent,
            false),
        appClickedEvents, decimalFormatter)
  }

  override fun onBindViewHolder(holder: EarnAppCoinsListAppViewHolder, position: Int) {
    holder.setApp(appList[position])
  }

  override fun getItemCount(): Int {
    return appList.size
  }

  override fun addToAdapter(objs: List<RewardApp>) {
    appList.addAll(objs)
  }

}