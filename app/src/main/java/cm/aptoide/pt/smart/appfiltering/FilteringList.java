package cm.aptoide.pt.smart.appfiltering;


import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

class FilteringList {
    public static final List<AppToRemove> removingList;
    public static final List<AppToRemove> filteringList;

    static  {
        removingList = new ArrayList<>();
        removingList.add(new AppToRemove("us.zoom.videomeetings", "5.4.7.946"));
        removingList.add(new AppToRemove("org.mozilla.firefox", "84.1.4"));

        filteringList = new ArrayList<>();
        filteringList.add(new AppToRemove("org.videolan.vlc", "3.3.4")) ;
        filteringList.add(new AppToRemove("org.videolan.vlc", "3.2.3")) ;

    }

}
