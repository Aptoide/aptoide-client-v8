package cm.aptoide.aptoideviews.socialmedia

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import cm.aptoide.aptoideviews.R
import com.jakewharton.rxbinding.view.RxView
import kotlinx.android.synthetic.main.social_media_view.view.*
import rx.Observable

class SocialMediaView : FrameLayout {

  constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
    inflate(context, R.layout.social_media_view, this)
  }


  fun onSocialMediaClick(): Observable<SocialMediaType> {
    return Observable.merge(
      onFacebookClicked(),
      onInstagramClicked(),
      onTwitterClicked(),
      onTikTokClicked()
    )
  }

  private fun onFacebookClicked(): Observable<SocialMediaType> {
    return RxView.clicks(facebook_button).map { SocialMediaType.FACEBOOK_CLICK }
  }

  private fun onInstagramClicked(): Observable<SocialMediaType> {
    return RxView.clicks(instagram_button).map { SocialMediaType.INSTAGRAM_CLICK }
  }

  private fun onTwitterClicked(): Observable<SocialMediaType> {
    return RxView.clicks(twitter_button).map { SocialMediaType.TWITTER_CLICK }
  }

  private fun onTikTokClicked(): Observable<SocialMediaType> {
    return RxView.clicks(tiktok_button).map { SocialMediaType.TIKTOK_CLICK }
  }

  enum class SocialMediaType { INSTAGRAM_CLICK, TWITTER_CLICK, FACEBOOK_CLICK, TIKTOK_CLICK}

}