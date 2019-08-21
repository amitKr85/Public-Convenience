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

public class HistoryListCursorAdapter extends CursorAdapter {

    Context mContext;
    public HistoryListCursorAdapter(Context context, Cursor c) {
        super(context, c,0);
        mContext=context;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.history_list_item,parent,false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView serviceTypeTextView=view.findViewById(R.id.history_list_item_service_type_text_view);
        TextView serviceDescTextView=view.findViewById(R.id.history_list_item_description_text_view);
        TextView providerNameTextView=view.findViewById(R.id.history_list_item_name_text_view);
        TextView providerUsernameTextView=view.findViewById(R.id.history_list_item_username_text_view);
        TextView providerTypeTextView=view.findViewById(R.id.history_list_item_provider_type_text_view);
        TextView providerCompanyTextView=view.findViewById(R.id.history_list_item_company_text_view);
        TextView providerContactTextView=view.findViewById(R.id.history_list_item_phone_text_view);
        final TextView statusTextView=view.findViewById(R.id.history_list_item_status_text_view);

        /**
         * getting data from cursor
         */
        final int appId=cursor.getInt(cursor.getColumnIndex(ServiceMgmtContract.AppointmentEntry._ID));
        int appStatus=cursor.getInt(cursor.getColumnIndex(ServiceMgmtContract.AppointmentEntry.COLUMN_STATUS));
        int providerId=cursor.getInt(cursor.getColumnIndex(ServiceMgmtContract.AppointmentEntry.COLUMN_PROVIDER_ID));
        int serviceId=cursor.getInt(cursor.getColumnIndex(ServiceMgmtContract.AppointmentEntry.COLUMN_SERVICE_ID));
        /**
         * retrieving data from service table and setting into TextViews
         */
        String serviceProjection[]={ServiceMgmtContract.ServiceEntry.COLUMN_TYPE, ServiceMgmtContract.ServiceEntry.COLUMN_SHORT_DESCRIPTION};
        Uri serviceIdUri= ContentUris.withAppendedId(ServiceMgmtContract.ServiceEntry.SERVICE_CONTENT_URI,serviceId);
        Cursor serviceCursor=mContext.getContentResolver()
                .query(serviceIdUri,serviceProjection,null,null,null);
        if(serviceCursor.moveToNext()){
            String serviceType=serviceCursor.getString(serviceCursor.getColumnIndex(ServiceMgmtContract.ServiceEntry.COLUMN_TYPE));
            String serviceDesc=serviceCursor.getString(serviceCursor.getColumnIndex(ServiceMgmtContract.ServiceEntry.COLUMN_SHORT_DESCRIPTION));

            serviceTypeTextView.setText(serviceType);
            serviceDescTextView.setText(serviceDesc);
        }
        serviceCursor.close();
        /**
         * retrieving data from provider table and setting into TextViews
         */
        String providerProjection[]={ServiceMgmtContract.ProviderEntry.COLUMN_TYPE, ServiceMgmtContract.ProviderEntry.COLUMN_COMPANY_NAME};
        Uri providerIDUri=ContentUris.withAppendedId(ServiceMgmtContract.ProviderEntry.PROVIDER_CONTENT_URI,providerId);
        Cursor providerCursor=mContext.getContentResolver()
                .query(providerIDUri,providerProjection,null,null,null);
        if(providerCursor.moveToNext()){
            String providerType=providerCursor.getString(providerCursor.getColumnIndex(ServiceMgmtContract.ProviderEntry.COLUMN_TYPE));
            String providerCompany=providerCursor.getString(providerCursor.getColumnIndex(ServiceMgmtContract.ProviderEntry.COLUMN_COMPANY_NAME));

            providerTypeTextView.setText(providerType);
            providerCompanyTextView.setText(providerCompany);
        }
        providerCursor.close();
        /**
         * retrieving data of provider from user table and setting into TextViews
         */
        String pUserProjection[]={ServiceMgmtContract.UserEntry.COLUMN_FULL_NAME, ServiceMgmtContract.UserEntry.COLUMN_USERNAME, ServiceMgmtContract.UserEntry.COLUMN_CONTACT_NUMBER};
        String pUserSelection= ServiceMgmtContract.UserEntry.COLUMN_IS_PROVIDER+"=?";
        String pUserSelectionArgs[]={String.valueOf(providerId)};
        Cursor pUserCursor=mContext.getContentResolver()
                .query(ServiceMgmtContract.UserEntry.USER_CONTENT_URI,pUserProjection,pUserSelection,pUserSelectionArgs,null);
        if(pUserCursor.moveToNext()){
            String providerName=pUserCursor.getString(pUserCursor.getColumnIndex(ServiceMgmtContract.UserEntry.COLUMN_FULL_NAME));
            String providerUsername="@"+pUserCursor.getString(pUserCursor.getColumnIndex(ServiceMgmtContract.UserEntry.COLUMN_USERNAME));
            String providerContact=pUserCursor.getString(pUserCursor.getColumnIndex(ServiceMgmtContract.UserEntry.COLUMN_CONTACT_NUMBER));

            providerNameTextView.setText(providerName);
            providerUsernameTextView.setText(providerUsername);
            providerContactTextView.setText(providerContact);
        }
        pUserCursor.close();
        /**
         * setting status
         */
        if(appStatus== ServiceMgmtContract.AppointmentEntry.STATUS_COMPLETED){
            statusTextView.setText("COMPLETED");
            statusTextView.setTextColor(mContext.getResources().getColor(R.color.colorPrimary));
        }
        else if(appStatus== ServiceMgmtContract.AppointmentEntry.STATUS_USER_CANCELED){
            statusTextView.setText("CANCELED BY YOU");
            statusTextView.setTextColor(mContext.getResources().getColor(R.color.colorCancel));
        }

    }
}
