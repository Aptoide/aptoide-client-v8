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

public class Rollback extends RealmObject {
	@PrimaryKey @Getter @Setter private int id;
	@Getter @Setter private String action;
	@Getter @Setter private String timestamp;
	@Getter @Setter private String md5;
	@Getter @Setter private String iconPath;
	@Getter @Setter private String packageName;
	@Getter @Setter private String version;
	@Getter @Setter private String previousVersion;
	@Getter @Setter private String name;
	@Getter @Setter private int confirmed;
	@Getter @Setter private String repositoryName;
}
