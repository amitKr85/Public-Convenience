package com.example.amit.projectapp2;

import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.amit.projectapp2.data.ServiceMgmtContract.*;

public class PendingListCursorAdapter extends CursorAdapter {
    Context mContext;

    public PendingListCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
        mContext=context;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.pending_list_item,parent,false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        TextView serviceTypeTextView=view.findViewById(R.id.pending_list_item_service_type_text_view);
        TextView serviceDescTextView=view.findViewById(R.id.pending_list_item_description_text_view);
        TextView providerNameTextView=view.findViewById(R.id.pending_list_item_name_text_view);
        TextView providerUsernameTextView=view.findViewById(R.id.pending_list_item_username_text_view);
        TextView providerTypeTextView=view.findViewById(R.id.pending_list_item_provider_type_text_view);
        TextView providerCompanyTextView=view.findViewById(R.id.pending_list_item_company_text_view);
        TextView providerContactTextView=view.findViewById(R.id.pending_list_item_phone_text_view);
        final TextView statusTextView=view.findViewById(R.id.pending_list_item_status_text_view);
        final Button completedButton=view.findViewById(R.id.pending_list_item_completed_button);
        final Button cancelButton=view.findViewById(R.id.pending_list_item_cancel_button);

        /**
         * getting data from cursor
         */
        final int appId=cursor.getInt(cursor.getColumnIndex(AppointmentEntry._ID));
        int providerId=cursor.getInt(cursor.getColumnIndex(AppointmentEntry.COLUMN_PROVIDER_ID));
        int serviceId=cursor.getInt(cursor.getColumnIndex(AppointmentEntry.COLUMN_SERVICE_ID));

        /**
         * setting visibility of Buttons because they might be hidden
         */

        completedButton.setVisibility(View.VISIBLE);
        cancelButton.setVisibility(View.VISIBLE);

        /**
         * retrieving data from service table and setting into TextViews
         */
        String serviceProjection[]={ServiceEntry.COLUMN_TYPE,ServiceEntry.COLUMN_SHORT_DESCRIPTION};
        Uri serviceIdUri= ContentUris.withAppendedId(ServiceEntry.SERVICE_CONTENT_URI,serviceId);
        Cursor serviceCursor=mContext.getContentResolver()
                .query(serviceIdUri,serviceProjection,null,null,null);
        if(serviceCursor.moveToNext()){
            String serviceType=serviceCursor.getString(serviceCursor.getColumnIndex(ServiceEntry.COLUMN_TYPE));
            String serviceDesc=serviceCursor.getString(serviceCursor.getColumnIndex(ServiceEntry.COLUMN_SHORT_DESCRIPTION));

            serviceTypeTextView.setText(serviceType);
            serviceDescTextView.setText(serviceDesc);
        }
        serviceCursor.close();
        /**
         * retrieving data from provider table and setting into TextViews
         */
        String providerProjection[]={ProviderEntry.COLUMN_TYPE,ProviderEntry.COLUMN_COMPANY_NAME};
        Uri providerIDUri=ContentUris.withAppendedId(ProviderEntry.PROVIDER_CONTENT_URI,providerId);
        Cursor providerCursor=mContext.getContentResolver()
                .query(providerIDUri,providerProjection,null,null,null);
        if(providerCursor.moveToNext()){
            String providerType=providerCursor.getString(providerCursor.getColumnIndex(ProviderEntry.COLUMN_TYPE));
            String providerCompany=providerCursor.getString(providerCursor.getColumnIndex(ProviderEntry.COLUMN_COMPANY_NAME));

            providerTypeTextView.setText(providerType);
            providerCompanyTextView.setText(providerCompany);
        }
        providerCursor.close();
        /**
         * retrieving data of provider from user table and setting into TextViews
         */
        String pUserProjection[]={UserEntry.COLUMN_FULL_NAME,UserEntry.COLUMN_USERNAME,UserEntry.COLUMN_CONTACT_NUMBER};
        String pUserSelection=UserEntry.COLUMN_IS_PROVIDER+"=?";
        String pUserSelectionArgs[]={String.valueOf(providerId)};
        Cursor pUserCursor=mContext.getContentResolver()
                .query(UserEntry.USER_CONTENT_URI,pUserProjection,pUserSelection,pUserSelectionArgs,null);
        if(pUserCursor.moveToNext()){
            String providerName=pUserCursor.getString(pUserCursor.getColumnIndex(UserEntry.COLUMN_FULL_NAME));
            String providerUsername="@"+pUserCursor.getString(pUserCursor.getColumnIndex(UserEntry.COLUMN_USERNAME));
            String providerContact=pUserCursor.getString(pUserCursor.getColumnIndex(UserEntry.COLUMN_CONTACT_NUMBER));

            providerNameTextView.setText(providerName);
            providerUsernameTextView.setText(providerUsername);
            providerContactTextView.setText(providerContact);
        }
        pUserCursor.close();

        /**
         * binding complete button
         */
        final Uri appUri=ContentUris.withAppendedId(AppointmentEntry.APPOINTMENT_CONTENT_URI,appId);

        completedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder=new AlertDialog.Builder(mContext);
                builder.setMessage("Completed for sure ?");
                builder.setNegativeButton("no",null);
                builder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ContentValues values=new ContentValues();
                        values.put(AppointmentEntry.COLUMN_STATUS,AppointmentEntry.STATUS_COMPLETED);

                        int rows=mContext.getContentResolver().update(appUri,values,null,null);
                        if(rows>0) {
                            Log.i("Pending adapter", "complete updated successfully");
                            Toast.makeText(mContext, "completed !!", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Log.i("Pending adapter", "complete can't update");
                            Toast.makeText(mContext, "unable to complete !!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                builder.create().show();
            }
        });

        /**
         * binding cancel button
         */
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder=new AlertDialog.Builder(mContext);
                builder.setMessage("Are you sure to cancel ?");
                builder.setNegativeButton("no",null);
                builder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ContentValues values=new ContentValues();
                        values.put(AppointmentEntry.COLUMN_STATUS,AppointmentEntry.STATUS_USER_CANCELED);

                        int rows=mContext.getContentResolver().update(appUri,values,null,null);
                        if(rows>0) {
                            Log.i("Pending adapter", "cancel updated successfully");
                            Toast.makeText(mContext, "canceled !!", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Log.i("Pending adapter", "cancel can't update");
                            Toast.makeText(mContext, "unable to cancel", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                builder.create().show();
            }
        });

    }
}
