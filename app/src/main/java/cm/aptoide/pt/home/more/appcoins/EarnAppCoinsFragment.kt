package cm.aptoide.pt.home.more.appcoins

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cm.aptoide.pt.R
import cm.aptoide.pt.home.bundles.apps.RewardApp
import cm.aptoide.pt.home.more.ListAppsEvent
import cm.aptoide.pt.home.more.ListAppsFragment
import rx.Observable
import rx.subjects.PublishSubject
import java.text.DecimalFormat
import javax.inject.Inject

class EarnAppCoinsFragment : ListAppsFragment<RewardApp, EarnAppCoinsListAppViewHolder>(),
    EarnAppCoinsListAppsView {

  @Inject
  lateinit var presenter: EarnAppCoinsListPresenter

  private val uiEventsListener = PublishSubject.create<ListAppsEvent<RewardApp>>()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    getFragmentComponent(savedInstanceState).inject(this)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    presenter.present()
  }

  override fun getSpanCount(): Int {
    return 2
  }

  override fun appClicked(): Observable<ListAppsEvent<RewardApp>> {
    return uiEventsListener
  }

  override fun buildViewHolder(): (ViewGroup, Int) -> EarnAppCoinsListAppViewHolder {
    return { parent, viewType ->
      EarnAppCoinsListAppViewHolder(
          LayoutInflater.from(parent.context).inflate(R.layout.earn_appcoins_more_item, parent,
              false), uiEventsListener, DecimalFormat("0.00"))
    }
  }

}