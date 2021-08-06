package cm.aptoide.pt.smart.appfiltering;

import androidx.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONObject;
import java.util.List;

class RemoveHelper {
    private static final String DATALIST = "datalist";
    private static final String LIST = "list";
    private static final String PACKAGE = "package";

    @Nullable
    JSONObject filterResponse(String jsonResponse, List<AppToRemove> removeList) {
        try {
            JSONObject jObject = new JSONObject(jsonResponse);

            JSONObject dataListObject = jObject.getJSONObject(DATALIST);
            JSONArray jArray = dataListObject.getJSONArray(LIST);
            JSONArray newJArray = new JSONArray();
            if (dataListObject != null ) {
                if (jArray != null) {
                    for (int i = 0; i < jArray.length(); i++) {
                        JSONObject application = jArray.getJSONObject(i);
                        if (application != null) {
                            String appPackage = application.getString(PACKAGE);
                            int indexToRemove = -1;
                            for (int j = 0; j < removeList.size(); j++) {
                                if (removeList.get(j).getAppPackage().equals(appPackage)) {
                                    indexToRemove = i;
                                    break;
                                }
                            }
                            if (indexToRemove < 0) {
                                newJArray.put(application);
                            }
                        }
                    }

                    // The SMART Document Camera app has been added to Aptoide, but we can't add it to our specific store.
                    // So, instead, we want to grab its information (i.e. the JSON that represents the app) & add it manually.
                    //
                    // When Aptoide loads, it gets a list of all apps sorted into different categories (Education, Games, etc.)
                    // The Doc Cam app is an "Education" app, so we want to include it with all of the other education apps.
                    // Unfortunately, the specific category isn't included in the JSON from the listApps call.
                    // (The getApp call does include the category, but it only gets called when you tap on an app to view its details.)
                    //
                    // The only useful info in the JSON is the list of existing apps. Since "BrainPOP Featured Movie" is an education app
                    // and it's extremely unlikely to show up in any other category, we use its presence to determine if we're looking at
                    // the education apps, and if so we add the Doc Cam app.
                    //
                    // The JSON was copied on July 28th, 2021.

                    if(jsonResponse.contains("BrainPOP Featured Movie")) {
                        String docCamJson = "{\"id\":58652019,\"name\":\"SMART Doccam\",\"package\":\"com.smarttechapp.SDC\",\"uname\":\"smart-document-camera-650-software\",\"size\":34399325,\"icon\":\"https:\\/\\/pool.img.aptoide.com\\/catappult\\/412f3b3b9ccdb0f666ed7ae6f959bbde_icon.png\",\"graphic\":\"https:\\/\\/pool.img.aptoide.com\\/catappult\\/88fb6bb690391620b52f6376637e9f65_fgraphic.png\",\"added\":\"2021-07-13 16:51:00\",\"modified\":\"2021-07-26 16:25:12\",\"updated\":\"2021-07-15 17:17:25\",\"uptype\":\"webservice\",\"store\":{\"id\":1966380,\"name\":\"catappult\",\"avatar\":\"https:\\/\\/pool.img.aptoide.com\\/catappult\\/8eda7e3bb9a41b8c6f6ed040f8060b6b_ravatar.png\",\"appearance\":{\"theme\":\"deep-purple\",\"description\":\"Unleash your apps.\\r\\nReach millions.\"},\"stats\":{\"apps\":98102,\"subscribers\":525128,\"downloads\":194671007}},\"file\":{\"vername\":\"2.1.1011.0\",\"vercode\":11,\"md5sum\":\"5906f1f64b510e5567df353dd6d763b8\",\"filesize\":34399325,\"path\":\"https:\\/\\/pool.apk.aptoide.com\\/catappult\\/com-smarttechapp-sdc-11-58652019-5906f1f64b510e5567df353dd6d763b8.apk\",\"path_alt\":\"https:\\/\\/pool.apk.aptoide.com\\/catappult\\/alt\\/Y29tLXNtYXJ0dGVjaGFwcC1zZGMtMTEtNTg2NTIwMTktNTkwNmYxZjY0YjUxMGU1NTY3ZGYzNTNkZDZkNzYzYjg.apk\",\"tags\":[],\"malware\":{\"rank\":\"UNKNOWN\"}},\"stats\":{\"downloads\":2,\"pdownloads\":2,\"rating\":{\"avg\":0,\"total\":0},\"prating\":{\"avg\":0,\"total\":0}},\"has_versions\":false,\"obb\":null,\"appcoins\":{\"advertising\":false,\"billing\":false}}";
                        JSONObject docCamJSONObject = new JSONObject(docCamJson);
                        newJArray.put(docCamJSONObject);
                    }

                    dataListObject.remove(LIST);
                    dataListObject.put(LIST, (Object) newJArray);
                    return jObject;
                }
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }
}