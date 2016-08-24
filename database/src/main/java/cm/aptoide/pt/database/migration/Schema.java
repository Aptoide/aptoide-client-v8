/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 19/05/2016.
 */

package cm.aptoide.pt.database.migration;

/**
 * Created with IntelliJ IDEA.
 * User: brutus
 * Date: 04-10-2013
 * Time: 14:40
 * To change this template use File | Settings | File Templates.
 *
 * hsousa:
 * This class defines (via its sub-classes) the tables for the database.
 * Do not add fields unrelated to database as the database process creation will
 * initialize them through reflection.
 */

//@Target(ElementType.ANNOTATION_TYPE = SQLtype)
public class Schema {

    /*
    @TableDefinition(
            indexes = {
                    @TableDefinition.Index(index_name = "installedIdx",
                            keys = @TableDefinition.Key(field = Installed.COLUMN_APKID))
            })
     */
    public static class Installed {

        public static final String COLUMN_ID = "id_installed";
        public final static String COLUMN_APKID = "package_name";
        public final static String COLUMN_NAME = "name";
        public final static String COLUMN_VERCODE = "version_code";
        public final static String COLUMN_VERNAME = "version_name";
        public final static String COLUMN_SIGNATURE = "signature";

        public static String getName() {
            return "installed";
        }
    }

    public static final class Repo {

        
        public final static String COLUMN_URL = "url";

        //@ColumnDefinition(type = SQLType.TEXT)
        public final static String COLUMN_APK_PATH = "apk_path";

        //@ColumnDefinition(type = SQLType.TEXT)
        public final static String COLUMN_ICONS_PATH = "icons_path";

        //@ColumnDefinition(type = SQLType.TEXT)
        public final static String COLUMN_WEBSERVICES_PATH = "webservices_path";

        //@ColumnDefinition(type = SQLType.TEXT)
        public final static String COLUMN_HASH = "hash";

        //@ColumnDefinition(type = SQLType.TEXT)
        public final static String COLUMN_THEME = "theme";

        //@ColumnDefinition(type = SQLType.TEXT)
        public final static String COLUMN_AVATAR = "avatar_url";

        //@ColumnDefinition(type = SQLType.INTEGER)
        public final static String COLUMN_DOWNLOADS = "downloads";

        //@ColumnDefinition(type = SQLType.TEXT)
        public final static String COLUMN_DESCRIPTION = "description";

        //@ColumnDefinition(type = SQLType.TEXT)
        public final static String COLUMN_VIEW = "list";

        //@ColumnDefinition(type = SQLType.TEXT)
        public final static String COLUMN_ITEMS = "items";

        //@ColumnDefinition(type = SQLType.INTEGER)
        public final static String COLUMN_LATEST_TIMESTAMP = "latest_timestamp";

        //@ColumnDefinition(type = SQLType.INTEGER)
        public final static String COLUMN_TOP_TIMESTAMP = "top_timestamp";

        //@ColumnDefinition(type = SQLType.BOOLEAN)
        public final static String COLUMN_IS_USER = "is_user";

        //@ColumnDefinition(type = SQLType.BOOLEAN)
        public final static String COLUMN_FAILED = "is_failed";

        //@ColumnDefinition(type = SQLType.TEXT)
        public final static String COLUMN_NAME = "name";

        //@ColumnDefinition(type = SQLType.TEXT)
        public final static String COLUMN_USERNAME = "username";

        //@ColumnDefinition(type = SQLType.TEXT)
        public final static String COLUMN_PASSWORD = "password";

        //@ColumnDefinition(type = SQLType.INTEGER, primaryKey = true, autoIncrement = true)
        public final static String COLUMN_ID = "id_repo";

        //@ColumnDefinition(type = SQLType.TEXT)
        public final static String COLUMN_FEATURED_GRAPHIC_PATH = "featured_graphic_path";

        public static String getName() {
            return "repo";
        }

        /*
        public static String getName() {
            return DatabaseProvider.ProviderConstants.REPO_TABLE_NAME;
        }
        */
    }


    /*
    @TableDefinition(
            indexes = {
                    @TableDefinition.Index(index_name = "RollbackIdx",
                            keys = @TableDefinition.Key(field = RollbackTbl.COLUMN_APKID))
            })
    */
    public static class RollbackTbl {

        //@ColumnDefinition(type = SQLType.TEXT)
        public final static String COLUMN_ACTION = "action";

        //@ColumnDefinition(type = SQLType.TEXT)
        public final static String COLUMN_TIMESTAMP = "timestamp";

        //@ColumnDefinition(type = SQLType.TEXT, defaultValue = "")
        public final static String COLUMN_MD5 = "md5";

        //@ColumnDefinition(type = SQLType.TEXT)
        public final static String COLUMN_ICONPATH = "icon_path";

