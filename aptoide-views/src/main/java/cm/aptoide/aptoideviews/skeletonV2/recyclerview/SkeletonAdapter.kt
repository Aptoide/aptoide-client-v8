package cm.aptoide.aptoideviews.skeletonV2.recyclerview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

internal class SkeletonAdapter(private val listItemLayoutResId: Int, private val itemCount: Int) :
    RecyclerView.Adapter<SkeletonViewHolder>() {

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SkeletonViewHolder {
    return SkeletonViewHolder(
        LayoutInflater.from(parent.context).inflate(listItemLayoutResId, parent, false))
  }

  override fun getItemCount(): Int = itemCount

  override fun onBindViewHolder(holder: SkeletonViewHolder, position: Int) = Unit
}