package cm.aptoide.pt.model.v7.store;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.Date;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Created by trinkes on 23/02/2017.
 */
@Data @Accessors(chain = true) public class HomeUser {
  private long id;
  private String name;
  private String avatar;
  @JsonFormat(pattern = "yyyy-MM-dd hh:mm:ss", timezone = "UTC") private Date added;
  @JsonFormat(pattern = "yyyy-MM-dd hh:mm:ss", timezone = "UTC") private Date modified;
}
