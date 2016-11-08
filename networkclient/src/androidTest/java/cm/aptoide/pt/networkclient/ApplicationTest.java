/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 22/04/2016.
 */

package cm.aptoide.pt.networkclient;

import android.app.Application;
import android.test.ApplicationTestCase;
import cm.aptoide.pt.networkclient.okhttp.UserAgentInterceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {

  public ApplicationTest() {
    super(Application.class);
  }
}
