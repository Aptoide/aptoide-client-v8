package cm.aptoide.pt

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

class RoomMigrationProvider {
  val migrations = arrayOf(object : Migration(100, 101) {
    override fun migrate(database: SupportSQLiteDatabase) {
      database.execSQL("ALTER TABLE download ADD COLUMN attributionId TEXT")
    }
  }, object : Migration(101, 102) {
    override fun migrate(database: SupportSQLiteDatabase) {
      // Boolean attributes must be defined as non-null integers with set default
      database.execSQL("ALTER TABLE installed ADD COLUMN enabled INTEGER DEFAULT 1 NOT NULL")
    }
  }, object : Migration(102, 103) {
    override fun migrate(database: SupportSQLiteDatabase) {
      database.execSQL("DELETE FROM `update` WHERE appcUpgrade=1")
      database.execSQL(
        "CREATE TABLE IF NOT EXISTS update_tmp (`packageName` TEXT NOT NULL, `appId` INTEGER NOT NULL, `label` TEXT, `icon` TEXT, `md5` TEXT, `apkPath` TEXT, `size` INTEGER NOT NULL, `updateVersionName` TEXT, `updateVersionCode` INTEGER NOT NULL, `excluded` INTEGER NOT NULL, `trustedBadge` TEXT, `alternativeApkPath` TEXT, `storeName` TEXT, `mainObbName` TEXT, `mainObbPath` TEXT, `mainObbMd5` TEXT, `patchObbName` TEXT, `patchObbPath` TEXT, `patchObbMd5` TEXT, `roomSplits` TEXT, `requiredSplits` TEXT, `hasAppc` INTEGER NOT NULL, PRIMARY KEY(`packageName`))"
      )
      database.execSQL(
        "INSERT INTO update_tmp (packageName, appId, label, icon, md5, apkPath, size, updateVersionName, updateVersionCode, excluded, trustedBadge, alternativeApkPath, storeName, mainObbName, mainObbPath, mainObbMd5, patchObbName, patchObbPath, patchObbMd5, roomSplits, requiredSplits, hasAppc) SELECT packageName, appId, label, icon, md5, apkPath, size, updateVersionName, updateVersionCode, excluded, trustedBadge, alternativeApkPath, storeName, mainObbName, mainObbPath, mainObbMd5, patchObbName, patchObbPath, patchObbMd5, roomSplits, requiredSplits, hasAppc FROM `update`"
      )
      database.execSQL("DROP TABLE `update`")
      database.execSQL("ALTER TABLE update_tmp RENAME TO `update`")
    }
  }, object : Migration(103, 104) {
    override fun migrate(database: SupportSQLiteDatabase) {
      database.execSQL("ALTER TABLE installed ADD COLUMN `appSize` INTEGER DEFAULT 1 NOT NULL")
    }
  }, object : Migration(104, 105) {
    override fun migrate(database: SupportSQLiteDatabase) {
      database.execSQL("UPDATE download SET icon='' WHERE icon IS NULL")
    }
  }, object : Migration(105, 106) {
    override fun migrate(database: SupportSQLiteDatabase) {
      database.execSQL("ALTER TABLE notification ADD COLUMN whitelistedPackages TEXT")
    }
  }, object : Migration(106, 107) {
    override fun migrate(database: SupportSQLiteDatabase) {
      database.execSQL(
        "CREATE TABLE IF NOT EXISTS appComingSoonRegistration (`packageName`TEXT NOT NULL, PRIMARY KEY(`packageName`) )"
      )
    }
  }, object : Migration(107, 108) {
    override fun migrate(database: SupportSQLiteDatabase) {
      database.execSQL("ALTER TABLE `update` ADD COLUMN downloadCampaigns TEXT")
    }
  }, object : Migration(108, 109) {
      override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE download RENAME COLUMN downloadSpeed TO averageApkDownloadSpeed")
        database.execSQL("ALTER TABLE download ADD COLUMN averageObbDownloadSpeed INTEGER DEFAULT 0 NOT NULL")
        database.execSQL("ALTER TABLE download ADD COLUMN averageSplitsDownloadSpeed INTEGER DEFAULT 0 NOT NULL")

      }
    }
  )
}