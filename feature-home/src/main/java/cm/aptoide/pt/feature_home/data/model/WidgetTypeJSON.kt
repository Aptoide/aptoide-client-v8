package cm.aptoide.pt.feature_home.data.model

import androidx.annotation.Keep

@Keep
@Suppress("unused")
enum class WidgetTypeJSON {
  APPS_GROUP,
  ADS,
  APPCOINS_ADS,
  ESKILLS,
  ACTION_ITEM,
  NEWS_ITEM,
  NEW_APP,
  NEW_APP_VERSION,
  IN_GAME_EVENT,
  APP_COMING_SOON,
  MY_GAMES,
  STORE_GROUPS,
  HTML_GAMES,
  GAMES_MATCH,
  DISPLAYS // this is still on the webservice but we don't render it... need to ask to remove for v10 vercode.
}