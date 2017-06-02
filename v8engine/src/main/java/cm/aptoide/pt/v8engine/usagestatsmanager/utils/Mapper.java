package cm.aptoide.pt.v8engine.usagestatsmanager.utils;

/**
 * Created by neuro on 01-06-2017.
 */
public interface Mapper<T, U> {

  U extract(T t);
}
