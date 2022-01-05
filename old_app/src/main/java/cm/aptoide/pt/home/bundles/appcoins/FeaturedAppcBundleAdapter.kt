package cm.aptoide.pt.home.bundles.appcoins

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import cm.aptoide.pt.R
import cm.aptoide.pt.home.bundles.apps.AppInBundleViewHolder
import cm.aptoide.pt.home.bundles.base.HomeBundle
import cm.aptoide.pt.home.bundles.base.HomeEvent
import cm.aptoide.pt.view.app.AppViewHolder
import cm.aptoide.pt.view.app.Application
import rx.subjects.PublishSubject
import java.text.DecimalFormat

class FeaturedAppcBundleAdapter(var apps: List<Application>,
                                val oneDecimalFormatter: DecimalFormat,
                                val appClickedEvents: PublishSubject<HomeEvent>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

  private var homeBundle: HomeBundle? = null
  private var bundlePosition: Int = -1

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
    return AppInBundleViewHolder(LayoutInflater.from(parent.context)
        .inflate(R.layout.bonus_appc_home_item, parent, false), appClickedEvents,
        oneDecimalFormatter)
  }

  override fun getItemCount(): Int {
    return apps.size
  }

  override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
    (holder as AppViewHolder).setApp(apps[position], homeBundle, bundlePosition)
  }

  fun update(apps: List<Application>) {
    this.apps = apps
    notifyDataSetChanged()
  }

  fun updateBundle(homeBundle: HomeBundle, position: Int) {
    this.homeBundle = homeBundle
    this.bundlePosition = position
  }
}