package cm.aptoide.pt.home.more

import android.support.v7.widget.RecyclerView

abstract class ListAppsAdapter<in T, V : RecyclerView.ViewHolder> : RecyclerView.Adapter<V>() {

  abstract fun addToAdapter(objs: List<T>)
}

