package com.example.amit.projectapp2;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.amit.projectapp2.data.ServiceMgmtContract.*;

public class PendingRequestFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    int mUserId;
    PendingListCursorAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mUserId = getArguments().getInt(MainActivity.ARG_USER_ID);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView=inflater.inflate(R.layout.fragment_pending_requests,container,false);

        adapter=new PendingListCursorAdapter(getContext(),null);

        ListView listView=rootView.findViewById(R.id.pending_list_view);
        listView.setAdapter(adapter);
        listView.setEmptyView(rootView.findViewById(R.id.pending_empty_view_placeholder));

        getLoaderManager().initLoader(0,null,this);
        return rootView;
    }

    @Override
    public android.support.v4.content.Loader<Cursor> onCreateLoader(int id, Bundle args) {
        /**
         * retrieving pending appointments from appointment table
         */
        String projection[]={AppointmentEntry._ID,AppointmentEntry.COLUMN_USER_ID
                ,AppointmentEntry.COLUMN_PROVIDER_ID,AppointmentEntry.COLUMN_SERVICE_ID
                ,AppointmentEntry.COLUMN_STATUS};
        String selection=AppointmentEntry.COLUMN_USER_ID+"=? AND "+AppointmentEntry.COLUMN_STATUS+"=?";
        String selectionArgs[]={String.valueOf(mUserId),String.valueOf(AppointmentEntry.STATUS_PENDING)};
        String sortOrder=AppointmentEntry._ID+" DESC";

        return new CursorLoader(getContext(),AppointmentEntry.APPOINTMENT_CONTENT_URI,projection,
                selection,selectionArgs,sortOrder);
    }

    @Override
    public void onLoadFinished(@NonNull android.support.v4.content.Loader<Cursor> loader, Cursor data) {
        adapter.swapCursor(data);

    }

    @Override
    public void onLoaderReset(@NonNull android.support.v4.content.Loader<Cursor> loader) {
        adapter.swapCursor(null);

    }
}
