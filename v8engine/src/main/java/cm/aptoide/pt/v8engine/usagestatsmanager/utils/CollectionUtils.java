package cm.aptoide.pt.v8engine.usagestatsmanager.utils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by neuro on 01-06-2017.
 */

public class CollectionUtils {

  public static <T, U> List<U> map(List<T> list, Mapper<T, U> mapper) {
    List<U> extractedList = new LinkedList<>();
    for (T t : list) {
      extractedList.add(mapper.extract(t));
    }

    return extractedList;
  }

  public static <T> List<T> removeDuplicates(List<T> list, Same<T> same,
      DuplicatesComparator<T> duplicatesComparator) {
    Map<T, Void> toRemove = new HashMap<>();

    Iterator<T> iterator = list.iterator();
    while (iterator.hasNext()) {
      T next = iterator.next();
      for (T t : list) {
        if (t != next && same.isSame(t, next) && duplicatesComparator.compare(t, next) >= 0) {
          toRemove.put(next, null);
        }
      }
    }

    list.removeAll(toRemove.keySet());
    return list;
  }

  public static <T> List<T> mergeAndRemoveDuplicates(List<T> t1, List<T> t2, Same<T> same,
      DuplicatesComparator<T> duplicatesComparator) {
    LinkedList<T> usageEvents = new LinkedList<>();

    usageEvents.addAll(t1);
    usageEvents.addAll(t2);

    return removeDuplicates(usageEvents, same, duplicatesComparator);
  }
}
