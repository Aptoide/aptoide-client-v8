package cm.aptoide.pt.home.more.eskills

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.HorizontalScrollView
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.widget.NestedScrollView
import cm.aptoide.analytics.implementation.navigation.ScreenTagHistory
import cm.aptoide.aptoideviews.recyclerview.GridRecyclerView
import cm.aptoide.pt.R
import cm.aptoide.pt.home.more.apps.ListAppsMoreViewHolder
import cm.aptoide.pt.home.more.base.ListAppsFragment
import cm.aptoide.pt.store.view.StoreTabGridRecyclerFragment
import cm.aptoide.pt.view.DarkBottomNavigationView
import cm.aptoide.pt.view.MainActivity
import cm.aptoide.pt.view.app.Application
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.AppBarLayout.OnOffsetChangedListener
import com.jakewharton.rxbinding.view.RxView
import kotlinx.android.synthetic.main.fragment_list_apps.apps_list
import kotlinx.android.synthetic.main.fragment_list_apps.error_view
import kotlinx.android.synthetic.main.partial_view_progress_bar.progress_bar
import rx.Observable
import java.text.DecimalFormat
import java.util.Locale
import javax.inject.Inject
import kotlin.math.abs

class EskillsInfoFragment : ListAppsFragment<Application, ListAppsMoreViewHolder>(),
  EskillsInfoView, DarkBottomNavigationView {

  @Inject
  lateinit var presenter: EskillsInfoPresenter

  private lateinit var toolbar: Toolbar
  private lateinit var statsScrollView: HorizontalScrollView
  private lateinit var eskillsImage: ImageView
  private lateinit var mainScrollView: NestedScrollView
  private lateinit var learnMoreSection: View
  private lateinit var learnMoreCard: View
  private lateinit var seeAllButton: Button
  private lateinit var walletDisclaimer: View

  private lateinit var appBarLayout: AppBarLayout


  companion object {
    @JvmStatic
    fun newInstance(
      title: String, tag: String, action: String, eventName: String
    ): EskillsInfoFragment {
      return EskillsInfoFragment().apply {
        arguments = Bundle().apply {
          putString(StoreTabGridRecyclerFragment.BundleCons.TITLE, title)
          putString(StoreTabGridRecyclerFragment.BundleCons.TAG, tag)
          putString(StoreTabGridRecyclerFragment.BundleCons.ACTION, action)
          putString(StoreTabGridRecyclerFragment.BundleCons.NAME, eventName)
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
    learnMoreSection = view.findViewById(R.id.learn_more)
    learnMoreCard = view.findViewById(R.id.learn_more_card)
    walletDisclaimer = view.findViewById(R.id.wallet_disclaimer)
    seeAllButton = view.findViewById(R.id.see_all_button)
    toolbar = view.findViewById(R.id.toolbar)
    appBarLayout = view.findViewById(R.id.app_bar_layout)
    appBarLayout.addOnOffsetChangedListener(OnOffsetChangedListener { appBarLayout: AppBarLayout, verticalOffset: Int ->
      val percentage = abs(verticalOffset).toFloat() / appBarLayout.totalScrollRange
      toolbar.background.alpha = (255 * percentage).toInt()
      view.findViewById<View>(R.id.eskills_logo_info).alpha = 1 - percentage
    })
    setupStatsView(view.findViewById(R.id.values_stats))
    setupFAQs(view.findViewById(R.id.eskills_faqs))
    presenter.present()
  }

  private fun setupStatsView(statsView: View) {
    val card1 = statsView.findViewById<View>(R.id.value_proposition_card_1)
    val card2 = statsView.findViewById<View>(R.id.value_proposition_card_2)
    val card3 = statsView.findViewById<View>(R.id.value_proposition_card_3)
    val card4 = statsView.findViewById<View>(R.id.value_proposition_card_4)

    setupStatsCard(
      card1, R.string.eskills_v2_card_1_title, "30", "$", R.string.eskills_v2_card_1_body
    )
    setupStatsCard(
      card2, R.string.eskills_v2_card_2_title, "6.5", "$", R.string.eskills_v2_card_2_body
    )
    setupStatsCard(
      card3, R.string.eskills_v2_card_3_title, "20", "$", R.string.eskills_v2_card_3_body
    )
    setupStatsCard(
      card4, R.string.eskills_v2_card_4_title, "24", "h", R.string.eskills_v2_card_4_body
    )
    setupStatsScrollView()
  }

  private fun setupStatsScrollView() {
    val isLeftToRight =
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
        TextUtils.getLayoutDirectionFromLocale(Locale.getDefault()) == View.LAYOUT_DIRECTION_LTR
      } else {
        true
      }

    // horizontal parallax effect with eskillsImage
    if (!isLeftToRight) {
      eskillsImage.scaleX = -1f
      statsScrollView.viewTreeObserver.addOnScrollChangedListener {
        val maxScroll = statsScrollView.getChildAt(0).width - (context as MainActivity).windowManager.defaultDisplay.width
        val scrollX = statsScrollView.scrollX
        eskillsImage.translationX = (maxScroll - scrollX.toFloat()) / 5
      }
    }
    else {
      statsScrollView.viewTreeObserver.addOnScrollChangedListener {
        val scrollX = statsScrollView.scrollX
        eskillsImage.translationX = -scrollX.toFloat() / 5
      }
    }
  }

  private fun setupStatsCard(
    card: View, title: Int, value: String, unit: String, description: Int
  ) {
    card.findViewById<TextView>(R.id.value_proposition_card_title).text =
      getString(title, value, unit)
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

  override fun handleMoreAppsClick(): Observable<Void> {
    return RxView.clicks(seeAllButton)
  }

  override fun handleLearnMoreClick(): Observable<Void> {
    return RxView.clicks(learnMoreCard)
  }

  override fun handleWalletDisclaimerClick(): Observable<Void> {
    return RxView.clicks(walletDisclaimer)
  }

  override fun scrollToInfo() {
    val scrollY = learnMoreSection.top - 32
    mainScrollView.smoothScrollTo(0, scrollY)
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

  // Recycler view
  override fun setupHeaderListener() = Unit

  override fun showResultsVisibility() {
    apps_list.visibility = View.VISIBLE
    error_view.visibility = View.GONE
    progress_bar.visibility = View.GONE
  }

  override fun showErrorVisibility() {
    error_view.visibility = View.VISIBLE
    apps_list.visibility = View.GONE
    progress_bar.visibility = View.GONE
  }

  override fun getItemSizeWidth(): Int {
    return 104
  }

  override fun getItemSizeHeight(): Int {
    return 165
  }

  override fun getAdapterStrategy(): GridRecyclerView.AdaptStrategy {
    return GridRecyclerView.AdaptStrategy.SCALE_KEEP_ASPECT_RATIO
  }

  override fun createViewHolder(): (ViewGroup, Int) -> ListAppsEskillsViewHolder {
    return { parent, _ ->
      ListAppsEskillsViewHolder(
        LayoutInflater.from(parent.context).inflate(
          R.layout.eskills_app_home_item, parent, false
        ), DecimalFormat("0.0")
      )
    }
  }

  override fun setToolbarInfo(title: String) {
    toolbar.setBackgroundColor(ResourcesCompat.getColor(resources, R.color.grey_900, null))
    toolbar.title = title
    toolbar.setTitleTextColor(Color.WHITE)
    toolbar.setSubtitleTextColor(Color.WHITE)
    val appCompatActivity = activity as AppCompatActivity?
    appCompatActivity!!.setSupportActionBar(toolbar)
    val actionBar = appCompatActivity.supportActionBar
    actionBar?.setDisplayHomeAsUpEnabled(true)
  }
}