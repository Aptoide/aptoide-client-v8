package cm.aptoide.pt.aptoidesdk.misc;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * Created by neuro on 08-11-2016.
 */

public class ObjectMapperFactory {

  private static ObjectMapperFactory instance = null;

  protected ObjectMapperFactory() {
  }

  public static ObjectMapperFactory getInstance() {
    if (instance == null) {
      synchronized (ObjectMapperFactory.class) {
        if (instance == null) {
          instance = new ObjectMapperFactory();
        }
      }
    }
    return instance;
  }

  public ObjectMapper createDefaultObjectMapper() {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    objectMapper.configure(SerializationFeature.WRITE_NULL_MAP_VALUES, false);
    objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
    objectMapper.configure(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL, true);
    objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);

    return objectMapper;
  }
}
