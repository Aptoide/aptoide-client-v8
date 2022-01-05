package cm.aptoide.pt.home.more.base

import cm.aptoide.pt.view.app.Application

data class ListAppsClickEvent<T : Application>(val application: T, val appPosition: Int)
