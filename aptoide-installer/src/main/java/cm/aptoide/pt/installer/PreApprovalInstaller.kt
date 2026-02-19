package cm.aptoide.pt.installer

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageInstaller.PACKAGE_SOURCE_STORE
import android.content.pm.PackageInstaller.Session
import android.content.pm.PackageInstaller.SessionParams.USER_ACTION_NOT_REQUIRED
import android.content.pm.PackageManager
import android.icu.util.ULocale
import android.os.Build
import androidx.annotation.RequiresApi
import cm.aptoide.pt.install_manager.AbortException
import cm.aptoide.pt.install_manager.dto.InstallPackageInfo
import cm.aptoide.pt.install_manager.dto.InstallationFile
import cm.aptoide.pt.install_manager.dto.hasObb
import cm.aptoide.pt.install_manager.workers.PackageInstaller
import cm.aptoide.pt.installer.di.DownloadsPath
import cm.aptoide.pt.installer.obb.ObbService
import cm.aptoide.pt.installer.obb.installOBBs
import cm.aptoide.pt.installer.obb.removeObbFromStore
import cm.aptoide.pt.installer.platform.INSTALL_SESSION_API_COMPLETE_ACTION
import cm.aptoide.pt.installer.platform.InstallEvents
import cm.aptoide.pt.installer.platform.InstallPermissions
import cm.aptoide.pt.installer.platform.InstallResult
import cm.aptoide.pt.installer.platform.PREAPPROVAL_SESSION_API_COMPLETE_ACTION
import cm.aptoide.pt.installer.platform.PreApprovalEvents
import cm.aptoide.pt.installer.platform.PreApprovalResult
import cm.aptoide.pt.installer.platform.UNINSTALL_API_COMPLETE_ACTION
import cm.aptoide.pt.installer.platform.UninstallEvents
import cm.aptoide.pt.installer.platform.UninstallResult
import cm.aptoide.pt.installer.platform.copyWithProgressTo
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.withTimeoutOrNull
import timber.log.Timber
import java.io.File
import java.util.Locale
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

