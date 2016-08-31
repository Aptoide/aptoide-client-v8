/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 06/07/2016.
 */

package cm.aptoide.pt.dataprovider.ws.v7;

import android.support.annotation.NonNull;

import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import cm.aptoide.pt.model.v7.BaseV7Response;
import cm.aptoide.pt.model.v7.Datalist;
import cm.aptoide.pt.model.v7.listapp.App;
import cm.aptoide.pt.model.v7.listapp.File;
import cm.aptoide.pt.model.v7.store.Store;
import cm.aptoide.pt.model.v7.timeline.AppUpdate;
import cm.aptoide.pt.model.v7.timeline.AppUpdateTimelineItem;
import cm.aptoide.pt.model.v7.timeline.Article;
import cm.aptoide.pt.model.v7.timeline.ArticleTimelineItem;
import cm.aptoide.pt.model.v7.timeline.Feature;
import cm.aptoide.pt.model.v7.timeline.FeatureTimelineItem;
import cm.aptoide.pt.model.v7.timeline.GetUserTimeline;
import cm.aptoide.pt.model.v7.timeline.Publisher;
import cm.aptoide.pt.model.v7.timeline.StoreLatestApps;
import cm.aptoide.pt.model.v7.timeline.StoreLatestAppsTimelineItem;
import cm.aptoide.pt.model.v7.timeline.TimelineCard;
import cm.aptoide.pt.model.v7.timeline.TimelineItem;
import cm.aptoide.pt.networkclient.WebService;
import cm.aptoide.pt.networkclient.okhttp.OkHttpClientFactory;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import rx.observers.TestSubscriber;

public class GetUserTimelineRequestIntegrationTest {

