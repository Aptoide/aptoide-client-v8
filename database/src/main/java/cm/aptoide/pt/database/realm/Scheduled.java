/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 12/05/2016.
 */

package cm.aptoide.pt.database.realm;

import io.realm.annotations.PrimaryKey;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by sithengineer on 12/05/16.
 */
public class Scheduled extends RealmSaveObject {
	@PrimaryKey @Getter @Setter private int appId;
	@Getter @Setter private String name;
	@Getter @Setter private String versionName;
	@Getter @Setter private String icon;

	//
	// deprecated
	//

	@Deprecated @Getter @Setter private String md5;
	@Deprecated @Getter @Setter private String packageName;
	@Deprecated @Getter @Setter private String repoName;

}
