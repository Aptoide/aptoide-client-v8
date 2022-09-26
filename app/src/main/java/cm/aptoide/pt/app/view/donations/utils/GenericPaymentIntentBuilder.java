package cm.aptoide.pt.app.view.donations.utils;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import com.google.android.gms.common.util.Hex;
import com.google.gson.Gson;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.Formatter;

/**
 * This class contains the help method to build the intent to call the BDS Wallet for generic
 * payments.
 */
public class GenericPaymentIntentBuilder {
  private static final int MAIN_NETWORK_ID = 1;
  private static final int ROPSTEN_NETWORK_ID = 3;

  /**
   * Method that generates the pending intent to call the wallet for a generic payment that follows
   * the EIP681.
   *
   * @param context The application context.
   * @param skuId The id for the SKU being purchased.
   * @param value The value to be transferred.
   * @param walletAddress The wallet address to transfer the value to.
   * @param packageName The package name of the application that is the reference of the transfer.
   * @param transferType The type of the transfer being done. Currently the predefined values are
   * {@value TransactionData#TYPE_INAPP} and {@value TransactionData#TYPE_DONATION}, but any value
   * can be applied.
   * @param payload The field to add any additional information to be included the transaction.
   * @param debug The field used to set the network to be used. If true the network id will be
   * {@value #ROPSTEN_NETWORK_ID} otherwise it will be {@value #MAIN_NETWORK_ID}
   *
   * @return The pending intent needed to call the wallet for a generic transaction.
   */
  public static PendingIntent buildBuyIntent(Context context, String skuId, String value,
      String walletAddress, String packageName, String transferType, String payload,
      boolean debug) {
    int networkId = debug ? ROPSTEN_NETWORK_ID : MAIN_NETWORK_ID;

    Single<String> getTokenContractAddress = Single.just("proxySdk.getAppCoinsAddress(networkId)")
            .subscribeOn(Schedulers.io());
    Single<String> getIabContractAddress = Single.just("proxySdk.getIabAddress(networkId)")
            .subscribeOn(Schedulers.io());

    return Single.zip(getTokenContractAddress, getIabContractAddress,
        (tokenContractAddress, iabContractAddress) -> buildPaymentIntent(context, networkId, skuId,
            value, tokenContractAddress, iabContractAddress, walletAddress, packageName,
            transferType, payload))
        .blockingGet();
  }

  private static PendingIntent buildPaymentIntent(Context context, int networkId, String skuId,
      String value, String tokenContractAddress, String iabContractAddress, String walletAddress,
      String packageName, String paymentType, String payload) {

    BigDecimal amount = new BigDecimal(value);
    amount = amount.multiply(BigDecimal.TEN.pow(18));

    Intent intent = new Intent(Intent.ACTION_VIEW);
    Uri data = Uri.parse(
        buildUriString(tokenContractAddress, iabContractAddress, amount, walletAddress, skuId,
            networkId, packageName, paymentType, payload));
    intent.setData(data);

    return PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
  }

  private static String buildUriString(String tokenContractAddress, String iabContractAddress,
      BigDecimal amount, String walletAddress, String skuId, int networkId, String packageName,
      String paymentType, String payload) {

    StringBuilder stringBuilder = new StringBuilder(4);
    try {
      Formatter formatter = new Formatter(stringBuilder);
      formatter.format("ethereum:%s@%d/buy?uint256=%s&address=%s&data=%s&iabContractAddress=%s",
          tokenContractAddress, networkId, amount.toString(), walletAddress,
          buildUriData(skuId, packageName, paymentType, payload), iabContractAddress);
    } catch (UnsupportedEncodingException e) {
      throw new RuntimeException("UTF-8 not supported!", e);
    }

    return stringBuilder.toString();
  }

  private static String buildUriData(String skuId, String packageName, String paymentType,
      String payload) throws UnsupportedEncodingException {
    return "0x" + Hex.bytesToStringUppercase(
        new Gson().toJson(new TransactionData(paymentType, packageName, skuId, payload))
            .getBytes("UTF-8"))
        .toLowerCase();
  }

  /**
   * Class used to build the content of the EIP681 composed data.
   */
  public static class TransactionData {
    /** Transaction data type for in app purchases. */
    public static final String TYPE_INAPP = "INAPP";
    /** Transaction data type for donations. */
    public static final String TYPE_DONATION = "DONATION";

    /** The type o transaction */
    String type;
    /** The domain/packageName to witch the transaction is to be done */
    String domain;
    /** The skuId of the items being "bought" */
    String skuId;
    /** The additional payload to be sent if needed */
    String payload;

    public TransactionData(String type, String domain, String skuId, String payload) {
      this.type = type;
      this.domain = domain;
      this.skuId = skuId;
      this.payload = payload;
    }
  }
}