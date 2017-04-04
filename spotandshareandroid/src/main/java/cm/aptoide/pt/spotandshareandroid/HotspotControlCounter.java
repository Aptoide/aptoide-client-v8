package cm.aptoide.pt.spotandshareandroid;

import android.content.SharedPreferences;
import lombok.Getter;

/**
 * Created by filipe on 29-03-2017.
 */

public class HotspotControlCounter {

  private static final int TIMEOUT = 5 * 60 * 1000;

  private static final String CLASS_IDENTIFIER =
      "HOTSPOT_" + HotspotControlCounter.class.getSimpleName();

  private static final String TIMESTAMP = CLASS_IDENTIFIER + "_TIMESTAMP";
  private static final String VALUE = CLASS_IDENTIFIER + "_VALUE";

  private final SharedPreferences prefs;
  private final HotspotSSIDCodeMapper hotspotSSIDCodeMapper;

  private long currentTimestamp;
  @Getter private int currentControlCounter = 0;

  public HotspotControlCounter(SharedPreferences prefs,
      HotspotSSIDCodeMapper hotspotSSIDCodeMapper) {
    this.prefs = prefs;
    this.hotspotSSIDCodeMapper = hotspotSSIDCodeMapper;

    init();
  }

  public void init() {
    if (!prefs.contains(TIMESTAMP)) {
      currentControlCounter = 0;
    } else {
      long timeStamp = prefs.getLong(TIMESTAMP, -1);

      if (isValid(timeStamp)) {
        currentControlCounter = prefs.getInt(VALUE, -1);
      } else {
        currentControlCounter = 0;
      }
    }
  }

  private boolean isValid(long timeStamp) {
    return (System.currentTimeMillis() - timeStamp) <= TIMEOUT;
  }

  private String getStringCounter() {
    return String.valueOf(hotspotSSIDCodeMapper.encode(currentControlCounter));
  }

  public String incrementAndGetStringCounter() {
    increment();
    return String.valueOf(hotspotSSIDCodeMapper.encode(currentControlCounter));
  }

  public HotspotControlCounter increment() {
    currentTimestamp = System.currentTimeMillis();
    currentControlCounter++;
    save();

    return this;
  }

  private void save() {
    prefs.edit().putLong(TIMESTAMP, currentTimestamp).putInt(VALUE, currentControlCounter).apply();
  }
}
