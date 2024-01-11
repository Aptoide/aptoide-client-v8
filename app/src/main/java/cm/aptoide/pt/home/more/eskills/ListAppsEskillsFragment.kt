package cm.aptoide.pt.home.more.eskills

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cm.aptoide.pt.R
import cm.aptoide.pt.home.more.apps.ListAppsMoreFragment
import cm.aptoide.pt.home.more.base.ListAppsView
import cm.aptoide.pt.view.app.Application
import java.text.DecimalFormat
import javax.inject.Inject

class ListAppsEskillsFragment : ListAppsMoreFragment(),
    ListAppsView<Application> {

  @Inject
  lateinit var eSkillsPresenter: ListAppsEskillsPresenter

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    getFragmentComponent(savedInstanceState).inject(this)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    this.presenter = eSkillsPresenter
    super.onViewCreated(view, savedInstanceState)
  }

  override fun createViewHolder(): (ViewGroup, Int) -> ListAppsEskillsViewHolder {
    return { parent, _ ->
      ListAppsEskillsViewHolder(
          LayoutInflater.from(parent.context).inflate(R.layout.eskills_app_home_item, parent,
              false), DecimalFormat("0.0"))
    }
  }
}
