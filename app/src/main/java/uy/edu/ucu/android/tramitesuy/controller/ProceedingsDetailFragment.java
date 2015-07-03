package uy.edu.ucu.android.tramitesuy.controller;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.HashMap;

import uy.edu.ucu.android.tramitesuy.R;
import uy.edu.ucu.android.tramitesuy.provider.ProceedingsContract;


public class ProceedingsDetailFragment extends android.support.v4.app.Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int PROCEEDINGS_DETAIL_LOADER = 0;
    // Current search filter
    private TextView mProceedingName;
    private TextView mProceedingDescription;
    private TextView mProceedingDependsOn;
    private TextView mProceedingWhenWhere;
    private TextView mProceedingReq;
    private TextView mProceedingUrl;
    private TextView mProceedingStatus;
    private TextView mProceedingCategory;
    private Button mMapButton;
    private Integer mProceedingId;
    private String mCategoryName;

    // These are the Contacts rows that we will retrieve.
    String[] PROCEEDING_DETAIL_PROJECTION = new String[] {
            ProceedingsContract.ProceedingEntry._ID,
            ProceedingsContract.ProceedingEntry.COLUMN_TITLE,
            ProceedingsContract.ProceedingEntry.COLUMN_DESCRIPTION,
            ProceedingsContract.ProceedingEntry.COLUMN_DEPENDS_ON,
            ProceedingsContract.ProceedingEntry.COLUMN_LOCATION_OTHER_DATA,
            ProceedingsContract.ProceedingEntry.COLUMN_REQUISITES,
            ProceedingsContract.ProceedingEntry.COLUMN_URL,
            ProceedingsContract.ProceedingEntry.COLUMN_STATUS };

    public ProceedingsDetailFragment() {
    }

    public static ProceedingsDetailFragment newInstance(Integer proceedingId, String categoryName){
        ProceedingsDetailFragment fragment = new ProceedingsDetailFragment();
        Bundle args = new Bundle();
        args.putInt("proceedingId",proceedingId);
        args.putString("categoryName",categoryName);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_proceedings_detail, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        mProceedingId = getArguments().getInt("proceedingId");
        mCategoryName = getArguments().getString("categoryName");
        mProceedingName = (TextView)view.findViewById(R.id.proceeding_name);
        mProceedingDescription = (TextView)view.findViewById(R.id.proceeding_description);
        mProceedingDependsOn = (TextView)view.findViewById(R.id.proceeding_depends_on);
        mProceedingWhenWhere = (TextView) view.findViewById(R.id.proceeding_when_where);
        mProceedingReq = (TextView)view.findViewById(R.id.proceeding_req);
        mProceedingUrl = (TextView)view.findViewById(R.id.proceeding_url);
        mProceedingStatus = (TextView)view.findViewById(R.id.proceeding_status);
        mProceedingCategory = (TextView)view.findViewById(R.id.proceeding_category);
        mMapButton = (Button) view.findViewById(R.id.ver_mapa);

        mMapButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MapActivity.class);
                intent.putExtra("proceedingId", mProceedingId);
                startActivity(intent);
            }
        });
        mProceedingUrl.setMovementMethod(LinkMovementMethod.getInstance());
        mProceedingUrl.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String url = mProceedingUrl.getText().toString();
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });

        // Prepare the loader.  Either re-connect with an existing one,
        // or start a new one.
        getLoaderManager().initLoader(PROCEEDINGS_DETAIL_LOADER, null, ProceedingsDetailFragment.this);
    }


    public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
        Uri baseUri = ProceedingsContract.ProceedingEntry.CONTENT_URI;
        String whereProceeding = ProceedingsContract.ProceedingEntry._ID + " = ?";
        String[] whereArgsProceeding = {mProceedingId.toString()};
        return new CursorLoader(getActivity(), baseUri,
                PROCEEDING_DETAIL_PROJECTION, whereProceeding, whereArgsProceeding, null);

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null){
            data.moveToFirst();

            mProceedingName.setText(data.getString(data.getColumnIndex(ProceedingsContract.ProceedingEntry.COLUMN_TITLE)));
            mProceedingDescription.setText(data.getString(data.getColumnIndex(ProceedingsContract.ProceedingEntry.COLUMN_DESCRIPTION)));
            mProceedingDependsOn.setText(data.getString(data.getColumnIndex(ProceedingsContract.ProceedingEntry.COLUMN_DEPENDS_ON)));
            String whenWhere = data.getString(data.getColumnIndex(ProceedingsContract.ProceedingEntry.COLUMN_LOCATION_OTHER_DATA));
            if(whenWhere != null){
                // Quitar \n del inicio
                int index = whenWhere.indexOf("\n");
                if (index < 2){
                    whenWhere = whenWhere.replaceFirst("\n","");
                }
                mProceedingWhenWhere.setText(whenWhere);
            }else{
                mProceedingWhenWhere.setText("No hay información disponible.");
            }
            String reqs = data.getString(data.getColumnIndex(ProceedingsContract.ProceedingEntry.COLUMN_REQUISITES));
            if(reqs != null){
                // Quitar \n del inicio
                int index = reqs.indexOf("\n");
                if (index < 2){
                    reqs = reqs.replaceFirst("\n","");
                }
                mProceedingReq.setText(reqs);
            }else{
                mProceedingReq.setText("No hay información disponible.");
            }
            mProceedingUrl.setText(data.getString(data.getColumnIndex(ProceedingsContract.ProceedingEntry.COLUMN_URL)));
            mProceedingStatus.setText(data.getString(data.getColumnIndex(ProceedingsContract.ProceedingEntry.COLUMN_STATUS)));
            mProceedingCategory.setText(mCategoryName);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // This is called when the last Cursor provided to onLoadFinished()
        // above is about to be closed.  We need to make sure we are no
        // longer using it.
        mProceedingName.setText("");
        mProceedingDescription.setText("");
        mProceedingDependsOn.setText("");
        mProceedingWhenWhere.setText("");
        mProceedingReq.setText("");
        mProceedingUrl.setText("");
        mProceedingStatus.setText("");
        mProceedingCategory.setText("");
    }


}
