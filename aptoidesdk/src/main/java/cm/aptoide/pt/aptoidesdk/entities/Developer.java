package cm.aptoide.pt.aptoidesdk.entities;

import cm.aptoide.pt.model.v7.GetAppMeta;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Created by neuro on 03-11-2016.
 */
@Data @AllArgsConstructor public class Developer {

  private final String name;
  private final String website;
  private final String email;

  public static Developer fromGetAppDeveloper(GetAppMeta.Developer developer) {

    String name = developer.getName();
    String website = developer.getWebsite();
    String email = developer.getEmail();

    return new Developer(name, website, email);
  }
}