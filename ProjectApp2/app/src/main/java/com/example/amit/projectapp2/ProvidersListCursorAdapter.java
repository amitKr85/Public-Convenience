package com.example.amit.projectapp2;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.amit.projectapp2.data.ServiceMgmtContract.*;

public class ProvidersListCursorAdapter extends CursorAdapter {
    Context mContext;
    public ProvidersListCursorAdapter(Context pcontext, Cursor c) {
        super(pcontext, c,0);
        mContext=pcontext;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.provider_list_item,parent,false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        int provid=cursor.getInt(cursor.getColumnIndex(ProviderEntry._ID));
        String name="";
        String type=cursor.getString(cursor.getColumnIndex(ProviderEntry.COLUMN_TYPE));
        String companyName=cursor.getString(cursor.getColumnIndex(ProviderEntry.COLUMN_COMPANY_NAME));
        String phone="";

        TextView nameTextView=view.findViewById(R.id.provider_list_item_name_text_view);
        TextView typeTextView=view.findViewById(R.id.provider_list_item_type_text_view);
        TextView companyNameTextView=view.findViewById(R.id.provider_list_item_company_text_view);
        TextView phoneTextView=view.findViewById(R.id.provider_list_item_phone_text_view);

        /**
         * getting provider info. from user table
         */
        String uProjection[]={UserEntry.COLUMN_FULL_NAME,UserEntry.COLUMN_CONTACT_NUMBER};
        String uSelection=UserEntry.COLUMN_IS_PROVIDER+"=?";
        String uSelectionArgs[]={String.valueOf(provid)};

        Cursor uCursor=mContext.getContentResolver().query(UserEntry.USER_CONTENT_URI,
                uProjection,uSelection,uSelectionArgs,null);
        if(uCursor.moveToNext()){
            name=uCursor.getString(uCursor.getColumnIndex(UserEntry.COLUMN_FULL_NAME));
            phone=uCursor.getString(uCursor.getColumnIndex(UserEntry.COLUMN_CONTACT_NUMBER));
        }
        //nameTextView.setText(String.valueOf(id));
        nameTextView.setText(name);
        typeTextView.setText(type);
        companyNameTextView.setText(companyName);
        phoneTextView.setText(phone);

    }
}
