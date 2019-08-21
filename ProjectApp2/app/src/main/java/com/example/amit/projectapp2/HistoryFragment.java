package com.example.amit.projectapp2;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.amit.projectapp2.data.ServiceMgmtContract;

public class HistoryFragment extends Fragment {
    int mUserId;
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
        View rootView=inflater.inflate(R.layout.fragment_history,container,false);

        /**
         * retrieving pending appointments from appointment table
         */
        String projection[]={ServiceMgmtContract.AppointmentEntry._ID, ServiceMgmtContract.AppointmentEntry.COLUMN_USER_ID
                , ServiceMgmtContract.AppointmentEntry.COLUMN_PROVIDER_ID, ServiceMgmtContract.AppointmentEntry.COLUMN_SERVICE_ID
                , ServiceMgmtContract.AppointmentEntry.COLUMN_STATUS};
        String selection= ServiceMgmtContract.AppointmentEntry.COLUMN_USER_ID+"=? AND "
                + ServiceMgmtContract.AppointmentEntry.COLUMN_STATUS+"!=?";
        String selectionArgs[]={String.valueOf(mUserId),String.valueOf(ServiceMgmtContract.AppointmentEntry.STATUS_PENDING)};
        String sortOrder= ServiceMgmtContract.AppointmentEntry._ID+" DESC";

        Cursor cursor=getContext().getContentResolver()
                .query(ServiceMgmtContract.AppointmentEntry.APPOINTMENT_CONTENT_URI,projection,selection,selectionArgs,sortOrder);

        HistoryListCursorAdapter adapter=new HistoryListCursorAdapter(getContext(),cursor);
        ListView listView=rootView.findViewById(R.id.history_list_view);
        listView.setAdapter(adapter);
        listView.setEmptyView(rootView.findViewById(R.id.history_empty_view_placeholder));

        return rootView;
    }
}
