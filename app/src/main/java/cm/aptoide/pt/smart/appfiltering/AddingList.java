package cm.aptoide.pt.smart.appfiltering;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

class AddingList {
    static final List<AppToAdd> addingList = new CopyOnWriteArrayList<>();

    private AddingList() {
    }

    public static void populateAddingList(List<AppToAdd> list) {
        addingList.clear();
        addingList.addAll(list);
    }
}
