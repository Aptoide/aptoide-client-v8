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