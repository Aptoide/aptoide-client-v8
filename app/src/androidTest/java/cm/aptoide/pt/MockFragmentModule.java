package cm.aptoide.pt;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import cm.aptoide.pt.account.view.ImageValidator;
import cm.aptoide.pt.account.view.exception.InvalidImageException;
import cm.aptoide.pt.networking.image.ImageLoader;
import java.util.ArrayList;
import java.util.List;
import rx.Completable;
import rx.Scheduler;
import rx.schedulers.Schedulers;

/**
 * Created by jose_messejana on 14-12-2017.
 */

public class MockFragmentModule extends FragmentModule {

  private final Fragment fragment;

  public MockFragmentModule(Fragment fragment, Bundle savedInstance, Bundle arguments,
      boolean isCreateStoreUserPrivacyEnabled, String packageName) {
    super(fragment, savedInstance, arguments, isCreateStoreUserPrivacyEnabled, packageName);
    this.fragment = fragment;
  }

  @Override ImageValidator provideImageValidator() {
    Scheduler scheduler = Schedulers.computation();
    return new ImageValidator(ImageLoader.with(fragment.getContext()), scheduler) {

      @Override public Completable validateOrGetException(String imagePath) {
        return Completable.defer(() -> {
          try {
            final List<InvalidImageException.ImageError> errors = new ArrayList<>();
            if (TestType.types.equals(TestType.TestTypes.ERRORDECONDINGTEST)) {
              errors.add(InvalidImageException.ImageError.ERROR_DECODING);
            }
            if (TestType.types.equals(TestType.TestTypes.MIN_HEIGHTTEST)) {
              errors.add(InvalidImageException.ImageError.MIN_HEIGHT);
            }
            if (TestType.types.equals(TestType.TestTypes.MIN_WIDTHTEST)) {
              errors.add(InvalidImageException.ImageError.MIN_WIDTH);
            }
            if (TestType.types.equals(TestType.TestTypes.MAX_HEIGHTTEST)) {
              errors.add(InvalidImageException.ImageError.MAX_HEIGHT);
            }
            if (TestType.types.equals(TestType.TestTypes.MAX_WIDTHTEST)) {
              errors.add(InvalidImageException.ImageError.MAX_WIDTH);
            }
            if (TestType.types.equals(TestType.TestTypes.MAX_IMAGE_SIZETEST)) {
              errors.add(InvalidImageException.ImageError.MAX_IMAGE_SIZE);
            }
            if (!errors.isEmpty()) {
              throw new InvalidImageException(errors);
            }
            return Completable.complete();
          } catch (InvalidImageException ex) {
            return Completable.error(ex);
          }
        })
            .subscribeOn(scheduler);
      }
    };
  }
}
