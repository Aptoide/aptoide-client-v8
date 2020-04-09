package cm.aptoide.pt.editorial.epoxy

import android.annotation.SuppressLint
import android.graphics.Rect
import android.util.Log
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import cm.aptoide.aptoideviews.video.WebChromeClientWithoutPlayerPlaceholder
import cm.aptoide.pt.R
import cm.aptoide.pt.aab.Split
import cm.aptoide.pt.app.DownloadModel
import cm.aptoide.pt.dataprovider.model.v7.Obb
import cm.aptoide.pt.editorial.*
import cm.aptoide.pt.home.SnapToStartHelper
import cm.aptoide.pt.networking.image.ImageLoader
import cm.aptoide.pt.utils.AptoideUtils
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.fa.epoxysample.bundles.models.base.BaseViewHolder
import rx.subjects.PublishSubject
import java.text.DecimalFormat
import java.util.*

@EpoxyModelClass(layout = R.layout.editorial_item_layout)
abstract class EditorialContentModel : EpoxyModelWithHolder<EditorialContentModel.CardHolder>() {
  @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
  var decimalFormat: DecimalFormat? = null
  @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
  var uiEventListener: PublishSubject<EditorialEvent>? = null
  @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
  var downloadEventListener: PublishSubject<EditorialDownloadEvent>? = null
  @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
  var bottomCardVisibilityChange: PublishSubject<Boolean>? = null

  @EpoxyAttribute
  var editorialContent: EditorialContent? = null
  @EpoxyAttribute
  var shouldAnimate: Boolean? = null

  private var isSoloCardVisible = false
  private lateinit var mediaBundleAdapter: MediaBundleAdapter

  override fun bind(holder: CardHolder) {
    initMisc(holder)

    editorialContent?.let { editorialItem ->
      if (editorialItem.hasTitle() || editorialItem.hasMessage()) {
        holder.itemText.visibility = View.VISIBLE
        manageTitleVisibility(holder, editorialItem)
        manageMessageVisibility(holder, editorialItem)
      } else {
        holder.itemText.visibility = View.GONE
      }
      manageMediaVisibility(holder, editorialItem)
      if (editorialItem.hasApp() && editorialItem.app.downloadModel != null) {
        setAppListeners(holder, editorialItem.app.name, editorialItem.app.packageName,
            editorialItem.app.md5sum, editorialItem.app.icon, editorialItem.app.verName,
            editorialItem.app.verCode,
            editorialItem.app.path, editorialItem.app.pathAlt, editorialItem.app.obb,
            editorialItem.app.id,
            editorialItem.app.size, editorialItem.app.splits, editorialItem.app.requiredSplits,
            editorialItem.app.rank, editorialItem.app.storeName,
            editorialItem.app.downloadModel.action)
        setAppInfo(holder, editorialItem.app.name, editorialItem.app.icon, editorialItem.app.avg)
        setDownloadModel(holder, editorialItem.app.downloadModel)

        holder.appCardLayout.visibility = View.VISIBLE
        holder.appCardLayout.scaleX = 1f
        holder.appCardLayout.scaleY = 1f
      } else {
        holder.appCardLayout.visibility = View.GONE
        holder.appCardLayout.scaleX = 0f
        holder.appCardLayout.scaleY = 0f
      }
      if (editorialItem.hasAction()) {
        manageActionVisibility(holder, editorialItem)
      }
    }
  }

  override fun unbind(holder: CardHolder) {
    shouldAnimate?.let { animate ->
      if (animate) {
        bottomCardVisibilityChange?.onNext(true)
      }
    }
    super.unbind(holder)
  }

  override fun onVisibilityChanged(percentVisibleHeight: Float, percentVisibleWidth: Float,
                                   visibleHeight: Int, visibleWidth: Int, holder: CardHolder) {
    var isVisible = isVisible(holder, visibleHeight.toFloat(), visibleWidth.toFloat())
    shouldAnimate?.let { animate ->
      if (animate && isVisible != isSoloCardVisible) {
        if (isVisible) {
          bottomCardVisibilityChange?.onNext(false)
        } else {
          bottomCardVisibilityChange?.onNext(true)
        }
        isSoloCardVisible = isVisible
      }
    }
    super.onVisibilityChanged(percentVisibleHeight, percentVisibleWidth, visibleHeight,
        visibleWidth, holder)
  }

