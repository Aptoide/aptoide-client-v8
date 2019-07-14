package cm.aptoide.pt.app

import cm.aptoide.pt.account.view.store.StoreManager
import cm.aptoide.pt.app.migration.AppcMigrationManager
import cm.aptoide.pt.dataprovider.model.v7.store.Store
import cm.aptoide.pt.install.Install
import cm.aptoide.pt.install.InstallManager
import cm.aptoide.pt.view.AppViewConfiguration
import cm.aptoide.pt.view.app.*
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.anyLong
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import rx.Observable
import rx.Single
import java.util.Collections.emptyList


class AppViewModelManagerTest {

  @Mock
  private lateinit var storeManager: StoreManager
  private lateinit var marketName: String
  @Mock
  private lateinit var appCenter: AppCenter
  private lateinit var downloadStateParser: DownloadStateParser
  @Mock
  private lateinit var installManager: InstallManager
  @Mock
  private lateinit var appcMigrationManager: AppcMigrationManager
  @Mock
  private lateinit var appCoinsManager: AppCoinsManager
  @Mock
  private lateinit var store: Store

  @Before
  fun setupAppViewModelManagerTest() {
    MockitoAnnotations.initMocks(this)
    marketName = "marketName"
    downloadStateParser = DownloadStateParser()
  }

  @Test
  fun testAppModelLoadWithAppId() {
    // Setup
    val appRating = AppRating(1f, 1, emptyList())
    val appStats = AppStats(appRating, appRating, 1, 1)
    val bdsFlags: List<String> = ArrayList()
    val detailedApp =
        DetailedApp(1.toLong(), "anyString", "anyString", 1.toLong(), "anyString", "anyString",
            "anyString", "anyString", true, null,
            null, null, null, null, 1.toLong(), null, null, null, 1, null, null, store, null,
            appStats, null, null, null, true, true, null, false, false, bdsFlags, false, "")
    val detailedAppRequestResult = DetailedAppRequestResult(detailedApp)
    val appViewConfiguration =
        AppViewConfiguration(1.toLong(), "anyString", "anyString", "", null, null, "", "", 0.0,
            "", "", "")

    val appViewModelManager =
        AppViewModelManager(appViewConfiguration, storeManager, marketName, appCenter,
            downloadStateParser, installManager, appcMigrationManager, appCoinsManager)

    // When the configuration is initialized with an app id and a result is returned
    `when`(appCenter.loadDetailedApp(1.toLong(), "anyString", "anyString")).thenReturn(
        Single.just(detailedAppRequestResult))
    `when`(store.id).thenReturn(1.toLong())
    `when`(storeManager.isSubscribed(anyLong())).thenReturn(Observable.just(true))

    var appViewViewModel = appViewModelManager.getAppModel()
        .toBlocking()
        .value()

    //Then the correct loadDetailedApp should be called
    verify(appCenter).loadDetailedApp(1.toLong(), "anyString", "anyString")

    //And a AppModel should be returned with a not null app, with no loading and no errors
    Assert.assertNotNull(appViewViewModel.getAppId())
    Assert.assertEquals(false, appViewViewModel.isLoading())
    Assert.assertEquals(false, appViewViewModel.hasError())

    //Test if app is cached
    appViewViewModel = appViewModelManager.getAppModel()
        .toBlocking()
        .value()
    Assert.assertNotNull(appViewViewModel.getAppId())
    Assert.assertEquals(false, appViewViewModel.isLoading())
    Assert.assertEquals(false, appViewViewModel.hasError())

    //Since there's a cached app there should not be any interactions with the AppCenter
    verifyZeroInteractions(appCenter)
  }

  @Test
  fun testAppModelLoadWithMd5() {
    // Setup with md5 and negative appId
    val appRating = AppRating(1f, 1, emptyList())
    val appStats = AppStats(appRating, appRating, 1, 1)
    val bdsFlags: List<String> = ArrayList()
    val detailedApp =
        DetailedApp(1.toLong(), "anyString", "anyString", 1.toLong(), "anyString", "anyString",
            "anyString", "anyString", true, null,
            null, null, null, null, 1.toLong(), "md5", null, null, 1, null, null, store, null,
            appStats, null, null, null, true, true, null, false, false, bdsFlags, false, "")
    val detailedAppRequestResult = DetailedAppRequestResult(detailedApp)
    val appViewConfiguration =
        AppViewConfiguration((-1).toLong(), "anyString", "anyString", "", null, null, "md5", "",
            0.0,
            "", "", "")

    val appViewModelManager =
        AppViewModelManager(appViewConfiguration, storeManager, marketName, appCenter,
            downloadStateParser, installManager, appcMigrationManager, appCoinsManager)

    // When the configuration is initialized with an app id and a result is returned
    `when`(appCenter.loadDetailedAppFromMd5("md5")).thenReturn(
        Single.just(detailedAppRequestResult))
    `when`(store.id).thenReturn(1.toLong())
    `when`(storeManager.isSubscribed(anyLong())).thenReturn(Observable.just(true))

    var appViewViewModel = appViewModelManager.getAppModel()
        .toBlocking()
        .value()

    //Then the correct loadDetailedApp should be called
    verify(appCenter).loadDetailedAppFromMd5("md5")

    //And a AppModel should be returned with a not null app, with no loading and no errors
    Assert.assertNotNull(appViewViewModel.getAppId())
    Assert.assertEquals(false, appViewViewModel.isLoading())
    Assert.assertEquals(false, appViewViewModel.hasError())

    //Test if app is cached
    appViewViewModel = appViewModelManager.getAppModel()
        .toBlocking()
        .value()
    Assert.assertNotNull(appViewViewModel.getAppId())
    Assert.assertEquals(false, appViewViewModel.isLoading())
    Assert.assertEquals(false, appViewViewModel.hasError())

    //Since there's a cached app there should not be any interactions with the AppCenter
    verifyZeroInteractions(appCenter)
  }

