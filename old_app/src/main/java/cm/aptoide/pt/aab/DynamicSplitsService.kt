package cm.aptoide.pt.aab

interface DynamicSplitsService {
  suspend fun getDynamicSplitsByMd5(apkMd5Sum: String): DynamicSplitsModel
}