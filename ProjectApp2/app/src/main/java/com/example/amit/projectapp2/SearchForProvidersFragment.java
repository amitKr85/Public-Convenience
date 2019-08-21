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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.amit.projectapp2.data.ServiceMgmtContract.*;

public class SearchForProvidersFragment extends Fragment {
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
        //return super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_search_for_providers, container, false);
        final Spinner servicesSpinner = rootView.findViewById(R.id.search_for_providers_type_spinner);
        ArrayAdapter servicesArrayAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.services_list_with_any,
                android.R.layout.simple_spinner_item);
        servicesArrayAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
        servicesSpinner.setAdapter(servicesArrayAdapter);

        ListView providersListView = rootView.findViewById(R.id.search_for_providers_list_view);
        final ProvidersListCursorAdapter mCursorAdapter = new ProvidersListCursorAdapter(getContext(), null);
        providersListView.setAdapter(mCursorAdapter);
        View emptyView = inflater.inflate(R.layout.empty_list_placeholder, container, false);
        providersListView.setEmptyView(rootView.findViewById(R.id.empty_view_placeholder));

        //((TextView)rootView.findViewById(R.id.search_for_providers_userid_text_view)).setText(String.valueOf(mUserId));


        Button searchButton = rootView.findViewById(R.id.search_for_providers_search_button);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /**
                 * getting user address
                 */
                int pinCode=0;
                String locality="", city="", state="", country="";
                Uri userIdUri = ContentUris.withAppendedId(UserEntry.USER_CONTENT_URI, mUserId);
                String uProjection[] = {UserEntry.COLUMN_PIN_CODE, UserEntry.COLUMN_LOCALITY,
                        UserEntry.COLUMN_CITY, UserEntry.COLUMN_STATE, UserEntry.COLUMN_COUNTRY};
                Cursor uCursor = getContext().getContentResolver()
                        .query(userIdUri, uProjection, null, null, null);
                if (uCursor.moveToNext()) {
                    pinCode=uCursor.getInt(uCursor.getColumnIndex(UserEntry.COLUMN_PIN_CODE));
                    locality=uCursor.getString(uCursor.getColumnIndex(UserEntry.COLUMN_LOCALITY));
                    city=uCursor.getString(uCursor.getColumnIndex(UserEntry.COLUMN_CITY));
                    state=uCursor.getString(uCursor.getColumnIndex(UserEntry.COLUMN_STATE));
                    country=uCursor.getString(uCursor.getColumnIndex(UserEntry.COLUMN_COUNTRY));
                }
                uCursor.close();
                /**
                 * finding provider in user area
                 */
                String type = servicesSpinner.getSelectedItem().toString();

                String pProjection[]={ProviderEntry._ID,ProviderEntry.COLUMN_TYPE,ProviderEntry.COLUMN_COMPANY_NAME};
                //Toast.makeText(getContext(), "search clicked", Toast.LENGTH_SHORT).show();
                String pSelection;
                String pSelectionArgs[];
                if (type.equals("Any")) {

                    pSelection=ProviderEntry.COLUMN_PIN_CODE+"=? AND "
                            +ProviderEntry.COLUMN_LOCALITY+"=? AND "
                            +ProviderEntry.COLUMN_CITY+"=? AND "
                            +ProviderEntry.COLUMN_STATE+"=? AND "
                            +ProviderEntry.COLUMN_COUNTRY+"=?";
                    pSelectionArgs=new String[]{String.valueOf(pinCode),locality,city,state,country};

                } else {
                    pSelection=ProviderEntry.COLUMN_TYPE+"=? AND "
                            +ProviderEntry.COLUMN_PIN_CODE+"=? AND "
                            +ProviderEntry.COLUMN_LOCALITY+"=? AND "
                            +ProviderEntry.COLUMN_CITY+"=? AND "
                            +ProviderEntry.COLUMN_STATE+"=? AND "
                            +ProviderEntry.COLUMN_COUNTRY+"=?";
                    pSelectionArgs=new String[]{type,String.valueOf(pinCode),locality,city,state,country};

                }
                /**
                 * finding providers
                 */
                Cursor pCursor=getContext().getContentResolver().query(ProviderEntry.PROVIDER_CONTENT_URI,
                        pProjection,pSelection,pSelectionArgs,null);
                /**
                 * swapping cursor of list view
                 */
                mCursorAdapter.swapCursor(pCursor);
            }
        });
        return rootView;
    }
}
