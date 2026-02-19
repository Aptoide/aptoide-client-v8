package com.aptoide.android.aptoidegames.installer.ff

import cm.aptoide.pt.feature_flags.domain.FeatureFlags
import cm.aptoide.pt.install_manager.dto.InstallPackageInfo
import com.aptoide.android.aptoidegames.analytics.GenericAnalytics
import com.aptoide.android.aptoidegames.installer.analytics.toAnalyticsPayload
import com.aptoide.android.aptoidegames.installer.ff.PreApprovalExperiment.Companion.FETCH_TIMEOUT_MS
import com.aptoide.android.aptoidegames.installer.ff.PreApprovalExperiment.Companion.FLAG_INSTALLER_TYPE
import com.aptoide.android.aptoidegames.installer.ff.PreApprovalExperiment.Companion.FLAG_PRE_APPROVAL_PACKAGES
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withTimeoutOrNull
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * A/B test experiment for the pre-approval installer.
 *
 * Relies on two remote config flags:
 * - [FLAG_INSTALLER_TYPE]: determines the installer variant ("default" or "preapproval").
 * - [FLAG_PRE_APPROVAL_PACKAGES]: a list of package names eligible for pre-approval install.
 *
 * The experiment is only active when both flags are successfully fetched.
 * An activation event is sent once we confirm that both flags were fetched correctly.
 */
@Singleton
class PreApprovalExperiment @Inject constructor(
  private val featureFlags: FeatureFlags,
  private val genericAnalytics: GenericAnalytics,
) {

  private val fetchMutex = Mutex()

  @Volatile
  private var cachedResult: ExperimentResult? = null
  private var activationEventSent = false

  /**
   * Returns whether the pre-approval installer should be used for the given [packageName].
   *
   * The PreApprovalInstaller is selected only when:
   * 1. The install started from AppView ([installPackageInfo] has context "AppView")
   * 2. The installer type flag is "preapproval".
   * 3. The pre-approval packages flag was successfully fetched.
   * 4. The [packageName] is in the list of eligible packages.
   *
   * Falls back to the default installer if the flags can't be fetched within [FETCH_TIMEOUT_MS].
   *
   * The activation event is only sent when the user installs an eligible package,
   * ensuring the A/B test cohort only includes users truly exposed to the experiment.
   */
  suspend fun shouldUsePreApprovalInstaller(
    packageName: String,
    installPackageInfo: InstallPackageInfo
  ): Boolean {
    val isFromAppView = installPackageInfo.payload?.toAnalyticsPayload()?.context == "AppView"
    if (!isFromAppView) return false

    val result = cachedResult ?: resolveExperiment()

    if (!result.isFetched) return false
    if (packageName !in result.eligiblePackages) return false

    //User eligible for AB test, since the flags are fetched and the package to download is eligible
    sendActivationEvent(result)

    return result.isPreApproval
  }

  private suspend fun resolveExperiment(): ExperimentResult = fetchMutex.withLock {
    cachedResult?.let { return it }

    val result = withTimeoutOrNull(FETCH_TIMEOUT_MS) {
      fetchExperimentResult()
    }

    if (result == null) {
      Timber.w("Pre-approval experiment timed out, falling back to default installer")
    }

    return (result ?: notFetched).also { cachedResult = it }
  }

  /**
   * Checks if the install belongs to the experiment based on the cached result.
   * Returns false if the experiment hasn't been resolved yet.
   *
   * An install is eligible when:
   * 1. The install started from AppView ([analyticsContext] == "AppView").
   * 2. The experiment flags were successfully fetched.
   * 3. The [packageName] is in the list of eligible packages.
   */
  fun isEligibleInstall(
    packageName: String,
    analyticsContext: String?
  ): Boolean {
    if (analyticsContext != "AppView") return false
    val result = cachedResult ?: return false
    return result.isFetched && packageName in result.eligiblePackages
  }

  fun sendInstallSuccessEvent(
    packageName: String,
    installPackageInfo: InstallPackageInfo
  ) {
    val context = installPackageInfo.payload?.toAnalyticsPayload()?.context
    if (!isEligibleInstall(packageName, context)) return

    genericAnalytics.logEvent(
      name = INSTALL_SUCCESS_EVENT,
      params = mapOf(
        "variant" to (cachedResult?.variant ?: "n/a"),
        "package_name" to packageName
      )
    )
  }

  private fun sendActivationEvent(result: ExperimentResult) {
    if (activationEventSent) return
    activationEventSent = true

    Timber.d("Pre-approval experiment activation: variant=${result.variant}")
    genericAnalytics.logEvent(
      name = ACTIVATION_EVENT,
      params = mapOf("variant" to result.variant)
    )
  }

  private suspend fun fetchExperimentResult(): ExperimentResult {
    val installerType = featureFlags.getFlagAsString(FLAG_INSTALLER_TYPE)
    val packages = featureFlags.getStringList(FLAG_PRE_APPROVAL_PACKAGES)

    val isValidVariant = installerType in VALID_VARIANTS
    val isFetched = isValidVariant && packages.isNotEmpty()

    if (isFetched) {
      Timber.d("Pre-approval experiment fetched: type=$installerType, packages=$packages")
    } else {
      Timber.d("Pre-approval experiment not active: type=$installerType, packages=$packages")
    }

    return ExperimentResult(
      isFetched = isFetched,
      variant = installerType ?: "n/a",
      isPreApproval = installerType == VARIANT_PRE_APPROVAL,
      eligiblePackages = packages.toSet()
    )
  }

  private data class ExperimentResult(
    val isFetched: Boolean,
    val variant: String,
    val isPreApproval: Boolean,
    val eligiblePackages: Set<String>,
  )

  private val notFetched = ExperimentResult(
    isFetched = false,
    variant = "n/a",
    isPreApproval = false,
    eligiblePackages = emptySet()
  )

  companion object {
    private const val FLAG_INSTALLER_TYPE = "exp_pre_approval_installer_type"
    private const val FLAG_PRE_APPROVAL_PACKAGES = "exp_pre_approval_packages"

    private const val VARIANT_DEFAULT = "default"
    private const val VARIANT_PRE_APPROVAL = "preapproval"
    private val VALID_VARIANTS = setOf(VARIANT_DEFAULT, VARIANT_PRE_APPROVAL)

    private const val ACTIVATION_EVENT = "exp_pre_approval_installer_activated"
    private const val INSTALL_SUCCESS_EVENT = "exp_pre_approval_install_success"

    private const val FETCH_TIMEOUT_MS = 3_000L
  }
}
