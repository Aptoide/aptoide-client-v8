package cm.aptoide.pt.ads

class MinimalAd(var packageName: String, var networkId: Long,
                var clickUrl: String?, var cpcUrl: String?,
                var cpdUrl: String?, var appId: Long, var adId: Long,
                var cpiUrl: String?, var name: String, var iconPath: String,
                var description: String, var downloads: Int, var stars: Int,
                var modified: Long, var isHasAppc: Boolean,
                var appcAmount: Double, var currencyAmount: Double,
                var currency: String, var currencySymbol: String)