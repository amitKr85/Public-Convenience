package com.example.amit.projectapp2;

import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.amit.projectapp2.data.ServiceMgmtContract.*;

public class RequestServiceFragment extends Fragment {

    public interface OnRequestDoneListener {
        public void onRequestDone();
    }

    int mUserId;
    OnRequestDoneListener mListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        super.onAttach(context);
        try {
            mListener = (OnRequestDoneListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnRequestDoneListener");
        }

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mUserId = getArguments().getInt(MainActivity.ARG_USER_ID);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_request_service, container, false);
        /**
         * setting up spinner
         */
        final Spinner servicesSpinner = rootView.findViewById(R.id.request_service_spinner);
        final ArrayAdapter servicesArrayAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.services_list,
                android.R.layout.simple_spinner_item);
        servicesArrayAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
        servicesSpinner.setAdapter(servicesArrayAdapter);

        Button okbutton = rootView.findViewById(R.id.request_service_button);
        final EditText shortEditText = rootView.findViewById(R.id.request_service_short_editText);
        final EditText detailEditText = rootView.findViewById(R.id.request_service_detail_editText);

        okbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String type = servicesSpinner.getSelectedItem().toString();
                String shortDesc = shortEditText.getText().toString();
                String detailDesc = detailEditText.getText().toString();

                /**
                 * check for empty desc and show dialog and return if empty
                 */
                if (EmptyString.isAnyEmptyString(shortDesc, detailDesc)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setMessage("empty description !!");
                    builder.setPositiveButton("OK", null);
                    builder.create().show();
                    return;
                }

                /**
                 * getting user address
                 */
                int pinCode = 0;
                String locality = "", city = "", state = "", country = "";
                Uri userIdUri = ContentUris.withAppendedId(UserEntry.USER_CONTENT_URI, mUserId);
                String uProjection[] = {UserEntry.COLUMN_PIN_CODE, UserEntry.COLUMN_LOCALITY,
                        UserEntry.COLUMN_CITY, UserEntry.COLUMN_STATE, UserEntry.COLUMN_COUNTRY};
                Cursor uCursor = getContext().getContentResolver()
                        .query(userIdUri, uProjection, null, null, null);
                if (uCursor.moveToNext()) {
                    pinCode = uCursor.getInt(uCursor.getColumnIndex(UserEntry.COLUMN_PIN_CODE));
                    locality = uCursor.getString(uCursor.getColumnIndex(UserEntry.COLUMN_LOCALITY));
                    city = uCursor.getString(uCursor.getColumnIndex(UserEntry.COLUMN_CITY));
                    state = uCursor.getString(uCursor.getColumnIndex(UserEntry.COLUMN_STATE));
                    country = uCursor.getString(uCursor.getColumnIndex(UserEntry.COLUMN_COUNTRY));
                }
                uCursor.close();
                /**
                 * finding provider in user's area
                 */
                String pProjection[] = {ProviderEntry._ID, ProviderEntry.COLUMN_TYPE, ProviderEntry.COLUMN_COMPANY_NAME};
                String pSelection;
                String pSelectionArgs[];
                pSelection = ProviderEntry.COLUMN_TYPE + "=? AND "
                        + ProviderEntry.COLUMN_PIN_CODE + "=? AND "
                        + ProviderEntry.COLUMN_LOCALITY + "=? AND "
                        + ProviderEntry.COLUMN_CITY + "=? AND "
                        + ProviderEntry.COLUMN_STATE + "=? AND "
                        + ProviderEntry.COLUMN_COUNTRY + "=?";
                pSelectionArgs = new String[]{type, String.valueOf(pinCode), locality, city, state, country};
                /**
                 * finding providers
                 */
                Cursor pCursor = getContext().getContentResolver().query(ProviderEntry.PROVIDER_CONTENT_URI,
                        pProjection, pSelection, pSelectionArgs, null);

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                if (pCursor.moveToNext()) {
                    int pId = pCursor.getInt(pCursor.getColumnIndex(ProviderEntry._ID));
                    String pType = pCursor.getString(pCursor.getColumnIndex(ProviderEntry.COLUMN_TYPE));
                    String pCompany = pCursor.getString(pCursor.getColumnIndex(ProviderEntry.COLUMN_COMPANY_NAME));
                    String pName = "", pUsername = "", pContactNumber = "";

                    /**
                     * inserting data into service table
                     */
                    ContentValues serviceValues=new ContentValues();
                    serviceValues.put(ServiceEntry.COLUMN_TYPE,type);
                    serviceValues.put(ServiceEntry.COLUMN_SHORT_DESCRIPTION,shortDesc);
                    serviceValues.put(ServiceEntry.COLUMN_DESCRIPTION,detailDesc);
                    Uri serviceUri=getContext().getContentResolver()
                            .insert(ServiceEntry.SERVICE_CONTENT_URI,serviceValues);
                    int serviceId=(int)ContentUris.parseId(serviceUri);
                    // if unable to insert data into service table then show alert dialog
                    if(serviceId<=0){
                        Log.i("Request Fragment","unable to insert data in service table");

                        AlertDialog.Builder serviceAlertbuilder=new AlertDialog.Builder(getContext());
                        serviceAlertbuilder.setMessage("Unable to request. Please try again later.");
                        serviceAlertbuilder.setPositiveButton("ok",null);
                        serviceAlertbuilder.create().show();
                        return;
                    }

                    /**
                     * insert data into appointment table
                     */
                    // status is pending at start
                    int status=AppointmentEntry.STATUS_PENDING;
                    ContentValues appValues=new ContentValues();
                    appValues.put(AppointmentEntry.COLUMN_USER_ID,mUserId);
                    appValues.put(AppointmentEntry.COLUMN_PROVIDER_ID,pId);
                    appValues.put(AppointmentEntry.COLUMN_SERVICE_ID,serviceId);
                    appValues.put(AppointmentEntry.COLUMN_STATUS,status);
                    Uri appUri=getContext().getContentResolver()
                            .insert(AppointmentEntry.APPOINTMENT_CONTENT_URI,appValues);
                    // if unable to insert data into appointment table then show alert dialog
                    int appId=(int)ContentUris.parseId(appUri);
                    if(appId<=0){
                        Log.i("Request Fragment","unable to insert data in appointment table");

                        AlertDialog.Builder serviceAlertbuilder=new AlertDialog.Builder(getContext());
                        serviceAlertbuilder.setMessage("Unable to request. Please try again later.");
                        serviceAlertbuilder.setPositiveButton("ok",null);
                        serviceAlertbuilder.create().show();
                        return;
                    }

                    /**
                     * getting provider info. from user table
                     */
                    String tempProjection[] = {UserEntry.COLUMN_USERNAME, UserEntry.COLUMN_FULL_NAME
                            , UserEntry.COLUMN_CONTACT_NUMBER};
                    String tempSelection = UserEntry.COLUMN_IS_PROVIDER + "=?";
                    String tempSelectionArgs[] = {String.valueOf(pId)};
                    Cursor providerPrivInfoCursor = getContext().getContentResolver()
                            .query(UserEntry.USER_CONTENT_URI, tempProjection, tempSelection, tempSelectionArgs, null);
                    if (providerPrivInfoCursor.moveToNext()) {
                        pName = providerPrivInfoCursor
                                .getString(providerPrivInfoCursor.getColumnIndex(UserEntry.COLUMN_FULL_NAME));
                        pUsername = providerPrivInfoCursor
                                .getString(providerPrivInfoCursor.getColumnIndex(UserEntry.COLUMN_USERNAME));
                        pContactNumber = providerPrivInfoCursor
                                .getString(providerPrivInfoCursor.getColumnIndex(UserEntry.COLUMN_CONTACT_NUMBER));
                    }

                    /*builder.setMessage(pId + " > " + pName + " > "+ pUsername + " > "+ pContactNumber
                            + " > " + pType + " > " + pCompany + " > ");
                    */
                    //setting up dialog view
                    View dialogView=inflater.inflate(R.layout.appointed_provider_item,container,false);
                    ((TextView)dialogView.findViewById(R.id.appointed_provider_name_text_view))
                            .setText(pName);
                    ((TextView)dialogView.findViewById(R.id.appointed_provider_username_text_view))
                            .setText("@"+pUsername);
                    ((TextView)dialogView.findViewById(R.id.appointed_provider_type_text_view))
                            .setText(pType);
                    ((TextView)dialogView.findViewById(R.id.appointed_provider_company_text_view))
                            .setText(pCompany);
                    ((TextView)dialogView.findViewById(R.id.appointed_provider_phone_text_view))
                            .setText(pContactNumber);

                    builder.setView(dialogView);
                    builder.setCancelable(false);
                    //redirecting to pending requests if provider found
                    builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mListener.onRequestDone();
                        }
                    });
                } else {
                    builder.setMessage("no match found");
                    builder.setPositiveButton("ok", null);
                }
                pCursor.close();
                builder.create().show();
            }
        });
        return rootView;
    }
}
