/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 02/09/2016.
 */

package cm.aptoide.pt.database.accessors;

import android.text.TextUtils;
import cm.aptoide.pt.database.realm.Scheduled;
import cm.aptoide.pt.database.realm.Update;
import cm.aptoide.pt.logger.Logger;
import io.realm.DynamicRealm;
import io.realm.DynamicRealmObject;
import io.realm.FieldAttribute;
import io.realm.RealmMigration;
import io.realm.RealmObjectSchema;
import io.realm.RealmSchema;
import java.util.Locale;

/**
 * Created by sithengineer on 12/05/16.
 *
 * This code is responsible to migrate between Realm schemas.
 *
 * <a href=https://github.com/realm/realm-java/blob/master/examples/migrationExample/src/main/java/io/realm/examples/realmmigrationexample/model/Migration.java>
 * For clarification see a migration example.
 * </a>
 */
class RealmToRealmDatabaseMigration implements RealmMigration {

  private static final String TAG = RealmToRealmDatabaseMigration.class.getName();

  @Override public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {

    // During a migration, a DynamicRealm is exposed. A DynamicRealm is an untyped variant of a normal Realm, but
    // with the same object creation and query capabilities.
    // A DynamicRealm uses Strings instead of Class references because the Classes might not even exist or have been
    // renamed.

    // Access the Realm schema in order to create, modify or delete classes and their fields.

    // DynamicRealm exposes an editable schema
    RealmSchema schema = realm.getSchema();

    //  Migrate from version 0 (<=8075) to version 1 (8076)
    if (oldVersion <= 8075) {

      oldVersion = 8075;

      schema.get("Scheduled")
          .removeField("appId");

      schema.get("Rollback")
          .setNullable("md5", true)
          .removeField("fileSize")
          .removeField("trustedBadge");

      realm.delete("Download");
      realm.delete("FileToDownload");

      schema.get("FileToDownload").removeField("appId").addPrimaryKey("md5");

      schema.get("Download")
          .removeField("appId")
          .addField("md5", String.class, FieldAttribute.PRIMARY_KEY);

      oldVersion++;

      Logger.w(TAG, "DB migrated to version " + oldVersion);
    }

    //  Migrate from version 1 (8076) to version 2 (8077)
    if (oldVersion == 8076) {
      RealmObjectSchema scheduledSchema = schema.get("Scheduled");
      if (scheduledSchema.hasPrimaryKey()) {
        scheduledSchema.removePrimaryKey();
      }

      // remove entries with duplicated MD5 fields
      // this leads to the removal of some Scheduled updates

      String previous_md5 = "";
      for (DynamicRealmObject dynamicRealmObject :
          realm.where("Scheduled").findAllSorted("md5")) {

        String current_md5 = dynamicRealmObject.getString("md5");
        if(TextUtils.equals(previous_md5, current_md5)){
          dynamicRealmObject.deleteFromRealm();
        }
        previous_md5 = current_md5;
      }

      scheduledSchema.removeField("md5");
      scheduledSchema.addField("md5", String.class, FieldAttribute.PRIMARY_KEY);

      scheduledSchema.addPrimaryKey("packageName");
      scheduledSchema.addField("appAction", String.class);

      schema.get("FileToDownload").removePrimaryKey();

      realm.where(Update.class.getSimpleName())
          //.equalTo(Update.LABEL, "").or()
          //.isNull(Update.LABEL)
          .findAll().deleteAllFromRealm();

      oldVersion++;

      Logger.w(TAG, "DB migrated to version " + oldVersion);
    }
  }
}
