package cm.aptoide.pt.social.data;

import android.view.View;

/**
 * Created by trinkes on 22/08/2017.
 */

public class AdResponse {
  private final View view;
  private final Status status;

  public AdResponse(View view, Status status) {
    this.view = view;
    this.status = status;
  }

  public View getView() {
    return view;
  }

  public Status getStatus() {
    return status;
  }

  public enum Status {
    error, ok,
  }
}

