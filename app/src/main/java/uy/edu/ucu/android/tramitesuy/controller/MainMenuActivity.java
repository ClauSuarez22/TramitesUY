package uy.edu.ucu.android.tramitesuy.controller;

import android.app.ActionBar;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import uy.edu.ucu.android.parser.model.Category;
import uy.edu.ucu.android.parser.model.Proceeding;
import uy.edu.ucu.android.tramitesuy.R;
import uy.edu.ucu.android.tramitesuy.provider.ProceedingsContract;


public class MainMenuActivity extends ListActivity {

    private Spinner mSpinner;
    private String[] mCategoriesStr;
    private HashMap<String,Category> mCategoriesHash;
    private CategoriesListAdapter mCategoriesAdapter;
    private ListView mListView;
    private List<Proceeding> mItemsList;

    // table name
    public static final String EMPTY_ITEM = "Seleccione una categoría";
    public static final String DB_FULL_PATH = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // FALTA SABER EL DB_FULL_PATH PARA QUE ANDE
        // if (!checkDataBase){
            //runIntentService();
        //}

        ActionBar actionBar = getActionBar();
        actionBar.setTitle("Tramites UY!");
        actionBar.setDisplayHomeAsUpEnabled(true);

        mItemsList = new ArrayList<Proceeding>();
        mSpinner = (Spinner)findViewById(R.id.spinner_categories);

        mListView =(ListView) findViewById(android.R.id.list);
        mCategoriesAdapter = new CategoriesListAdapter(MainMenuActivity.this, 0, mItemsList);
        mListView.setAdapter(mCategoriesAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Proceeding selectedItem = mCategoriesAdapter.getItem(position);
                String proceedingId = selectedItem.getId();
                // Esta dando outOfBound [VER]
                //String categoryId = selectedItem.getCategories().get(0).getId(); // Primer categoria [VER]
                String categoryId = "1"; // A fuego
                Intent intent = new Intent(MainMenuActivity.this, DetailActivity.class);
                intent.putExtra("proceedingId", proceedingId);
                intent.putExtra("categoryId", categoryId);
                startActivity(intent);
            }
        });

        new GetCategoriesAsyncTask(MainMenuActivity.this).execute();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void runIntentService() {

        Intent serviceIntent = new Intent(this, MyIntentService.class);
        startService(serviceIntent);

    }

    /**
     * Check if the database exist
     *
     * @return true if it exists, false if it doesn't
     */
    private boolean checkDataBase() {
        SQLiteDatabase checkDB = null;
        try {
            checkDB = SQLiteDatabase.openDatabase(DB_FULL_PATH, null,
                    SQLiteDatabase.OPEN_READONLY);
            checkDB.close();
        } catch (SQLiteException e) {
            // database doesn't exist yet.
        }
        return checkDB != null;
    }

    // Async Task for Categories
    private class GetCategoriesAsyncTask extends AsyncTask<String, Integer, List<Category>> {

        private Context mContext;
        private ProgressDialog mProgressDialog;

        public GetCategoriesAsyncTask(Context context){
            this.mContext = context;
        }

        @Override
        protected void onPreExecute(){

            mProgressDialog = ProgressDialog.show(mContext, null,mContext.getString(R.string.loading), true, false);
        }

        @Override
        protected List<Category> doInBackground(String... params) {

            try {
                //Carga todas las categorias
                Cursor categoryCursor = mContext.getContentResolver().query(
                        ProceedingsContract.CategoryEntry.buildAllCategoryUri(),
                        null,
                        null,
                        null,
                        null);

                return EntitiesService.getAllCategories(categoryCursor);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        public void onPostExecute(List<Category> categories){

            // initialize adapter with Seasons
            if(categories != null) {
                mCategoriesHash = new HashMap<>();
                mCategoriesStr = new String[categories.size() + 1];

                mCategoriesStr[0] = EMPTY_ITEM;
                mCategoriesHash.put(EMPTY_ITEM, null);
                int i = 1;
                for (Category item : categories) {
                    mCategoriesStr[i] = item.getName();
                    mCategoriesHash.put(item.getName(),item);
                    i++;
                }

                ArrayAdapter<String> spAdapter = new ArrayAdapter(MainMenuActivity.this, android.R.layout.simple_spinner_item, mCategoriesStr);
                spAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                mSpinner.setAdapter(spAdapter);

                mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

                        if (mCategoriesStr[position] != EMPTY_ITEM){
                            String categoryId = mCategoriesHash.get(mCategoriesStr[position]).getId();
                            new GetProceedingsAsyncTask(MainMenuActivity.this)
                                    .execute(categoryId);
                        }else{
                            // Limpiar lista
                            mItemsList.clear();
                            mCategoriesAdapter.setmItems(mItemsList);
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parentView) {
                        // Nop
                    }
                });
            }else{
                Toast.makeText(mContext, "Error obteniendo categorias", Toast.LENGTH_SHORT).show();
            }

            if(mProgressDialog.isShowing()) mProgressDialog.dismiss();
        }

    }


    // Async Task for Proceedings
    private class GetProceedingsAsyncTask extends AsyncTask<String, Integer, List<Proceeding>> {

        private Context mContext;
        private ProgressDialog mProgressDialog;

        public GetProceedingsAsyncTask(Context context){
            this.mContext = context;
        }

        @Override
        protected void onPreExecute(){

            mProgressDialog = ProgressDialog.show(mContext, null,"Cargando...", true, false);
        }

        @Override
        protected List<Proceeding> doInBackground(String... params) {

            // Param Filter CategoryId
            String categoryId = params[0];

            try {

                String whereProceeding = ProceedingsContract.ProceedingEntry.COLUMN_CAT_KEY + " = ?";
                String[] whereArgsProceeding = {categoryId};

                Cursor proceedingCursor = mContext.getContentResolver().query(
                        ProceedingsContract.ProceedingEntry.buildAllProceedingUri(),
                        null,
                        whereProceeding,
                        whereArgsProceeding,
                        null);

                return EntitiesService.getAllProceeding(proceedingCursor);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        public void onPostExecute(List<Proceeding> proceedings){

            // initialize adapter with proceedings
            if(proceedings != null) {
                mCategoriesAdapter.setmItems(proceedings);
            }else{
                Toast.makeText(mContext, "Error obteniendo trámites", Toast.LENGTH_SHORT).show();
            }
            if(mProgressDialog.isShowing()) mProgressDialog.dismiss();
        }

    }

}
