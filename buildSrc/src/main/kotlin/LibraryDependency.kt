import org.gradle.api.JavaVersion

object LibraryVersionOldModules {
  //other modules
  const val RXJAVA_2 = "2.2.19"
  const val RXJAVA_2_ANDROID = "2.1.1"
  const val RXJAVA_2_INTEROP = "0.13.7"
  const val RXJAVA = "1.3.8"
  const val RXJAVA_ANDROID = "1.2.1"
  const val RX_LINT = "1.2"
  const val RXJAVA_PROGUARD_RULES = "1.2.7.0"
  const val APP_COMPAT = "1.4.2"
  const val RX_RELAY = "1.2.0"
  const val PARCELER = "1.1.12"
  const val FACEBOOK = "4.41.0"
  const val FLURRY = "12.2.0"
  const val RAKAM = "2.7.14"
  const val AMPLITUDE = "2.34.1"
  const val INDICATIVE = "1.1.0"
  const val COROUTINES = "1.6.0"
  const val ROOM = "2.4.0"
  const val GSON = "2.8.2"
  const val STATE_MACHINE = "0.1.2"
  const val CORE_KTX = "1.9.0"
  const val CONSTRAINT_LAYOUT = "2.1.2"
  const val MATERIAL = "1.4.0"
  const val JW_RX_BINDING = "1.0.0"
  const val EPOXY = "3.8.0"
  const val PLAY_SERVICES_BASEMENT = "16.1.0"
  const val RETROFIT = "2.9.0"
  const val JACKSON = "2.8.5"
  const val OK_HTTP = "4.9.3"
  const val MOSHI = "1.14.0"
}

private object LibraryVersion {
  //main modules
  const val CORE_KTX = "1.10.1"
  const val APP_COMPAT = "1.6.1"
  const val MATERIAL = "1.9.0"
  const val CONSTRAINT_LAYOUT = "2.1.4"
  const val RETROFIT = "2.9.0"
  const val RETROFIT_GSON_CONVERTER = "2.9.0"
  const val OK_HTTP = "4.11.0"
  const val TIMBER = "5.0.1"
  const val LOTTIE = "6.0.0"
  const val FRAGMENT_KTX = "1.5.7"
  const val LIFECYCLE = "2.6.1"
  const val COIL = "2.3.0"
  const val ROOM = "2.5.1"
  const val ACTIVITY_COMPOSE = "1.7.1"
  const val COMPOSE = "1.4.3"
  const val VIEWMODEL_COMPOSE = "2.6.1"
  const val NAVIGATION_COMPOSE = "2.5.3"
  const val MATERIAL_ICONS_EXTENDED = "1.4.3"
  const val HILT = GradlePluginVersion.HILT
  const val HILT_NAV_COMPOSE = "1.0.0"
  const val RXJAVA = "1.3.8"
  const val RXJAVA_2 = "2.2.21"
  const val GSON = "2.10.1"
  const val CUSTOM_CHROME_TAB = "1.5.0"
  const val FIREBASE = "32.0.0"
  const val DATASTORE = "1.0.0"
  const val GMS_PLAY_SERVICES_ADS = "18.0.1"
  const val ACCOMPANIST_PAGER = "0.30.1"
  const val ACCOMPANIST_WEBVIEW = "0.30.1"
  const val ACCOMPANIST_PERMISSIONS = "0.30.1"
  const val LOTTIE_COMPOSE = "6.0.0"
}

object JavaLibrary {
  val SOURCE_COMPATIBILITY_JAVA_VERSION = JavaVersion.VERSION_17
  val TARGET_COMPATIBILITY_JAVA_VERSION = JavaVersion.VERSION_17
}