	@Test
	public void shouldReturnAppUpdate() throws Exception {
		final MockWebServer server = new MockWebServer();

		server.enqueue(new MockResponse().setResponseCode(200).setBody("{\n" +
				"   \"info\": {\n" +
				"      \"status\": \"OK\",\n" +
				"      \"time\": {\n" +
				"         \"seconds\": 0.0098769664764404,\n" +
				"         \"human\": \"9 milliseconds\"\n" +
				"      }\n" +
				"   },\n" +
				"   \"datalist\":{  \n" +
				"      \"total\":11,\n" +
				"      \"count\":9,\n" +
				"      \"offset\":0,\n" +
				"      \"limit\":25,\n" +
				"      \"next\":25,\n" +
				"      \"hidden\":0,\n" +
				"      \"loaded\":true, " +
				"      \"list\": [{  \n" +
				"         \"type\":\"APP_UPDATE\",\n" +
				"         \"data\":  \n" +
				"                  {  \n" +
				"                     \"id\":18849509,\n" +
				"                     \"name\":\"Home Budget with Sync Lite\",\n" +
				"                     \"package\":\"com.anishu.homebudget.lite\",\n" +
				"                     \"size\":2372649,\n" +
				"                     \"icon\":\"http://pool.img.aptoide" +
				".com/apps/db4d92cd5e5d1df2fa8d679853db4810_icon.png\",\n" +
				"                     \"graphic\":null,\n" +
				"                     \"added\":\"2016-05-05 17:29:34\",\n" +
				"                     \"modified\":\"2016-05-05 17:29:34\",\n" +
				"                     \"updated\":\"2016-06-16 03:21:48\",\n" +
				"                     \"uptype\":\"webservice\",\n" +
				"                     \"store\":{  \n" +
				"                        \"id\":15,\n" +
				"                        \"name\":\"apps\",\n" +
				"                        \"avatar\":\"http://pool.img.aptoide" +
				".com/apps/815872daa4e7a55f93cb3692aff65e31_ravatar.jpg\",\n" +
				"                        \"appearance\":{  \n" +
				"                           \"theme\":\"default\",\n" +
				"                           \"description\":\"Aptoide Official App Store\"\n" +
				"                        },\n" +
				"                        \"stats\":{  \n" +
				"                           \"apps\":15,\n" +
				"                           \"subscribers\":15,\n" +
				"                           \"downloads\":1000944445\n" +
				"                        }\n" +
				"                     },\n" +
				"                     \"file\":{  \n" +
				"                        \"vername\":\"3.2.2\",\n" +
				"                        \"vercode\":39,\n" +
				"                        \"md5sum\":\"4401f53566e6402e3ae6f69f89518c65\",\n" +
				"                        \"path\":\"http://pool.apk.aptoide" +
				".com/apps/com-anishu-homebudget-lite-39-18849509-4401f53566e6402e3ae6f69f89518c65.apk\",\n" +
				"                        \"path_alt\":\"http://pool.apk.aptoide" +
				".com/apps/alt" +
				"/Y29tLWFuaXNodS1ob21lYnVkZ2V0LWxpdGUtMzktMTg4NDk1MDktNDQwMWY1MzU2NmU2NDAyZTNhZTZmNjlmODk1MThjNjU" +
				".apk\",\n" +
				"                        \"filesize\":2372649\n" +
				"                     },\n" +
				"                     \"stats\":{  \n" +
				"                        \"downloads\":1073,\n" +
				"                        \"rating\":{  \n" +
				"                           \"avg\":3,\n" +
				"                           \"total\":2,\n" +
				"                           \"votes\":[  \n" +
				"                              {  \n" +
				"                                 \"value\":5,\n" +
				"                                 \"count\":1\n" +
				"                              },\n" +
				"                              {  \n" +
				"                                 \"value\":4,\n" +
				"                                 \"count\":0\n" +
				"                              },\n" +
				"                              {  \n" +
				"                                 \"value\":3,\n" +
				"                                 \"count\":0\n" +
				"                              },\n" +
				"                              {  \n" +
				"                                 \"value\":2,\n" +
				"                                 \"count\":0\n" +
				"                              },\n" +
				"                              {  \n" +
				"                                 \"value\":1,\n" +
				"                                 \"count\":1\n" +
				"                              }\n" +
				"                           ]\n" +
				"                        }\n" +
				"                     },\n" +
				"                     \"obb\":null\n" +
				"           }" +
				"         " +
				"       }" +
				"]}" +
				"}"));

		server.start();

		final GetUserTimelineRequest request = getGetUserTimelineRequest("", server.url("/").toString(), "1234", "ABC", 1, "bla", "PT-BR", "MyQ", 5, true, 0, Collections.emptyList());

		TestSubscriber<GetUserTimeline> testSubscriber = new TestSubscriber<>();
		request.observe().subscribe(testSubscriber);
		testSubscriber.awaitTerminalEvent();
		testSubscriber.assertNoErrors();
		testSubscriber.assertValueCount(1);
		final List<TimelineItem<TimelineCard>> items = Arrays.asList(new AppUpdateTimelineItem
				(getAppUpdate(null, 18849509, "Home Budget " + "with Sync Lite", "com.anishu.homebudget.lite",
						2372649, "http://pool.img.aptoide" +
						".com/apps/db4d92cd5e5d1df2fa8d679853db4810_icon" + ".png", "2016-05-05 17:29:34",
						"2016-05-05" + " 17:29:34", "2016-06-16 03:21:48", "webservice", getFile("3" + ".2.2", 39,
								"4401f53566e6402e3ae6f69f89518c65", "http://pool.apk.aptoide" +
						".com/apps/com-anishu-homebudget-lite-39-18849509" +
						"-4401f53566e6402e3ae6f69f89518c65.apk", "http://pool.apk.aptoide" + "" +
										".com/apps/alt/Y29tLWFuaXNodS1ob21lYnVkZ2V0LWxpdGUtMzktMTg4NDk1MDktNDQwMWY1MzU2NmU2NDAyZTNhZTZmNjlmODk1MThjNjU.apk", 2372649), getStats(1073, getRating(3, 2, Arrays
						.asList(getVote(5, 1), getVote(4, 0), getVote(3, 0), getVote(2, 0), getVote(1, 1)))), getStore
								(15, "apps", "http://pool.img.aptoide" +
						".com/apps/815872daa4e7a55f93cb3692aff65e31_ravatar" + ".jpg", null, null, getAppearance
										("Aptoide Official App Store", "default"), getStats(15, 15, 1000944445)))));
		testSubscriber.assertValue(getUserTimeline(BaseV7Response.Info.Status.OK, 0.0098769664764404, "9 milliseconds", getDataList(items, true, 0, 25, 25, 0, 9, 11)));
		testSubscriber.assertCompleted();

		server.shutdown();
	}

