package cm.aptoide.pt.smart.appfiltering;


import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

class FilteringList {
    public static final List<AppToRemove> removingList;

    static  {
        removingList = new ArrayList<>();
        removingList.add(new AppToRemove("us.zoom.videomeetings", "5.4.7.946"));
        removingList.add(new AppToRemove("org.mozilla.firefox", "84.1.4"));
    }


}
