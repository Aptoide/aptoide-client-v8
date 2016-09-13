package cm.aptoide.accountmanager.util;

import lombok.Data;

/**
 * Created by trinkes on 5/5/16.
 * <p/>
 * This class is used to send the user information
 * <li>{@link #userName}</li>
 * <li>{@link #nickName}</li>
 * <li>{@link #queueName}</li>
 * <li>{@link #userAvatar}</li>
 * <li>{@link #userRepo}</li>
 * <li>{@link #userAvatarRepo}</li>
 * <li>{@link #matureSwitch}</li>
 */
@Data public class UserInfo {

  /**
   * User name it's usually the email address. It has to be unique
   */
  String userName;
  /**
   * This is the name shown on comments to identify the user.
   */
  String nickName;
  /**
   * This identify the client and it's used on web install
   */
  String queueName;
  /**
   * Link to user avatar
   */
  String userAvatar;
  /**
   * Name of the user's repo
   */
  String userRepo;
  /**
   * Ling to user repo's avatar
   */
  String userAvatarRepo;
  /**
   * define the state of mature switch
   */
  boolean matureSwitch;
}
