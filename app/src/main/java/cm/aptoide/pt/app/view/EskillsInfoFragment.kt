package cm.aptoide.pt.app.view

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.HorizontalScrollView
import android.widget.ImageView
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import cm.aptoide.analytics.implementation.navigation.ScreenTagHistory
import cm.aptoide.pt.R
import cm.aptoide.pt.view.BackButtonFragment
import cm.aptoide.pt.view.NotBottomNavigationView
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.AppBarLayout.OnOffsetChangedListener
import kotlin.math.abs

class EskillsInfoFragment : BackButtonFragment(), EskillsInfoView, NotBottomNavigationView {

  private lateinit var toolbar: Toolbar
  private lateinit var statsScrollView: HorizontalScrollView
  private lateinit var eskillsImage: ImageView
  private lateinit var mainScrollView: ScrollView
  private lateinit var learnMore: View
  private lateinit var appBarLayout : AppBarLayout


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
    statsScrollView = view.findViewById(R.id.values_stats_scrollable)
    mainScrollView = view.findViewById(R.id.scroll_view)
    eskillsImage = view.findViewById(R.id.eskills_art)
    learnMore = view.findViewById(R.id.learn_more)
    toolbar = view.findViewById(R.id.toolbar)

    appBarLayout = view.findViewById(R.id.app_bar_layout)
    appBarLayout.addOnOffsetChangedListener(OnOffsetChangedListener {
        appBarLayout: AppBarLayout, verticalOffset: Int ->
      val percentage =
        abs(verticalOffset).toFloat() / appBarLayout.totalScrollRange
      toolbar.background.alpha = (255 * percentage).toInt()
      view.findViewById<View>(R.id.eskills_logo_info).alpha = 1 - percentage
    })
    setupStatsView(view.findViewById(R.id.values_stats))
    setupFAQs(view.findViewById(R.id.eskills_faqs))
    setupToolbar()

    /*if (arguments?.getBoolean(NAVIGATE_TO_INFO) == true) {
      view.post {
        navigateToInfo()
      }
    }*/
  }

  private fun setupToolbar() {
    toolbar.setBackgroundColor(ResourcesCompat.getColor(resources, R.color.grey_900, null))
    toolbar.setTitle("Earn more")
    toolbar.setTitleTextColor(Color.WHITE)
    toolbar.setSubtitleTextColor(Color.WHITE)
    val appCompatActivity = activity as AppCompatActivity?
    appCompatActivity!!.setSupportActionBar(toolbar)
    val actionBar = appCompatActivity.supportActionBar
    actionBar?.setDisplayHomeAsUpEnabled(true)
  }

  private fun navigateToInfo() {
    val scrollY = learnMore.top - mainScrollView.height / 3
    mainScrollView.smoothScrollTo(0, scrollY)
  }
  private fun setupStatsScrollView() {
    // horizontal parallax effect with eskillsImage
    statsScrollView.viewTreeObserver.addOnScrollChangedListener {
      val scrollX = statsScrollView.scrollX
      eskillsImage.translationX = -scrollX.toFloat() / 5
    }
  }

  private fun setupStatsView(statsView: View) {
    val card1 = statsView.findViewById<View>(R.id.value_proposition_card_1)
    val card2 = statsView.findViewById<View>(R.id.value_proposition_card_2)
    val card3 = statsView.findViewById<View>(R.id.value_proposition_card_3)
    val card4 = statsView.findViewById<View>(R.id.value_proposition_card_4)

    setupStatsCard(card1, R.string.eskills_v2_card_1_title, "30", "$", R.string.eskills_v2_card_1_body)
    setupStatsCard(card2, R.string.eskills_v2_card_2_title, "6.5", "$", R.string.eskills_v2_card_2_body)
    setupStatsCard(card3, R.string.eskills_v2_card_3_title, "20", "$", R.string.eskills_v2_card_3_body)
    setupStatsCard(card4, R.string.eskills_v2_card_4_title, "24", "h", R.string.eskills_v2_card_4_body)

    statsView.findViewById<View>(R.id.learn_more_card).setOnClickListener {
      navigateToInfo()
    }
    setupStatsScrollView()
  }

  private fun setupStatsCard(card: View, title: Int, value: String, unit: String, description: Int) {
    card.findViewById<TextView>(R.id.value_proposition_card_title).text = getString(title, value, unit)
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
        arrow.setImageDrawable(ContextCompat.getDrawable(context!!, R.drawable.ic_down_arrow))
      } else {
        answerView.visibility = View.VISIBLE
        arrow.setImageDrawable(ContextCompat.getDrawable(context!!, R.drawable.ic_up_arrow))
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