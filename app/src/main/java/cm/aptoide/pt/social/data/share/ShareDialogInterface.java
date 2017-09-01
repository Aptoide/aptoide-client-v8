package cm.aptoide.pt.social.data.share;

import android.content.DialogInterface;
import rx.Observable;

public interface ShareDialogInterface<T> extends DialogInterface {
  Observable<ShareEvent> shares();

  Observable<ShareEvent> cancels();

  void show();

  void setup(T post);

  void setupMinimalPost(T originalPost, T minimalPost);
}
