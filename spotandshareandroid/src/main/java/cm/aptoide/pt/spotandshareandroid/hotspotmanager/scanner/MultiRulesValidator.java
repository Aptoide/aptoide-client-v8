package cm.aptoide.pt.spotandshareandroid.hotspotmanager.scanner;

import cm.aptoide.pt.spotandshareandroid.hotspotmanager.Validator;
import java.util.Iterator;
import java.util.List;

/**
 * Created by neuro on 28-06-2017.
 */
public class MultiRulesValidator<T> {

  private final List<Validator<T>> validators;

  MultiRulesValidator(List<Validator<T>> validators) {
    this.validators = validators;
  }

  public List<T> filter(List<T> values) {
    List<T> tmp = values;

    // TODO: 28-06-2017 neuro optimize
    for (Validator<T> validator : validators) {
      Iterator<T> iterator = tmp.iterator();
      while (iterator.hasNext()) {
        T scanResult = iterator.next();
        if (!validator.validate(scanResult)) {
          iterator.remove();
        }
      }
    }

    return tmp;
  }
}
