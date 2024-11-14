package com.aptoide.android.aptoidegames.chatbot.data

import cm.aptoide.pt.feature_apps.data.App

interface GetAppApiRepository {
    suspend fun getApp(packageName: String, bypassCache: Boolean = false): App
}
