/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 02/09/2016.
 */

package cm.aptoide.pt.database.accessors;

import cm.aptoide.pt.database.realm.Update;
import cm.aptoide.pt.logger.Logger;
import io.realm.DynamicRealm;
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

    //  Migrate from version 0 (<=8075) to version 1 (8076):
    //    ~ PK in Download changed from a long called "id" to a String called "md5"
    //    + boolean "isDownloading" in Scheduled
    //    - long appId in FileToDownload
    //    ~ set "md5" as PK in FileToDownload
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

      schema.get("FileToDownload")
          .removeField("appId")
          .addPrimaryKey("md5");

      schema.get("Download")
          .removeField("appId")
          .addField("md5", String.class, FieldAttribute.PRIMARY_KEY);

      oldVersion++;
      Logger.w(TAG, "db version = " + oldVersion);
    }

    //  Migrate from version 1 (8076) to version 2 (8077)
    if(oldVersion == 8076) {
      RealmObjectSchema schemaFile = schema.get("Scheduled");
      if(schemaFile.hasPrimaryKey()){
        schemaFile.removePrimaryKey();
      }
      schemaFile.removeField("md5");
      schemaFile.addField("md5", String.class, FieldAttribute.PRIMARY_KEY);

      realm.where(Update.class.getSimpleName())
          //.equalTo(Update.LABEL, "").or()
          //.isNull(Update.LABEL)
          .findAll()
          .deleteAllFromRealm();

      oldVersion++;
      Logger.w(TAG, "db version = " + oldVersion);
    }

  }
}
