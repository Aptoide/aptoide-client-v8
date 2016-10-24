package cm.aptoide.pt.aptoidesdk.entities;

import lombok.Getter;

/**
 * Created by neuro on 24-10-2016.
 */
public class Ad {

  Clicks clicks;
  @Getter private long id;

  class Clicks {
    final String cpcUrl;
    final String cpiUrl;
    final String cpdUrl;

    public Clicks(String cpcUrl, String cpiUrl, String cpdUrl) {
      this.cpcUrl = cpcUrl;
      this.cpiUrl = cpiUrl;
      this.cpdUrl = cpdUrl;
    }
  }
}
