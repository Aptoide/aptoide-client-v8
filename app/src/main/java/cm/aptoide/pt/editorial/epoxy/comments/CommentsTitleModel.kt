package cm.aptoide.pt.editorial.epoxy.comments

import android.content.Context
import android.content.res.Resources
import android.view.View
import android.widget.*
import cm.aptoide.pt.R
import cm.aptoide.pt.reviews.LanguageFilterHelper.LanguageFilter
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.fa.epoxysample.bundles.models.base.BaseViewHolder
import rx.subjects.PublishSubject
import java.util.*

@EpoxyModelClass(layout = R.layout.comments_title_item)
abstract class CommentsTitleModel : EpoxyModelWithHolder<CommentsTitleModel.CardHolder>() {

  @EpoxyAttribute
  var title: String? = null
  @EpoxyAttribute
  var count: Int? = null
  @EpoxyAttribute
  var commentFilters: CommentFilters? = null
  @EpoxyAttribute
  var filterChangeSubject: PublishSubject<ChangeFilterEvent>? = null

  override fun bind(holder: CardHolder) {
    holder.title.text = title
    count?.let { c ->
      if (c < 0) {
        holder.count.visibility = View.INVISIBLE
      } else {
        holder.count.visibility = View.VISIBLE
        holder.count.text = "$count"
      }
    }
    commentFilters?.let { filters ->
      if (filters.filters.isNotEmpty()) {
        holder.filter.visibility = View.VISIBLE
        holder.filter.adapter =
            setupCommentsFilterLanguageSpinnerAdapter(filters.filters, holder.itemView.context)
        holder.filter.setSelection(filters.activePosition)
        holder.filter.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
          override fun onNothingSelected(parent: AdapterView<*>?) {}

          override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int,
                                      id: Long) {
            if (position != filters.activePosition) {
              filterChangeSubject?.onNext(ChangeFilterEvent(
                  CommentFilters(commentFilters?.filters ?: Collections.emptyList(), position)))
            }
          }
        }
      } else {
        holder.filter.visibility = View.GONE
      }
    }
  }

  private fun setupCommentsFilterLanguageSpinnerAdapter(filters: List<LanguageFilter>,
                                                        context: Context): SpinnerAdapter {
    val adapter =
        ArrayAdapter(context, R.layout.simple_language_spinner_item,
            createSpinnerAdapterRowsList(filters, context.resources))
    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
    return adapter
  }

  private fun createSpinnerAdapterRowsList(filters: List<LanguageFilter>,
                                           resources: Resources): MutableList<String> {
    val strings: MutableList<String> =
        LinkedList()
    for (languageFilter in filters) {
      strings.add(resources.getString(languageFilter.stringId))
    }
    return strings
  }

  class CardHolder : BaseViewHolder() {
    val title by bind<TextView>(R.id.title)
    val count by bind<TextView>(R.id.count)
    val filter by bind<Spinner>(R.id.comments_filter)
  }
}