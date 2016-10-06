/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 02/09/2016.
 */

package cm.aptoide.pt.database.accessors;

import cm.aptoide.pt.logger.Logger;
import io.realm.DynamicRealm;
import io.realm.FieldAttribute;
import io.realm.RealmMigration;
import io.realm.RealmSchema;
import java.util.Locale;

/**
 * Created by sithengineer on 12/05/16.
 *
 * This code is responsible to migrate Realm versions in between
 */
class RealmToRealmDatabaseMigration implements RealmMigration {

  private static final String TAG = RealmToRealmDatabaseMigration.class.getName();

  @Override public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {

    Logger.w(TAG,
        String.format(Locale.ROOT, "realm database migration from version %d to %d", oldVersion,
            newVersion));

    // DynamicRealm exposes an editable schema
    RealmSchema schema = realm.getSchema();

    //  Migrate to version 1:
    //    ~ PK in Download changed from a long called "id" to a String called "md5"
    //    + boolean "isDownloading" in Scheduled
    //    - long appId in FileToDownload
    //    ~ set "md5" as PK in FileToDownload
    if (oldVersion == 1) {
      schema.get("Download")
          .addField("md5", String.class, FieldAttribute.PRIMARY_KEY)
          .removeField("id");

      schema.get("FileToDownload")
          .addPrimaryKey("md5")
          .removeField("appId");

      schema.get("Scheduled")
          .addField("isDownloading", boolean.class);

      oldVersion++;
    }

  }
}
