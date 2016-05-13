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
public class ExcludedUpdate extends RealmObject {
	@PrimaryKey @Getter @Setter private long id;

	@Getter @Setter private String packageName;
	@Getter @Setter private String name;
	@Getter @Setter private String icon;

	//
	// deprecated
	//

	@Deprecated @Getter @Setter private String versionName;
	@Deprecated @Getter @Setter private String versionCode;
}
