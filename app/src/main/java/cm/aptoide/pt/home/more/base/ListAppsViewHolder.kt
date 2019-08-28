package cm.aptoide.pt.home.more.base

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import cm.aptoide.pt.view.app.Application

abstract class ListAppsViewHolder<in T : Application>(v: View) : RecyclerView.ViewHolder(v) {
  abstract fun bindApp(app: T)
}