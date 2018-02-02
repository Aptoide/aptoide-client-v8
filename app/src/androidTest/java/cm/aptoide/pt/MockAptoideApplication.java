package cm.aptoide.pt;

public class MockAptoideApplication extends VanillaApplication {

  private ApplicationComponent applicationComponent;

  @Override public ApplicationComponent getApplicationComponent() {
    if (applicationComponent == null) {
      applicationComponent = DaggerApplicationComponent.builder()
          .applicationModule(
              new MockApplicationModule(this,
                  getAptoideMd5sum()))
          .flavourApplicationModule(
              new FlavourApplicationModule(this)
          )
          .build();
    }
    return applicationComponent;
  }
}