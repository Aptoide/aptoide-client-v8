package cm.aptoide.pt.v8engine.view.recycler.widget.implementations.grid;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import cm.aptoide.pt.crashreports.CrashReports;
import cm.aptoide.pt.model.v7.GetApp;
import cm.aptoide.pt.utils.design.ShowMessage;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.OfficialAppDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.widget.Widget;
import com.jakewharton.rxbinding.view.RxView;

public class OfficialAppWidget extends Widget<OfficialAppDisplayable> {

  private static final String TAG = OfficialAppWidget.class.getName();

  private Button installButton;

  public OfficialAppWidget(View itemView) {
    super(itemView);
  }

  @Override protected void assignViews(View itemView) {
    installButton = (Button) itemView.findViewById(R.id.app_install_button);
  }

  @Override public void bindView(OfficialAppDisplayable displayable) {

    final GetApp app = displayable.getPojo();

    // TODO: 7/12/2016 sithengineer

    // check if app is installed. if it is, show open button

    compositeSubscription.add(RxView.clicks(installButton)
        .subscribe(a -> {
          // TODO: 7/12/2016 sithengineer
          ShowMessage.asSnack(installButton, "to do");
        }, err -> {
          Log.e(TAG, "", err);
          CrashReports.logException(err);
        }));
  }
}
