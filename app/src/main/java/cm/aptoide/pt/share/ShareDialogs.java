package cm.aptoide.pt.share;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import cm.aptoide.pt.R;
import cm.aptoide.pt.networking.image.ImageLoader;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
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

  public static Observable<ShareResponse> createInstalledShareDialog(Context context,
      String title) {
    return Observable.create((Subscriber<? super ShareResponse> subscriber) -> {
      final AlertDialog alertDialog = new AlertDialog.Builder(context).setTitle(title)
          .setItems(R.array.installed_share_options_array, (dialogInterface, i) -> {
            if (!subscriber.isUnsubscribed()) {
              switch (i) {
                case 0:
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

  public static Observable<ShareResponse> createStoreShareDialog(Context context, String title,
      String storeIcon) {
    return Observable.create((Subscriber<? super ShareResponse> subscriber) -> {
      final AlertDialog alertDialog = new AlertDialog.Builder(context).setTitle(title)
          .setItems(R.array.store_share_options_array, (dialogInterface, i) -> {
            if (!subscriber.isUnsubscribed()) {
              switch (i) {
                case 0:
                  subscriber.onNext(ShareResponse.USING);
                  subscriber.onCompleted();
                  break;
              }
            }
          })
          .create();

      ImageLoader.with(context)
          .loadIntoTarget(storeIcon, new SimpleTarget<GlideDrawable>() {
            @Override public void onResourceReady(GlideDrawable resource,
                GlideAnimation<? super GlideDrawable> glideAnimation) {
              alertDialog.setIcon(resource);
            }
          });

      // cleaning up
      subscriber.add(Subscriptions.create(() -> alertDialog.dismiss()));
      alertDialog.show();
    });
  }

  public enum ShareResponse {

    SHARE_APP,

    SHARE_EXTERNAL,

    SHARE_TIMELINE,

    USING
  }
}
