package cm.aptoide.pt.home.more.base

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import android.view.ViewTreeObserver
import cm.aptoide.pt.view.app.Application
import rx.subjects.PublishSubject

class ListAppsAdapter<T : Application, V : ListAppsViewHolder<T>>(
    val viewHolderBuilder: (ViewGroup, Int) -> V) : RecyclerView.Adapter<V>() {

  private var clickListener = PublishSubject.create<ListAppsClickEvent<T>>()
  private var appList: ArrayList<T> = ArrayList()

  private var itemSizeRatio: Double? = null
  private var itemFillWidth: Boolean = false

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): V {
    val vh = viewHolderBuilder(parent, viewType)
    vh.itemView.setOnClickListener {
      clickListener.onNext(
          ListAppsClickEvent(appList[vh.adapterPosition],
              vh.adapterPosition))
    }
    if (itemFillWidth) {
      itemSizeRatio?.also { ratio ->
        vh.itemView.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT)
        vh.itemView.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
          override fun onGlobalLayout() {
            val layoutParams = vh.itemView.layoutParams
            layoutParams.height = (vh.itemView.width * (1.0 / ratio)).toInt()
            vh.itemView.layoutParams = layoutParams
            vh.itemView.viewTreeObserver.removeOnGlobalLayoutListener(this)
          }
        })
      } ?: vh.itemView.viewTreeObserver.addOnGlobalLayoutListener(object :
          ViewTreeObserver.OnGlobalLayoutListener {
        override fun onGlobalLayout() {
          val layoutParams = vh.itemView.layoutParams
          layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
          vh.itemView.layoutParams = layoutParams
          vh.itemView.viewTreeObserver.removeOnGlobalLayoutListener(this)
        }
      })
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

  fun setItemRatioSize(itemSizeRatio: Double) {
    if (itemSizeRatio > 0) {
      this.itemSizeRatio = itemSizeRatio
    }
  }

  fun setItemFillWidth(fillWidth: Boolean) {
    this.itemFillWidth = fillWidth
  }
}