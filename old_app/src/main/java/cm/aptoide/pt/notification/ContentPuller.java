/*
 * Copyright (c) 2017.
 * Modified by Marcelo Benites on 18/01/2017.
 */

package cm.aptoide.pt.notification;

import android.content.Context;
import android.content.Intent;

/**
 * Created by marcelobenites on 18/01/17.
 */

public class ContentPuller {

  private final Context context;

  public ContentPuller(Context context) {
    this.context = context;
  }

  public void start() {
    context.startService(new Intent(context, PullingContentService.class));
  }
}
