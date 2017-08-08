/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 22/04/2016.
 */

package cm.aptoide.pt.dataprovider.model.v7;

import cm.aptoide.pt.dataprovider.model.v7.listapp.ListAppVersions;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Created by hsousa on 28/10/15.
 */
@Data @EqualsAndHashCode(callSuper = true) public class GetApp extends BaseV7Response {

  private Nodes nodes;

  @Data public static class Nodes {

    private GetAppMeta meta;
    private ListAppVersions versions;
    private GroupDatalist groups;
  }
}
