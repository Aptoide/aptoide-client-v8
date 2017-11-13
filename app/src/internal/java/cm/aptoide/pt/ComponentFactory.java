package cm.aptoide.pt;

/**
 * Created by jose_messejana on 13-11-2017.
 */

public class ComponentFactory {

  public static ApplicationComponentTest create(AptoideApplication context){
    return DaggerApplicationComponent.builder().applicationModule(new ApplicationModule(context, context.getImageCachePath(), context.getCachePath(),
        context.getAccountType(), context.getPartnerId(), context.getMarketName(), context.getExtraId(), context.getAptoidePackage(),
        context.getAptoideMd5sum(), context.getLoginPreferences())).build();
  }
}
