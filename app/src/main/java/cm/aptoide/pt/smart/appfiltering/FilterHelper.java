package cm.aptoide.pt.smart.appfiltering;

import android.util.Log;

import androidx.annotation.Nullable;

import cm.aptoide.pt.smart.appfiltering.data.GetAppResponse;
import cm.aptoide.pt.smart.appfiltering.data.Version;
import cm.aptoide.pt.smart.appfiltering.data.Versions;
import cm.aptoide.pt.smart.appfiltering.data.File;
import cm.aptoide.pt.smart.appfiltering.data.Nodes;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.List;
public class FilterHelper {
    private static final String APK_LINK = "https://pool.apk.aptoide.com/";
    private static final String TAG = "FilterHelper";

    @Nullable
    public JSONObject filterResponse(String jsonResponse, List<AppToRemove> filterList) {
        Gson gson = new Gson();
        GetAppResponse response = null;
        try {
            response = gson.fromJson(jsonResponse, GetAppResponse.class);
        }
        catch (JsonSyntaxException e) {
            Log.e(TAG, "Json syntax exception: " + e.getMessage());
        }

        if (notNullCheck(response)) {
            response.getNodes().setVersions(removeVersions(response.getNodes().getVersions(), filterList));
            rebuildLatestVersionIfNeeded(response, filterList);
            try {
                JSONObject ob = new JSONObject(gson.toJson(response));
                return ob;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Nullable
    public JSONObject filterVersionRequest(String jsonResponse, List<AppToRemove> filterList) {
        Gson gson = new Gson();
        Versions response = gson.fromJson(jsonResponse, Versions.class);

        response = removeVersions(response, filterList);
        try {
            JSONObject ob = new JSONObject(gson.toJson(response));
            return ob;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void rebuildLatestVersionIfNeeded(GetAppResponse response, List<AppToRemove> filterList) {
        boolean shouldRebuild = shouldRebuildLatestVersion(getLatestVersion(response), filterList);
        if (shouldRebuild) {
            List<Version> versions = response.getNodes().getVersions().getList();
            if (!versions.isEmpty()) {
                Version maxVersion = versions.get(0);
                for (Version version : versions) {
                    if (version.getFile().getVercode() > maxVersion.getFile().getVercode() ) {
                        maxVersion = version;
                    }
                }
                rebuildLatestVersion(response, maxVersion);
            }
        }
    }

    private void rebuildLatestVersion(GetAppResponse response, Version version) {
        String apkLink = buildLinkForTheSpecificVersion(version);
        File apkFile = response.getNodes().getMeta().getData().getFile();
        apkFile.setVername(version.getFile().getVername());
        apkFile.setVercode(version.getFile().getVercode());
        apkFile.setPath(apkLink);
        apkFile.setMd5sum(version.getFile().getMd5sum());

        // This is just a visual change - we want to display the correct size for the app & the
        // progress bar to go to 100%. Without this, the download would still succeed but the
        // progress bar might cut off early or go beyond 100%, depending on the size of the apk
        // that we filtered compared to the one that we're now using.
        response.getNodes().getMeta().getData().setSize(version.getSize());
    }

    private String getLatestVersion(GetAppResponse response) {
        return response.getNodes().getMeta().getData().getFile().getVername();
    }

    private boolean notNullCheck(GetAppResponse response) {
        return response != null
                && response.getNodes() != null
                && response.getNodes().getMeta() != null
                && response.getNodes().getMeta().getData() != null
                && response.getNodes().getMeta().getData().getFile() != null
                && response.getNodes().getVersions() != null;
    }

    private boolean shouldRebuildLatestVersion(String latestVersion, List<AppToRemove> filterList) {
        boolean result = false;
        for(AppToRemove app: filterList) {
            if (latestVersion.equals(app.getVersion())) {
                result = true;
                break;
            }
        }

        return result;
    }

    private String buildLinkForTheSpecificVersion(Version version) {
        String link = APK_LINK  + version.getStore().getName()
                + "/" + version.getPackage().replace(".", "-") +
                "-" + version.getFile().getVercode() + "-" +
                version.getId() + "-" + version.getFile().getMd5sum()
                + ".apk";
        return link;
    }



    private Versions removeVersions(Versions versions, List<AppToRemove> appsToRemove) {
        for (AppToRemove app: appsToRemove) {
            removeVersion(versions, app.getVersion());
        }
        return versions;
    }

    private void removeVersion(Versions versions, String version) {
        List<Version> versionsList = versions.getList();
        if (versionsList != null) {
            Iterator<Version> iterator = versionsList.listIterator();

            while (iterator.hasNext()) {
                if (iterator.next().getFile().getVername().equals(version)) {
                    iterator.remove();
                    break;
                }
            }
        }
    }
}
