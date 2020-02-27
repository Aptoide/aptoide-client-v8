package com.fa.epoxysample.bundles.models.base

import android.view.View
import com.airbnb.epoxy.EpoxyHolder
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

/**
 * A pattern for easier itemView binding with an [EpoxyHolder]
 *
 */
abstract class BaseViewHolder : EpoxyHolder() {
  lateinit var itemView: View

  override fun bindView(itemView: View) {
    this.itemView = itemView
  }

  protected fun <V : View> bind(id: Int): ReadOnlyProperty<BaseViewHolder, V> =
      Lazy { holder: BaseViewHolder, prop ->
        holder.itemView.findViewById(id) as V?
            ?: throw IllegalStateException("View ID $id for '${prop.name}' not found.")
      }

  /**
   * Taken from Kotterknife.
   * https://github.com/JakeWharton/kotterknife
   */
  private class Lazy<V>(
      private val initializer: (BaseViewHolder, KProperty<*>) -> V
  ) : ReadOnlyProperty<BaseViewHolder, V> {
    private object EMPTY

    private var value: Any? = EMPTY

    override fun getValue(thisRef: BaseViewHolder, property: KProperty<*>): V {
      if (value == EMPTY) {
        value = initializer(thisRef, property)
      }
      @Suppress("UNCHECKED_CAST")
      return value as V
    }
  }
}