package cm.aptoide.pt.app.view.googleplayservices

import android.util.Log
import cm.aptoide.pt.app.DownloadModel
import cm.aptoide.pt.app.DownloadStateParser
import cm.aptoide.pt.install.Install
import cm.aptoide.pt.install.InstallManager
import cm.aptoide.pt.install.InstalledRepository
import rx.Observable

class PlayServicesAppsProvider(private val installedRepository: InstalledRepository,
                               private val installManager: InstallManager,
                               private val downloadStateParser: DownloadStateParser) {

  fun getPlayServicesApps(): Observable<List<PlayServicesApp>> {
    return getHandpickedApps()
        .flatMap { apps ->
          Observable.just(apps)
              .flatMapIterable { apps -> apps }
              .flatMap { app ->
                val appObs = Observable.just(app)
                val isAppInstalled = installedRepository.isInstalled(app.packageName)
                val appDownload =
                    installManager.getInstall(app.md5sum, app.packageName, app.versionCode.toInt())
                Observable.zip(appObs, isAppInstalled,
                    appDownload) { appO, isInstalled, appDl ->
                  mergeToPlayServiceApp(appO, isInstalled, appDl)
                }
              }
              .doOnError { e -> e.printStackTrace() }
              .toList()
        }
  }

  private fun mergeToPlayServiceApp(app: PlayServicesApp, isInstalled: Boolean,
                                    appDownload: Install): PlayServicesApp {
    app.isInstalled = isInstalled
    app.downloadModel = mapDownloadModel(appDownload.type, appDownload.progress, appDownload.state)
    return app
  }

  private fun mapDownloadModel(type: Install.InstallationType, progress: Int,
                               state: Install.InstallationStatus): DownloadModel {
    return DownloadModel(downloadStateParser.parseDownloadType(type, false, false, false),
        progress, downloadStateParser.parseDownloadState(state), null)
  }

  fun getHandpickedApps(): Observable<List<PlayServicesApp>> {
    val playStore =
        PlayServicesApp(appName = "Google Play Store", id = -2, packageName = "com.android.vending",
            md5sum = "69a86acc66c915e65f117dc05a7ef190",
            versionName = "16.4.14-all [0] [PR] 265152471",
            versionCode = 81641400L,
            path = "http://pool.apk.aptoide.com/emad64/com-android-vending-81641400-47358332-69a86acc66c915e65f117dc05a7ef190.apk",
            pathAlt = "http://pool.apk.aptoide.com/emad64/com-android-vending-81641400-47358332-69a86acc66c915e65f117dc05a7ef190.apk",
            size = 21076900, developer = "Google?")

    val playServices =
        PlayServicesApp(appName = "Google Play Services", id = -3,
            packageName = "com.google.android.gms",
            md5sum = "520e7ab2544cc44a4c3d6e15959f4535",
            versionName = "18.7.19 (020400-262610125)",
            versionCode = 18719010L,
            path = "http://www.apkmirror.com/wp-content/themes/APKMirror/download.php?id=773529",
            pathAlt = "http://www.apkmirror.com/wp-content/themes/APKMirror/download.php?id=773529",
            size = 54726233, developer = "Google?")

    val googleAccountManager =
        PlayServicesApp(appName = "Google Account Manager", id = -4,
            packageName = "com.google.android.gsf.login",
            md5sum = "fe0627491c199c1b65615f1a5c2c90b6",
            versionName = "5.1-1743759",
            versionCode = 22L,
            path = "http://www.apkmirror.com/wp-content/themes/APKMirror/download.php?id=97485",
            pathAlt = "http://www.apkmirror.com/wp-content/themes/APKMirror/download.php?id=97485",
            size = 4991563, developer = "Google?")

    val googleServicesFramework =
        PlayServicesApp(appName = "Google Services Framework", id = -5,
            packageName = "com.google.android.gsf",
            md5sum = "fe0627491c199c1b65615f1a5c2c90b6",
            versionName = "6.0.1",
            versionCode = 23L,
            path = "http://www.apkmirror.com/wp-content/themes/APKMirror/download.php?id=65083",
            pathAlt = "http://www.apkmirror.com/wp-content/themes/APKMirror/download.php?id=65083",
            size = 3976028, developer = "Google?")

    val googleContactsSync =
        PlayServicesApp(appName = "Google Services Framework", id = -6,
            packageName = "com.google.android.syncadapters.contacts",
            md5sum = "20133f6fbb00d5c13cff462942d5b50b",
            versionName = "9-4832352",
            versionCode = 28L,
            path = "http://www.apkmirror.com/wp-content/themes/APKMirror/download.php?id=646361",
            pathAlt = "http://www.apkmirror.com/wp-content/themes/APKMirror/download.php?id=646361",
            size = 1662264, developer = "Google?")

    val googleCalendarSync =
        PlayServicesApp(appName = "Google Calendar Sync", id = -7,
            packageName = "com.google.android.syncadapters.calendar",
            md5sum = "1442d2f2c5ec83efd788d8ce9815978c",
            versionName = "5.2.3-99827563-release",
            versionCode = 2015080710L,
            path = "http://www.apkmirror.com/wp-content/themes/APKMirror/download.php?id=72565",
            pathAlt = "http://www.apkmirror.com/wp-content/themes/APKMirror/download.php?id=72565",
            size = 1838235, developer = "Google?")

    val twitchClips = PlayServicesApp(appName = "Twitch Clips", id = -8,
        packageName = "com.onedevarmy.twitchclips",
        md5sum = "b5fa654eef1b2a5f97ec5cde31573a59",
        versionName = "0.8-betac",
        versionCode = 9L,
        path = "http://premium.apk.aptoide.com/catappult/com-onedevarmy-twitchclips-9-46048194-b5fa654eef1b2a5f97ec5cde31573a59.apk",
        pathAlt = "http://premium.apk.aptoide.com/catappult/com-onedevarmy-twitchclips-9-46048194-b5fa654eef1b2a5f97ec5cde31573a59.apk",
        size = 6250959, developer = "Joao")
    return Observable.just(listOf(twitchClips, playStore))
  }
}