/*
 * Copyright (c) 2016.
 * Modified by marcelo.benites@aptoide.com on 06/05/2016.
 */

package cm.aptoide.pt.v8engine.abtesting;

import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.v8engine.BuildConfig;
import com.seatgeek.sixpack.Alternative;
import com.seatgeek.sixpack.Sixpack;
import com.seatgeek.sixpack.SixpackBuilder;
import com.seatgeek.sixpack.log.LogLevel;
import java.util.HashSet;
import java.util.Set;
import okhttp3.Credentials;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import rx.Observable;
import rx.schedulers.Schedulers;

public class ABTestManager {

  public static final String SEARCH_TAB_TEST = "search-result";
  private static final String TAG = ABTestManager.class.getSimpleName();
  private static ABTestManager instance;
  private final OkHttpClient httpClient;
  private final String sixpackUrl;
  private final Set<ABTest<?>> tests;
  private final Set<ABTest<?>> controlTests;
  private SixpackBuilder sixpackBuilder;
  private Sixpack sixpack;

  private ABTestManager(SixpackBuilder sixpackBuilder, OkHttpClient httpClient, String sixpackUrl) {
    this.sixpackBuilder = sixpackBuilder;
    this.httpClient = httpClient;
    this.sixpackUrl = sixpackUrl;
    this.tests = new HashSet<>();
    this.controlTests = new HashSet<>();
  }

  public static ABTestManager getInstance() {
    if (instance == null) {
      instance = new ABTestManager(new SixpackBuilder(), new OkHttpClient.Builder().authenticator(
          (route, response) -> response.request()
              .newBuilder()
              .header("Authorization",
                  Credentials.basic(BuildConfig.SIXPACK_USER, BuildConfig.SIXPACK_PASSWORD))
              .build())
          .build(), BuildConfig.SIXPACK_URL);
    }
    return instance;
  }

  public Observable<Void> initialize(String clientId) {
    Logger.i(TAG, "initialize() called with: " + "clientId = [" + clientId + "]");
    initializeSixpack(clientId);
    registerTests();
    return prefetchTests().doOnNext(success -> Logger.i(TAG, "ABTestManager initialized"));
  }

  private void initializeSixpack(String clientId) {
    sixpack = sixpackBuilder.setSixpackUrl(HttpUrl.parse(sixpackUrl))
        .setHttpClient(httpClient)
        .setClientId(clientId)
        .setLogLevel(BuildConfig.DEBUG ? LogLevel.VERBOSE : LogLevel.NONE)
        .build();
  }

  @SuppressWarnings("unchecked") private void registerTests() {
    tests.add(new SixpackABTest(sixpack.experiment()
        .withName(SEARCH_TAB_TEST)
        .withAlternatives(new Alternative(SearchTabAlternativeParser.FOLLOWED_STORES),
            new Alternative(SearchTabAlternativeParser.ALL_STORES))
        .build(), new SearchTabAlternativeParser()));
  }

  private Observable<Void> prefetchTests() {
    return Observable.from(tests)
        .observeOn(Schedulers.computation())
        .flatMap(test -> test.prefetch()
            .toList())
        .map(success -> null);
  }

  @SuppressWarnings("unchecked") public <T> ABTest<T> get(String name) {
    if (isInitialized()) {
      for (ABTest test : tests) {
        if (test.getName()
            .equals(name)) {
          return test;
        }
      }
      throw new IllegalArgumentException("No AB test for name: " + name);
    } else {
      return getControl(name);
    }
  }

  private boolean isInitialized() {
    return sixpack != null;
  }

  @SuppressWarnings("unchecked") private <T> ABTest<T> getControl(String name) {
    synchronized (controlTests) {
      registerControlTests();
      for (ABTest test : controlTests) {
        if (test.getName()
            .equals(name)) {
          return test;
        }
      }
      throw new IllegalArgumentException("No AB test for name: " + name);
    }
  }

  private void registerControlTests() {
    if (controlTests.isEmpty()) {
      controlTests.add(new ControlABTest<>(SEARCH_TAB_TEST, ""));
    }
  }
}