	@Test
	public void shouldReturnLatestApps() throws Exception {

		final MockWebServer server = new MockWebServer();

		double seconds = 0.0098769664764404;
		String human = "9 milliseconds";

		int id = 15;
		String name = "apps";
		String avatar = "http://pool.img.aptoide.com/apps/815872daa4e7a55f93cb3692aff65e31_ravatar.jpg";
		String added = "2010-11-04 12:21:52";
		String modified = "2010-11-04 12:21:52";
		String theme = "default";
		String description = "Aptoide Official App Store";
		int apps = 168747;
		int subscribers = 143460;
		int downloads = 996949250;
		int appId = 19267958;
		String appName = "PhotoDirector - Bundle Version";
		String packageName = "com.cyberlink.photodirector.bundle";
		int size = 42221786;
		String icon = "http://pool.img.aptoide.com/apps/bf798d32cbd34cd918eb4bd5a784d36d_icon.png";
		String graphic = null;
		String appAdded = "2016-06-09 00:04:33";
		String appModified = "2016-06-09 00:04:33";
		String appUpdated = "2016-06-16 15:46:47";
		String uptype = "webservice";
		String fileVersionName = "4.0.1";
		int fileVersionCode = 6004010;
		String md5 = "e719a83b029e8e8b3310d9a3cba4858a";
		int appDownloads = 627;
		int stars = 5;
		int total = 2;
		server.enqueue(new MockResponse().setResponseCode(200).setBody("{\n" +
				"   \"info\": {\n" +
				"      \"status\": \"OK\",\n" +
				"      \"time\": {\n" +
				"         \"seconds\": " + seconds + ",\n" +
				"         \"human\": \"" + human + "\"\n" +
				"      }\n" +
				"   },\n" +
				"   \"datalist\":{  \n" +
				"      \"total\":11,\n" +
				"      \"count\":9,\n" +
				"      \"offset\":0,\n" +
				"      \"limit\":25,\n" +
				"      \"next\":25,\n" +
				"      \"hidden\":0,\n" +
				"      \"loaded\":true, " +
				"      \"list\": [ \n" +
				"{  \n" +
				"   \"type\":\"APPS_LATEST\",\n" +
				"   \"data\":  \n" +
				"      {  \n" +
				"         \"store\":{  \n" +
				"            \"id\":" + id + ",\n" +
				"            \"name\":\"" + name + "\",\n" +
				"            \"avatar\":\"" + avatar + "\",\n" +
				"            \"added\":\"" + added + "\",\n" +
				"            \"modified\":\"" + modified + "\",\n" +
				"            \"appearance\":{  \n" +
				"               \"theme\":\"" + theme + "\",\n" +
				"               \"description\":\"" + description + "\"\n" +
				"            },\n" +
				"            \"stats\":{  \n" +
				"               \"apps\":" + apps + ",\n" +
				"               \"subscribers\":" + subscribers + ",\n" +
				"               \"downloads\":" + downloads + "\n" +
				"            }\n" +
				"         },\n" +
				"         \"apps\":[  \n" +
				"            {  \n" +
				"               \"id\":" + appId + ",\n" +
				"               \"name\":\"" + appName + "\",\n" +
				"               \"package\":\"" + packageName + "\",\n" +
				"               \"size\":" + size + ",\n" +
				"               \"icon\":\"" + icon + "\",\n" +
				"               \"graphic\":" + graphic + ",\n" +
				"               \"added\":\"" + appAdded + "\",\n" +
				"               \"modified\":\"" + appModified + "\",\n" +
				"               \"updated\":\"" + appUpdated + "\",\n" +
				"               \"uptype\":\"" + uptype + "\",\n" +
				"               \"file\":{  \n" +
				"                  \"vername\":\"" + fileVersionName + "\",\n" +
				"                  \"vercode\":" + fileVersionCode + ",\n" +
				"                  \"md5sum\":\"" + md5 + "\"\n" +
				"               },\n" +
				"               \"stats\":{  \n" +
				"                  \"downloads\":" + appDownloads + ",\n" +
				"                  \"rating\":{  \n" +
				"                     \"avg\":" + stars + ",\n" +
				"                     \"total\":" + total + ",\n" +
				"                     \"votes\":[  \n" +
				"                        {  \n" +
				"                           \"value\":5,\n" +
				"                           \"count\":2\n" +
				"                        },\n" +
				"                        {  \n" +
				"                           \"value\":4,\n" +
				"                           \"count\":0\n" +
				"                        },\n" +
				"                        {  \n" +
				"                           \"value\":3,\n" +
				"                           \"count\":0\n" +
				"                        },\n" +
				"                        {  \n" +
				"                           \"value\":2,\n" +
				"                           \"count\":0\n" +
				"                        },\n" +
				"                        {  \n" +
				"                           \"value\":1,\n" +
				"                           \"count\":0\n" +
				"                        }\n" +
				"                     ]\n" +
				"                  }\n" +
				"               }\n" +
				"            }\n" +
				"         ]\n" +
				"      }\n" +
				"   \n" +
				"}" +
				"   ]}\n" +
				"}"));

		server.start();

		final GetUserTimelineRequest request = getGetUserTimelineRequest(server.url("/").toString(), server.url("/")
				.toString(), "1234", "ABC", 1, "bla", "PT-BR", "MyQ", 5, true, 0, Collections.emptyList());

		TestSubscriber<GetUserTimeline> testSubscriber = new TestSubscriber<>();
		request.observe().subscribe(testSubscriber);
		testSubscriber.awaitTerminalEvent();
		testSubscriber.assertNoErrors();
		testSubscriber.assertValueCount(1);
		final List<TimelineItem<TimelineCard>> storeLatestAppsTimelineItems = Arrays.asList(new
				StoreLatestAppsTimelineItem(new StoreLatestApps(null, getStore(id, name, avatar, getDate("UTC", added,
				"yyyy-MM-dd hh:mm:ss"), getDate("UTC", modified, "yyyy-MM-dd hh:mm:ss"), getAppearance(description,
				theme), getStats(apps, subscribers, downloads)), Arrays
				.asList(getApp(new App(), appId, appName, packageName, size, icon, graphic, appAdded, appModified, appUpdated,
						uptype, getFile(fileVersionName, fileVersionCode, md5, null, null, 0), getStats(appDownloads,
								getRating(stars, total, Arrays
						.asList(getVote(5, 2), getVote(4, 0), getVote(3, 0), getVote(2, 0), getVote(1, 0)))))))));
		testSubscriber.assertValue(getUserTimeline(BaseV7Response.Info.Status.OK, seconds, human, getDataList(storeLatestAppsTimelineItems, true, 0, 25, 25, 0, 9, 11)));
		testSubscriber.assertCompleted();

		server.shutdown();
	}

