package uy.edu.ucu.android.tramitesuy.controller;

import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

import uy.edu.ucu.android.parser.model.Category;
import uy.edu.ucu.android.parser.model.Dependence;
import uy.edu.ucu.android.parser.model.Location;
import uy.edu.ucu.android.parser.model.Proceeding;
import uy.edu.ucu.android.parser.model.WhenAndWhere;
import uy.edu.ucu.android.tramitesuy.provider.ProceedingsContract;

/**
 * Created by csuarez on 28/06/15.
 */
public class EntitiesService {

    public static List<Category> categories = new ArrayList<>();
    public static List<Proceeding> categoryProceeding = new ArrayList<>();

    /**
     * Gets all proceeding stored on the Proceeding table
     * @return
     */
    public static List<Proceeding> getAllProceeding( Cursor cursor ){

        List<Proceeding> todos = new ArrayList<>();

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            todos.add(cursorToProceeding(cursor));
            cursor.moveToNext();
        }
        cursor.close();

        categoryProceeding = todos;

        return todos;
    }

    /**
     * Turns a cursor into a Proceeding model object
     * @param cursor cursor with row data
     * @return proceeding object
     */
    public static Proceeding cursorToProceeding(Cursor cursor) {

        Proceeding proceeding = new Proceeding();
        proceeding.setId(cursor.getString(cursor.getColumnIndex(ProceedingsContract.ProceedingEntry._ID)));
        proceeding.setUrl(cursor.getString(cursor.getColumnIndex(ProceedingsContract.ProceedingEntry.COLUMN_URL)));
        proceeding.setTitle(cursor.getString(cursor.getColumnIndex(ProceedingsContract.ProceedingEntry.COLUMN_TITLE)));
        proceeding.setDescription(cursor.getString(cursor.getColumnIndex(ProceedingsContract.ProceedingEntry.COLUMN_DESCRIPTION)));
        proceeding.setOnlineAccess(cursor.getString(cursor.getColumnIndex(ProceedingsContract.ProceedingEntry.COLUMN_ONLINE_ACCESS)));
        proceeding.setRequisites(cursor.getString(cursor.getColumnIndex(ProceedingsContract.ProceedingEntry.COLUMN_REQUISITES)));
        proceeding.setProcess(cursor.getString(cursor.getColumnIndex(ProceedingsContract.ProceedingEntry.COLUMN_PROCESS)));
        proceeding.setMail(cursor.getString(cursor.getColumnIndex(ProceedingsContract.ProceedingEntry.COLUMN_MAIL)));
        proceeding.setStatus(cursor.getString(cursor.getColumnIndex(ProceedingsContract.ProceedingEntry.COLUMN_STATUS)));

        Dependence dependence = new Dependence();
        dependence.setOrganization(cursor.getString(cursor.getColumnIndex(ProceedingsContract.ProceedingEntry.COLUMN_DEPENDS_ON)));
        proceeding.setDependence( dependence );

        WhenAndWhere whenAndWhere = new WhenAndWhere();
        whenAndWhere.setOtherData(cursor.getString(cursor.getColumnIndex(ProceedingsContract.ProceedingEntry.COLUMN_LOCATION_OTHER_DATA)));
        proceeding.setWhenAndWhere(whenAndWhere);

        return proceeding;

    }

    public static Proceeding getProceedingById( List<Proceeding> proceedings, String proceedingId){
        Proceeding proceeding = null;

        for (Proceeding proc : proceedings)
        {
            if (proc.getId().equals(proceedingId))
            {
                proceeding = proc;
                break;
            }
        }

        return proceeding;
    }

    /**
     * Gets all proceeding stored on the Location table
     * @return
     */
    public static List<Location> getAllLocations( Cursor cursor ){

        List<Location> todos = new ArrayList<>();

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            todos.add(cursorToLocation(cursor));
            cursor.moveToNext();
        }
        cursor.close();

        return todos;
    }

    /**
     * Turns a cursor into a Location model object
     * @param cursor cursor with row data
     * @return proceeding object
     */
    public static Location cursorToLocation(Cursor cursor) {

        Location location = new Location();
        location.setAddress(cursor.getString(cursor.getColumnIndex(ProceedingsContract.LocationEntry.COLUMN_ADDRESS)));
        location.setCity(cursor.getString(cursor.getColumnIndex(ProceedingsContract.LocationEntry.COLUMN_CITY)));
        location.setComments(cursor.getString(cursor.getColumnIndex(ProceedingsContract.LocationEntry.COLUMN_COMMENTS)));
        location.setIsUruguay(cursor.getString(cursor.getColumnIndex(ProceedingsContract.LocationEntry.COLUMN_IS_URUGUAY)));
        location.setPhone(cursor.getString(cursor.getColumnIndex(ProceedingsContract.LocationEntry.COLUMN_PHONE)));
        location.setState(cursor.getString(cursor.getColumnIndex(ProceedingsContract.LocationEntry.COLUMN_STATE)));
        location.setTime(cursor.getString(cursor.getColumnIndex(ProceedingsContract.LocationEntry.COLUMN_TIME)));

        return location;

    }

    /**
     * Gets all proceeding stored on the Category table
     * @return
     */
    public static List<Category> getAllCategories( Cursor cursor ){

        List<Category> todos = new ArrayList<>();

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            todos.add(cursorToCategory(cursor));
            cursor.moveToNext();
        }
        cursor.close();

        categories = todos;

        return todos;
    }

    /**
     * Turns a cursor into a Category model object
     * @param cursor cursor with row data
     * @return proceeding object
     */
    public static Category cursorToCategory(Cursor cursor) {

        Category category = new Category();
        category.setId(cursor.getString(cursor.getColumnIndex(ProceedingsContract.CategoryEntry._ID)));
        category.setCode(cursor.getString(cursor.getColumnIndex(ProceedingsContract.CategoryEntry.COLUMN_CODE)));
        category.setName(cursor.getString(cursor.getColumnIndex(ProceedingsContract.CategoryEntry.COLUMN_NAME)));

        return category;

    }

    public static Category getCategoryById( List<Category> categories, String categoryId){
        Category category = null;

        for (Category categ : categories)
        {
            if (categ.getId().equals(categoryId))
            {
                category = categ;
                break;
            }
        }

        return category;
    }
}
