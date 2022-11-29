package cm.aptoide.pt.feature_appview.presentation

interface TabsListProvider {

  fun getTabsList(): List<Pair<AppViewTab, Int>>

}
