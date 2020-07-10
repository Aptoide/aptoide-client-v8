package cm.aptoide.aptoideviews.filters

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.annotation.CheckResult
import androidx.recyclerview.widget.RecyclerView
import cm.aptoide.aptoideviews.R
import kotlinx.android.synthetic.main.filters_view_layout.view.*
import rx.Observable

class FiltersView @JvmOverloads constructor(context: Context,
                                            attrs: AttributeSet? = null,
                                            defStyleAttr: Int = 0) :
    FrameLayout(context, attrs, defStyleAttr) {

  private var filters: List<Filter> = ArrayList()
  private var filterChangedEventsListener: FiltersChangedEventListener? = null
  private val filterEventListener = object : FilterEventListener {
    override fun onFilterEvent(eventType: FilterEventListener.EventType, filter: Filter?) {
      when (eventType) {
        FilterEventListener.EventType.CLEAR_EVENT_CLICK -> {
          clearFilters()
        }
        FilterEventListener.EventType.FILTER_CLICK -> {
          val i = filters.indexOf(filter)
          if (i >= 0) {
            val newMutList = ArrayList(filters)
            newMutList[i] = Filter(filters[i].name, !filters[i].selected, filters[i].identifier)
            val list = newMutList.toList()
            setFilters(list)
            filterChangedEventsListener?.onFiltersChanged(list)
          }
        }
      }
    }
  }
  private var controller: FiltersController = FiltersController(filterEventListener)

  init {
    inflate(context, R.layout.filters_view_layout, this)
    initViews()
  }

  private fun initViews() {
    filters_recycler_view.setController(controller)
    controller.adapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {

      override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
        if (positionStart == 0) {
          filters_recycler_view.layoutManager?.scrollToPosition(0)
        }
      }

      override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
        if (fromPosition > toPosition) {
          filters_recycler_view.layoutManager?.scrollToPosition(toPosition)
        }
      }
    })
  }

  fun setFilters(filters: List<Filter>) {
    generateIds(filters)
    this.filters = filters
    refreshData()
  }

  fun getFilters(): List<Filter> {
    return filters
  }

  @CheckResult
  fun filtersChangedEvents(): Observable<List<Filter>> {
    return Observable.create(FilterChangedEventOnSubscribe(this))
  }

  private fun clearFilters() {
    val newMutList = ArrayList(filters)
    for (i in 0 until newMutList.size) {
      newMutList[i] = Filter(filters[i].name, false, filters[i].identifier)
    }
    setFilters(newMutList.toList())
    filterChangedEventsListener?.onFiltersChanged(newMutList.toList())
  }

  private fun generateIds(filters: List<Filter>) {
    for ((i, filter) in filters.withIndex()) {
      filter.id = i
    }
  }

  internal fun setFiltersChangedEventsListener(listener: FiltersChangedEventListener?) {
    this.filterChangedEventsListener = listener
  }

  private fun refreshData() {
    controller.setData(filters)
  }
}