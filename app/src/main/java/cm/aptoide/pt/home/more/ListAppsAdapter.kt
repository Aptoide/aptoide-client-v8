package cm.aptoide.pt.home.more

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import cm.aptoide.pt.view.app.Application

class ListAppsAdapter<T : Application, V : ListAppsViewHolder<T>>(
    val viewHolderBuilder: (ViewGroup, Int) -> V) : RecyclerView.Adapter<V>() {

  private var appList: ArrayList<T> = arrayListOf()

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): V {
    return viewHolderBuilder(parent, viewType)
  }

  override fun onBindViewHolder(holder: V, position: Int) {
    holder.bindApp(appList[position])
  }

  override fun getItemCount(): Int {
    return appList.size
  }

  fun addToAdapter(objs: List<T>) {
    appList.addAll(objs)
  }
}