package com.aptoide.aptoide_ab_testing.service

import com.aptoide.aptoide_ab_testing.model.EvalContext
import com.aptoide.aptoide_ab_testing.model.Flag
import com.aptoide.aptoide_ab_testing.model.PostEvaluationResponseJson

interface FlagrService {
    suspend fun getFlag(flagID: String): Flag

    suspend fun postEvaluation(body: EvalContext): PostEvaluationResponseJson
}