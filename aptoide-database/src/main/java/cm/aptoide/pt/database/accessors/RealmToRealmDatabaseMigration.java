/*
 * Copyright (c) 2016.
 * Modified on 02/09/2016.
 */

package cm.aptoide.pt.database.accessors;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import cm.aptoide.pt.database.realm.Installed;
import cm.aptoide.pt.logger.Logger;
import io.realm.DynamicRealm;
import io.realm.DynamicRealmObject;
import io.realm.FieldAttribute;
import io.realm.RealmMigration;
import io.realm.RealmObjectSchema;
import io.realm.RealmSchema;

/**
 * Created on 12/05/16.
 * <p>
 * This code is responsible to migrate between Realm schemas.
 * <p>
 * <a href=https://github.com/realm/realm-java/blob/master/examples/migrationExample/src/main/java/io/realm/examples/realmmigrationexample/model/Migration.java>
 * For clarification see a migration example.
 * </a>
 */
public class RealmToRealmDatabaseMigration implements RealmMigration {

  private static final String TAG = RealmToRealmDatabaseMigration.class.getName();
  private final Context context;

  public RealmToRealmDatabaseMigration(Context context) {

    this.context = context;
  }

  @Override public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {

    // During a migration, a DynamicRealm is exposed. A DynamicRealm is an untyped variant of a normal Realm, but
    // with the same object creation and query capabilities.
    // A DynamicRealm uses Strings instead of Class references because the Classes might not even exist or have been
    // renamed.

    // Access the Realm schema in order to create, modify or delete classes and their fields.

    // DynamicRealm exposes an editable schema
    Logger.getInstance()
        .w(TAG, "migrate(): from: " + oldVersion + " to: " + newVersion);

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

      Logger.getInstance()
          .w(TAG, "DB migrated to version " + oldVersion);
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

      realm.where("Update")
          //.equalTo(Update.LABEL, "").or()
          //.isNull(Update.LABEL)
          .findAll()
          .deleteAllFromRealm();

      oldVersion++;

      Logger.getInstance()
          .w(TAG, "DB migrated to version " + oldVersion);
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

      Logger.getInstance()
          .w(TAG, "DB migrated to version " + oldVersion);
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

      realm.delete("PaymentConfirmation");
      realm.delete("PaymentAuthorization");

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

    if (oldVersion == 8082) {
      schema.create("Notification")
          .addField("key", String.class, FieldAttribute.PRIMARY_KEY)
          .addField("abTestingGroup", String.class)
          .addField("body", String.class)
          .addField("campaignId", int.class)
          .addField("img", String.class)
          .addField("lang", String.class)
          .addField("title", String.class)
          .addField("url", String.class)
          .addField("urlTrack", String.class)
          .addField("type", int.class)
          .addField("timeStamp", long.class)
          .addField("dismissed", long.class)
          .addField("appName", String.class)
          .addField("graphic", String.class);

      oldVersion++;
    }

    if (oldVersion == 8083) {
      schema.get("Notification")
          .addField("ownerId", String.class)
          .transform(notification -> notification.set("ownerId", ""));

      oldVersion++;
    }

    if (oldVersion == 8084) {
      realm.delete("PaymentConfirmation");
      schema.get("PaymentConfirmation")
          .addField("paymentMethodId", int.class);

      schema.get("Installed")
          .removePrimaryKey()
          .addField("packageAndVersionCode", String.class)
          .transform(obj -> obj.setString("packageAndVersionCode",
              obj.getString("packageName") + obj.getInt("versionCode")))
          .addPrimaryKey("packageAndVersionCode")
          .addField("status", int.class)
          .transform(obj -> obj.setInt("status", Installed.STATUS_COMPLETED))
          .addField("type", int.class)
          .transform(obj -> obj.setInt("type", Installed.TYPE_UNKNOWN));

      oldVersion++;
    }

    if (oldVersion == 8085) {
      schema.get("MinimalAd")
          .addField("downloads", Integer.class)
          .addField("stars", Integer.class)
          .addField("modified", Long.class);

      oldVersion++;
    }

    if (oldVersion == 8086) {
      realm.delete("PaymentConfirmation");
      schema.get("PaymentConfirmation")
          .removeField("productId")
          .addField("id", String.class, FieldAttribute.PRIMARY_KEY)
          .addField("productId", String.class, FieldAttribute.REQUIRED)
          .addField("sellerId", String.class, FieldAttribute.REQUIRED)
          .addField("clientToken", String.class)
          .addField("successUrl", String.class)
          .addField("confirmationUrl", String.class)
          .addField("payload", String.class);

      realm.delete("PaymentAuthorization");

      oldVersion++;
    }

    if (oldVersion == 8087) {
      schema.get("Notification")
          .addField("expire", Long.class)
          .transform(notification -> notification.set("expire", null));

      oldVersion++;
    }

    if (oldVersion == 8088) {
      schema.get("Notification")
          .addField("notificationCenterUrlTrack", String.class)
          .transform(notification -> notification.set("notificationCenterUrlTrack", null));
      oldVersion++;
    }

    if (oldVersion == 8089) {
      schema.get("Notification")
          .addField("processed", boolean.class)
          .transform(notification -> notification.set("processed", true));

      oldVersion++;
    }

    if (oldVersion == 8090) {
      realm.delete("PaymentConfirmation");

      schema.create("RealmAuthorization")
          .addField("id", String.class, FieldAttribute.PRIMARY_KEY)
          .addField("customerId", String.class, FieldAttribute.REQUIRED)
          .addField("status", String.class, FieldAttribute.REQUIRED)
          .addField("type", String.class, FieldAttribute.REQUIRED)
          .addField("metadata", String.class, FieldAttribute.REQUIRED)
          .addField("transactionId", String.class, FieldAttribute.REQUIRED)
          .addField("description", String.class)
          .addField("amount", double.class)
          .addField("currency", String.class)
          .addField("currencySymbol", String.class);

      oldVersion++;
    }
    if (oldVersion == 8091) {
      schema.create("RealmEvent")
          .addField("timestamp", long.class, FieldAttribute.PRIMARY_KEY)
          .addField("eventName", String.class)
          .addField("action", int.class)
          .addField("context", String.class)
          .addField("data", String.class);
      oldVersion++;
    }

    if (oldVersion == 8092) {
      realm.delete("Rollback");
      realm.delete("Scheduled");

      schema.get("Download")
          .removeField("scheduled");
      oldVersion++;
    }

    if (oldVersion == 8093) {
      schema.create("Installation")
          .addField("packageName", String.class, FieldAttribute.PRIMARY_KEY)
          .addField("icon", String.class)
          .addField("name", String.class)
          .addField("versionCode", int.class)
          .addField("versionName", String.class);

      oldVersion++;
    }

    if (oldVersion == 8094) {
      schema.create("RealmExperiment")
          .addField("experimentName", String.class, FieldAttribute.PRIMARY_KEY)
          .addField("requestTime", long.class)
          .addField("assignment", String.class)
          .addField("payload", String.class)
          .addField("partOfExperiment", boolean.class)
          .addField("experimentOver", boolean.class);

      oldVersion++;
    }
    if (oldVersion == 8095) {
      SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
      sharedPreferences.edit()
          .putBoolean("updatesFilterAlphaBetaKey", false)
          .apply();

      oldVersion++;
    }
    if (oldVersion == 8096) {
      schema.get("Update")
          .addField("appcUpgrade", boolean.class)
          .transform(upgrade -> upgrade.set("appcUpgrade", false));

      oldVersion++;
    }
  }
}
