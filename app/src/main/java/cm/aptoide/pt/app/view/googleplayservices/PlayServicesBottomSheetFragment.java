package cm.aptoide.pt.app.view.googleplayservices;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import cm.aptoide.pt.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.jakewharton.rxbinding.view.RxView;
import javax.inject.Inject;
import rx.Observable;
import rx.subjects.PublishSubject;

public class PlayServicesBottomSheetFragment extends BaseBottomSheetDialogFragment
    implements PlayServicesView {

  @Inject PlayServicesPresenter presenter;

  private Button laterButton;
  private Button installButton;
  private PublishSubject<Boolean> resumeInstall;

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    getFragmentComponent(savedInstanceState).inject(this);
    setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.Aptoide_BottomSheetTheme);
  }

  @Nullable @Override public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.appview_google_services_dialog, container, false);
  }

  @Override public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    laterButton = view.findViewById(R.id.later_button);
    installButton = view.findViewById(R.id.install_button);

    attachPresenter(presenter);
  }

  @Override public void onDestroy() {
    super.onDestroy();
    laterButton = null;
    installButton = null;
  }

  @Override public Observable<Void> clickLater() {
    return RxView.clicks(laterButton);
  }

  @Override public Observable<Void> clickInstall() {
    return RxView.clicks(installButton);
  }

  @Override public void dismissView() {
    this.dismiss();
    resumeInstall.onNext(true);
  }

 @Override public void setResumeInstallInstallSubject(
     PublishSubject<Boolean> resumeInstallInstallSubject){
    resumeInstall = resumeInstallInstallSubject;
 }
}