  private fun setDownloadModel(holder: CardHolder, downloadModel: EditorialDownloadModel?) {
    val resources = holder.itemView.resources
    downloadModel?.let { model ->
      if (model.isDownloading) {
        setPlaceHolderDownloadingInfo(holder, downloadModel)
      } else {
        setPlaceHolderDefaultStateInfo(holder, downloadModel,
            resources.getString(R.string.appview_button_update),
            resources.getString(R.string.appview_button_install),
            resources.getString(R.string.appview_button_open),
            resources.getString(R.string.appview_button_downgrade))
      }
    }
  }

  private fun manageActionVisibility(holder: CardHolder, editorialItem: EditorialContent) {
    holder.actionButton.text = editorialItem.actionTitle
    holder.actionButton.visibility = View.VISIBLE
    holder.actionButton.setOnClickListener {
      uiEventListener?.onNext(
          EditorialEvent(EditorialEvent.Type.ACTION, editorialItem.actionUrl))
    }
  }

  private fun manageTitleVisibility(holder: CardHolder, editorialItem: EditorialContent) {
    if (editorialItem.hasTitle()) {
      holder.title.visibility = View.VISIBLE
      if (editorialItem.position == 0) {
        holder.firstTitle.text = editorialItem.title
        holder.firstTitle.visibility = View.VISIBLE
      } else {
        holder.secondaryTitle.text = editorialItem.title
        holder.secondaryTitle.visibility = View.VISIBLE
      }
    } else {
      holder.title.visibility = View.GONE
    }
  }

  private fun manageMessageVisibility(holder: CardHolder, editorialItem: EditorialContent) {
    if (editorialItem.hasMessage()) {
      holder.message.text = editorialItem.message
      holder.message.visibility = View.VISIBLE
    } else {
      holder.message.visibility = View.GONE
    }
  }

  @SuppressLint("SetJavaScriptEnabled")
  private fun manageMediaVisibility(holder: CardHolder, editorialItem: EditorialContent) {
    if (editorialItem.hasMedia()) {
      val editorialMediaList = editorialItem.media
      holder.media.visibility = View.VISIBLE
      if (editorialItem.hasListOfMedia()) {
        mediaBundleAdapter.add(editorialMediaList)
        holder.mediaList.visibility = View.VISIBLE
      } else {
        holder.mediaList.visibility = View.GONE
        val editorialMedia = editorialMediaList[0]
        if (editorialMedia.hasDescription()) {
          holder.descriptionSwitcher.setCurrentText(editorialMedia.description)
          holder.descriptionSwitcher.visibility = View.VISIBLE
        } else {
          holder.descriptionSwitcher.visibility = View.GONE
        }
        if (editorialMedia.isImage) {
          ImageLoader.with(holder.itemView.context)
              .load(editorialMedia.url, holder.image)
          holder.image.visibility = View.VISIBLE
        } else {
          holder.image.visibility = View.GONE
        }
        if (editorialMedia.isVideo) {
          if (editorialMedia.thumbnail != null) {
            ImageLoader.with(holder.itemView.context)
                .load(editorialMedia.thumbnail, holder.videoThumbnail)
          }
          if (editorialMedia.hasUrl()) {
            holder.videoThumbnailContainer.visibility = View.VISIBLE
            holder.videoThumbnailContainer.setOnClickListener {
              uiEventListener?.onNext(
                  EditorialEvent(EditorialEvent.Type.MEDIA, editorialMedia.url))
            }
          } else {
            holder.videoThumbnailContainer.visibility = View.GONE
          }
        }
        if (editorialMedia.isEmbedded) {
          holder.embeddedVideo.webViewClient = WebViewClient()
          holder.embeddedVideo.settings.javaScriptEnabled = true
          holder.embeddedVideo.loadUrl(editorialMedia.url)
          holder.embeddedVideo.webChromeClient = WebChromeClientWithoutPlayerPlaceholder()
          holder.embeddedVideo.visibility = View.VISIBLE
        } else {
          holder.embeddedVideo.visibility = View.GONE
        }
      }
    } else {
      holder.media.visibility = View.GONE
    }
  }

  private fun setAppInfo(holder: CardHolder, appName: String, image: String,
                         rating: Float) {
    ImageLoader.with(holder.itemView.context).load(image, holder.appCardImage)
    holder.appCardImage.visibility = View.VISIBLE
    if (rating == 0f) {
      holder.appCardRating.setText(R.string.appcardview_title_no_stars)
    } else {
      holder.appCardRating.text = decimalFormat?.format(rating.toDouble())
    }
    holder.appCardRatingLayout.visibility = View.VISIBLE
    holder.appCardNameWithRating.text = appName
    holder.appCardNameWithRating.visibility = View.VISIBLE
    holder.appCardLayout.visibility = View.VISIBLE
  }

