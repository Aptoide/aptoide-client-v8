package cm.aptoide.analytics;

import java.util.Map;

public interface KeyValueNormalizer {
  /**
   * Parses a Key Value map input to something where missing values or errors are handled.
   * In Vanilla for example null values are parsed into empty strings.
   *
   * @param data map to parse
   *
   * @return map after being parsed by a subset of rules.
   */
  Map<String, Object> normalize(Map<String, Object> data);
}
