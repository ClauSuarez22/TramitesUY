package uy.edu.ucu.android.tramitesuy.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import uy.edu.ucu.android.tramitesuy.data.ProceedingsOpenHelper;

/**
 * Proceedings Content Provider
 * El alumno debera completar esta clase para cumplir con los requerimientos de la letra
 * Ver TODOs en los comentarios en el codigo como referencia
 */
public class ProceedingsProvider extends ContentProvider {

    private static final String TAG = ProceedingsProvider.class.getSimpleName();

    private static final UriMatcher mUriMatcher = buildUriMatcher();

    private ProceedingsOpenHelper mOpenHelper;

    static final int PROCEEDING = 100;
    static final int PROCEEDING_CATEGORY = 101;
    static final int CATEGORY = 300;
    static final int LOCATION = 400;
    static final int LOCATION_PROCEEDING = 401;


    public ProceedingsProvider() {}

    /**
     * Builds the URI matcher specifying the Authority, Path and integer to return when matched
     * @return UriMatcher
     */
    private static UriMatcher buildUriMatcher() {

        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = ProceedingsContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, ProceedingsContract.PATH_PROCEEDING, PROCEEDING);
        matcher.addURI(authority, ProceedingsContract.PATH_CATEGORY, CATEGORY);
        matcher.addURI(authority, ProceedingsContract.PATH_LOCATION, LOCATION);
        matcher.addURI(authority, ProceedingsContract.PATH_PROCEEDING + "/" + ProceedingsContract.PATH_CATEGORY, PROCEEDING_CATEGORY);
        matcher.addURI(authority, ProceedingsContract.PATH_LOCATION + "/" + ProceedingsContract.PATH_PROCEEDING, LOCATION_PROCEEDING);

        return matcher;
    }

    /**
     * Gets the MIME types for the different URIs the Content Provider supports
     * Uses the Uri Matcher to determine what kind of URI this is.
     * @param uri uri to match with
     * @return MIME type as defined in the Contract class
     */
    @Override
    public String getType(Uri uri) {
        final int match = mUriMatcher.match(uri);

        switch (match) {
            // add missing MIME types for the URis you added above
            case CATEGORY:
                return ProceedingsContract.CategoryEntry.CONTENT_TYPE;
            case LOCATION:
                return ProceedingsContract.LocationEntry.CONTENT_TYPE;
            case PROCEEDING:
                return ProceedingsContract.ProceedingEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }


    /**
     * Called when the provider is instantiated. You must not do heavy initializations here
     * @return boolean indicating if the provider initialized correctly or not
     */
    @Override
    public boolean onCreate() {
        mOpenHelper = new ProceedingsOpenHelper(getContext());
        return true;
    }


    /**
     * Metodo que deben implementar
     * Determina en base a la Uri que tabla/s debe consultar
     * @param uri to determine wich query to execute
     * @param projection query columns projection
     * @param selection selection criteria (where clause)
     * @param selectionArgs selection parameters
     * @param sortOrder order by clause
     * @return
     */
    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        switch (mUriMatcher.match(uri)) {
            case PROCEEDING: //Llamar al metodo buildAllProceedingUri()
                qb.setTables(ProceedingsContract.ProceedingEntry.TABLE_NAME);

                if ( uri.getPathSegments().size() > 1 )
                {
                    qb.appendWhere( ProceedingsContract.ProceedingEntry._ID + " = " + ProceedingsContract.ProceedingEntry.getProceedingFromUri( uri ));
                }

                break;
            case PROCEEDING_CATEGORY: //Llamar al metodo buildProceedingCategory()
                qb.setTables(ProceedingsContract.ProceedingEntry.TABLE_NAME);
                qb.appendWhere( ProceedingsContract.ProceedingEntry.COLUMN_CAT_KEY + " = " + ProceedingsContract.ProceedingEntry.getCategoryFromUri( uri ));
                break;
            case CATEGORY: //Llamar al metodo buildAllCategoryUri()
                qb.setTables(ProceedingsContract.CategoryEntry.TABLE_NAME);
                break;
            case LOCATION_PROCEEDING: //Llamar al metodo buildLocationProceeding()
                qb.setTables(ProceedingsContract.LocationEntry.TABLE_NAME);
                qb.appendWhere( ProceedingsContract.LocationEntry.COLUMN_PROC_KEY + " = " + ProceedingsContract.LocationEntry.getProceedingFromUri( uri ));
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        if (TextUtils.isEmpty(sortOrder)) sortOrder = "_ID ASC";

        Cursor c = qb.query(db, projection, selection, selectionArgs, null,
                null, sortOrder);
        c.setNotificationUri(getContext().getContentResolver(), uri);

        return c;
    }


    /**
     * Actualiza las rows en una tabla base a la Uri y los criterios de seleccion
     * @param uri uri used to match and determine table to update rows from
     * @param values values to update records with
     * @param selection selection criteria (where clause)
     * @param selectionArgs selection parameters
     * @return number of rows updated
     */
    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        // TODO: implementar en caso de que lo crean necesario en su app
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * Elimina las rows en una tabla en base a la Uri y los criterios de seleccion
     * @param uri uri used to match and determine table to delete rows from
     * @param selection selection criteria (where clause)
     * @param selectionArgs selection parameters
     * @return number of rows deleted
     */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // TODO: implementar en caso de que lo crean necesario en su app
        throw new UnsupportedOperationException("Not yet implemented");
    }


    /**
     * Inserta rows en las tablas en base a la Uri que recibe
     * @param uri to match and determine table
     * @param values to insert
     * @return number of rows inserted
     */
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = mUriMatcher.match(uri);
        Uri returnUri;
        switch (match){
            case CATEGORY: {
                long _id = db.insert(ProceedingsContract.CategoryEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = ProceedingsContract.CategoryEntry.buildCategoryUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case PROCEEDING: {
                long _id = db.insert(ProceedingsContract.ProceedingEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = ProceedingsContract.ProceedingEntry.buildProceedingUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case LOCATION: {
                long _id = db.insert(ProceedingsContract.LocationEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = ProceedingsContract.LocationEntry.buildLocationProceeding(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return returnUri;
    }

    /**
     * Inserta registros en una misma transaccion para optimizar la carga de datos
     * IMPORTANTE! Este metodo no es utilizado, y no deberian tener que utilizarlo.
     * Esta aqui para que les quede de ejemplo como insertar registros en bulk en una misma transaccion como una optimizacion posible.
     * @param uri to match and determine table
     * @param values to insert
     * @return number of rows inserted
     */
    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = mUriMatcher.match(uri);
        switch (match) {
            case PROCEEDING:
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(ProceedingsContract.ProceedingEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            default:
                return super.bulkInsert(uri, values);
        }
    }
}