  @Test
  fun testAppModelLoadWithUniqueName() {
    // Setup with unique name (with negativeId and without md5)
    val appRating = AppRating(1f, 1, emptyList())
    val appStats = AppStats(appRating, appRating, 1, 1)
    val bdsFlags: List<String> = ArrayList()
    val detailedApp =
        DetailedApp(1.toLong(), "anyString", "anyString", 1.toLong(), "anyString", "anyString",
            "anyString", "anyString", true, null,
            null, null, null, null, 1.toLong(), null, null, null, 1, null, null, store, null,
            appStats, null, null, null, true, true, "uniqueName", false, false, bdsFlags, false, "")
    val detailedAppRequestResult = DetailedAppRequestResult(detailedApp)
    val appViewConfiguration =
        AppViewConfiguration((-1).toLong(), "anyString", "anyString", "", null, null, null,
            "uniqueName", 0.0,
            "", "", "")

    val appViewModelManager =
        AppViewModelManager(appViewConfiguration, storeManager, marketName, appCenter,
            downloadStateParser, installManager, appcMigrationManager, appCoinsManager)

    // When the configuration is initialized with an app id and a result is returned
    `when`(appCenter.loadDetailedAppFromUniqueName("uniqueName")).thenReturn(
        Single.just(detailedAppRequestResult))
    `when`(store.id).thenReturn(1.toLong())
    `when`(storeManager.isSubscribed(anyLong())).thenReturn(Observable.just(true))

    var appViewViewModel = appViewModelManager.getAppModel()
        .toBlocking()
        .value()

    //Then the correct loadDetailedApp should be called
    verify(appCenter).loadDetailedAppFromUniqueName("uniqueName")

    //And a AppModel should be returned with a not null app, with no loading and no errors
    Assert.assertNotNull(appViewViewModel.getAppId())
    Assert.assertEquals(false, appViewViewModel.isLoading())
    Assert.assertEquals(false, appViewViewModel.hasError())

    //Test if app is cached
    appViewViewModel = appViewModelManager.getAppModel()
        .toBlocking()
        .value()
    Assert.assertNotNull(appViewViewModel.getAppId())
    Assert.assertEquals(false, appViewViewModel.isLoading())
    Assert.assertEquals(false, appViewViewModel.hasError())

    //Since there's a cached app there should not be any interactions with the AppCenter
    verifyZeroInteractions(appCenter)
  }

  @Test
  fun testAppModelLoadDefault() {
    // Setup without unique name, with negativeId and without md5 (last resort)
    val appRating = AppRating(1f, 1, emptyList())
    val appStats = AppStats(appRating, appRating, 1, 1)
    val bdsFlags: List<String> = ArrayList()
    val detailedApp =
        DetailedApp((-1).toLong(), "anyString", "packageName", 1.toLong(), "anyString", "anyString",
            "anyString", "anyString", true, null,
            null, null, null, null, 1.toLong(), null, null, null, 1, null, null, store, null,
            appStats, null, null, null, true, true, null, false, false, bdsFlags, false, "")
    val detailedAppRequestResult = DetailedAppRequestResult(detailedApp)
    val appViewConfiguration =
        AppViewConfiguration((-1).toLong(), "packageName", "storeName", "", null, null, null, null,
            0.0,
            "", "", "")

    val appViewModelManager =
        AppViewModelManager(appViewConfiguration, storeManager, marketName, appCenter,
            downloadStateParser, installManager, appcMigrationManager, appCoinsManager)

    // When the configuration is initialized with an app id and a result is returned
    `when`(appCenter.loadDetailedApp("packageName", "storeName")).thenReturn(
        Single.just(detailedAppRequestResult))
    `when`(store.id).thenReturn(1.toLong())
    `when`(store.name).thenReturn("storeName")
    `when`(storeManager.isSubscribed(anyLong())).thenReturn(Observable.just(true))

    var appViewViewModel = appViewModelManager.getAppModel()
        .toBlocking()
        .value()

    //Then the correct loadDetailedApp should be called
    verify(appCenter).loadDetailedApp("packageName", "storeName")

    //And a AppModel should be returned with a not null app, with no loading and no errors
    Assert.assertNotNull(appViewViewModel.getAppId())
    Assert.assertEquals(false, appViewViewModel.isLoading())
    Assert.assertEquals(false, appViewViewModel.hasError())

    //Test if app is cached
    appViewViewModel = appViewModelManager.getAppModel()
        .toBlocking()
        .value()
    Assert.assertNotNull(appViewViewModel.getAppId())
    Assert.assertEquals(false, appViewViewModel.isLoading())
    Assert.assertEquals(false, appViewViewModel.hasError())

    //Since there's a cached app there should not be any interactions with the AppCenter
    verifyZeroInteractions(appCenter)
  }

