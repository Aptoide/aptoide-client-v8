package com.aptoide.aptoide_ab_testing.network

import com.aptoide.aptoide_ab_testing.FlagrException
import com.aptoide.aptoide_ab_testing.model.EvalContext
import com.aptoide.aptoide_ab_testing.model.Flag
import com.aptoide.aptoide_ab_testing.model.PostEvaluationResponseJson
import com.aptoide.aptoide_ab_testing.service.FlagrService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

class FlagrApiService(
    private val flagrBaseHost: String,
    okHttpClient: OkHttpClient) : FlagrService {

    private val flagr: Flagr =
        Retrofit.Builder()
            .baseUrl(flagrBaseHost)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
            .create(Flagr::class.java)

    override suspend fun getFlag(flagID: String): Flag {
        return withContext(Dispatchers.IO) {
            val getFlagResponse = flagr.getFlag(flagID)

            val flag = getFlagResponse.body()

            if (getFlagResponse.isSuccessful && flag != null) {
                return@withContext flag!!
            } else {
                throw FlagrException(getFlagResponse.message(), getFlagResponse.code())
            }
        }
    }

    override suspend fun postEvaluation(body: EvalContext): PostEvaluationResponseJson {
        return withContext(Dispatchers.IO) {
            val postEvaluationResult = flagr.postEvaluation(body)

            val evaluation = postEvaluationResult.body()

            if (postEvaluationResult.isSuccessful && evaluation != null) {
                return@withContext evaluation!!
            } else {
                throw  FlagrException(postEvaluationResult.message(), postEvaluationResult.code())
            }
        }
    }

    interface Flagr {
        @GET("/api/v1/flags/{flagID}")
        suspend fun getFlag(
            @Path("flagID") flagID: String
        ): Response<Flag>

        @POST("/api/v1/evaluation")
        suspend fun postEvaluation(
            @Body body: EvalContext
        ) : Response<PostEvaluationResponseJson>
    }
}