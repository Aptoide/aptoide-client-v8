/*
 * Copyright (c) 2016.
 * Modified on 24/06/2016.
 */

package cm.aptoide.pt.dataprovider.util.referrer;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Created by neuro on 08-10-2015.
 */
public class ReferrerUtils {

  public static final int RETRIES = 2;
  public static final int TIME_OUT = 5;
  public static final ReferrersMap excludedNetworks = new ReferrersMap();
  protected static final ScheduledExecutorService executorService =
      Executors.newSingleThreadScheduledExecutor();
}
