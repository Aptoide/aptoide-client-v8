package cm.aptoide.pt.v8engine.view.share;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import cm.aptoide.pt.v8engine.R;
import rx.Observable;
import rx.Subscriber;
import rx.subscriptions.Subscriptions;

/**
 * Created by neuro on 17-05-2017.
 */
public class ShareDialogs {

  public static Observable<ShareResponse> createAppviewShareDialog(Context context, String title) {
    return Observable.create((Subscriber<? super ShareResponse> subscriber) -> {
      final AlertDialog alertDialog = new AlertDialog.Builder(context).setTitle(title)
          .setItems(R.array.share_options_array, (dialogInterface, i) -> {
            if (!subscriber.isUnsubscribed()) {
              switch (i) {
                case 0:
                  subscriber.onNext(ShareResponse.SHARE_EXTERNAL);
                  subscriber.onCompleted();
                  break;
                case 1:
                  subscriber.onNext(ShareResponse.SHARE_TIMELINE);
                  subscriber.onCompleted();
                  break;
              }
            }
          })
          .create();
      // cleaning up
      subscriber.add(Subscriptions.create(() -> alertDialog.dismiss()));
      alertDialog.show();
    });
  }

  public static Observable<ShareResponse> createAppviewShareWithSpotandShareDialog(Context context,
      String title) {
    return Observable.create((Subscriber<? super ShareResponse> subscriber) -> {
      final AlertDialog alertDialog = new AlertDialog.Builder(context).setTitle(title)
          .setItems(R.array.share_options_array_with_spot_and_share, (dialogInterface, i) -> {
            if (!subscriber.isUnsubscribed()) {
              switch (i) {
                case 0:
                  subscriber.onNext(ShareResponse.SHARE_EXTERNAL);
                  subscriber.onCompleted();
                  break;
                case 1:
                  subscriber.onNext(ShareResponse.SHARE_TIMELINE);
                  subscriber.onCompleted();
                  break;
                case 2:
                  subscriber.onNext(ShareResponse.SHARE_SPOT_AND_SHARE);
                  subscriber.onCompleted();
                  break;
              }
            }
          })
          .create();
      // cleaning up
      subscriber.add(Subscriptions.create(() -> alertDialog.dismiss()));
      alertDialog.show();
    });
  }

  public static Observable<ShareResponse> createInstalledShareWithSpotandShareDialog(
      Context context, String title) {
    return Observable.create((Subscriber<? super ShareResponse> subscriber) -> {
      final AlertDialog alertDialog = new AlertDialog.Builder(context).setTitle(title)
          .setItems(R.array.installed_share_options_array_with_spot_and_share,
              (dialogInterface, i) -> {
                if (!subscriber.isUnsubscribed()) {
                  switch (i) {
                    case 0:
                      subscriber.onNext(ShareResponse.SHARE_TIMELINE);
                      subscriber.onCompleted();
                      break;
                    case 1:
                      subscriber.onNext(ShareResponse.SHARE_SPOT_AND_SHARE);
                      subscriber.onCompleted();
                      break;
                  }
                }
              })
          .create();
      // cleaning up
      subscriber.add(Subscriptions.create(() -> alertDialog.dismiss()));
      alertDialog.show();
    });
  }

  public enum ShareResponse {

    SHARE_APP,

    SHARE_EXTERNAL,

    SHARE_TIMELINE,

    SHARE_SPOT_AND_SHARE
  }
}
