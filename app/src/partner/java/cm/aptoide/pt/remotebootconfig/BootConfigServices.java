package cm.aptoide.pt.remotebootconfig;

import cm.aptoide.pt.remotebootconfig.datamodel.RemoteBootConfig;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by diogoloureiro on 11/08/2017
 *
 * Defines the boot config services
 */

public interface BootConfigServices {

  /**
   * get the remote boot config
   *
   * @param package_name package name of the apk
   * @param config_type type of bootconfig (mobile for us)
   * @param oem_id oem id of the partner
   * @param aptoide_vercode applications version code
   */
  @GET("get/") Call<RemoteBootConfig> getRemoteBootConfig(
      @Query("package_name") String package_name, @Query("config_type") String config_type,
      @Query("oem_id") String oem_id, @Query("aptoide_vercode") String aptoide_vercode);
}