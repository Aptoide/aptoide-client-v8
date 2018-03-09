package cm.aptoide.pt.home;

import android.support.annotation.NonNull;
import cm.aptoide.pt.dataprovider.model.v7.BaseV7EndlessDataListResponse;
import java.util.Collections;
import java.util.List;
import rx.functions.Func1;

/**
 * Created by jdandrade on 08/03/2018.
 */

public class BundlesResponseMapper {
  @NonNull Func1<BaseV7EndlessDataListResponse, List<AppBundle>> map() {
    return homeResponse -> Collections.EMPTY_LIST;
  }
}
