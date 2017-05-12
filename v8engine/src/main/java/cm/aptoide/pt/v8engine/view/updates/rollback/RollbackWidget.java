package cm.aptoide.pt.v8engine.view.updates.rollback;

import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import cm.aptoide.pt.actions.PermissionService;
import cm.aptoide.pt.database.realm.Rollback;
import cm.aptoide.pt.imageloader.ImageLoader;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.utils.design.ShowMessage;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.crashreports.CrashReport;
import cm.aptoide.pt.v8engine.view.recycler.widget.Widget;
import com.jakewharton.rxbinding.view.RxView;
import java.text.DateFormat;

import static android.text.format.DateFormat.getTimeFormat;

public class RollbackWidget extends Widget<RollbackDisplayable> {

  private static final String TAG = RollbackWidget.class.getSimpleName();

  private ImageView appIcon;
  private TextView appName;
  private TextView appUpdateVersion;
  private TextView appState;
  private TextView rollbackAction;

  public RollbackWidget(View itemView) {
    super(itemView);
  }

  @Override protected void assignViews(View itemView) {
    appIcon = (ImageView) itemView.findViewById(R.id.app_icon);
    appName = (TextView) itemView.findViewById(R.id.app_name);
    appState = (TextView) itemView.findViewById(R.id.app_state);
    appUpdateVersion = (TextView) itemView.findViewById(R.id.app_update_version);
    rollbackAction = (TextView) itemView.findViewById(R.id.ic_action);
  }

  @Override public void bindView(RollbackDisplayable displayable) {
    final Rollback pojo = displayable.getPojo();

    final FragmentActivity context = getContext();
    ImageLoader.with(context)
        .load(pojo.getIcon(), appIcon);
    appName.setText(pojo.getAppName());
    appUpdateVersion.setText(pojo.getVersionName());

    StringBuilder builder = new StringBuilder();
    switch (Rollback.Action.valueOf(pojo.getAction())) {
      case UPDATE:
        builder.append(context.getString(R.string.rollback_updated));
        rollbackAction.setText(R.string.downgrade);
        break;
      case DOWNGRADE:
        builder.append(context.getString(R.string.rollback_downgraded));
        rollbackAction.setText(R.string.update);
        break;
      case UNINSTALL:
        builder.append(context.getString(R.string.rollback_uninstalled));
        rollbackAction.setText(R.string.install);
        break;
      case INSTALL:
        builder.append(context.getString(R.string.rollback_installed));
        rollbackAction.setText(R.string.uninstall);
        break;
    }
    DateFormat timeFormat = getTimeFormat(context);
    builder.append(" ");
    builder.append(
        String.format(context.getString(R.string.at_time), timeFormat.format(pojo.getTimestamp())));
    appState.setText(builder.toString());

    compositeSubscription.add(RxView.clicks(rollbackAction)
        .subscribe(view -> {

          final PermissionService permissionRequest = ((PermissionService) context);

          permissionRequest.requestAccessToExternalFileSystem(() -> {
            Rollback.Action action = Rollback.Action.valueOf(pojo.getAction());
            switch (action) {
              case DOWNGRADE:
                displayable.update(getFragmentNavigator());
                break;
              case INSTALL:
                //only if the app is installed
                //ShowMessage.asSnack(view, R.string.uninstall_msg);
                ShowMessage.asSnack(context, R.string.uninstall);
                compositeSubscription.add(
                    displayable.uninstall(context, displayable.getDownloadFromPojo())
                        .subscribe(uninstalled -> {
                        }, throwable -> throwable.printStackTrace()));
                break;

              case UNINSTALL:
                displayable.install(getFragmentNavigator());
                break;

              case UPDATE:
                displayable.update(getFragmentNavigator());
                break;
            }
          }, () -> {
            Logger.e(TAG, "Unable to access external file system");
          });
        }, throwable -> CrashReport.getInstance()
            .log(throwable)));
  }
}
