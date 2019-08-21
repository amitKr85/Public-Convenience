package com.example.amit.projectapp2;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.amit.projectapp2.data.ServiceMgmtContract.*;

/**
 * Created by AMIT on 22-03-2018.
 */

public class LoginFragment extends Fragment {

    public interface OnLoginClickedListener{
        public void onLoginClikedListener(int userId);
    }
    OnLoginClickedListener mListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (OnLoginClickedListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnLoginClickedListener");
        }

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        /*
            inflating layout from fragment_login_layout to show login fragment
         */
        View rootView=inflater.inflate(R.layout.fragment_login_layout,container,false);
        /*
            setting up onclick listener object for buttons
         */
        View.OnClickListener onClickListener=new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(v.getId()==R.id.login_button)
                    loginButtonClicked(v);
                if(v.getId()==R.id.registeration_button)
                    signupButtonClicked(v);
            }
        };
        /*
            finding and binding onclick listener on login and new registration buttons
         */
        Button loginButton=rootView.findViewById(R.id.login_button);
        loginButton.setOnClickListener(onClickListener);
        Button signupButton=rootView.findViewById(R.id.registeration_button);
        signupButton.setOnClickListener(onClickListener);
        /*
            after all setting up returning layout
         */
        return rootView;
    }

    public void loginButtonClicked(View v){
       // Toast.makeText(getActivity(), "loginbutton clicked", Toast.LENGTH_SHORT).show();
        String username=((EditText)getActivity().findViewById(R.id.username_login_edit_text)).getText().toString();
        String password=((EditText)getActivity().findViewById(R.id.password_login_edit_text)).getText().toString();

        String projection[]={UserEntry._ID};
        String selection= UserEntry.COLUMN_USERNAME+"=? AND "+UserEntry.COLUMN_PASSWORD+"=?";
        String selectionArgs[]={username,password};

        Cursor cursor=getContext().getContentResolver().query(UserEntry.USER_CONTENT_URI,
                projection,selection,selectionArgs,null);

        if(cursor.getCount()>0){
            cursor.moveToNext();
            int userId=cursor.getInt(cursor.getColumnIndex(UserEntry._ID));
            Log.i("Login Fragment","user id="+userId);
            cursor.close();
            /**
             * deleting previous any stored id and inserting user id in current_user_id table
             * if login executed successfully
             */
            getActivity().getContentResolver().delete(CurrentIdEntry.CURRENT_ID_CONTENT_URI,
                    null,null);
            ContentValues values=new ContentValues();
            values.put(CurrentIdEntry.COLUMN_USER_ID,userId);
            getActivity().getContentResolver().insert(CurrentIdEntry.CURRENT_ID_CONTENT_URI,values);

            /**
             * removing login fragment from layout
             */
            Fragment loginFragment=getActivity().getSupportFragmentManager().findFragmentById(R.id.main_fragment_container);
            getActivity().getSupportFragmentManager().beginTransaction().remove(loginFragment).commit();

            /**
             * going back to main activity
             */
            mListener.onLoginClikedListener(userId);
        }
        else{
            cursor.close();
            //Toast.makeText(getContext(),"Incorrect Username / Password",Toast.LENGTH_SHORT).show();
            AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
            builder.setMessage("Incorrect Username / Password");
            builder.setPositiveButton("OK",null);
            builder.create().show();
        }
    }
    public void signupButtonClicked(View v){
        /**
         *showing and replacing current login fragment with signup fragment
         */
        Fragment signUpFragment=new SignUpFragment();
        FragmentManager fragmentManager=getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();

        /*
            adding animation for signup fragment both for entering and exiting
         */
        fragmentTransaction.setCustomAnimations(R.anim.enter_from_right_anim,R.anim.exit_to_left_anim,
                R.anim.enter_from_left_anim,R.anim.exit_to_right_anim);

        fragmentTransaction.replace(R.id.main_fragment_container,signUpFragment);
        /**
         * addToBackStack causes users to show previous fragment when user press back button
         */
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}
