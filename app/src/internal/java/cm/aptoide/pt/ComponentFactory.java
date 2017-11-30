package cm.aptoide.pt;

/**
 * Created by jose_messejana on 13-11-2017.
 */

public class ComponentFactory {

  public static ApplicationComponent create(AptoideApplication context){
    return DaggerApplicationComponent.builder().applicationModule(new ApplicationModule(context, context.getImageCachePath(), context.getCachePath(),
        context.getAccountType(), context.getPartnerId(), context.getMarketName(), context.getExtraId(), context.getAptoidePackage(),
        context.getAptoideMd5sum(), context.getLoginPreferences())).build();
  }
  public static FragmentComponent create(ActivityComponent activityComponent, Fragment fragment, Bundle savedInstance, boolean dismissToNavigateToMainView, boolean navigateToHome, boolean goToHome,
      boolean isEditProfile, boolean isCreateStoreUserPrivacyEnabled, String packageName){
    return activityComponent.plus(new FragmentModule(fragment, savedInstance, dismissToNavigateToMainView, navigateToHome, goToHome, isEditProfile,
        isCreateStoreUserPrivacyEnabled, packageName));
  }
}
