package com.example.amit.projectapp2;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.amit.projectapp2.data.ServiceMgmtContract;

public class PendingAppointmentsCursorAdapter extends CursorAdapter {
    Context mContext;
    public PendingAppointmentsCursorAdapter(Context context, Cursor c) {
        super(context, c,0);
        mContext=context;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.pending_appointments_list_item,parent,false);

    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        TextView serviceTypeTextView=view.findViewById(R.id.pending_app_list_item_service_type_text_view);
        TextView serviceShortDescTextView=view.findViewById(R.id.pending_app_list_item_short_description_text_view);
        TextView serviceDescTextView=view.findViewById(R.id.pending_app_list_item_description_text_view);
        TextView userNameTextView=view.findViewById(R.id.pending_app_list_item_name_text_view);
        TextView userPhoneTextView=view.findViewById(R.id.pending_app_list_item_phone_text_view);
        TextView userAddressTextView=view.findViewById(R.id.pending_app_list_item_address_text_view);

        /**
         * getting data from cursor
         */
        final int appId=cursor.getInt(cursor.getColumnIndex(ServiceMgmtContract.AppointmentEntry._ID));
        int appStatus=cursor.getInt(cursor.getColumnIndex(ServiceMgmtContract.AppointmentEntry.COLUMN_STATUS));
        int userId=cursor.getInt(cursor.getColumnIndex(ServiceMgmtContract.AppointmentEntry.COLUMN_USER_ID));
        int serviceId=cursor.getInt(cursor.getColumnIndex(ServiceMgmtContract.AppointmentEntry.COLUMN_SERVICE_ID));
        /**
         * retrieving data from service table and setting into TextViews
         */
        String serviceProjection[]={ServiceMgmtContract.ServiceEntry.COLUMN_TYPE,
                ServiceMgmtContract.ServiceEntry.COLUMN_SHORT_DESCRIPTION, ServiceMgmtContract.ServiceEntry.COLUMN_DESCRIPTION};
        Uri serviceIdUri= ContentUris.withAppendedId(ServiceMgmtContract.ServiceEntry.SERVICE_CONTENT_URI,serviceId);
        Cursor serviceCursor=mContext.getContentResolver()
                .query(serviceIdUri,serviceProjection,null,null,null);
        if(serviceCursor.moveToNext()){
            String serviceType=serviceCursor.getString(serviceCursor.getColumnIndex(ServiceMgmtContract.ServiceEntry.COLUMN_TYPE));
            String serviceShortDesc=serviceCursor.getString(serviceCursor.getColumnIndex(ServiceMgmtContract.ServiceEntry.COLUMN_SHORT_DESCRIPTION));
            String serviceDesc=serviceCursor.getString(serviceCursor.getColumnIndex(ServiceMgmtContract.ServiceEntry.COLUMN_DESCRIPTION));

            serviceTypeTextView.setText(serviceType);
            serviceShortDescTextView.setText(serviceShortDesc);
            serviceDescTextView.setText(serviceDesc);
        }
        serviceCursor.close();
        /**
         * retrieving data of provider from user table and setting into TextViews
         */
        String pUserProjection[]={ServiceMgmtContract.UserEntry.COLUMN_FULL_NAME
                ,ServiceMgmtContract.UserEntry.COLUMN_CONTACT_NUMBER
                ,ServiceMgmtContract.UserEntry.COLUMN_HOUSE_NUMBER
                , ServiceMgmtContract.UserEntry.COLUMN_lANDMARK
                , ServiceMgmtContract.UserEntry.COLUMN_LOCALITY
                , ServiceMgmtContract.UserEntry.COLUMN_CITY
                , ServiceMgmtContract.UserEntry.COLUMN_PIN_CODE
                , ServiceMgmtContract.UserEntry.COLUMN_STATE
                , ServiceMgmtContract.UserEntry.COLUMN_COUNTRY};
        Uri userUri=ContentUris.withAppendedId(ServiceMgmtContract.UserEntry.USER_CONTENT_URI,userId);
        Cursor pUserCursor=mContext.getContentResolver().query(userUri,pUserProjection,null,null,null);

        if(pUserCursor.moveToNext()){
            String userName=pUserCursor.getString(pUserCursor.getColumnIndex(ServiceMgmtContract.UserEntry.COLUMN_FULL_NAME));
            String userContact=pUserCursor.getString(pUserCursor.getColumnIndex(ServiceMgmtContract.UserEntry.COLUMN_CONTACT_NUMBER));
            String house=pUserCursor.getString(pUserCursor.getColumnIndex(ServiceMgmtContract.UserEntry.COLUMN_HOUSE_NUMBER));
            String landmark=pUserCursor.getString(pUserCursor.getColumnIndex(ServiceMgmtContract.UserEntry.COLUMN_lANDMARK));
            String locality=pUserCursor.getString(pUserCursor.getColumnIndex(ServiceMgmtContract.UserEntry.COLUMN_LOCALITY));
            String pincode=pUserCursor.getString(pUserCursor.getColumnIndex(ServiceMgmtContract.UserEntry.COLUMN_PIN_CODE));
            String city=pUserCursor.getString(pUserCursor.getColumnIndex(ServiceMgmtContract.UserEntry.COLUMN_CITY));
            String state=pUserCursor.getString(pUserCursor.getColumnIndex(ServiceMgmtContract.UserEntry.COLUMN_STATE));
            String country=pUserCursor.getString(pUserCursor.getColumnIndex(ServiceMgmtContract.UserEntry.COLUMN_COUNTRY));

            String address=house+"\nnear "+landmark+"\n"+locality+"\n"+pincode+"\n"+city+"\n"+state+"\n"+country;
            userNameTextView.setText(userName);
            userPhoneTextView.setText(userContact);
            userAddressTextView.setText(address);

        }
        pUserCursor.close();
    }
}