        //@ColumnDefinition(type = SQLType.TEXT)
        public final static String COLUMN_APKID = "package_name";

        //@ColumnDefinition(type = SQLType.TEXT)
        public final static String COLUMN_VERSION = "version";

        //@ColumnDefinition(type = SQLType.TEXT)
        public final static String COLUMN_PREVIOUS_VERSION = "previous_version";

        //@ColumnDefinition(type = SQLType.TEXT)
        public final static String COLUMN_NAME = "name";

        //@ColumnDefinition(type = SQLType.INTEGER)
        public final static String COLUMN_CONFIRMED = "confirmed";

        //@ColumnDefinition(type = SQLType.TEXT)
        public final static String COLUMN_REPO = "reponame";


        public static String getName() {
            return "rollbacktbl";
        }

    }


    public static class Excluded {


        //@ColumnDefinition(type = SQLType.TEXT)
        public final static String COLUMN_PACKAGE_NAME = "package_name";

        //@ColumnDefinition(type = SQLType.TEXT)
        public final static String COLUMN_NAME = "name";

        //@ColumnDefinition(type = SQLType.TEXT)
        public final static String COLUMN_ICONPATH = "iconpath";

        //@ColumnDefinition(type = SQLType.INTEGER)
        public final static String COLUMN_VERCODE = "vercode";

        //@ColumnDefinition(type = SQLType.TEXT)
        public final static String COLUMN_VERNAME = "version_name";

        public static String getName() {
            return "excluded";
        }

    }

    public static class Scheduled {

        //@ColumnDefinition(type = SQLType.TEXT)
        public final static String COLUMN_PACKAGE_NAME = "package_name";

        //@ColumnDefinition(type = SQLType.TEXT)
        public final static String COLUMN_NAME = "name";

        //@ColumnDefinition(type = SQLType.TEXT)
        public final static String COLUMN_VERSION_NAME = "version_name";

        //@ColumnDefinition(type = SQLType.TEXT)
        public final static String COLUMN_MD5 = "md5";

        //@ColumnDefinition(type = SQLType.TEXT)
        public final static String COLUMN_REPO = "repo_name";

        //@ColumnDefinition(type = SQLType.TEXT)
        public final static String COLUMN_ICON = "icon";

        public static String getName() {
            return "scheduled";
        }

    }


    public static class Updates {

        //@ColumnDefinition(type = SQLType.TEXT)
        public static final String COLUMN_PACKAGE = "package_name";

        //@ColumnDefinition(type = SQLType.INTEGER)
        public static final String COLUMN_VERCODE = "version_code";

        //@ColumnDefinition(type = SQLType.TEXT)
        public static final String COLUMN_SIGNATURE = "signature";

        //@ColumnDefinition(type = SQLType.DATE)
        public static final String COLUMN_TIMESTAMP = "timestamp";

        //@ColumnDefinition(type = SQLType.TEXT)
        public static final String COLUMN_MD5 = "md5";

        //@ColumnDefinition(type = SQLType.TEXT)
        public static final String COLUMN_REPO = "repo";

        //@ColumnDefinition(type = SQLType.TEXT)
        public static final String COLUMN_URL = "url";

        //@ColumnDefinition(type = SQLType.REAL)
        public static final String COLUMN_FILESIZE = "filesize";

        //@ColumnDefinition(type = SQLType.TEXT)
        public static final String COLUMN_UPDATE_VERNAME = "update_vername";

        //@ColumnDefinition(type = SQLType.TEXT)
        public static final String COLUMN_ALT_URL = "alt_url";

        //@ColumnDefinition(type = SQLType.TEXT)
        public static final String COLUMN_ICON = "icon";

        //@ColumnDefinition(type = SQLType.TEXT)
        public static final String COLUMN_UPDATE_VERCODE = "update_vercode";

        public static String getName() {
            return "updates";
        }
    }

    public static class ExcludedAds {

        //@ColumnDefinition(type = SQLType.TEXT)
        public static final String COLUMN_PACKAGE = "package_name";

        public static String getName() {
            return "excludedads";
        }
    }

    /*
    @TableDefinition(

            uniques = @TableDefinition.Composite_Unique(
                    fields = {AmazonABTesting.COLUMN_PACKAGE_NAME}),
            indexes = {
                    @TableDefinition.Index(index_name = "package_name",
                            keys = @TableDefinition.Key(field = AmazonABTesting.COLUMN_PACKAGE_NAME, descending = true)),

            })
            */
    public static class AmazonABTesting {

        //@ColumnDefinition(type = SQLType.TEXT)
        public static final String COLUMN_PACKAGE_NAME = "package_name";

        public static String getName() {
            return "amazonabtesting";
        }

    }
}
