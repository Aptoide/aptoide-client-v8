package com.aptoide.android.aptoidegames.editorial.data

import com.aptoide.android.aptoidegames.editorial.data.model.EditorialListResponse
import com.aptoide.android.aptoidegames.editorial.data.model.EditorialResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Retrofit service for the new public Editorial read API. Anonymous, no auth.
 * Base URL comes from BuildConfig.EDITORIAL_API_DOMAIN.
 *
 * - [getEditorials] is CDN-cacheable (honor server Cache-Control).
 * - [getEditorial] is served as Cache-Control: no-store (do not cache).
 */
interface EditorialServiceApi {

  @GET("editorials")
  suspend fun getEditorials(
    @Query("locale") locale: String = "en",
    @Query("limit") limit: Int = 25,
    @Query("cursor") cursor: String? = null,
    @Query("app") app: String? = null,
    @Query("subtype") subtype: String? = null,
  ): EditorialListResponse

  @GET("editorials/{slug}")
  suspend fun getEditorial(
    @Path("slug") slug: String,
  ): EditorialResponse
}
