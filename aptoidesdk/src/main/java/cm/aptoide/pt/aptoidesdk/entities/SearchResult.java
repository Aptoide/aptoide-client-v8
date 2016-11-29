package cm.aptoide.pt.aptoidesdk.entities;

import lombok.Data;

/**
 * Created by neuro on 21-10-2016.
 */
@Data public final class SearchResult {

  private final long id;
  private final String name;
  private final String packageName;
  private final long size;
  private final String iconPath;
  private final String storeName;
  private final long downloads;
}