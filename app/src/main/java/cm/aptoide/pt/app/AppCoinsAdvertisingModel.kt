package cm.aptoide.pt.app

data class AppCoinsAdvertisingModel(val appcReward: Double = -1.0,
                                    val hasAdvertising: Boolean = false,
                                    val fiatReward: Double = -1.0,
                                    val fiatCurrency: String = "",
                                    val appcBudget: Double = -1.0,
                                    val endDate: String = "")