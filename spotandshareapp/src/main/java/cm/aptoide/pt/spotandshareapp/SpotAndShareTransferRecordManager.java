package cm.aptoide.pt.spotandshareapp;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import cm.aptoide.pt.spotandshareandroid.transfermanager.Transfer;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by filipe on 10-07-2017.
 */

public class SpotAndShareTransferRecordManager {

  private Context context;
  private List<Transfer> transferList;

  public SpotAndShareTransferRecordManager(Context context) {
    this.context = context;
  }

  public List<TransferAppModel> getTransferAppModelList(List<Transfer> transferList) {
    this.transferList = transferList;
    List<TransferAppModel> appModelList = new LinkedList<>();
    for (Transfer transfer : transferList) {
      appModelList.add(new TransferAppModel(transfer.getAndroidAppInfo()
          .getAppName(), transfer.getAndroidAppInfo()
          .getPackageName(), "", convertByteToDrawable(transfer.getAndroidAppInfo()
          .getIcon()), transfer.getState(), transfer.getAndroidAppInfo()
          .getSenderName(), transfer.hashCode()));
    }
    return appModelList;
  }

  private Drawable convertByteToDrawable(byte[] icon) {
    return new BitmapDrawable(context.getResources(),
        BitmapFactory.decodeByteArray(icon, 0, icon.length));
  }

  public void acceptApp(TransferAppModel transferAppModel) {
    for (int i = 0; i < transferList.size(); i++) {
      if (transferAppModel.getHashcode() == transferList.get(i)
          .hashCode()) {
        transferList.get(i)
            .accept();
        break;
      }
    }
  }
}
