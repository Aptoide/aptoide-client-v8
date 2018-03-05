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

import static org.junit.Assert.fail;
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

  @Mock AppService appService;

  @Mock AppsList serviceApps;

  private AppCenterRepository appCenterRepository;
  private List<Application> apps;
  private List<Application> serviceAppsList;
  private AbstractMap.SimpleEntry<Integer, List<Application>> pair;
  private Map<Long, AbstractMap.SimpleEntry<Integer, List<Application>>> storeApplications;

  @Before public void setupAppCenterRepository() {
    MockitoAnnotations.initMocks(this);

    apps = constroyList(0);
    serviceAppsList = constroyList(0);
    storeApplications = new HashMap<>();
    pair = new AbstractMap.SimpleEntry<>(OFFSET, apps);
    appCenterRepository = new AppCenterRepository(appService, storeApplications);
  }

  @Test public void loadNextAppsWithCacheTest() {
    //Given a list of application in cache
    //The user wants to see more apps
    //It should return a new set of apps (if they exist) and update the cache correctly
    storeApplications.put(FIRST_STORE_ID, pair);
    Assert.assertNotNull(storeApplications.get(FIRST_STORE_ID));
    when(appService.loadApps(FIRST_STORE_ID, OFFSET, LIMIT)).thenReturn(Single.just(serviceApps));
    when(serviceApps.getList()).thenReturn(serviceAppsList);
    when(serviceApps.hasErrors()).thenReturn(false);
    when(serviceApps.isLoading()).thenReturn(false);
    AppsList returnedList = appCenterRepository.loadNextApps(FIRST_STORE_ID, LIMIT)
        .toBlocking()
        .value();

    verify(appService).loadApps(FIRST_STORE_ID, OFFSET, LIMIT);
    //The returned list is the same as the one in the entry set
    Assert.assertEquals(apps.toString(), returnedList.getList()
        .toString());
    //The returned list is the same size as the one it imported from the webservice plus the ones it existed on start
    Assert.assertEquals(LISTSIZE * 2, storeApplications.get(FIRST_STORE_ID)
        .getValue()
        .size());
  }

  @Test public void loadNextAppsWithNoCacheTest() {
    //Given the user has no apps in cache
    //The user wants to see more apps
    //It should return a new set of apps (if they exist) and update the cache correctly
    storeApplications.put(FIRST_STORE_ID, pair);
    Assert.assertNull(storeApplications.get(SECOND_STORE_ID));
    when(appService.loadApps(SECOND_STORE_ID, 0, LIMIT)).thenReturn(Single.just(serviceApps));
    when(serviceApps.getList()).thenReturn(serviceAppsList);
    when(serviceApps.hasErrors()).thenReturn(false);
    when(serviceApps.isLoading()).thenReturn(false);
    AppsList returnedList = appCenterRepository.loadNextApps(SECOND_STORE_ID, LIMIT)
        .toBlocking()
        .value();

    verify(appService).loadApps(SECOND_STORE_ID, 0, LIMIT);
    //The returned list is the same as the one in the entry set
    Assert.assertEquals(apps.toString(), returnedList.getList()
        .toString());
    //The returned list is the same size as the one it imported from the webservice
    Assert.assertEquals(LISTSIZE, storeApplications.get(SECOND_STORE_ID)
        .getValue()
        .size());
  }

  @Test public void loadFreshAppsTest() {
    //When the user opens apps for the first time
    //It should return a new set of apps
    Assert.assertNull(storeApplications.get(FIRST_STORE_ID));
    when(appService.loadFreshApps(FIRST_STORE_ID, LIMIT)).thenReturn(Single.just(serviceApps));
    when(serviceApps.getList()).thenReturn(serviceAppsList);
    when(serviceApps.hasErrors()).thenReturn(false);
    when(serviceApps.isLoading()).thenReturn(false);
    AppsList returnedList = appCenterRepository.loadFreshApps(FIRST_STORE_ID, LIMIT)
        .toBlocking()
        .value();

    verify(appService).loadFreshApps(FIRST_STORE_ID, LIMIT);
    //The returned list is the same as the one in the entry set
    Assert.assertEquals(apps.toString(), returnedList.getList()
        .toString());
    //The returned list is the same size as the one it imported from the webservice
    Assert.assertEquals(LISTSIZE, storeApplications.get(FIRST_STORE_ID)
        .getValue()
        .size());
  }

  @Test public void getApplicationsWithNoCacheTest() {
    //if there aren't cached apps, limit apps will be requested to server
    Assert.assertNull(storeApplications.get(FIRST_STORE_ID));
    when(appService.loadApps(FIRST_STORE_ID, 0, LIMIT)).thenReturn(Single.just(serviceApps));
    when(serviceApps.getList()).thenReturn(serviceAppsList);
    when(serviceApps.hasErrors()).thenReturn(false);
    when(serviceApps.isLoading()).thenReturn(false);

    AppsList returnedList = appCenterRepository.getApplications(FIRST_STORE_ID, LIMIT)
        .toBlocking()
        .value();

    Assert.assertNotNull(returnedList);
  }

  @Test public void getApplicationsWithCacheWithAppsLeftTest() {
    //if there are cached apps, a multiple of limit apps will be returned if there are AppsLeft
    storeApplications.put(FIRST_STORE_ID, pair);
    Assert.assertNotNull(storeApplications.get(FIRST_STORE_ID));
    when(appService.loadApps(anyLong(), anyInt(), anyInt())).thenReturn(Single.just(serviceApps));
    when(serviceApps.getList()).thenReturn(serviceAppsList);
    when(serviceApps.hasErrors()).thenReturn(false);
    when(serviceApps.isLoading()).thenReturn(false);

    AppsList returnedList = appCenterRepository.getApplications(FIRST_STORE_ID, LIMIT)
        .toBlocking()
        .value();

    Assert.assertNotNull(returnedList);
  }

  @Test public void getApplicationsWithCacheWithNoAppsLeftTest() {
    //if there are cached apps, but no appsLeft it should return the same list of apps that are in cache
    fail("TODO. No case found where AppsLeft == 0");
  }

  private List<Application> constroyList(int extraSize) {
    apps = new ArrayList<>();
    for (int i = 0; i < LISTSIZE + extraSize; i++) {
      apps.add(new Application(Integer.toString(i), "", i, i, "", i));
    }
    return apps;
  }
}
