/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 02/09/2016.
 */

package cm.aptoide.pt.database.accessors;

import android.text.TextUtils;
import cm.aptoide.pt.database.realm.PaymentAuthorization;
import cm.aptoide.pt.database.realm.PaymentConfirmation;
import cm.aptoide.pt.database.realm.Update;
import cm.aptoide.pt.logger.Logger;
import io.realm.DynamicRealm;
import io.realm.DynamicRealmObject;
import io.realm.FieldAttribute;
import io.realm.RealmMigration;
import io.realm.RealmObjectSchema;
import io.realm.RealmSchema;

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

      schema.get("FileToDownload")
          .removeField("appId")
          .addPrimaryKey("md5");

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
      for (DynamicRealmObject dynamicRealmObject : realm.where("Scheduled")
          .findAllSorted("md5")) {

        String current_md5 = dynamicRealmObject.getString("md5");
        if (TextUtils.equals(previous_md5, current_md5)) {
          dynamicRealmObject.deleteFromRealm();
        }
        previous_md5 = current_md5;
      }

      scheduledSchema.removeField("md5");
      scheduledSchema.addField("md5", String.class, FieldAttribute.PRIMARY_KEY);

      realm.where(Update.class.getSimpleName())
          //.equalTo(Update.LABEL, "").or()
          //.isNull(Update.LABEL)
          .findAll()
          .deleteAllFromRealm();

      oldVersion++;

      Logger.w(TAG, "DB migrated to version " + oldVersion);
    }

    //  Migrate from version 2 (8077) to version 3 (8078)
    // (re)create (Store)MinimalAd schema
    if (oldVersion == 8077) {

      RealmObjectSchema scheduledSchema = schema.get("Scheduled");
      scheduledSchema.removePrimaryKey();
      scheduledSchema.addPrimaryKey("packageName");
      if (!scheduledSchema.hasField("appAction")) {
        scheduledSchema.addField("appAction", String.class);
      }

      //schema.get("FileToDownload").removePrimaryKey();

      // the old schema is removed because we know that no other entity points to this one
      if (schema.contains("StoreMinimalAd")) {
        schema.remove("StoreMinimalAd");
      }
      if (schema.contains("MinimalAd")) {
        schema.remove("MinimalAd");
      }

      RealmObjectSchema minimalAdSchema = schema.create("MinimalAd");
      minimalAdSchema.addField("description", String.class)
          .addField("packageName", String.class)
          .addField("networkId", Long.class)
          .addField("clickUrl", String.class)
          .addField("cpcUrl", String.class)
          .addField("cpdUrl", String.class)
          .addField("appId", Long.class)
          .addField("adId", Long.class)
          .addField("cpiUrl", String.class)
          .addField("name", String.class)
          .addField("iconPath", String.class);

      RealmObjectSchema downloadSchema = schema.get("Download");

      if (!downloadSchema.hasField("packageName")) {
        downloadSchema.addField("packageName", String.class);
      }

      if (!downloadSchema.hasField("versionCode")) {
        downloadSchema.addField("versionCode", int.class);
      }

      if (!downloadSchema.hasField("action")) {
        downloadSchema.addField("action", int.class);
      }

      if (!downloadSchema.hasField("scheduled")) {
        downloadSchema.addField("scheduled", boolean.class);
      }

      oldVersion++;

      Logger.w(TAG, "DB migrated to version " + oldVersion);
    }

    if (oldVersion == 8078) {
      RealmObjectSchema downloadSchema = schema.get("FileToDownload");

      if (!downloadSchema.hasField("versionName")) {
        downloadSchema.addField("versionName", String.class);
      }

      downloadSchema = schema.get("Download");
      if (!downloadSchema.hasField("versionName")) {
        downloadSchema.addField("versionName", String.class);
      }

      oldVersion++;
    }

    if (oldVersion == 8079) {

      schema.get("PaymentConfirmation")
          .addField("status", String.class, FieldAttribute.REQUIRED)
          .transform(paymentConfirmation -> paymentConfirmation.set("status", "SYNCING_ERROR"))
          .removePrimaryKey()
          .addPrimaryKey("productId")
          .removeField("paymentId")
          .removeField("price")
          .removeField("currency")
          .removeField("taxRate")
          .removeField("icon")
          .removeField("title")
          .removeField("description")
          .removeField("priceDescription")
          .removeField("apiVersion")
          .removeField("sku")
          .removeField("packageName")
          .removeField("developerPayload")
          .removeField("type")
          .removeField("appId")
          .removeField("storeName");

      schema.create("PaymentAuthorization")
          .addField("paymentId", Integer.class, FieldAttribute.PRIMARY_KEY)
          .addField("url", String.class)
          .addField("redirectUrl", String.class)
          .addField("status", String.class, FieldAttribute.REQUIRED);

      oldVersion++;
    }
    if (oldVersion == 8080) {
      schema.get("Download")
          .addField("downloadError", int.class);

      realm.delete(PaymentConfirmation.class.getSimpleName());
      realm.delete(PaymentAuthorization.class.getSimpleName());

      schema.get("PaymentConfirmation")
          .addField("payerId", String.class, FieldAttribute.REQUIRED);

      schema.get("PaymentAuthorization")
          .addField("payerId", String.class, FieldAttribute.REQUIRED);

      oldVersion++;
    }

    if (oldVersion == 8081) {
      if (schema.contains("StoredMinimalAd")) {
        schema.remove("StoredMinimalAd");
      }

      schema.create("StoredMinimalAd")
          .addField("packageName", String.class, FieldAttribute.PRIMARY_KEY,
              FieldAttribute.REQUIRED)
          .addField("referrer", String.class)
          .addField("cpcUrl", String.class)
          .addField("cpdUrl", String.class)
          .addField("cpiUrl", String.class)
          .addField("timestamp", Long.class)
          .addField("adId", Long.class);

      oldVersion++;
    }
  }
}
