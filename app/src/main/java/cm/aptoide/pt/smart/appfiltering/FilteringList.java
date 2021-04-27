package cm.aptoide.pt.smart.appfiltering;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

class FilteringList {
    static final List<AppToRemove> removingList = new CopyOnWriteArrayList<>();
    static final List<AppToRemove> filteringList = new CopyOnWriteArrayList<>();

    private FilteringList() {
    }

    public static void populateFilteringList(List<AppToRemove> list) {
        filteringList.clear();
        filteringList.addAll(list);
    }

    public static void populateRemovingList(List<AppToRemove> list) {
        removingList.clear();
        removingList.addAll(list);
    }
}
