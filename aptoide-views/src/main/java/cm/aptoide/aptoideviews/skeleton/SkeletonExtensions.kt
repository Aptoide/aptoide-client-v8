@file:JvmName("SkeletonUtils")

package cm.aptoide.aptoideviews.skeleton

import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import cm.aptoide.aptoideviews.skeleton.recyclerview.SkeletonRecyclerViewWrapper

@JvmOverloads
fun RecyclerView.applySkeleton(
    @LayoutRes listItemLayoutResId: Int,
    itemCount: Int = 9): Skeleton = SkeletonRecyclerViewWrapper(
    this, listItemLayoutResId, itemCount)

@JvmOverloads
fun View.applySkeleton(
    parent: ViewGroup,
    @LayoutRes skeletonLayoutResId: Int): Skeleton = SkeletonViewWrapper(
    this, parent,
    skeletonLayoutResId)
