package cm.aptoide.pt.donations

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.*

class DonationsService(private val serviceV8: ServiceV8) {

  suspend fun getDonations(packageName: String): List<Donation> {
    return withContext(Dispatchers.IO) {
      val donationsResponse = serviceV8.getDonations(packageName, 5)
      val donationsResponseBody = donationsResponse.body()
      if (donationsResponse.isSuccessful && donationsResponseBody != null) {
        return@withContext mapToDonationsList(donationsResponseBody)
      } else {
        return@withContext arrayListOf<Donation>()
      }
    }
  }


  private fun mapToDonationsList(
      donationsResponse: GetDonations): List<Donation> {
    val result: MutableList<Donation> = ArrayList<Donation>()
    for (donor in donationsResponse.items) result.add(
        Donation(donor.domain, donor.owner, donor.appc.toFloat()))
    return result
  }

  interface ServiceV8 {
    @GET("broker/8.20181010/leaderboard/donations")
    suspend fun getDonations(
        @Query("domain") packageName: String,
        @Query("limit") limit: Int): Response<GetDonations>
  }
}