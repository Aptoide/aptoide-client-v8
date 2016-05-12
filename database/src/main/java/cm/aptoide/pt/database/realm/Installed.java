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
public class Installed  extends RealmObject {
	@PrimaryKey @Getter @Setter private int id;
	@Getter @Setter private String packageName;
	@Getter @Setter private String name;
	@Getter @Setter private String versionCode;
	@Getter @Setter private String versionName;
	@Getter @Setter private String signature;
}
