package cm.aptoide.pt.share;

import android.content.Context;
import android.graphics.drawable.Drawable;
import androidx.appcompat.app.AlertDialog;
import cm.aptoide.pt.R;
import cm.aptoide.pt.networking.image.ImageLoader;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import rx.Observable;
import rx.Subscriber;
import rx.subscriptions.Subscriptions;

/**
 * Created by neuro on 17-05-2017.
 */
public class ShareDialogs {

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
          .loadIntoTarget(storeIcon, new SimpleTarget<Drawable>() {
            @Override public void onResourceReady(Drawable resource,
                Transition<? super Drawable> glideAnimation) {
              alertDialog.setIcon(resource);
            }
          });

      // cleaning up
      subscriber.add(Subscriptions.create(() -> alertDialog.dismiss()));
      alertDialog.show();
    });
  }

  public enum ShareResponse {
    USING
  }
}
