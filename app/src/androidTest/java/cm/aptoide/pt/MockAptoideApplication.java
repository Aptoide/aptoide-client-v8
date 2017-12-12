package cm.aptoide.pt;

public class MockAptoideApplication extends VanillaApplication {

  private ApplicationComponent applicationComponent;

  @Override public ApplicationComponent getApplicationComponent() {
    if (applicationComponent == null) {
      applicationComponent = DaggerApplicationComponent.builder()
          .applicationModule(
              new MockApplicationModule(this, getImageCachePath(), getCachePath(), getAccountType(),
                  getPartnerId(), getMarketName(), getExtraId(), getAptoidePackage(),
                  getAptoideMd5sum(), getLoginPreferences()))
          .build();
    }
    return applicationComponent;
  }
}