  @Test
  fun testAppViewModel() {
    val appRating = AppRating(1f, 1, emptyList())
    val appStats = AppStats(appRating, appRating, 1, 1)
    val bdsFlags: List<String> = ArrayList()
    val detailedApp =
        DetailedApp((-1).toLong(), "anyString", "packageName", 1.toLong(), "anyString", "anyString",
            "anyString", "anyString", true, null,
            null, null, null, null, 1.toLong(), "anyString", null, null, 1, null, null, store, null,
            appStats, null, null, null, false, true, null, false, true, bdsFlags, false, "")
    val detailedAppRequestResult = DetailedAppRequestResult(detailedApp)
    val appViewConfiguration =
        AppViewConfiguration((-1).toLong(), "packageName", "storeName", "", null, null, null, null,
            0.0,
            "", "", "")

    val appViewModelManager =
        spy(AppViewModelManager(appViewConfiguration, storeManager, marketName, appCenter,
            downloadStateParser, installManager, appcMigrationManager, appCoinsManager))

    `when`(store.id).thenReturn(1.toLong())
    `when`(store.name).thenReturn("storeName")
    `when`(storeManager.isSubscribed(anyLong())).thenReturn(Observable.just(true))
    `when`(appCenter.loadDetailedApp("packageName", "storeName")).thenReturn(
        Single.just(detailedAppRequestResult))
    `when`(appcMigrationManager.isAppMigrated("packageName")).thenReturn(
        Observable.just(false))
    `when`(appcMigrationManager.isMigrationApp("packageName", "", 1, 1.toLong(), true)).thenReturn(
        Observable.just(false))
    `when`(installManager.getInstall("anyString", "packageName", 1)).thenReturn(
        Observable.just(
            Install(0, Install.InstallationStatus.INITIAL_STATE, Install.InstallationType.INSTALL,
                false, 0, "anyString", "packageName", 1, "1", "anyString", "anyString")))
    `when`(appCoinsManager.hasActiveCampaign("packageName", 1)).thenReturn(Single.just(true))

    var appViewModel = appViewModelManager.getAppViewModel().toBlocking().value()

    // Test our AppModel
    verify(appCenter).loadDetailedApp("packageName", "storeName")
    Assert.assertEquals(-1, appViewModel.appModel.appId)
    Assert.assertEquals("packageName", appViewModel.appModel.packageName)

    // Test our DownloadModel
    Assert.assertEquals(DownloadModel.Action.INSTALL, appViewModel.downloadModel.action)
    Assert.assertEquals(DownloadModel.DownloadState.INDETERMINATE, appViewModel.downloadModel.downloadState)
    Assert.assertEquals(0, appViewModel.downloadModel.progress)

    // Test our AppCoinsModel
    verify(appCoinsManager).hasActiveCampaign("packageName", 1)
    Assert.assertEquals(true, appViewModel.appCoinsViewModel.hasAdvertising())

    // Test our MigrationModel
    Assert.assertEquals(false, appViewModel.migrationModel.isMigrated)

    // Repeat our test and verify caches
    appViewModel = appViewModelManager.getAppViewModel().toBlocking().value()
    verifyZeroInteractions(appCenter) // AppModel
    verifyZeroInteractions(appCoinsManager) // AppCoinsModel

    // Still check that the result is the same
    Assert.assertEquals(-1, appViewModel.appModel.appId)
    Assert.assertEquals("packageName", appViewModel.appModel.packageName)
    Assert.assertEquals(DownloadModel.Action.INSTALL, appViewModel.downloadModel.action)
    Assert.assertEquals(DownloadModel.DownloadState.INDETERMINATE, appViewModel.downloadModel.downloadState)
    Assert.assertEquals(0, appViewModel.downloadModel.progress)
    Assert.assertEquals(true, appViewModel.appCoinsViewModel.hasAdvertising())
    Assert.assertEquals(false, appViewModel.migrationModel.isMigrated)

  }


}