/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 09/05/2016.
 */

package cm.aptoide.pt.aptoideclientv8;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.util.LinkedList;

import cm.aptoide.pt.dataprovider.ws.v7.GetAppRequest;
import cm.aptoide.pt.dataprovider.ws.v7.ListSearchAppsRequest;
import cm.aptoide.pt.dataprovider.ws.v7.listapps.ListAppVersionsRequest;
import cm.aptoide.pt.dataprovider.ws.v7.listapps.ListAppsUpdatesRequest;
import cm.aptoide.pt.dataprovider.ws.v7.store.GetStoreDisplaysRequest;
import cm.aptoide.pt.dataprovider.ws.v7.store.GetStoreMetaRequest;
import cm.aptoide.pt.dataprovider.ws.v7.store.GetStoreRequest;
import cm.aptoide.pt.dataprovider.ws.v7.store.GetStoreTabsRequest;
import cm.aptoide.pt.dataprovider.ws.v7.store.GetStoreWidgetsRequest;
import cm.aptoide.pt.model.v7.GetStoreWidgets;
import cm.aptoide.pt.model.v7.store.GetStore;
import cm.aptoide.pt.networkclient.interfaces.ErrorRequestListener;
import cm.aptoide.pt.networkclient.interfaces.SuccessRequestListener;
import cm.aptoide.pt.v8engine.fragment.GridRecyclerFragment;

public class MainActivityTestes extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_testes);

		getSupportFragmentManager().beginTransaction()
				.replace(R.id.contentor, new GridRecyclerFragment() {
					@Override
					public void load(boolean refresh) {

					}
				})
				.commit();

		final GetStore[] gaga = new GetStore[1];
		GetStoreRequest getStoreRequest = GetStoreRequest.of("apps");
		getStoreRequest.execute(getStoreResponse -> {
			System.out.println("Ss: " + getStoreResponse.getNodes().getMeta());
		}, System.out::println);

		GetAppRequest.of(18176420).execute(getApp -> {
			System.out.println("Teste 2: " + getApp);
		}, new ErrorRequestListener() {
			@Override
			public void onError(Throwable e) {
				System.out.println(e);
			}
		});

		LinkedList<ListAppsUpdatesRequest.ApksData> apksData = new LinkedList<>();
		apksData.add(new ListAppsUpdatesRequest.ApksData("cm.aptoide.pt", 300,
				"D5:90:A7:D7:92:FD:03:31:54:2D:99:FA:F9:99:76:41:79:07:73:A9"));
		ListAppsUpdatesRequest listAppsUpdatesRequest = ListAppsUpdatesRequest.of();
		listAppsUpdatesRequest.getBody().setApksData(apksData);
		listAppsUpdatesRequest.observe().subscribe(System.out::println, System.out::println);

		ListAppVersionsRequest listAppVersionsRequest = ListAppVersionsRequest.of();
		listAppVersionsRequest.getBody().setAppId(18711899);
		listAppVersionsRequest.execute(System.out::println);

		ListSearchAppsRequest of = ListSearchAppsRequest.of("hay day");
		of.execute(listSearchApps -> System.out.println("ListSearchAppsRequest: " +
				listAppsUpdatesRequest));

		GetStoreMetaRequest.of("apps")
				.execute(getStoreMeta -> System.out.println("getStoreMeta: " +
						listAppsUpdatesRequest));

		GetStoreDisplaysRequest.of("apps")
				.execute(getStoreMeta -> System.out.println("GetStoreDisplaysRequest: " +
						listAppsUpdatesRequest));

		GetStoreTabsRequest.of("apps").execute(System.out::println);
		GetStoreWidgetsRequest.of("apps").execute(System.out::println);

		GetStoreWidgetsRequest.of("apps").execute(new SuccessRequestListener<GetStoreWidgets>() {
			@Override
			public void onSuccess(GetStoreWidgets getStoreWidgets) {
				System.out.println(getStoreRequest);
			}
		});
	}
}
