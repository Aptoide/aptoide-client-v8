package cm.aptoide.pt.dataprovider.interfaces;

import android.content.Context;
import android.support.annotation.NonNull;
import rx.Observable;

/**
 * Created by neuro on 17-10-2016.
 */

public interface TokenInvalidator {
  Observable<String> invalidateAccessToken(@NonNull Context context);
}
