package cm.aptoide.pt.v8engine.notification.view;

import cm.aptoide.pt.v8engine.notification.AptoideNotification;
import cm.aptoide.pt.v8engine.presenter.View;
import java.util.List;

/**
 * Created by pedroribeiro on 16/05/17.
 */

public interface InboxView extends View {

  void showNotifications(List<AptoideNotification> notifications);
}
