/*
 * Copyright (c) 2016.
 * Modified on 06/07/2016.
 */

package cm.aptoide.pt.dataprovider.model.v7;

import java.util.List;
import lombok.Data;

/**
 * Created by neuro on 27-04-2016.
 */
@Data public class DataList<T> {

  private int total;
  private int count;
  private int offset;
  private Integer limit;
  private int next;
  private int hidden;
  private boolean loaded;
  private List<T> list;
}
