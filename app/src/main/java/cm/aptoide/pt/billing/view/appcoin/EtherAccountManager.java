package cm.aptoide.pt.billing.view.appcoin;

import android.content.SharedPreferences;
import cm.aptoide.pt.EthereumApi;
import cm.aptoide.pt.ethereumj.crypto.ECKey;
import java.math.BigInteger;
import org.spongycastle.util.encoders.Hex;
import rx.Observable;
import rx.functions.Action1;

/**
 * Created by neuro on 30-10-2017.
 */

public class EtherAccountManager {
  private final EthereumApi ethereumApi;
  private final String ETHER_ACCOUNT_MANAGER_KEY = "EtherAccountManagerKey";
  private final SharedPreferences sharedPreferences;
  private ECKey ecKey;
  private volatile long nonce = -1;

  public EtherAccountManager(EthereumApi ethereumApi, SharedPreferences sharedPreferences) {
    this.ethereumApi = ethereumApi;
    this.sharedPreferences = sharedPreferences;
    if (isStored()) {
      ecKey = ECKey.fromPrivate(getStoredECKey());
    } else {
      ecKey = new ECKey();
      storeKey(ecKey);
    }
  }

  private void storeKey(ECKey ecKey) {
    sharedPreferences.edit()
        .putString(ETHER_ACCOUNT_MANAGER_KEY, ecKey.getPrivKey()
            .toString())
        .apply();
  }

  private boolean isStored() {
    return sharedPreferences.contains(ETHER_ACCOUNT_MANAGER_KEY);
  }

  private BigInteger getStoredECKey() {
    return new BigInteger(sharedPreferences.getString(ETHER_ACCOUNT_MANAGER_KEY, null));
  }

  public Observable<Long> getCurrentNonce() {
    if (nonce == -1) {
      return ethereumApi.getCurrentNonce(Hex.toHexString(ecKey.getAddress()))
          .doOnNext(new Action1<Long>() {
            @Override public void call(Long aLong) {
              nonce = aLong;
            }
          });
    } else {
      return Observable.just(nonce++);
    }
  }

  public void load() {
    // TODO: 30-10-2017 neuro
  }

  public ECKey getECKey() {
    return ecKey;
  }

  public void createNewAccount() {
    nonce = 0;
    ecKey = new ECKey();
    storeKey(ecKey);
  }
}