  private fun setAppListeners(holder: CardHolder, appName: String, packageName: String,
                              md5sum: String,
                              icon: String, verName: String,
                              verCode: Int, path: String,
                              pathAlt: String,
                              obb: Obb?,
                              id: Long,
                              size: Long,
                              splits: List<Split>?,
                              requiredSplits: List<String>?,
                              trustedBadge: String,
                              storeName: String, action: DownloadModel.Action) {
    holder.cancelDownload.setOnClickListener {
      downloadEventListener?.onNext(
          EditorialDownloadEvent(EditorialEvent.Type.CANCEL, appName, packageName, md5sum, icon,
              verName, verCode, path, pathAlt, obb, size, splits, requiredSplits))
    }
    holder.resumeDownload.setOnClickListener {
      downloadEventListener?.onNext(
          EditorialDownloadEvent(EditorialEvent.Type.RESUME, appName, packageName, md5sum, icon,
              verName, verCode, path, pathAlt, obb, action, size, splits, requiredSplits,
              trustedBadge, storeName))
    }
    holder.pauseDownload.setOnClickListener {
      downloadEventListener?.onNext(
          EditorialDownloadEvent(EditorialEvent.Type.PAUSE, appName, packageName, md5sum, icon,
              verName, verCode, path, pathAlt, obb, size, splits, requiredSplits))
    }
    holder.appCardButton.setOnClickListener {
      downloadEventListener?.onNext(
          EditorialDownloadEvent(EditorialEvent.Type.BUTTON, appName, packageName, md5sum, icon,
              verName, verCode, path, pathAlt, obb, action, size, splits, requiredSplits,
              trustedBadge, storeName))
    }
    holder.appCardLayout.setOnClickListener {
      uiEventListener?.onNext(EditorialEvent(EditorialEvent.Type.APPCARD, id, packageName))
    }
  }

  private fun setPlaceHolderDownloadingInfo(holder: CardHolder, downloadModel: DownloadModel) {
    holder.downloadInfoLayout.visibility = View.VISIBLE
    holder.cardInfoLayout.visibility = View.GONE
    setDownloadState(holder, downloadModel.progress, downloadModel.downloadState)
  }

  private fun setPlaceHolderDefaultStateInfo(holder: CardHolder, downloadModel: DownloadModel?,
                                             update: String,
                                             install: String, open: String,
                                             downgrade: String) {
    holder.downloadInfoLayout.visibility = View.GONE
    holder.cardInfoLayout.visibility = View.VISIBLE
    setButtonText(holder, downloadModel, update, install, open, downgrade)
  }

