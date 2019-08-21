package com.example.amit.projectapp2;

import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.amit.projectapp2.data.ServiceMgmtContract.*;

/**
 * Created by AMIT on 23-03-2018.
 */

public class SignUpFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        /*
            inflating sign up fragment from fragment_signup_layout layout
         */
        View rootView=inflater.inflate(R.layout.fragment_signup_layout,container,false);

        /*
            finding checkbox (for registration of service provider) and binding up onCheckedChangeListener
         */
        CheckBox providerCheckBox=rootView.findViewById(R.id.is_provider_signup_check_box);
        providerCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isChecked){
                    Fragment providerSignupFragment=new ProviderSignUpFragment();
                    FragmentManager fragmentManager=getActivity().getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
                    fragmentTransaction.setCustomAnimations(R.anim.enter_from_top_anim,R.anim.exit_to_top_anim);
                    fragmentTransaction.add(R.id.provider_signup_fragment_container,providerSignupFragment);
                    fragmentTransaction.commit();
                }
                else {
                    Fragment providerSignupFragment=getFragmentManager().findFragmentById(R.id.provider_signup_fragment_container);
                    FragmentManager fragmentManager=getActivity().getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
                    //fragmentTransaction.setCustomAnimations(R.anim.enter_from_top_anim,R.anim.exit_to_top_anim);
                    fragmentTransaction.remove(providerSignupFragment);
                    fragmentTransaction.commit();
                }
            }
        });
        /*
            binding onclickListener to signup button
         */
        Button signUpButton=rootView.findViewById(R.id.sign_up_button);
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             //   Toast.makeText(getActivity(), "sign Up clicked", Toast.LENGTH_SHORT).show();

                /**
                 * getting values from user section first
                 */
                String fullName=((EditText)getActivity().findViewById(R.id.name_signup_edit_text))
                        .getText().toString().toUpperCase();
                String username=((EditText)getActivity().findViewById(R.id.username_signup_edit_text))
                        .getText().toString().toLowerCase();
                String password=((EditText)getActivity().findViewById(R.id.password_signup_edit_text))
                        .getText().toString();
                String contactNo=((EditText)getActivity().findViewById(R.id.contact_number_signup_edit_text))
                        .getText().toString();
                String pinCode=((EditText)getActivity().findViewById(R.id.pin_code_signup_edit_text))
                        .getText().toString();
                String houseNo=((EditText)getActivity().findViewById(R.id.house_no_signup_edit_text))
                        .getText().toString().toUpperCase();
                String locality=((EditText)getActivity().findViewById(R.id.locality_signup_edit_text))
                        .getText().toString().toUpperCase();
                String landmark=((EditText)getActivity().findViewById(R.id.landmark_signup_edit_text))
                        .getText().toString().toUpperCase();
                String city=((EditText)getActivity().findViewById(R.id.city_signup_edit_text))
                        .getText().toString().toUpperCase();
                String state=((EditText)getActivity().findViewById(R.id.state_signup_edit_text))
                        .getText().toString().toUpperCase();
                String country=((EditText)getActivity().findViewById(R.id.country_signup_edit_text))
                        .getText().toString().toUpperCase();
                /**
                 * checking for empty inputs and return if exists otherwise continue
                 */
                if(EmptyString.isAnyEmptyString(fullName,username,password,contactNo,pinCode,
                        houseNo,locality,landmark,city,state,country)){
                    //Toast.makeText(getContext(),"Empty input in user section",Toast.LENGTH_SHORT).show();
                    AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
                    builder.setMessage("Empty input in user section.");
                    builder.setPositiveButton("OK",null);
                    builder.create().show();
                    return;
                }
                /**
                 * checking if the username is already exists
                 */
                String selection=UserEntry.COLUMN_USERNAME+"=?";
                String selectionArgs[]={username};
                Cursor cursor=getContext().getContentResolver().query(UserEntry.USER_CONTENT_URI,
                        null,selection,selectionArgs,null);
                // if username already exists
                if(cursor.getCount()>0){
                    //Toast.makeText(getContext(),"Username already exists",Toast.LENGTH_SHORT).show();
                    AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
                    builder.setMessage("Username already exists.");
                    builder.setPositiveButton("OK",null);
                    builder.create().show();
                    cursor.close();
                    return;
                }
                // if same username doesn't exists then continue
                else{
                    cursor.close();
                }



                /**
                 * if the signed user is a provider insert row in provider table first
                 * because we need the inserted provider row's id to input in user table
                 */
                CheckBox isProviderCheckBox=getActivity().findViewById(R.id.is_provider_signup_check_box);
                int isProvider=-1;
                if(isProviderCheckBox.isChecked()){
                    Spinner typeSpinner=getActivity().findViewById(R.id.service_type_provider_signup_spinner);
                    //type string
                    String type=typeSpinner.getSelectedItem().toString();
                    //checking if the address is same as living or not
                    CheckBox sameAsLivingCheckbox=getActivity().findViewById(R.id.sames_as_living_provider_signup_checkbox);
                    String pPinCode,pLocality,pCity,pState,pCountry;
                    if(sameAsLivingCheckbox.isChecked()){
                        pPinCode=((EditText)getActivity().findViewById(R.id.pin_code_signup_edit_text))
                                .getText().toString();
                        pLocality=((EditText)getActivity().findViewById(R.id.locality_signup_edit_text))
                                .getText().toString().toUpperCase();
                        pCity=((EditText)getActivity().findViewById(R.id.city_signup_edit_text))
                                .getText().toString().toUpperCase();
                        pState=((EditText)getActivity().findViewById(R.id.state_signup_edit_text))
                                .getText().toString().toUpperCase();
                        pCountry=((EditText)getActivity().findViewById(R.id.country_signup_edit_text))
                                .getText().toString().toUpperCase();
                    }
                    else{
                        pPinCode=((EditText)getActivity().findViewById(R.id.pin_code_provider_signup_edit_text))
                                .getText().toString();
                        pLocality=((EditText)getActivity().findViewById(R.id.locality_provider_signup_edit_text))
                                .getText().toString().toUpperCase();
                        pCity=((EditText)getActivity().findViewById(R.id.city_provider_signup_edit_text))
                                .getText().toString().toUpperCase();
                        pState=((EditText)getActivity().findViewById(R.id.state_provider_signup_edit_text))
                                .getText().toString().toUpperCase();
                        pCountry=((EditText)getActivity().findViewById(R.id.country_provider_signup_edit_text))
                                .getText().toString().toUpperCase();
                    }
                    String company=((EditText)getActivity().findViewById(R.id.company_provider_signup_edit_text))
                            .getText().toString();

                    //checking if any empty input present and return if any present else continue
                    if(EmptyString.isAnyEmptyString(pPinCode,pLocality,pCity,pState,pCountry,company)){
                        //Toast.makeText(getContext(),"Empty input in provider section",Toast.LENGTH_SHORT).show();
                        AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
                        builder.setMessage("Empty input in provider section.");
                        builder.setPositiveButton("OK",null);
                        builder.create().show();
                        return;
                    }
                    /**
                     * setting up values for each column
                     */
                    ContentValues values=new ContentValues();
                    values.put(ProviderEntry.COLUMN_TYPE,type);
                    values.put(ProviderEntry.COLUMN_PIN_CODE,pPinCode);
                    values.put(ProviderEntry.COLUMN_LOCALITY,pLocality);
                    values.put(ProviderEntry.COLUMN_CITY,pCity);
                    values.put(ProviderEntry.COLUMN_STATE,pState);
                    values.put(ProviderEntry.COLUMN_COUNTRY,pCountry);
                    values.put(ProviderEntry.COLUMN_COMPANY_NAME,company);
                    //inserting row in provider table first
                    Uri uri=getContext().getContentResolver().insert(ProviderEntry.PROVIDER_CONTENT_URI,values);
                    Log.i("signup fragment","prov. insert uri="+uri);
                    isProvider= (int) ContentUris.parseId(uri);
                }
                /**
                 * inserting row in user table
                 */
                ContentValues values=new ContentValues();
                values.put(UserEntry.COLUMN_USERNAME,username);
                values.put(UserEntry.COLUMN_PASSWORD,password);
                values.put(UserEntry.COLUMN_FULL_NAME,fullName);
                values.put(UserEntry.COLUMN_CONTACT_NUMBER,contactNo);
                values.put(UserEntry.COLUMN_PIN_CODE,pinCode);
                values.put(UserEntry.COLUMN_HOUSE_NUMBER,houseNo);
                values.put(UserEntry.COLUMN_LOCALITY,locality);
                values.put(UserEntry.COLUMN_lANDMARK,landmark);
                values.put(UserEntry.COLUMN_CITY,city);
                values.put(UserEntry.COLUMN_STATE,state);
                values.put(UserEntry.COLUMN_COUNTRY,country);
                values.put(UserEntry.COLUMN_IS_PROVIDER,isProvider);

                Uri uri=getContext().getContentResolver().insert(UserEntry.USER_CONTENT_URI,values);
                Log.i("signup fragment","user. insert uri="+uri);

                //Toast.makeText(getContext(), "signed Up successfully", Toast.LENGTH_SHORT).show();
                showSuccessDialog();

            }
        });
        return rootView;
    }

    private void showSuccessDialog() {
        AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
        builder.setMessage("Signed up successfully. Kindly Login Again.");
        builder.setCancelable(false);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });
        AlertDialog dialog=builder.create();
        dialog.show();
    }
}