@Singleton
class PreApprovalInstaller @Inject constructor(
  @ApplicationContext private val context: Context,
  @DownloadsPath private val downloadsPath: File,
  private val installEvents: InstallEvents,
  private val preApprovalEvents: PreApprovalEvents,
  private val uninstallEvents: UninstallEvents,
  private val installPermissions: InstallPermissions,
  private val preApprovalIconProvider: PreApprovalIconProvider,
) : PackageInstaller {
  private val initialPermissionsAllowed = context.getPermissionsState()
  private val preApprovedSessions: MutableMap<String, Int> = ConcurrentHashMap()

  override fun requestUserPreApproval(
    packageName: String,
    installPackageInfo: InstallPackageInfo,
  ): Flow<Unit> = flow {
    //API level does not support pre-approval
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
      emit(Unit)
      return@flow
    }

    //Package name already pre-approved — verify session is still valid
    preApprovedSessions[packageName]?.let { sessionId ->
      if (context.packageManager.packageInstaller.getSessionInfo(sessionId) != null) {
        emit(Unit)
        return@flow
      }
      // Session is no longer valid, remove and re-request preapproval
      Timber.w("Pre-approved session $sessionId for $packageName is no longer valid")
      preApprovedSessions.remove(packageName)
    }

    if (installPackageInfo.hasObb()) {
      installPermissions.checkIfCanWriteExternal()
    }
    installPermissions.checkIfCanInstall()

    val details = buildPreapprovalDetails(
      packageName = packageName,
      installPackageInfo = installPackageInfo
    )

    if (details == null) {
      Timber.w("Preapproval skipped: missing app label for $packageName")
      emit(Unit)
      return@flow
    }

    val sessionId = createInstallSession(packageName)
    val preapprovalIntentSender = PendingIntent
      .getBroadcast(
        context,
        PREAPPROVAL_REQUEST_CODE,
        Intent(PREAPPROVAL_SESSION_API_COMPLETE_ACTION)
          .setPackage(context.packageName)
          .putExtra("${context.packageName}.pn", packageName)
          .putExtra("${context.packageName}.ap", installPackageInfo.payload),
        PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
      )
      .intentSender

    context.packageManager.packageInstaller.openSession(sessionId).use { session ->
      runCatching {
        session.requestUserPreapproval(details, preapprovalIntentSender)
      }.onFailure { error ->
        Timber.w(error, "Preapproval request failed for $packageName")
        runCatching { session.abandon() }
        preApprovedSessions.remove(packageName)
        emit(Unit)
        return@flow
      }
    }

    when (val result = preApprovalEvents.events.filter { it.sessionId == sessionId }.first()) {
      is PreApprovalResult.Success -> {
        preApprovedSessions[packageName] = sessionId
        emit(Unit)
      }

      is PreApprovalResult.Blocked -> {
        Timber.i("Preapproval not available for $packageName: ${result.message}")
        runCatching { context.packageManager.packageInstaller.abandonSession(sessionId) }
        preApprovedSessions.remove(packageName)
        emit(Unit)
      }

      is PreApprovalResult.Abort -> {
        Timber.i("Preapproval aborted for $packageName: ${result.message}")
        runCatching { context.packageManager.packageInstaller.abandonSession(sessionId) }
        preApprovedSessions.remove(packageName)
        throw AbortException(result.message)
      }

      is PreApprovalResult.Fail -> {
        Timber.w("Preapproval failed for $packageName: ${result.message}")
        runCatching { context.packageManager.packageInstaller.abandonSession(sessionId) }
        preApprovedSessions.remove(packageName)
        emit(Unit)
      }
    }
  }

  @SuppressLint("RequestInstallPackagesPolicy")
  override fun install(
    packageName: String,
    installPackageInfo: InstallPackageInfo,
  ): Flow<Int> = flow {
    emit(0)
    if (installPackageInfo.hasObb()) {
      installPermissions.checkIfCanWriteExternal()
    }
    installPermissions.checkIfCanInstall()

    val filesDir = File(downloadsPath, packageName)

    if (!filesDir.exists()) throw IllegalStateException("Necessary files do not exist for app $packageName")

    val checkFraction = 49
    val (apkFiles, obbFiles) = installPackageInfo.getCheckedFiles(filesDir) {
      emit((it * checkFraction).toInt())
    }
    val totalApkSize = apkFiles.totalLength
    val totalObbSize = obbFiles.totalLength
    val apkFraction = 49.0 * totalApkSize / (totalApkSize + totalObbSize)
    val obbFraction = 49.0 * totalObbSize / (totalApkSize + totalObbSize)
    if (totalObbSize > 0) {
      if (initialPermissionsAllowed) {
        obbFiles.installOBBs(packageName) {
          emit(checkFraction + (obbFraction * it / totalObbSize).toInt())
        }
      } else {
        ObbService.bindServiceAndWaitForResult(
          context = context,
          packageName = packageName,
          obbFilePaths = obbFiles.map { it.absolutePath }
        ).let { movedOBBFiles ->
          if (movedOBBFiles) {
            //TODO: improve installation progress when using OBBService
            emit(checkFraction + (obbFraction / totalObbSize).toInt())
          } else {
            throw IllegalStateException("Error moving OBB files")
          }
        }
      }
    }

    val preapprovedSessionId = preApprovedSessions.remove(packageName)
    val sessionId = preapprovedSessionId ?: createInstallSession(packageName)
    val progressBase = checkFraction + obbFraction.toInt()

    var result = commitSessionInstall(
      sessionId = sessionId,
      packageName = packageName,
      installPackageInfo = installPackageInfo,
      apkFiles = apkFiles,
      totalApkSize = totalApkSize,
      progressBase = progressBase,
      apkFraction = apkFraction,
      emitProgress = { emit(it) },
      isPreapprovedSession = preapprovedSessionId != null,
    )

    // If the pre-approved session failed after commit, retry with a fresh session
    // so the system prompts the user for install permission
    if (result is InstallResult.Fail && preapprovedSessionId != null) {
      Timber.i("Pre-approved session install failed for $packageName, retrying with standard install")
      val freshSessionId = createInstallSession(packageName)
      result = commitSessionInstall(
        sessionId = freshSessionId,
        packageName = packageName,
        installPackageInfo = installPackageInfo,
        apkFiles = apkFiles,
        totalApkSize = totalApkSize,
        progressBase = progressBase,
        apkFraction = apkFraction,
        emitProgress = { emit(it) },
      )
    }

    when (result) {
      is InstallResult.Fail -> {
        if (totalObbSize > 0) removeObbFromStore(packageName)
        throw Exception(result.message)
      }

      is InstallResult.Abort -> {
        if (totalObbSize > 0) removeObbFromStore(packageName)
        throw AbortException(result.message)
      }

      is InstallResult.Success -> {
        emit(100)
        filesDir.deleteRecursively()
        obbFiles.deleteFromCache()
      }
    }
  }
    .distinctUntilChanged()

  override fun uninstall(packageName: String): Flow<Int> = flow {
    emit(0)
    val id = Random.nextInt()

    val intentSender = PendingIntent
      .getBroadcast(
        context,
        UNINSTALL_REQUEST_CODE,
        Intent(UNINSTALL_API_COMPLETE_ACTION)
          .putExtra("${context.packageName}.uninstall_id", id)
          .setPackage(context.packageName),
        // This is essential to be like that for having extras in the intent
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
          PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        } else {
          PendingIntent.FLAG_UPDATE_CURRENT
        }
      )
      .intentSender

    context.packageManager.packageInstaller.uninstall(packageName, intentSender)
    emit(99)

    when (val result = uninstallEvents.events.filter { it.id == id }.first()) {
      is UninstallResult.Fail -> throw Exception(result.message)
      is UninstallResult.Abort -> throw AbortException(result.message)
      is UninstallResult.Success -> emit(100)
    }
  }

  /**
   * Opens a session, loads the APK files, commits, and waits for the install result.
   * Returns the [InstallResult] without throwing on failure, so the caller can decide
   * whether to retry (e.g. when a pre-approved session fails).
   *
   * When [isPreapprovedSession] is true, also listens for preapproval failure events
   * since the system may report errors through that channel instead of the install channel.
   */
  @SuppressLint("RequestInstallPackagesPolicy")
  private suspend fun commitSessionInstall(
    sessionId: Int,
    packageName: String,
    installPackageInfo: InstallPackageInfo,
    apkFiles: List<File>,
    totalApkSize: Long,
    progressBase: Int,
    apkFraction: Double,
    emitProgress: suspend (Int) -> Unit,
    isPreapprovedSession: Boolean = false,
  ): InstallResult {
    val session = context.packageManager.packageInstaller.openSession(sessionId)
    try {
      session.loadFiles(apkFiles) {
        emitProgress(progressBase + (apkFraction * it / totalApkSize).toInt())
      }
      session.commit(
        PendingIntent
          .getBroadcast(
            context,
            SESSION_INSTALL_REQUEST_CODE,
            Intent(INSTALL_SESSION_API_COMPLETE_ACTION)
              .setPackage(context.packageName)
              .putExtra("${context.packageName}.pn", packageName)
              .putExtra("${context.packageName}.ap", installPackageInfo.payload),
            // This is essential to be like that for having extras in the intent
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
              PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            } else {
              PendingIntent.FLAG_UPDATE_CURRENT
            }
          )
          .intentSender
      )
      emitProgress(99)

      val installResultFlow = installEvents.events
        .filter { it.sessionId == sessionId }

      return if (isPreapprovedSession) {
        // Also listen for preapproval failures, since the system may report
        // pre-approved session errors after the session commit
        val preapprovalFailureFlow = preApprovalEvents.events
          .filter { it.sessionId == sessionId && it !is PreApprovalResult.Success }
          .map<PreApprovalResult, InstallResult> { preapprovalResult ->
            when (preapprovalResult) {
              is PreApprovalResult.Fail -> InstallResult.Fail(sessionId, preapprovalResult.message)
              is PreApprovalResult.Blocked -> InstallResult.Fail(
                sessionId,
                preapprovalResult.message
              )

              is PreApprovalResult.Abort -> InstallResult.Abort(
                sessionId,
                preapprovalResult.message
              )

              is PreApprovalResult.Success -> error("Unreachable")
            }
          }
        merge(installResultFlow, preapprovalFailureFlow).first()
      } else {
        installResultFlow.first()
      }
    } catch (e: Exception) {
      runCatching { session.abandon() }
      throw e
    } finally {
      session.close()
    }
  }

  private suspend fun InstallPackageInfo.getCheckedFiles(
    downloadsDir: File,
    progress: suspend (Double) -> Unit,
  ): Pair<List<File>, List<File>> = installationFiles.run {
    val apks = mutableListOf<File>()
    val obbs = mutableListOf<File>()
    forEachIndexed { index, value ->
      when (value.type) {
        InstallationFile.Type.BASE,
        InstallationFile.Type.PFD_INSTALL_TIME,
        InstallationFile.Type.PAD_INSTALL_TIME,
          -> apks.add(value.toCheckedFile(downloadsDir))

        InstallationFile.Type.OBB_MAIN,
        InstallationFile.Type.OBB_PATCH,
          -> obbs.add(value.toCheckedFile(downloadsDir))

        else -> {}
      }

      progress(index + 1.0 / size)
    }
    apks to obbs
  }

  private suspend fun Session.loadFiles(
    files: Collection<File>,
    progress: suspend (Long) -> Unit,
  ) {
    var processedSize: Long = 0
    files.forEach { file ->
      val size = file.length()
      file.inputStream()
        .use { apkStream ->
          openWrite(file.name, 0, size)
            .use { sessionStream ->
              apkStream
                .copyWithProgressTo(sessionStream)
                .collect {
                  progress(processedSize + it)
                }
              fsync(sessionStream)
            }
        }
      processedSize += size
    }
  }

  private fun createInstallSession(packageName: String): Int =
    context.packageManager.packageInstaller.createSession(buildSessionParams(packageName))

  private fun buildSessionParams(packageName: String): android.content.pm.PackageInstaller.SessionParams =
    android.content.pm.PackageInstaller
      .SessionParams(android.content.pm.PackageInstaller.SessionParams.MODE_FULL_INSTALL)
      .apply {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
          setRequestUpdateOwnership(true)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
          setRequireUserAction(USER_ACTION_NOT_REQUIRED)
        }
        setAppPackageName(packageName)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
          setPackageSource(PACKAGE_SOURCE_STORE)
        }

        setInstallLocation(PackageInfo.INSTALL_LOCATION_AUTO)
        setInstallReason(PackageManager.INSTALL_REASON_USER)
      }

  @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
  private suspend fun buildPreapprovalDetails(
    packageName: String,
    installPackageInfo: InstallPackageInfo,
  ): android.content.pm.PackageInstaller.PreapprovalDetails? {
    val label = installPackageInfo.appLabel ?: getInstalledAppLabel(packageName)
    if (label == null) return null

    val icon = installPackageInfo.iconUrl?.let {
      withTimeoutOrNull(ICON_FETCH_TIMEOUT_MS) { preApprovalIconProvider.getIcon(it) }
    }

    val builder = android.content.pm.PackageInstaller.PreapprovalDetails.Builder()
      .setPackageName(packageName)
      .setLabel(label)
      .setLocale(ULocale.forLocale(Locale.getDefault()))

    if (icon != null) {
      builder.setIcon(icon)
    }

    return builder.build()
  }

  private fun getInstalledAppLabel(packageName: String): CharSequence? =
    runCatching {
      val appInfo = context.packageManager.getApplicationInfo(packageName, 0)
      context.packageManager.getApplicationLabel(appInfo)
    }.getOrNull()

  companion object {
    private const val SESSION_INSTALL_REQUEST_CODE = 18
    private const val UNINSTALL_REQUEST_CODE = 19
    private const val PREAPPROVAL_REQUEST_CODE = 20
    private const val ICON_FETCH_TIMEOUT_MS = 3_000L
  }
}