  private fun setDownloadState(holder: CardHolder, progress: Int,
                               downloadState: DownloadModel.DownloadState) {
    Log.i("DOWNLOADSTATE", "SETTING")
    val pauseShowing =
        LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT, 4f)
    val pauseHidden =
        LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT, 2f)
    when (downloadState) {
      DownloadModel.DownloadState.ACTIVE -> {
        holder.downloadProgressBar.isIndeterminate = false
        holder.downloadProgressBar.progress = progress
        holder.downloadProgressValue.text = "$progress%"
        holder.pauseDownload.visibility = View.VISIBLE
        holder.cancelDownload.visibility = View.GONE
        holder.resumeDownload.visibility = View.GONE
        holder.downloadControlsLayout.layoutParams = pauseShowing
      }
      DownloadModel.DownloadState.INDETERMINATE -> {
        holder.downloadProgressBar.isIndeterminate = true
        holder.pauseDownload.visibility = View.VISIBLE
        holder.cancelDownload.visibility = View.GONE
        holder.resumeDownload.visibility = View.GONE
        holder.downloadControlsLayout.layoutParams = pauseShowing
      }
      DownloadModel.DownloadState.PAUSE -> {
        holder.downloadProgressBar.isIndeterminate = false
        holder.downloadProgressBar.progress = progress
        holder.downloadProgressValue.text = "$progress%"
        holder.pauseDownload.visibility = View.GONE
        holder.cancelDownload.visibility = View.VISIBLE
        holder.resumeDownload.visibility = View.VISIBLE
        holder.downloadControlsLayout.layoutParams = pauseHidden
      }
      DownloadModel.DownloadState.COMPLETE -> {
        holder.downloadProgressBar.isIndeterminate = true
        holder.pauseDownload.visibility = View.VISIBLE
        holder.cancelDownload.visibility = View.GONE
        holder.resumeDownload.visibility = View.GONE
        holder.downloadControlsLayout.layoutParams = pauseShowing
      }
      else -> {
      }
    }
  }

  private fun setButtonText(holder: CardHolder, model: DownloadModel?, update: String,
                            install: String, open: String,
                            downgrade: String) {
    when (model?.action) {
      DownloadModel.Action.UPDATE -> holder.appCardButton.text = update
      DownloadModel.Action.INSTALL -> holder.appCardButton.text = install
      DownloadModel.Action.OPEN -> holder.appCardButton.text = open
      DownloadModel.Action.DOWNGRADE -> holder.appCardButton.text = downgrade
    }
  }

  open fun isVisible(holder: CardHolder, screenHeight: Float, screenWidth: Float): Boolean {
    val placeHolderPosition = Rect()
    holder.appCardLayout.getLocalVisibleRect(placeHolderPosition)
    val screen = Rect(0, 0, screenWidth.toInt(),
        screenHeight.toInt() - holder.appCardLayout.height * 2)
    return placeHolderPosition.intersect(screen)
  }


  private fun initMisc(holder: CardHolder) {
    mediaBundleAdapter = MediaBundleAdapter(ArrayList(), uiEventListener)
    holder.mediaList.adapter = mediaBundleAdapter
  }

  class CardHolder : BaseViewHolder() {
    val itemText by bind<View>(R.id.editorial_item_text)
    val title by bind<View>(R.id.editorial_item_title)
    val firstTitle by bind<TextView>(R.id.editorial_item_first_title)
    val secondaryTitle by bind<TextView>(R.id.editorial_item_secondary_title)
    val message by bind<TextView>(R.id.editorial_item_message)
    val media by bind<View>(R.id.editorial_item_media)
    val image by bind<ImageView>(R.id.editorial_image)
    val embeddedVideo by bind<WebView>(R.id.embedded_video)
    val videoThumbnail by bind<ImageView>(R.id.editorial_video_thumbnail)
    val videoThumbnailContainer by bind<FrameLayout>(R.id.editorial_video_thumbnail_container)
    val descriptionSwitcher by bind<TextSwitcher>(R.id.editorial_image_description_switcher)
    val mediaList by bind<RecyclerView>(R.id.editoral_image_list)
    val appCardLayout by bind<View>(R.id.app_cardview)
    val actionButton by bind<Button>(R.id.action_button)
    val appCardButton by bind<Button>(R.id.appview_install_button)
    val appCardNameWithRating by bind<TextView>(R.id.app_title_textview_with_rating)
    val appCardImage by bind<ImageView>(R.id.app_icon_imageview)
    val appCardRating by bind<TextView>(R.id.rating_label)
    val appCardRatingLayout by bind<View>(R.id.rating_layout)
    val cardInfoLayout by bind<RelativeLayout>(R.id.card_info_install_layout)
    val downloadControlsLayout by bind<View>(R.id.install_controls_layout)
    val downloadInfoLayout by bind<LinearLayout>(R.id.appview_transfer_info)
    val downloadProgressBar by bind<ProgressBar>(R.id.appview_download_progress_bar)
    val downloadProgressValue by bind<TextView>(R.id.appview_download_progress_number)
    val cancelDownload by bind<ImageView>(R.id.appview_download_cancel_button)
    val resumeDownload by bind<ImageView>(R.id.appview_download_resume_download)
    val pauseDownload by bind<ImageView>(R.id.appview_download_pause_download)

    lateinit var layoutManager: LinearLayoutManager

    override fun bindView(itemView: View) {
      super.bindView(itemView)
      layoutManager = LinearLayoutManager(itemView.context, RecyclerView.HORIZONTAL, false)
      SnapToStartHelper().attachToRecyclerView(mediaList)

      val fadeIn: Animation = AlphaAnimation(0f, 1f)
      fadeIn.duration = 1000
      val fadeOut: Animation = AlphaAnimation(1f, 0f)
      fadeOut.duration = 500
      descriptionSwitcher.inAnimation = fadeIn
      descriptionSwitcher.outAnimation = fadeOut

      mediaList.addItemDecoration(object : ItemDecoration() {
        override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView,
                                    state: RecyclerView.State) {
          val margin = AptoideUtils.ScreenU.getPixelsForDip(6, view.resources)
          outRect[0, 0, margin] = 0
        }
      })
      mediaList.layoutManager = layoutManager
    }
  }
}