	@Test
	public void shouldReturnFeatures() throws Exception {

		final MockWebServer server = new MockWebServer();

		server.enqueue(new MockResponse().setResponseCode(200).setBody("{\n" +
				"   \"info\": {\n" +
				"      \"status\": \"OK\",\n" +
				"      \"time\": {\n" +
				"         \"seconds\": 0.0098769664764404,\n" +
				"         \"human\": \"9 milliseconds\"\n" +
				"      }\n" +
				"   },\n" +
				"   \"datalist\":{  \n" +
				"      \"total\":11,\n" +
				"      \"count\":9,\n" +
				"      \"offset\":0,\n" +
				"      \"limit\":25,\n" +
				"      \"next\":25,\n" +
				"      \"hidden\":0,\n" +
				"      \"loaded\":true, " +
				"      \"list\": [ \n" +
				"{  \n" +
				"   \"type\":\"FEATURE\",\n" +
				"   \"data\":{  \n" +
				"         \"title\":\"Here, have a Xiaomi Redmi Note 3 pro on us\",\n" +
				"         \"thumbnail\":\"https://d36eyd5j1kt1m6.cloudfront.net/user-assets/127178/tXbdAxcnnJSp852q/01" +
				".png?1461237960\",\n" +
				"         \"url\":\"http://blog.aptoide.com/here-have-a-xiaomi-redmi-note-3-pro-on-us/\",\n" +
				"         \"date\":\"2016-04-20\"\n" +
				"      }\n" +
				"}" +
				"   ]}\n" +
				"}"));

		server.start();

		final GetUserTimelineRequest request = getGetUserTimelineRequest(server.url("/").toString(), server.url("/")
				.toString(), "1234", "ABC", 1, "bla", "PT-BR", "MyQ", 5, true, 0, Collections.emptyList());

		TestSubscriber<GetUserTimeline> testSubscriber = new TestSubscriber<>();
		request.observe().subscribe(testSubscriber);
		testSubscriber.awaitTerminalEvent();
		testSubscriber.assertNoErrors();
		testSubscriber.assertValueCount(1);
		final List<TimelineItem<TimelineCard>> items = Arrays.asList(new FeatureTimelineItem(new Feature(null, "Here, have a Xiaomi " +
				"Redmi Note 3 pro on us", "https://d36eyd5j1kt1m6.cloudfront" +
				".net/user-assets/127178/tXbdAxcnnJSp852q/01.png?1461237960", "http://blog.aptoide" +
				".com/here-have-a-xiaomi-redmi-note-3-pro-on-us/", getDate("UTC", "2016-04-20", "yyyy-MM-dd"),
				null)));
		testSubscriber.assertValue(getUserTimeline(BaseV7Response.Info.Status.OK, 0.0098769664764404, "9 " +
				"milliseconds", getDataList(items, true, 0, 25, 25, 0, 9, 11)));
		testSubscriber.assertCompleted();

		server.shutdown();
	}

