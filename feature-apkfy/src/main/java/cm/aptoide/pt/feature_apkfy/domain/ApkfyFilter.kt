package cm.aptoide.pt.feature_apkfy.domain

interface ApkfyFilter {

  fun filter(apkfyModel: ApkfyModel): ApkfyModel?
}
