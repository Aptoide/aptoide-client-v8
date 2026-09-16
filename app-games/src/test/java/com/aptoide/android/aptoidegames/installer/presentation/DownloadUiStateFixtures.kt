package com.aptoide.android.aptoidegames.installer.presentation

import cm.aptoide.pt.download_view.presentation.DownloadUiState
import cm.aptoide.pt.install_manager.dto.InstallPackageInfo

/**
 * Every [DownloadUiState] split by whether its button can still start an install. Both the
 * attribution gate and the AppView diversion key off that split, so the two lists are shared
 * rather than restated per test. Completeness is enforced by the compiler instead: the `when`
 * in `canTriggerInlineInstall` is exhaustive, so a new subtype breaks the build there first.
 */
internal object DownloadUiStateFixtures {

  private val packageInfo = InstallPackageInfo(0)

  /** States whose action can divert into a Play inline install. */
  val preTap: List<DownloadUiState> = listOf(
    DownloadUiState.Install(installWith = {}),
    DownloadUiState.Outdated(open = {}, updateWith = {}, uninstall = {}),
    DownloadUiState.Migrate(open = {}, uninstall = {}, migrateWith = {}),
    DownloadUiState.MigrateAlias(migrateAliasWith = {}),
    DownloadUiState.Error(retryWith = {}),
  )

  /** The absent state plus everything already past the tap. */
  val postTap: List<DownloadUiState?> = listOf(
    null,
    DownloadUiState.Waiting(installPackageInfo = packageInfo, action = null),
    DownloadUiState.Downloading(installPackageInfo = packageInfo, cancel = {}),
    DownloadUiState.ReadyToInstall(cancel = {}),
    DownloadUiState.Installing(installPackageInfo = packageInfo),
    DownloadUiState.Uninstalling(installPackageInfo = packageInfo),
    DownloadUiState.Installed(open = {}, uninstall = {}),
  )

  val all: List<DownloadUiState?> = preTap + postTap
}