	@Test
	public void shouldReturnArticles() throws Exception {

		final MockWebServer server = new MockWebServer();

		double seconds = 0.0098769664764404;
		String human = "9 milliseconds";
		String title = "Best backup apps: save all your Android's innards";
		String thumbnail = "https://fs01.androidpit" +
				".info/userfiles/6727621/image/2nd_YEAR/Daily_business/AndroidPIT-backup-9981-w1218h580.jpg";

		String publisher = "androidpit";
		String url = "https://www.androidpit.com/best-backup-apps";
		String date = "2016-04-28";
		String avatarUrl = "http://cdn03.androidauthority.net/wp-content/uploads/2015/07/favicon.png";
		String id = "438a3a96445ff8be9374bb2cb6a6a94ddcd5d3e0c0352bf3b9365fc19ae674a8";
		server.enqueue(new MockResponse().setResponseCode(200).setBody("{\n" +
				"   \"info\": {\n" +
				"      \"status\": \"OK\",\n" +
				"      \"time\": {\n" +
				"         \"seconds\": " + seconds + ",\n" +
				"         \"human\": \"" + human + "\"\n" +
				"      }\n" +
				"   },\n" +
				"   \"datalist\":{  \n" +
				"      \"total\":11,\n" +
				"      \"count\":9,\n" +
				"      \"offset\":0,\n" +
				"      \"limit\":25,\n" +
				"      \"next\":25,\n" +
				"      \"hidden\":0,\n" +
				"      \"loaded\":true, " +
				"      \"list\": [ \n" +
				"{  \n" +
				"   \"type\":\"ARTICLE\",\n" +
				"   \"data\":  \n" +
				"      {  \n" +
				"         \"uid\":\"" + id + "\",\n" +
				"         \"title\":\"" + title + "\",\n" +
				"         \"thumbnail\":\"" + thumbnail + "\"," +
				"\n" +
				          "\"publisher\":{  \n" +
				"                  \"name\":\"" + publisher + "\",\n" +
				"                  \"logo\":\"" + avatarUrl + "\"\n" +
				"               },         " +
				"         \"url\":\"" + url + "\",\n" +
				"         \"date\":\"" + date + "\"\n" +
				"      }\n" +
				"   \n" +
				"}" +
				"   ]}\n" +
				"}"));

		server.start();

		final GetUserTimelineRequest request = getGetUserTimelineRequest(server.url("/").toString(), server.url("/")
				.toString(), "1234", "ABC", 1, "bla", "PT-BR", "MyQ", 5, true, 0, Collections.emptyList());

		TestSubscriber<GetUserTimeline> testSubscriber = new TestSubscriber<>();
		request.observe().subscribe(testSubscriber);
		testSubscriber.awaitTerminalEvent();
		testSubscriber.assertNoErrors();
		testSubscriber.assertValueCount(1);
		final List<TimelineItem<TimelineCard>> items = Arrays.asList(new ArticleTimelineItem(new Article(id, title, thumbnail,
				getPublisher(publisher, avatarUrl), url, getDate("UTC", date, "yyyy-MM-dd"), null)));
		testSubscriber.assertValue(getUserTimeline(BaseV7Response.Info.Status.OK, seconds, human, getDataList(items, true, 0, 25, 25, 0, 9, 11)));
		testSubscriber.assertCompleted();

		server.shutdown();
	}

