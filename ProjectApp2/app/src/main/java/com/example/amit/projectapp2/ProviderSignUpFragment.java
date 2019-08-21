package com.example.amit.projectapp2;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;

/**
 * Created by AMIT on 23-03-2018.
 */

public class ProviderSignUpFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView=inflater.inflate(R.layout.fragment_provider_signup_layout,container,false);
        Spinner servicesSpinner=rootView.findViewById(R.id.service_type_provider_signup_spinner);
        ArrayAdapter servicesArrayAdapter= ArrayAdapter.createFromResource(getActivity(),R.array.services_list,
                android.R.layout.simple_spinner_item);
        servicesArrayAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
        servicesSpinner.setAdapter(servicesArrayAdapter);

        CheckBox sameAsLivingCheckbox=rootView.findViewById(R.id.sames_as_living_provider_signup_checkbox);
        sameAsLivingCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){

                    ((EditText)getActivity().findViewById(R.id.pin_code_provider_signup_edit_text))
                            .setVisibility(View.GONE);
                    ((EditText)getActivity().findViewById(R.id.locality_provider_signup_edit_text))
                            .setVisibility(View.GONE);
                    ((EditText)getActivity().findViewById(R.id.city_provider_signup_edit_text))
                            .setVisibility(View.GONE);
                    ((EditText)getActivity().findViewById(R.id.state_provider_signup_edit_text))
                            .setVisibility(View.GONE);
                    ((EditText)getActivity().findViewById(R.id.country_provider_signup_edit_text))
                            .setVisibility(View.GONE);
                }
                else{

                    ((EditText)getActivity().findViewById(R.id.pin_code_provider_signup_edit_text))
                            .setVisibility(View.VISIBLE);
                    ((EditText)getActivity().findViewById(R.id.locality_provider_signup_edit_text))
                            .setVisibility(View.VISIBLE);
                    ((EditText)getActivity().findViewById(R.id.city_provider_signup_edit_text))
                            .setVisibility(View.VISIBLE);
                    ((EditText)getActivity().findViewById(R.id.state_provider_signup_edit_text))
                            .setVisibility(View.VISIBLE);
                    ((EditText)getActivity().findViewById(R.id.country_provider_signup_edit_text))
                            .setVisibility(View.VISIBLE);

                }
            }
        });
        return rootView;
    }
}
