package uy.edu.ucu.android.tramitesuy.util;

import android.content.ContentProviderOperation;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import uy.edu.ucu.android.parser.model.Category;
import uy.edu.ucu.android.parser.model.Location;
import uy.edu.ucu.android.parser.model.Proceeding;
import uy.edu.ucu.android.parser.provider.ProceedingProvider;
import uy.edu.ucu.android.tramitesuy.provider.ProceedingsContract;


/**
 * Utils class that includes the logic to insert the proceedings in the database
 * You should use this static methods to populate the proceedings database from the appropiate application component
 * IMPORTANT! YOU SHOULD NOT CHANGE THIS CLASS!
 */
public class Utils {

    private static final String TAG = Utils.class.getSimpleName();

    public static void loadProceedings(Context context) throws Exception{

        List<Proceeding> proceedings = ProceedingProvider.getInstance(context).getProceedings();
        Log.d(TAG, "Populating database with proceedings...");
        ArrayList<ContentProviderOperation> operations = new ArrayList<>();
        for(int index = 0; index < proceedings.size(); index++){

            Proceeding proc = proceedings.get(index);

            if(proc.getCategories().size() > 0) {
                Category cat = proc.getCategories().get(0);

                long categoryId = addCategory(context, cat);

                operations.add(ContentProviderOperation.newInsert(ProceedingsContract.ProceedingEntry.CONTENT_URI)
                        .withValue(ProceedingsContract.ProceedingEntry.COLUMN_TITLE, proc.getTitle())
                        .withValue(ProceedingsContract.ProceedingEntry.COLUMN_DESCRIPTION, proc.getDescription())
                        .withValue(ProceedingsContract.ProceedingEntry.COLUMN_MAIL, proc.getMail())
                        .withValue(ProceedingsContract.ProceedingEntry.COLUMN_URL, proc.getUrl())
                        .withValue(ProceedingsContract.ProceedingEntry.COLUMN_REQUISITES, proc.getRequisites())
                        .withValue(ProceedingsContract.ProceedingEntry.COLUMN_PROCESS, proc.getProcess())
                        .withValue(ProceedingsContract.ProceedingEntry.COLUMN_ONLINE_ACCESS, proc.getOnlineAccess())
                        .withValue(ProceedingsContract.ProceedingEntry.COLUMN_DEPENDS_ON, proc.getDependence().getOrganization())
                        .withValue(ProceedingsContract.ProceedingEntry.COLUMN_STATUS, proc.getStatus())
                        .withValue(ProceedingsContract.ProceedingEntry.COLUMN_COST, proc.getTakeIntoAccount() != null ? proc.getTakeIntoAccount().getCost() : null)
                        .withValue(ProceedingsContract.ProceedingEntry.COLUMN_HOW_TO_APPLY, proc.getTakeIntoAccount() != null ? proc.getTakeIntoAccount().getHowToApply() : null)
                        .withValue(ProceedingsContract.ProceedingEntry.COLUMN_TAKE_INTO_ACCOUNT_OTHER_DATA, proc.getTakeIntoAccount() != null ? proc.getTakeIntoAccount().getOtherData() : null)
                        .withValue(ProceedingsContract.ProceedingEntry.COLUMN_LOCATION_OTHER_DATA, proc.getWhenAndWhere().getOtherData())
                        // category FK
                        .withValue(ProceedingsContract.ProceedingEntry.COLUMN_CAT_KEY, categoryId)
                        .withYieldAllowed(true)
                        .build());

                int lastProceedingIndex = operations.size() - 1;

                for (Location location : proc.getWhenAndWhere().getLocations()) {
                    operations.add(ContentProviderOperation.newInsert(ProceedingsContract.LocationEntry.CONTENT_URI)
                            .withValue(ProceedingsContract.LocationEntry.COLUMN_IS_URUGUAY, location.getIsUruguay())
                            .withValue(ProceedingsContract.LocationEntry.COLUMN_ADDRESS, location.getAddress())
                            .withValue(ProceedingsContract.LocationEntry.COLUMN_CITY, location.getCity())
                            .withValue(ProceedingsContract.LocationEntry.COLUMN_COMMENTS, location.getComments())
                            .withValue(ProceedingsContract.LocationEntry.COLUMN_PHONE, location.getPhone())
                            .withValue(ProceedingsContract.LocationEntry.COLUMN_ADDRESS, location.getAddress())
                            .withValue(ProceedingsContract.LocationEntry.COLUMN_STATE, location.getState())
                            // proceeding FK
                            .withValueBackReference(ProceedingsContract.LocationEntry.COLUMN_PROC_KEY, lastProceedingIndex)
                            .withYieldAllowed(true)
                            .build());
                }

            }
        }

        // apply the insert operations in batch
        context.getContentResolver().applyBatch(ProceedingsContract.CONTENT_AUTHORITY, operations);

        String status = "Proceedings sync complete";
        Log.d(TAG, status);

    }

    private static long addCategory(Context context, Category cat) {
        long categoryId;

        // checking if category already exists
        Cursor categoryCursor = context.getContentResolver().query(
                ProceedingsContract.CategoryEntry.CONTENT_URI,
                new String[]{ProceedingsContract.CategoryEntry._ID},
                ProceedingsContract.CategoryEntry.COLUMN_CODE + " = ?",
                new String[]{cat.getCode()},
                null);

        if (categoryCursor.moveToFirst()) {
            int categoryIdIndex = categoryCursor.getColumnIndex(ProceedingsContract.CategoryEntry._ID);
            categoryId = categoryCursor.getLong(categoryIdIndex);
        } else {
            ContentValues categoryValues = new ContentValues();

            categoryValues.put(ProceedingsContract.CategoryEntry.COLUMN_CODE, cat.getCode());
            categoryValues.put(ProceedingsContract.CategoryEntry.COLUMN_NAME, cat.getName());

            Uri insertedUri = context.getContentResolver().insert(
                    ProceedingsContract.CategoryEntry.CONTENT_URI,
                    categoryValues
            );

            // The resulting URI contains the ID for the row.  Extract the locationId from the Uri.
            categoryId = ContentUris.parseId(insertedUri);
        }

        categoryCursor.close();
        return categoryId;

    }
}
