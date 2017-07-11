package cm.aptoide.pt.spotandshareandroid.transfermanager;

import cm.aptoide.pt.spotandshare.socket.message.interfaces.AndroidAppInfoAccepter;
import java.util.List;
import lombok.Getter;
import rx.Observable;

/**
 * Created by neuro on 11-07-2017.
 */

public class TransferManager {

  private final TransferListRelay transferListRelay;
  @Getter private final AndroidAppInfoAccepter androidAppInfoAccepter;

  public TransferManager() {
    transferListRelay = new TransferListRelay();

    androidAppInfoAccepter =
        androidAppInfoAccepter1 -> transferListRelay.add(new Transfer(androidAppInfoAccepter1));
  }

  public Observable<List<Transfer>> observeTransfers() {
    return transferListRelay.asObservable();
  }
}
