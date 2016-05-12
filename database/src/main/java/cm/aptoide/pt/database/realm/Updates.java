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
 */
public class Updates extends RealmObject {
	@PrimaryKey @Getter @Setter private int id;
	@Getter @Setter private String packageName;
	@Getter @Setter private int versionCode;
	@Getter @Setter private String signature;
	@Getter @Setter private long timestamp;
	@Getter @Setter private String md5;
	@Getter @Setter private String url;
	@Getter @Setter private double fileSize;
	@Getter @Setter private String updateVersionName;
	@Getter @Setter private String alternativeUrl;
	@Getter @Setter private String icon;
	@Getter @Setter private String updateVersionCode;
}
