package uy.edu.ucu.android.tramitesuy.provider;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Contract class for the proceeding schema
 * TODO: Seguramente tengan que cambiar esta clase de manera de cumplir con los requerimientos
 */
public class ProceedingsContract {

    // name of the provider in the app
    public static final String CONTENT_AUTHORITY = "uy.edu.ucu.android.tramitesuy";

    // base of all uris
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    // possible paths
    public static final String PATH_PROCEEDING = "proceeding";
    public static final String PATH_CATEGORY = "category";
    public static final String PATH_LOCATION = "location";

    // proceeding path
    public static final class ProceedingEntry implements BaseColumns{

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_PROCEEDING).build();

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PROCEEDING;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PROCEEDING;

        // table name
        public static final String TABLE_NAME = "proceeding";

        // proceeding columns
        public static final String COLUMN_URL = "url";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_ONLINE_ACCESS = "oaccess";
        public static final String COLUMN_REQUISITES = "requisites";
        public static final String COLUMN_PROCESS = "process";
        public static final String COLUMN_MAIL = "mail";
        public static final String COLUMN_STATUS = "status";
        public static final String COLUMN_LOCATION_OTHER_DATA = "location_other_data";
        public static final String COLUMN_HOW_TO_APPLY = "how_to_apply";
        public static final String COLUMN_COST = "cost";
        public static final String COLUMN_TAKE_INTO_ACCOUNT_OTHER_DATA = "take_into_account_other_data";
        public static final String COLUMN_DEPENDS_ON = "depends_on";
        public static final String COLUMN_CAT_KEY = "category_id";

        //Retorna la Uri que obtiene todos los proceeding
        public static Uri buildAllProceedingUri() {
            return CONTENT_URI;
        }

        //Retorna la Uri que obtiene el detalle de un proceeding a partir de su ID
        public static Uri buildProceedingUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    // category path
    public static final class CategoryEntry implements BaseColumns{

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_CATEGORY).build();

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CATEGORY;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CATEGORY;

        // table name
        public static final String TABLE_NAME = "category";

        // table columns
        public static final String COLUMN_CODE = "code";
        public static final String COLUMN_NAME = "name";

        //Retorna la Uri que obtiene todas las Category
        public static Uri buildAllCategoryUri() {
            return CONTENT_URI;
        }

        //Retorna la Uri que obtiene el detalle de una category a partir de su ID
        public static Uri buildCategoryUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    // location path
    public static final class LocationEntry implements BaseColumns{

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_LOCATION).build();

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_LOCATION;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_LOCATION;

        // table name
        public static final String TABLE_NAME = "location";


        // table columns
        public static final String COLUMN_IS_URUGUAY = "is_uruguay";
        public static final String COLUMN_STATE = "state";
        public static final String COLUMN_CITY = "city";
        public static final String COLUMN_ADDRESS = "address";
        public static final String COLUMN_TIME = "time";
        public static final String COLUMN_PHONE = "phone";
        public static final String COLUMN_COMMENTS = "comments";
        public static final String COLUMN_PROC_KEY = "proceeding_id";

        //Retorna la Uri que obtiene todas location
        public static Uri buildAllLocationUri() {
            return CONTENT_URI;
        }

        //Retorna la Uri que obtiene las location asociadas a un proceeding
        public static Uri buildLocationProceeding(long proceedingId) {
            return ContentUris.withAppendedId(Uri.withAppendedPath(CONTENT_URI, PATH_PROCEEDING), proceedingId);
        }
    }

}
