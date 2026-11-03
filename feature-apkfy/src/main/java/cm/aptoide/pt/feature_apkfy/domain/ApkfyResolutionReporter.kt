package cm.aptoide.pt.feature_apkfy.domain

/** Reports how apkfy's title resolved, so titles that cannot be installed come to light. */
fun interface ApkfyResolutionReporter {

  fun report(packageName: String?, resolution: ApkfyResolution)
}
