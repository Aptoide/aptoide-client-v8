package cm.aptoide.aptoideviews.filters

import com.airbnb.epoxy.TypedEpoxyController

class FiltersController(val eventListener: FilterEventListener) :
    TypedEpoxyController<List<Filter>>() {

  private var selectionList: List<Filter>? = null

  override fun buildModels(list: List<Filter>) {
    if (list.find { f -> f.selected } != null) {
      ClearFiltersModel_()
          .id("clear_filter")
          .eventListener(eventListener)
          .addTo(this)
    }
    for (filter in sortList(list)) {
      FilterModel_()
          .id(filter.id)
          .filter(filter)
          .eventListener(eventListener)
          .addTo(this)
    }
  }

  /**
   * This orders so that the unselected items remain always in the same original order but the
   * selected ones are ordered by the order of user selection.
   *
   * The algorithm is as follows:
   *  1. Create / update a list with the selected filters by order of arrival by:
   *    1.1. Removing previously selected items that are no longer selected
   *    1.2. Adding new selected items that were not previously selected
   *  2. To this selected list, add every element of the original list
   *  3. Remove duplicates while maintaining the same order (stable)
   */
  private fun sortList(list: List<Filter>): List<Filter> {
    updateSelectionList(list)
    val newArrayList = ArrayList(selectionList ?: ArrayList()).apply { addAll(list) }
    return newArrayList.distinctBy { f -> f.id }
  }

  private fun updateSelectionList(newList: List<Filter>) {
    var mutList = ArrayList(selectionList ?: ArrayList())
    mutList =
        mutList.filter { f -> newList.find { f2 -> f.id == f2.id }?.selected == true } as ArrayList<Filter>
    mutList.addAll(newList.filter { f -> f.selected })
    selectionList = mutList.distinctBy { f -> f.id }
  }

}