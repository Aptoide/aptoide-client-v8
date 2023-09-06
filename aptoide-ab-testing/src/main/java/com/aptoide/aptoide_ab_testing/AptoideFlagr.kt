package com.aptoide.aptoide_ab_testing

import com.aptoide.aptoide_ab_testing.model.EvalContext
import com.aptoide.aptoide_ab_testing.model.Flag
import com.aptoide.aptoide_ab_testing.model.PostEvaluationResponseJson
import com.aptoide.aptoide_ab_testing.service.FlagrService

class AptoideFlagr(private val service: FlagrService) {

    suspend fun getFlag(flagID: String): Flag {
        if (flagID.isBlank()) {
            throw FlagrException(message = "Flag ID is blank")
        }

        val isNumeric = flagID.toIntOrNull()
        if (isNumeric == null) {
            throw FlagrException(message = "Flag ID is not a number")
        }

        return service.getFlag(flagID)
    }

    suspend fun postEvaluation(body: EvalContext): PostEvaluationResponseJson {

        return service.postEvaluation(body)
    }
}