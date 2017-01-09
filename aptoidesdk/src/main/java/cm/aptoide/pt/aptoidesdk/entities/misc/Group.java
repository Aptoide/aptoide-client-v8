package cm.aptoide.pt.aptoidesdk.entities.misc;

import lombok.Getter;

/**
 * Created by neuro on 09-01-2017.
 */

public enum Group {
  GAMES("GAMES", 2),
  ;

  @Getter private final String name;
  @Getter private final int id;

  private Group(String name, int id) {
    this.name = name;
    this.id = id;
  }
}
