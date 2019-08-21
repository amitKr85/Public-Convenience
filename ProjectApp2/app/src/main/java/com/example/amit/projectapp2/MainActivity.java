package com.example.amit.projectapp2;

import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.amit.projectapp2.data.ServiceMgmtContract.*;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, LoginFragment.OnLoginClickedListener,
        RequestServiceFragment.OnRequestDoneListener{

    public static final String ARG_USER_ID="ARG_USER_ID";

    DrawerLayout mDrawerLayout;
    NavigationView mNavigationView;
    ActionBarDrawerToggle toggle;

    int mUserId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        mNavigationView.setNavigationItemSelectedListener(this);

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

        /**
         * checking if anybody already logged in
         */
        Cursor cursor = getContentResolver().query(CurrentIdEntry.CURRENT_ID_CONTENT_URI,
                null, null, null, null);
        if (cursor.getCount() > 0) {
            cursor.moveToNext();
            mUserId = cursor.getInt(cursor.getColumnIndex(CurrentIdEntry.COLUMN_USER_ID));
            setUpAtStart();
        } else {
            /**
             * locking navigation drawer and nav. button
             */
            mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            toggle.setDrawerIndicatorEnabled(false);

            /**
             * showing layout for login or signup
             */
            Fragment loginFragment = new LoginFragment();
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.main_fragment_container, loginFragment);
            transaction.commit();
        }
    }
    private void setUpAtStart() {
        Uri uri=ContentUris.withAppendedId(UserEntry.USER_CONTENT_URI,mUserId);
        String projection[]={UserEntry.COLUMN_FULL_NAME,UserEntry.COLUMN_USERNAME,UserEntry.COLUMN_IS_PROVIDER};
        Cursor cursor=getContentResolver().query(uri,projection,null,null,null);
        if(cursor.moveToNext()){
            String name=cursor.getString(cursor.getColumnIndex(UserEntry.COLUMN_FULL_NAME));
            String username="@"+cursor.getString(cursor.getColumnIndex(UserEntry.COLUMN_USERNAME));
            int isProvider=cursor.getInt(cursor.getColumnIndex(UserEntry.COLUMN_IS_PROVIDER));

            View navHeaderView=mNavigationView.getHeaderView(0);
            ((TextView)navHeaderView.findViewById(R.id.nav_name_text_view)).setText(name);
            ((TextView)navHeaderView.findViewById(R.id.nav_username_text_view)).setText(username);
            if(UserEntry.isProvider(isProvider)){
                ((TextView)navHeaderView.findViewById(R.id.nav_provider_user_text_view)).setText("Provider");
                /**
                 * if user is a provider then rename "pending request" to "pending appoint."
                 * and hide request for providers menu item
                 */
                mNavigationView.getMenu().findItem(R.id.nav_pending_appointments).setVisible(true);
            }else{
                ((TextView)navHeaderView.findViewById(R.id.nav_provider_user_text_view)).setText("User");
                /**
                 * if user is a service seeker then set back "pending request"
                 * and show request for providers menu item
                 */
                mNavigationView.getMenu().findItem(R.id.nav_pending_appointments).setVisible(false);
            }
        }
        MenuItem item=mNavigationView.getMenu().findItem(R.id.nav_search_for_providers);
        onNavigationItemSelected(item);
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            AlertDialog.Builder builder=new AlertDialog.Builder(this);
            builder.setMessage("Are you sure to exit ?");
            builder.setNegativeButton("cancel",null);
            builder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    MainActivity.super.onBackPressed();
                }
            });
            builder.create().show();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Log.i("main activity","onNavigationItemSelected");
        if (id == R.id.nav_pending_requests) {
            pendingRequests();
        } else if(id==R.id.nav_pending_appointments) {
            pendingAppointments();
        }else if (id == R.id.nav_history) {
            history();
        } else if (id == R.id.nav_request_service) {
            requestService();

        } else if (id == R.id.nav_search_for_providers) {
            Log.i("main activity","search");
            searchForProviders();

        } else if (id == R.id.nav_feedback) {
            Intent feedbackIntent=new Intent(this,FeedbackActivity.class);
            feedbackIntent.setData(ContentUris.withAppendedId(UserEntry.USER_CONTENT_URI,mUserId));
            startActivity(feedbackIntent);

        } else if (id == R.id.nav_logout) {
            logoutClicked();

        } else if (id == R.id.nav_about) {
            Intent aboutIntent=new Intent(this,AboutActivity.class);
            startActivity(aboutIntent);
        }

        item.setChecked(true);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void pendingAppointments() {

        setTitle("Pending Appointments");
        Fragment fragment=new PendingAppointmentsFragment();
        Bundle bundle=new Bundle();
        bundle.putInt(ARG_USER_ID,mUserId);
        fragment.setArguments(bundle);

        getSupportFragmentManager().beginTransaction().replace(R.id.main_fragment_container,fragment).commit();
    }

    private void history() {

        setTitle("History");
        Fragment fragment=new HistoryFragment();
        Bundle bundle=new Bundle();
        bundle.putInt(ARG_USER_ID,mUserId);
        fragment.setArguments(bundle);

        getSupportFragmentManager().beginTransaction().replace(R.id.main_fragment_container,fragment).commit();
    }

    private void pendingRequests() {

        setTitle("Pending Requests");
        Fragment fragment=new PendingRequestFragment();
        Bundle bundle=new Bundle();
        bundle.putInt(ARG_USER_ID,mUserId);
        fragment.setArguments(bundle);

        getSupportFragmentManager().beginTransaction().replace(R.id.main_fragment_container,fragment).commit();
    }

    private void requestService() {

        setTitle("Request Service");
        Fragment fragment=new RequestServiceFragment();
        Bundle bundle=new Bundle();
        bundle.putInt(ARG_USER_ID,mUserId);
        fragment.setArguments(bundle);

        getSupportFragmentManager().beginTransaction().replace(R.id.main_fragment_container,fragment).commit();
    }

    private void searchForProviders() {

        setTitle("Search Providers");
        Fragment fragment=new SearchForProvidersFragment();
        Bundle bundle=new Bundle();
        bundle.putInt(ARG_USER_ID,mUserId);
        fragment.setArguments(bundle);

        getSupportFragmentManager().beginTransaction().replace(R.id.main_fragment_container,fragment).commit();

    }

    private void logoutClicked() {
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setMessage("Are you sure to log out ?");
        builder.setNegativeButton("No",null);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                /**
                 * deleting user id from current_id table
                 */
                getContentResolver().delete(CurrentIdEntry.CURRENT_ID_CONTENT_URI,
                        null,null);
                /**
                 * disabling navigation drawer
                 */
                mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                toggle.setDrawerIndicatorEnabled(false);
                /**
                 * adding login fragment
                 */
                Fragment loginFragment = new LoginFragment();
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.main_fragment_container, loginFragment);
                transaction.commit();
            }
        });
        builder.create().show();
    }

    @Override
    public void onLoginClikedListener(int userId) {
        mUserId = userId;
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        toggle.setDrawerIndicatorEnabled(true);

        setUpAtStart();

    }

    @Override
    public void onRequestDone() {
        MenuItem item=mNavigationView.getMenu().findItem(R.id.nav_pending_requests);
        onNavigationItemSelected(item);

    }
}
