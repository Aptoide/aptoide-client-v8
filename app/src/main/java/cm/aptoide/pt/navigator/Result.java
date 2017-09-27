package cm.aptoide.pt.navigator;

import android.content.Intent;
import android.support.annotation.Nullable;

public class Result {

  private final int requestCode;
  private final int resultCode;
  @Nullable private final Intent data;

  public Result(int requestCode, int resultCode, @Nullable Intent data) {
    this.requestCode = requestCode;
    this.resultCode = resultCode;
    this.data = data;
  }

  public int getRequestCode() {
    return requestCode;
  }

  public int getResultCode() {
    return resultCode;
  }

  public @Nullable Intent getData() {
    return data;
  }
}