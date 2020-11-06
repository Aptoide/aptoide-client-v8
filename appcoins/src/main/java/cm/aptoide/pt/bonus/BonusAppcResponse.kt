package cm.aptoide.pt.bonus

import java.util.*

data class BonusAppcResponse(val result: List<Result> = Collections.emptyList(),
                             val status: String? = null,
                             val update_date: String? = null) {

  data class Result(val amount: Int = 0, val bonus: Int = 0, val level: Int = 0)
}