	@Test
	public void shouldReturnEmptyList() throws Exception {

		final MockWebServer server = new MockWebServer();

		server.enqueue(new MockResponse().setResponseCode(200).setBody("{\n" +
				"   \"info\": {\n" +
				"      \"status\": \"OK\",\n" +
				"      \"time\": {\n" +
				"         \"seconds\": 0.0098769664764404,\n" +
				"         \"human\": \"9 milliseconds\"\n" +
				"      }\n" +
				"   },\n" +
				"   \"datalist\":{  \n" +
				"      \"total\":11,\n" +
				"      \"count\":9,\n" +
				"      \"offset\":0,\n" +
				"      \"limit\":25,\n" +
				"      \"next\":25,\n" +
				"      \"hidden\":0,\n" +
				"      \"loaded\":true, " +
				"      \"list\": [ ]}\n" +
				"}"));

		server.start();

		final GetUserTimelineRequest request = getGetUserTimelineRequest(server.url("/").toString(), server.url("/")
				.toString(), "1234", "ABC", 1, "bla", "PT-BR", "MyQ", 5, true, 0, Collections.emptyList());

		TestSubscriber<GetUserTimeline> testSubscriber = new TestSubscriber<>();
		request.observe().subscribe(testSubscriber);
		testSubscriber.awaitTerminalEvent();
		testSubscriber.assertNoErrors();
		testSubscriber.assertValueCount(1);
		testSubscriber.assertValue(getUserTimeline(BaseV7Response.Info.Status.OK, 0.0098769664764404, "9 " +
				"milliseconds", getDataList(Collections
				.emptyList(), true, 0, 25, 25, 0, 9, 11)));
		testSubscriber.assertCompleted();

		server.shutdown();
	}

	@NonNull
	private AppUpdate getAppUpdate(String cardId, int id, String name, String packageName, int size, String icon, String added, String modified, String updated,
	                               String uptype, File file, App.Stats stats, Store store) throws ParseException {
		AppUpdate app = (AppUpdate) getApp(new AppUpdate(cardId), id, name, packageName, size, icon, null, added, modified, updated, uptype, file, stats);
		app.setStore(store);
		return app;
	}

	@NonNull
	private App getApp(App app, int id, String name, String packageName, int size, String icon, String graphic, String added, String modified, String updated, String uptype, File file, App.Stats stats) throws
			ParseException {
		app.setId(id);
		app.setName(name);
		app.setPackageName(packageName);
		app.setSize(size);
		app.setIcon(icon);
		app.setGraphic(graphic);
		app.setAdded(getDate("UTC", added, "yyyy-MM-dd HH:mm:ss"));
		app.setModified(getDate("UTC", modified, "yyyy-MM-dd HH:mm:ss"));
		app.setUpdated(getDate("UTC", updated, "yyyy-MM-dd HH:mm:ss"));
		app.setUptype(uptype);
		app.setFile(file);
		app.setStats(stats);
		return app;
	}

