package cm.aptoide.pt.feature_campaigns.data

import cm.aptoide.pt.feature_campaigns.CampaignRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import javax.inject.Inject

class CampaignApiRepository @Inject constructor(private val client: OkHttpClient) :
  CampaignRepository {

  override suspend fun knock(url: String) =
    withContext(Dispatchers.IO) {
      suspendCancellableCoroutine<Unit> { cont ->
        client.newCall(Request.Builder().url(url).build())
          .enqueue(object : Callback {
            override fun onFailure(call: okhttp3.Call, e: IOException) {
              if (cont.isActive) cont.resumeWith(Result.success(Unit))
            }

            @Throws(IOException::class)
            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
              if (cont.isActive) cont.resumeWith(Result.success(Unit))
              response.body?.close()
            }
          })
      }
    }
}