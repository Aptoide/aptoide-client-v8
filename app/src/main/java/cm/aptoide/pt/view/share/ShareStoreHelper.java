package cm.aptoide.pt.view.share;

import android.content.Context;
import android.content.Intent;
import cm.aptoide.pt.R;
import cm.aptoide.pt.crashreports.CrashReport;
import java.util.Locale;
import rx.Observable;

/**
 * Created by neuro on 16-05-2017.
 */
public class ShareStoreHelper {

  private final Context context;
  private final String marketName;

  public ShareStoreHelper(Context context, String marketName) {
    this.context = context;
    this.marketName = marketName;
  }

  public void shareStore(String storeUrl, String storeIconPath) {

    String title = context.getString(R.string.share_store);

    Observable<ShareDialogs.ShareResponse> storeShareDialog =
        ShareDialogs.createStoreShareDialog(context, title, storeIconPath);
    storeShareDialog.subscribe(eResponse -> {
      if (ShareDialogs.ShareResponse.USING == eResponse) {
        caseDefaultShare(storeUrl);
      }
    }, CrashReport.getInstance()::log);
  }

  private void caseDefaultShare(String storeUrl) {
    String msg = String.format(Locale.ENGLISH,
        context.getString(R.string.hello_follow_me_on_aptoide) + " " + storeUrl, marketName);

    Intent sharingIntent = new Intent(Intent.ACTION_SEND);
    sharingIntent.setType("text/plain");
    sharingIntent.putExtra(Intent.EXTRA_TEXT, msg);
    context.startActivity(Intent.createChooser(sharingIntent, context.getString(R.string.share)));
  }
}