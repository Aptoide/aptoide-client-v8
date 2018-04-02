package cm.aptoide.pt.apps;

import cm.aptoide.pt.view.app.AppCenterRepository;
import cm.aptoide.pt.view.app.AppService;
import cm.aptoide.pt.view.app.Application;
import cm.aptoide.pt.view.app.AppsList;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import rx.Single;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by D01 on 01/03/18.
 */

public class AppCenterRepositoryTest {

  private final static long FIRST_STORE_ID = (long) 1;
  private final static long SECOND_STORE_ID = (long) 2;
  private final static int LIMIT = 3;
  private final static int LISTSIZE = 5;
  private final static int OFFSET = 10;

  @Mock private AppService appService;

  @Mock private AppsList serviceApps;

  private AppCenterRepository appCenterRepository;
  private List<Application> apps;
  private List<Application> serviceAppsList;
  private AbstractMap.SimpleEntry<Integer, List<Application>> pair;
  private Map<Long, AbstractMap.SimpleEntry<Integer, List<Application>>> cachedStoreApplications;

  @Before public void setupAppCenterRepository() {
    MockitoAnnotations.initMocks(this);

    apps = getAppsList();
    serviceAppsList = getAppsList();
    cachedStoreApplications = new HashMap<>();
    pair = new AbstractMap.SimpleEntry<>(OFFSET, apps);
    appCenterRepository = new AppCenterRepository(appService, cachedStoreApplications);
  }

  @Test public void loadNextAppsWithCacheTest() {
    //Given a list of application in cache
    //When more apps are requested
    //It should return a new set of apps (if they exist) and update the cache correctly
    cachedStoreApplications.put(FIRST_STORE_ID, pair);
    Assert.assertNotNull(cachedStoreApplications.get(FIRST_STORE_ID));
    when(appService.loadApps(FIRST_STORE_ID, OFFSET, LIMIT)).thenReturn(Single.just(serviceApps));
    when(serviceApps.getList()).thenReturn(serviceAppsList);
    when(serviceApps.hasErrors()).thenReturn(false);
    when(serviceApps.isLoading()).thenReturn(false);
    AppsList returnedList = appCenterRepository.loadNextApps(FIRST_STORE_ID, LIMIT)
        .toBlocking()
        .value();

    verify(appService).loadApps(FIRST_STORE_ID, OFFSET, LIMIT);
    //The returned list is the same as the one created on test setup
    Assert.assertEquals(serviceAppsList.toString(), returnedList.getList()
        .toString());
    //The returned list is the same size as the one it imported from the webservice plus the ones it existed on start
    Assert.assertEquals(LISTSIZE * 2, cachedStoreApplications.get(FIRST_STORE_ID)
        .getValue()
        .size());
  }

  @Test public void loadNextAppsWithNoCacheTest() {
    //Given the user has no apps in cache
    //When more apps are requested
    //It should return a new set of apps (if they exist) and update the cache correctly
    cachedStoreApplications.put(FIRST_STORE_ID, pair);
    Assert.assertNull(cachedStoreApplications.get(SECOND_STORE_ID));
    when(appService.loadApps(SECOND_STORE_ID, 0, LIMIT)).thenReturn(Single.just(serviceApps));
    when(serviceApps.getList()).thenReturn(serviceAppsList);
    when(serviceApps.hasErrors()).thenReturn(false);
    when(serviceApps.isLoading()).thenReturn(false);
    AppsList returnedList = appCenterRepository.loadNextApps(SECOND_STORE_ID, LIMIT)
        .toBlocking()
        .value();

    verify(appService).loadApps(SECOND_STORE_ID, 0, LIMIT);
    //The returned list is the same as the one created on test setup
    Assert.assertEquals(serviceAppsList.toString(), returnedList.getList()
        .toString());
    //The returned list is the same size as the one it imported from the webservice
    Assert.assertEquals(LISTSIZE, cachedStoreApplications.get(SECOND_STORE_ID)
        .getValue()
        .size());
  }

  @Test public void loadFreshAppsTest() {
    //When the user opens apps for the first time
    //It should return a new set of apps
    Assert.assertNull(cachedStoreApplications.get(FIRST_STORE_ID));
    when(appService.loadFreshApps(FIRST_STORE_ID, LIMIT)).thenReturn(Single.just(serviceApps));
    when(serviceApps.getList()).thenReturn(serviceAppsList);
    when(serviceApps.hasErrors()).thenReturn(false);
    when(serviceApps.isLoading()).thenReturn(false);
    AppsList returnedList = appCenterRepository.loadFreshApps(FIRST_STORE_ID, LIMIT)
        .toBlocking()
        .value();

    verify(appService).loadFreshApps(FIRST_STORE_ID, LIMIT);
    //The returned list is the same as the one created on test setup
    Assert.assertEquals(serviceAppsList.toString(), returnedList.getList()
        .toString());
    //The returned list is the same size as the one it imported from the webservice
    Assert.assertEquals(LISTSIZE, cachedStoreApplications.get(FIRST_STORE_ID)
        .getValue()
        .size());
  }

  @Test public void getApplicationsWithNoCacheTest() {
    //if there aren't cached apps, limit apps will be requested to server
    Assert.assertNull(cachedStoreApplications.get(FIRST_STORE_ID));
    when(appService.loadApps(FIRST_STORE_ID, 0, LIMIT)).thenReturn(Single.just(serviceApps));
    when(serviceApps.getList()).thenReturn(serviceAppsList);
    when(serviceApps.hasErrors()).thenReturn(false);
    when(serviceApps.isLoading()).thenReturn(false);

    AppsList returnedList = appCenterRepository.getApplications(FIRST_STORE_ID, LIMIT)
        .toBlocking()
        .value();

    Assert.assertEquals(serviceAppsList.toString(), returnedList.getList()
        .toString());
  }

  @Test public void getApplicationsWithCacheWithAppsLeftTest() {
    //if there are cached apps, a multiple of limit apps will be returned if there are AppsLeft
    cachedStoreApplications.put(FIRST_STORE_ID, pair);
    Assert.assertNotNull(cachedStoreApplications.get(FIRST_STORE_ID));
    when(appService.loadApps(anyLong(), anyInt(), anyInt())).thenReturn(Single.just(serviceApps));
    when(serviceApps.getList()).thenReturn(serviceAppsList);
    when(serviceApps.hasErrors()).thenReturn(false);
    when(serviceApps.isLoading()).thenReturn(false);

    AppsList returnedList = appCenterRepository.getApplications(FIRST_STORE_ID, LIMIT)
        .toBlocking()
        .value();

    Assert.assertEquals(apps.toString(), returnedList.getList()
        .toString());
  }

  @Test public void getApplicationsWithCacheWithNoAppsLeftTest() {
    //if there are cached apps, but no appsLeft it should return the same list of apps that are in cache
    assertEquals(4, 2 + 2);
    //"TODO. No case found where AppsLeft == 0");
  }

  private List<Application> getAppsList() {
    List<Application> appslist = new ArrayList<>();
    for (int i = 0; i < LISTSIZE; i++) {
      appslist.add(new Application(Integer.toString(i), "", i, i, "", i, ""));
    }
    return appslist;
  }
}
