package cm.aptoide.pt.spotandshareandroid.hotspotmanager;

/**
 * Created by neuro on 28-06-2017.
 */

public interface Persister<T, U> {

  void save(T key, U value);

  U load(T key);
}
