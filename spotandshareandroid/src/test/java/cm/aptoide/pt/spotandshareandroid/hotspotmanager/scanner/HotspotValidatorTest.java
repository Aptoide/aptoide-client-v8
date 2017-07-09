package cm.aptoide.pt.spotandshareandroid.hotspotmanager.scanner;

import cm.aptoide.pt.spotandshareandroid.hotspotmanager.MultiRulesValidator;
import cm.aptoide.pt.spotandshareandroid.hotspotmanager.Validator;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by neuro on 28-06-2017.
 */
public class HotspotValidatorTest {

  private List<Validator<Integer>> validators;

  @Before public void init() {
    validators = new LinkedList<>();
    validators.add(integer -> integer % 3 == 0);
    validators.add(integer -> integer % 2 == 0);
  }

  @Test public void validateListWithOnlyOneValidValue() {
    List<Integer> integers = createList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

    MultiRulesValidator<Integer> integerMultiRulesValidator = new MultiRulesValidator<>(validators);
    List<Integer> validate = integerMultiRulesValidator.filter(integers);

    assertEquals(validate, new LinkedList<>(Collections.singletonList(6)));
  }

  @Test public void validateListWithNoValidValues() {
    List<Integer> integers = createList(1, 2, 3, 4, 5);

    MultiRulesValidator<Integer> integerMultiRulesValidator = new MultiRulesValidator<>(validators);
    List<Integer> validate = integerMultiRulesValidator.filter(integers);

    assertEquals(validate, new LinkedList<>(Collections.emptyList()));
  }

  @Test public void validateListWithAllValidValues() {
    List<Integer> integers = createList(6, 12, 30);

    MultiRulesValidator<Integer> integerMultiRulesValidator = new MultiRulesValidator<>(validators);
    List<Integer> validate = integerMultiRulesValidator.filter(integers);

    assertEquals(validate, new LinkedList<>(Arrays.asList(6, 12, 30)));
  }

  private List<Integer> createList(Integer... integers) {
    List<Integer> tmp = new LinkedList<>();

    for (Integer integer : integers) {
      tmp.add(integer);
    }

    return tmp;
  }
}