package cm.aptoide.pt.app.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import cm.aptoide.analytics.implementation.navigation.ScreenTagHistory
import cm.aptoide.pt.R
import cm.aptoide.pt.view.BackButtonFragment
import cm.aptoide.pt.view.NotBottomNavigationView

class EskillsInfoFragment : BackButtonFragment(), EskillsInfoView, NotBottomNavigationView {
  companion object {
    @JvmStatic
    fun newInstance(): EskillsInfoFragment {
      return EskillsInfoFragment().apply {
        arguments = Bundle().apply {

        }
      }
    }
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    getFragmentComponent(savedInstanceState).inject(this)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    setupStatsView(view.findViewById(R.id.values_stats))
    setupFAQs(view.findViewById(R.id.eskills_faqs))
  }

  private fun setupStatsView(statsView: View) {
    val card1 = statsView.findViewById<View>(R.id.value_proposition_card_1)
    val card2 = statsView.findViewById<View>(R.id.value_proposition_card_2)
    val card3 = statsView.findViewById<View>(R.id.value_proposition_card_3)
    val card4 = statsView.findViewById<View>(R.id.value_proposition_card_4)

    setupStatsCard(card1, R.string.eskills_v2_card_1_title, R.string.eskills_v2_card_1_body)
    setupStatsCard(card2, R.string.eskills_v2_card_2_title, R.string.eskills_v2_card_2_body)
    setupStatsCard(card3, R.string.eskills_v2_card_3_title, R.string.eskills_v2_card_3_body)
    setupStatsCard(card4, R.string.eskills_v2_card_4_title, R.string.eskills_v2_card_4_body)
  }

  private fun setupStatsCard(card: View, title: Int, description: Int) {
    card.findViewById<TextView>(R.id.value_proposition_card_title)
      .setText(title) // TODO handle plurals and placeholders (?)
    card.findViewById<TextView>(R.id.value_proposition_card_body).setText(description)
  }

  private fun setupFAQs(faqsView: View) {
    val faq1 = faqsView.findViewById<View>(R.id.eskills_faqs_item_1)
    val faq2 = faqsView.findViewById<View>(R.id.eskills_faqs_item_2)
    val faq3 = faqsView.findViewById<View>(R.id.eskills_faqs_item_3)

    setupFAQItem(faq1, R.string.eskills_v2_faqs_1_title, R.string.eskills_v2_faqs_1_body_1)
    setupFAQItem(faq2, R.string.eskills_v2_faqs_2_title, R.string.eskills_v2_faqs_2_body_1)
    setupFAQItem(faq3, R.string.eskills_v2_faqs_3_title, R.string.eskills_v2_faqs_3_body_1)
  }

  private fun setupFAQItem(faq: View, question: Int, answer: Int) {
    faq.findViewById<TextView>(R.id.eskills_faq_question).setText(question)
    val answerView = faq.findViewById<TextView>(R.id.eskills_faq_answer)
    val arrow = faq.findViewById<ImageView>(R.id.eskills_faq_arrow)
    answerView.setText(answer)
    faq.setOnClickListener {
      if (answerView.visibility == View.VISIBLE) {
        answerView.visibility = View.GONE
        arrow.setImageDrawable(ContextCompat.getDrawable(context!!, R.drawable.ic_up_arrow))
      } else {
        answerView.visibility = View.VISIBLE
        arrow.setImageDrawable(ContextCompat.getDrawable(context!!, R.drawable.ic_down_arrow))
      }
    }
  }

  override fun getHistoryTracker(): ScreenTagHistory? {
    return ScreenTagHistory.Builder.build(
      this.javaClass.simpleName
    )
  }


  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
  ): View? {
    return inflater.inflate(R.layout.fragment_eskills_info, container, false)
  }
}