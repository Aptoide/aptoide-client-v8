package cm.aptoide.pt

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

class RoomMigrationProvider {
  val migrations = arrayOf(object : Migration(100, 101) {
    override fun migrate(database: SupportSQLiteDatabase) {
      database.execSQL("ALTER TABLE download " + " ADD COLUMN attributionId TEXT")
    }
  }, object : Migration(101, 102) {
    override fun migrate(database: SupportSQLiteDatabase) {
      // Boolean attributes must be defined as non-null integers with set default
      database.execSQL("ALTER TABLE installed " + " ADD COLUMN enabled INTEGER DEFAULT 1 NOT NULL")
    }
  })
}