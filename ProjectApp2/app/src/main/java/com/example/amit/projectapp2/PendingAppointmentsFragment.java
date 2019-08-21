package com.example.amit.projectapp2;

import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.amit.projectapp2.data.ServiceMgmtContract;

public class PendingAppointmentsFragment extends Fragment {
    int mUserId;
    PendingAppointmentsCursorAdapter adapter;

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
        View rootView=inflater.inflate(R.layout.fragment_pending_appointments,container,false);

        String tProjection[]={ServiceMgmtContract.UserEntry.COLUMN_IS_PROVIDER};
        Uri tUri= ContentUris.withAppendedId(ServiceMgmtContract.UserEntry.USER_CONTENT_URI,mUserId);
        Cursor tcursor=getContext().getContentResolver().query(tUri,tProjection,null,null,null);

        int providerId=-1;
        if(tcursor.moveToNext()){
            providerId=tcursor.getInt(tcursor.getColumnIndex(ServiceMgmtContract.UserEntry.COLUMN_IS_PROVIDER));
        }

        String projection[]={ServiceMgmtContract.AppointmentEntry._ID, ServiceMgmtContract.AppointmentEntry.COLUMN_USER_ID
                , ServiceMgmtContract.AppointmentEntry.COLUMN_PROVIDER_ID, ServiceMgmtContract.AppointmentEntry.COLUMN_SERVICE_ID
                , ServiceMgmtContract.AppointmentEntry.COLUMN_STATUS};
        String selection= ServiceMgmtContract.AppointmentEntry.COLUMN_PROVIDER_ID+"=? AND "
                + ServiceMgmtContract.AppointmentEntry.COLUMN_STATUS+"=?";
        String selectionArgs[]={String.valueOf(providerId),String.valueOf(ServiceMgmtContract.AppointmentEntry.STATUS_PENDING)};
        String sortOrder= ServiceMgmtContract.AppointmentEntry._ID+" ASC";

        Cursor cursor=getContext().getContentResolver()
                .query(ServiceMgmtContract.AppointmentEntry.APPOINTMENT_CONTENT_URI,projection,selection,selectionArgs,sortOrder);

        adapter=new PendingAppointmentsCursorAdapter(getContext(),cursor);

        ListView listView=rootView.findViewById(R.id.pending_appointments_list_view);
        listView.setAdapter(adapter);
        listView.setEmptyView(rootView.findViewById(R.id.pending_appointments_empty_view_placeholder));

        return rootView;
    }
}
