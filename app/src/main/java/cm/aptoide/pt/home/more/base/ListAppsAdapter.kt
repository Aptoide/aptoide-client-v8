package cm.aptoide.pt.home.more.base

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import cm.aptoide.pt.view.app.Application
import rx.subjects.PublishSubject

class ListAppsAdapter<T : Application, V : ListAppsViewHolder<T>>(
    val viewHolderBuilder: (ViewGroup, Int) -> V) : RecyclerView.Adapter<V>() {

  private var clickListener = PublishSubject.create<ListAppsClickEvent<T>>()
  private var appList: ArrayList<T> = ArrayList()

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): V {
    val vh = viewHolderBuilder(parent, viewType)
    vh.itemView.setOnClickListener {
      clickListener.onNext(
          ListAppsClickEvent(appList[vh.adapterPosition],
              vh.adapterPosition))
    }
    return vh
  }

  override fun onBindViewHolder(holder: V, position: Int) {
    holder.bindApp(appList[position])
  }

  override fun getItemCount(): Int {
    return appList.size
  }

  fun setData(objs: List<T>) {
    appList = ArrayList(objs)
    // Ideally this would use DiffUtil instead
    notifyDataSetChanged()
  }

  fun getClickListener(): PublishSubject<ListAppsClickEvent<T>> {
    return clickListener
  }

  fun addData(apps: List<T>) {
    val adapterSize = appList.size
    appList.addAll(apps)
    notifyItemRangeInserted(adapterSize, apps.size)
  }
}