package cm.aptoide.pt.home.bundles.appcoins

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import cm.aptoide.pt.R
import cm.aptoide.pt.home.bundles.apps.RewardApp
import cm.aptoide.pt.home.bundles.base.AppBundle
import cm.aptoide.pt.home.bundles.base.HomeBundle
import cm.aptoide.pt.home.bundles.base.HomeEvent
import rx.subjects.PublishSubject
import java.text.DecimalFormat

class EarnAppCoinsListAdapter(private val decimalFormatter: DecimalFormat,
                              private val appClickedEvents: PublishSubject<HomeEvent>) :
    RecyclerView.Adapter<EarnAppCoinsItemViewHolder>() {


  private var appBundle: AppBundle? = null
  private var bundlePosition: Int = -1

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EarnAppCoinsItemViewHolder {
    return EarnAppCoinsItemViewHolder(LayoutInflater.from(parent.context)
        .inflate(R.layout.earn_appcoins_item, parent, false), appClickedEvents, decimalFormatter)
  }

  override fun onBindViewHolder(holder: EarnAppCoinsItemViewHolder, position: Int) {
    holder.setApp(appBundle!!.apps!![position] as RewardApp, appBundle,
        bundlePosition)
  }

  override fun getItemCount(): Int {
    return appBundle?.apps?.size ?: 0
  }

  fun updateBundle(bundle: AppBundle, position: Int) {
    if (bundle.type != HomeBundle.BundleType.APPCOINS_ADS)
      throw IllegalArgumentException("Wrong bundle type")

    appBundle = bundle
    bundlePosition = position
  }


}

