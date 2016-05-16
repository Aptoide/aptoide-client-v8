/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 12/05/2016.
 */

package cm.aptoide.pt.database.realm;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by sithengineer on 12/05/16.
 *
 * TODO create mapper POJO <> this
 */

public class Store extends RealmObject {
	@PrimaryKey @Getter @Setter private int storeId;
	@Getter @Setter private String url;
	@Getter @Setter private String apkPath;
	@Getter @Setter private String iconPath;
	@Getter @Setter private String webServicesPath;
	@Getter @Setter private String hash;
	@Getter @Setter private String theme;
	@Getter @Setter private String avatarUrl;
	@Getter @Setter private int downloads;
	@Getter @Setter private String description;
	@Getter @Setter private String list;
	@Getter @Setter private String items;
	@Getter @Setter private int latestTimestamp;
	@Getter @Setter private int topTimestamp;
	@Getter @Setter private boolean isUser;
	@Getter @Setter private boolean isFailed;
	@Getter @Setter private String storeName;
	@Getter @Setter private String username;
	@Getter @Setter private String password;

}
