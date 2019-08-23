package cm.aptoide.pt.home.more

import android.support.v7.widget.RecyclerView
import android.view.View
import cm.aptoide.pt.view.app.Application

abstract class ListAppsViewHolder<in T : Application>(v: View) : RecyclerView.ViewHolder(v) {
  abstract fun bindApp(app: T)
}