	@NonNull
	private App.Stats getStats(int downloads, App.Stats.Rating rating) {
		App.Stats stats = new App.Stats();
		stats.setDownloads(downloads);
		stats.setRating(rating);
		return stats;
	}

	@NonNull
	private App.Stats.Rating getRating(int avg, int total, List<App.Stats.Rating.Vote> votes) {
		App.Stats.Rating rating = new App.Stats.Rating();
		rating.setAvg(avg);
		rating.setTotal(total);
		rating.setVotes(votes);
		return rating;
	}

	@NonNull
	private File getFile(String vername, int vercode, String md5sum, String path, String pathAlt, int filesize) {
		File file = new File();
		file.setVername(vername);
		file.setVercode(vercode);
		file.setMd5sum(md5sum);
		file.setPath(path);
		file.setPathAlt(pathAlt);
		file.setFilesize(filesize);
		return file;
	}

	@NonNull
	private Store getStore(int id, String name, String avatar, Date dateAdded, Date dateModified, Store.Appearance
			appearance, Store.Stats stats) throws ParseException {
		final Store store = new Store();
		store.setId(id);
		store.setName(name);
		store.setAvatar(avatar);
		store.setAdded(dateAdded);
		store.setModified(dateModified);
		store.setAppearance(appearance);
		store.setStats(stats);
		return store;
	}

	@NonNull
	private Store.Stats getStats(int apps, int subscribers, int downloads) {
		Store.Stats stats = new Store.Stats();
		stats.setApps(apps);
		stats.setSubscribers(subscribers);
		stats.setDownloads(downloads);
		return stats;
	}

	@NonNull
	private Store.Appearance getAppearance(String description, String theme) {
		Store.Appearance appearance = new Store.Appearance();
		appearance.setDescription(description);
		appearance.setTheme(theme);
		return appearance;
	}

	@NonNull
	private GetUserTimelineRequest getGetUserTimelineRequest(String url, String baseHost, String aptoideId, String accessToken, int aptoideVercode, String
			cdn, String language, String q, Integer limit, boolean mature, int offset, List<String> packages) {
		return new GetUserTimelineRequest(url, new GetUserTimelineRequest.Body(limit, offset, packages), OkHttpClientFactory.newClient(),
				WebService.getDefaultConverter(), baseHost);
	}

	@NonNull
	private GetUserTimeline getUserTimeline(BaseV7Response.Info.Status status, double seconds, String humanTime, Datalist<TimelineItem<TimelineCard>>
			dataList) {
		final GetUserTimeline getUserTimeline = new GetUserTimeline();

		getUserTimeline.setDatalist(dataList);

		final BaseV7Response.Info info = new BaseV7Response.Info();
		info.setStatus(status);

		final BaseV7Response.Info.Time time = new BaseV7Response.Info.Time();

		time.setSeconds(seconds);
		time.setHuman(humanTime);

		info.setTime(time);
		getUserTimeline.setInfo(info);
		return getUserTimeline;
	}

	@NonNull
	private Datalist<TimelineItem<TimelineCard>> getDataList(List<TimelineItem<TimelineCard>> list, boolean loaded, int hidden, int next, Integer limit, int
			offset, int
			count, int total) {
		Datalist<TimelineItem<TimelineCard>> datalist = new Datalist<>();
		datalist.setTotal(total);
		datalist.setCount(count);
		datalist.setOffset(offset);
		datalist.setLimit(limit);
		datalist.setNext(next);
		datalist.setHidden(hidden);
		datalist.setLoaded(loaded);
		datalist.setList(list);
		return datalist;
	}

	private Date getDate(String timezone, String date, String dateFormat) throws ParseException {
		SimpleDateFormat df = new SimpleDateFormat(dateFormat);
		df.setTimeZone(TimeZone.getTimeZone(timezone));
		return df.parse(date);
	}

	private App.Stats.Rating.Vote getVote(int value, int count) {
		App.Stats.Rating.Vote vote = new App.Stats.Rating.Vote();
		vote.setValue(value);
		vote.setCount(count);
		return vote;
	}

	private Publisher getPublisher(String publisher, String avatarUrl) {
		return new Publisher(publisher, avatarUrl, "");
	}

}
