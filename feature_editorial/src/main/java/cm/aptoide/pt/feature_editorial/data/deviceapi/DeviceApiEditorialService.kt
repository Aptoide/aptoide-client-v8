package cm.aptoide.pt.feature_editorial.data.deviceapi

import cm.aptoide.pt.feature_editorial.data.deviceapi.model.EditorialListResponse
import cm.aptoide.pt.feature_editorial.data.deviceapi.model.EditorialResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/** Editorial API (`editorial.openapi.json`), same host as the device API. */
interface DeviceApiEditorialService {

  @GET("editorials")
  suspend fun listEditorials(
    @Query("locale") locale: String? = null,
    @Query("app") app: String? = null,
    @Query("subtype") subtype: String? = null,
    @Query("cursor") cursor: String? = null,
    @Query("limit") limit: Int? = null,
  ): EditorialListResponse

  @GET("editorials/{slug}")
  suspend fun getEditorial(@Path("slug") slug: String): EditorialResponse
}
