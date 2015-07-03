package uy.edu.ucu.android.tramitesuy.controller;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;

import uy.edu.ucu.android.tramitesuy.R;
import uy.edu.ucu.android.tramitesuy.provider.ProceedingsContract;


public class ProceedingsListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final int CATEGORY_LOADER_ID = 0;
    private static final int PROCEEDINGS_FILTER_LOADER_ID = 1;

    // Current search filter
    private String mProceedingFilter = "";
    private Spinner mSpinner;
    private ListView mListView;
    // This is the Adapter being used to display the list's data.
    private SimpleCursorAdapter mCategoriesAdapter;
    private SimpleCursorAdapter mProceedingsAdapter;
    private Integer mSelectedCategory;
    private String mSelectedCategoryName;
    private boolean mDualPane;

    // These are the Contacts rows that we will retrieve.
    String[] ALL_CATEGORIES_PROJECTION = new String[] {
            ProceedingsContract.CategoryEntry._ID,
            ProceedingsContract.CategoryEntry.COLUMN_NAME,
    };

    String[] PROCEEDINGS_LIST_PROJECTION = new String[] {
            ProceedingsContract.ProceedingEntry._ID,
            ProceedingsContract.ProceedingEntry.COLUMN_TITLE,
            ProceedingsContract.ProceedingEntry.COLUMN_DESCRIPTION,
            ProceedingsContract.ProceedingEntry.COLUMN_DEPENDS_ON
    };

    public ProceedingsListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mProceedingFilter = "";
        mSelectedCategory = 1;
    }
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        inflater.inflate(R.menu.menu_proceedings_list_fragment, menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu){
        super.onPrepareOptionsMenu(menu);

        MenuItem item = menu.findItem(R.id.action_search);
        SearchView searchView = new SearchView(getActivity());
        // to change searchview text color
//        EditText searchEditText = (EditText) searchView.findViewById(R.id.search_src_text);
//        searchEditText.setTextColor(getResources().getColor(R.color.white));
//        searchEditText.setHintTextColor(getResources().getColor(R.color.white));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // called when the user submits the query introduced in the searchview
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // called when user changes the query in the search view
                //((ArrayAdapter<IListViewType>) mListView.getAdapter()).getFilter().filter(newText);
                mProceedingFilter = newText;
                getLoaderManager().restartLoader(PROCEEDINGS_FILTER_LOADER_ID, null, ProceedingsListFragment.this);
                return true;
            }
        });

        item.setActionView(searchView);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_proceedings_list, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        View detailsFrame = getActivity().findViewById(R.id.detail_fragment);
        mDualPane = detailsFrame != null && detailsFrame.getVisibility() == View.VISIBLE;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        mSpinner = (Spinner) view.findViewById(R.id.spinner_categories);
        mListView = (ListView) view.findViewById(android.R.id.list);
        mCategoriesAdapter = new SimpleCursorAdapter(getActivity(),
                android.R.layout.simple_spinner_item, null,
                new String[] {
                        ProceedingsContract.CategoryEntry.COLUMN_NAME},
                new int[] { android.R.id.text1 }, 0);
        mCategoriesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setAdapter(mCategoriesAdapter);

        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                Cursor cursor = (Cursor) mCategoriesAdapter.getItem(position);
                mSelectedCategory = cursor.getInt(0);
                mSelectedCategoryName = cursor.getString(1);
                mProceedingFilter = "";
                getLoaderManager().restartLoader(PROCEEDINGS_FILTER_LOADER_ID, null, ProceedingsListFragment.this);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                //Nop
            }
        });

        mProceedingsAdapter = new SimpleCursorAdapter(getActivity(),
            R.layout.proceeding_list_item, null,
            new String[] {
                    ProceedingsContract.ProceedingEntry.COLUMN_TITLE,
                    ProceedingsContract.ProceedingEntry.COLUMN_DESCRIPTION,
                    ProceedingsContract.ProceedingEntry.COLUMN_DEPENDS_ON},
            new int[] {
                    R.id.proceeding_title,
                    R.id.proceeding_desc,
                    R.id.proceeding_depends_on }, 0);
        mListView.setAdapter(mProceedingsAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = (Cursor) mProceedingsAdapter.getItem(position);
                Integer proceedingId = cursor.getInt(cursor.getColumnIndex(ProceedingsContract.ProceedingEntry._ID));
                if(mDualPane) {
                    mListView.setItemChecked(position, true);
                    ProceedingsDetailFragment proceedingDetails = (ProceedingsDetailFragment)
                            getFragmentManager().findFragmentById(R.id.detail_fragment);
                    if (proceedingDetails == null || proceedingDetails.getShownIndex() != position) {
                        proceedingDetails = ProceedingsDetailFragment.newInstance(proceedingId,  mSelectedCategoryName);
                        getFragmentManager().beginTransaction()
                                .replace(R.id.detail_fragment, proceedingDetails)
                                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                                .commit();
                    }
                }else{
                    Intent intent = new Intent(getActivity(), ProceedingDetailActivity.class);
                    intent.putExtra("proceedingId", proceedingId);
                    intent.putExtra("categoryName", mSelectedCategoryName);
                    startActivity(intent);
                }
            }
        });

        // Prepare the loader.  Either re-connect with an existing one,
        // or start a new one.
        getLoaderManager().initLoader(CATEGORY_LOADER_ID, null, ProceedingsListFragment.this);
        getLoaderManager().initLoader(PROCEEDINGS_FILTER_LOADER_ID, null, ProceedingsListFragment.this);
    }


    public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
        CursorLoader cursor = null;
        // we can use the id param to determine which loader it's being created
        // in this case we only have one loader, so there is no point in the ID
        Uri baseUri = null;
        switch(id) {
            case CATEGORY_LOADER_ID: {
                baseUri = ProceedingsContract.CategoryEntry.CONTENT_URI;
                return new CursorLoader(getActivity(), baseUri,
                        ALL_CATEGORIES_PROJECTION, null, null, null);
            }
            case PROCEEDINGS_FILTER_LOADER_ID: {
                if (mProceedingFilter.equals("")) {
                    String whereProceeding = ProceedingsContract.ProceedingEntry.COLUMN_CAT_KEY + " = ?";
                    String[] whereArgsProceeding = {mSelectedCategory.toString()};
                    baseUri = ProceedingsContract.ProceedingEntry.buildAllProceedingUri();
                    cursor = new CursorLoader(getActivity(), baseUri,
                            PROCEEDINGS_LIST_PROJECTION, whereProceeding,
                            whereArgsProceeding, null);
                    return cursor;
                } else {
                    baseUri = ProceedingsContract.ProceedingEntry.buildAllProceedingUri();
                    String whereProceeding = ProceedingsContract.ProceedingEntry.COLUMN_DEPENDS_ON + " like ? or "
                             +  ProceedingsContract.ProceedingEntry.COLUMN_TITLE + " like ?";
                    String[] whereArgsProceeding = {"%" + mProceedingFilter + "%", "%" + mProceedingFilter + "%"};

                    cursor = new CursorLoader(getActivity(), baseUri,
                            PROCEEDINGS_LIST_PROJECTION, whereProceeding,
                            whereArgsProceeding, null);
                    return cursor;
                }

            }
        }
        return cursor;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // old cursor once we return.)
        switch(loader.getId()) {
            case CATEGORY_LOADER_ID: {
                mCategoriesAdapter.swapCursor(data);
                break;
            }
            case PROCEEDINGS_FILTER_LOADER_ID: {
                mProceedingsAdapter.swapCursor(data);
                break;
            }
            default: {
                break;
            }

        };
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // This is called when the last Cursor provided to onLoadFinished()
        // above is about to be closed.  We need to make sure we are no
        // longer using it.
        switch(loader.getId()) {
            case CATEGORY_LOADER_ID: {
                mCategoriesAdapter.swapCursor(null);
                break;
            }
            case PROCEEDINGS_FILTER_LOADER_ID: {
                mProceedingsAdapter.swapCursor(null);
                break;
            }
            default: {
                break;
            }

        };
    }


}