object LibraryDependency {
  const val CORE_KTX = "androidx.core:core-ktx:${LibraryVersion.CORE_KTX}"
  const val APP_COMPAT = "androidx.appcompat:appcompat:${LibraryVersion.APP_COMPAT}"
  const val MATERIAL = "com.google.android.material:material:${LibraryVersion.MATERIAL}"
  const val CONSTRAINT_LAYOUT =
    "androidx.constraintlayout:constraintlayout:${LibraryVersion.CONSTRAINT_LAYOUT}"
  const val KOTLIN = "org.jetbrains.kotlin:kotlin-stdlib-jdk7:${CoreVersion.KOTLIN}"
  const val KOTLIN_REFLECT = "org.jetbrains.kotlin:kotlin-reflect:${CoreVersion.KOTLIN}"
  const val RETROFIT = "com.squareup.retrofit2:retrofit:${LibraryVersion.RETROFIT}"
  const val RETROFIT_GSON_CONVERTER =
    "com.squareup.retrofit2:converter-gson:${LibraryVersion.RETROFIT_GSON_CONVERTER}"
  const val OK_HTTP = "com.squareup.okhttp3:okhttp:${LibraryVersion.OK_HTTP}"
  const val LOGGING_INTERCEPTOR =
    "com.squareup.okhttp3:logging-interceptor:${LibraryVersion.OK_HTTP}"
  const val TIMBER = "com.jakewharton.timber:timber:${LibraryVersion.TIMBER}"
  const val RXJAVA_2 = "io.reactivex.rxjava2:rxjava:${LibraryVersion.RXJAVA_2}"
  const val COROUTINES =
    "org.jetbrains.kotlinx:kotlinx-coroutines-android:${CoreVersion.COROUTINES}"
  const val COROUTINES_CORE =
    "org.jetbrains.kotlinx:kotlinx-coroutines-core:${CoreVersion.COROUTINES}"
  const val COROUTINES_RXJAVA_2 =
    "org.jetbrains.kotlinx:kotlinx-coroutines-rx2:${CoreVersion.COROUTINES}"
  const val FRAGMENT_KTX = "androidx.fragment:fragment-ktx:${LibraryVersion.FRAGMENT_KTX}"
  const val LIFECYCLE_COMMON = "androidx.lifecycle:lifecycle-common:${LibraryVersion.LIFECYCLE}"
  const val LIFECYCLE_PROCESS = "androidx.lifecycle:lifecycle-process:${LibraryVersion.LIFECYCLE}"
  const val LIFECYCLE_RUNTIME_KTX =
    "androidx.lifecycle:lifecycle-runtime-ktx:${LibraryVersion.LIFECYCLE}"
  const val NAVIGATION_FRAGMENT_KTX =
    "androidx.navigation:navigation-fragment-ktx:${CoreVersion.NAVIGATION}"
  const val NAVIGATION_UI_KTX = "androidx.navigation:navigation-ui-ktx:${CoreVersion.NAVIGATION}"
  const val COIL = "io.coil-kt:coil:${LibraryVersion.COIL}"
  const val COIL_COMPOSE = "io.coil-kt:coil-compose:${LibraryVersion.COIL}"
  const val LOTTIE = "com.airbnb.android:lottie:${LibraryVersion.LOTTIE}"
  const val ROOM = "androidx.room:room-runtime:${LibraryVersion.ROOM}"
  const val ROOM_COMPILER = "androidx.room:room-compiler:${LibraryVersion.ROOM}"
  const val ROOM_KTX = "androidx.room:room-ktx:${LibraryVersion.ROOM}"
  const val ROOM_RXJAVA2 = "androidx.room:room-rxjava2:${LibraryVersion.ROOM}"
  const val ACTIVITY_COMPOSE =
    "androidx.activity:activity-compose:${LibraryVersion.ACTIVITY_COMPOSE}"
  const val MATERIAL_COMPOSE = "androidx.compose.material:material:${LibraryVersion.COMPOSE}"
  const val ANIMATION_COMPOSE = "androidx.compose.animation:animation:${LibraryVersion.COMPOSE}"
  const val UI_TOOLING_COMPOSE = "androidx.compose.ui:ui-tooling:${LibraryVersion.COMPOSE}"
  const val UI_COMPOSE = "androidx.compose.ui:ui:${LibraryVersion.COMPOSE}"
  const val UI_UTIL = "androidx.compose.ui:ui-util:${LibraryVersion.COMPOSE}"
  const val VIEWMODEL_COMPOSE =
    "androidx.lifecycle:lifecycle-viewmodel-compose:${LibraryVersion.VIEWMODEL_COMPOSE}"
  const val NAVIGATION_COMPOSE =
    "androidx.navigation:navigation-compose:${LibraryVersion.NAVIGATION_COMPOSE}"
  const val MATERIAL_ICONS_EXTENDED =
    "androidx.compose.material:material-icons-extended:${LibraryVersion.MATERIAL_ICONS_EXTENDED}"
  const val HILT = "com.google.dagger:hilt-android:${LibraryVersion.HILT}"
  const val HILT_NAV_COMPOSE =
    "androidx.hilt:hilt-navigation-compose:${LibraryVersion.HILT_NAV_COMPOSE}"
  const val HILT_COMPILER = "com.google.dagger:hilt-compiler:${LibraryVersion.HILT}"
  const val RXJAVA = "io.reactivex:rxjava:${LibraryVersion.RXJAVA}"
  const val GSON = "com.google.code.gson:gson:${LibraryVersion.GSON}"
  const val CUSTOM_CHROME_TAB = "androidx.browser:browser:${LibraryVersion.CUSTOM_CHROME_TAB}"
  const val FIREBASE_BOM = "com.google.firebase:firebase-bom:${LibraryVersion.FIREBASE}"
  const val FIREBASE_ANALYTICS = "com.google.firebase:firebase-analytics-ktx"
  const val FIREBASE_CRASHLYTICS = "com.google.firebase:firebase-crashlytics-ktx"
  const val FIREBASE_MESSAGING = "com.google.firebase:firebase-messaging-ktx"
  const val DATASTORE = "androidx.datastore:datastore-preferences:${LibraryVersion.DATASTORE}"
  const val GMS_PLAY_SERVICES_ADS =
    "com.google.android.gms:play-services-ads-identifier:${LibraryVersion.GMS_PLAY_SERVICES_ADS}"
  const val ACCOMPANIST_PAGER =
    "com.google.accompanist:accompanist-pager:${LibraryVersion.ACCOMPANIST_PAGER}"
  const val ACCOMPANIST_WEBVIEW =
    "com.google.accompanist:accompanist-webview:${LibraryVersion.ACCOMPANIST_WEBVIEW}"
  const val ACCOMPANIST_PERMISSIONS =
    "com.google.accompanist:accompanist-permissions:${LibraryVersion.ACCOMPANIST_PERMISSIONS}"
  const val LOTTIE_COMPOSE =
    "com.airbnb.android:lottie-compose:${LibraryVersion.LOTTIE_COMPOSE}"
}
