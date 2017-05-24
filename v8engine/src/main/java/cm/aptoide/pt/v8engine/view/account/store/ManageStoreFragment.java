package cm.aptoide.pt.v8engine.view.account.store;

import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.view.fragment.UIComponentFragment;
import com.jakewharton.rxbinding.view.RxView;
import rx.Observable;

public class ManageStoreFragment extends UIComponentFragment implements ManageStoreView {

  private View selectStoreImage;
  private View saveDataButton;
  private View cancelChangesButton;
  private EditText storeName;
  private EditText storeDescription;
  private ThemeSelector themeSelector;

  private ManageStoreModel storeModel;

  @Override public Observable<Void> selectStoreImageClick() {
    return RxView.clicks(selectStoreImage);
  }

  @Override public Observable<ManageStoreModel> saveDataClick() {
    return RxView.clicks(saveDataButton).map(__ -> {
      updateStoreModel();
      return storeModel;
    });
  }

  private void updateStoreModel() {
    if(!TextUtils.isEmpty(storeName.toString()))
  }

  @Override public Observable<Void> cancelClick() {
    return RxView.clicks(cancelChangesButton);
  }

  @Override public int getContentViewId() {
    return R.layout.fragment_manage_store;
  }

  @Override public void bindViews(@Nullable View view) {

  }

  @Override public void setupViews() {

  }
}
