package com.rakesh.demoapplication;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.rakesh.demoapplication.Utils.Utils;
import com.rakesh.demoapplication.customview.Backable;
import com.rakesh.demoapplication.fragment.AnimationActivity;
import com.rakesh.demoapplication.fragment.AplicatinTrackerFragment;
import com.rakesh.demoapplication.fragment.BrowserLayoutFragment;
import com.rakesh.demoapplication.fragment.CropFragment;
import com.rakesh.demoapplication.fragment.DelayAutocompleteFragment;
import com.rakesh.demoapplication.fragment.DevicePolicyFragment;
import com.rakesh.demoapplication.fragment.ImageProcessFragment;
import com.rakesh.demoapplication.fragment.SVGFragment;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    public DrawerLayout drawer;
    public MaterialSearchView mSearchView;
    ArrayList<Fragment> stackFragment = new ArrayList<>();
    ActionBarDrawerToggle toggle;
    Toolbar toolbar;
    private FragmentManager.OnBackStackChangedListener mBackStackChangedListener
            = new FragmentManager.OnBackStackChangedListener() {
        @Override
        public void onBackStackChanged() {
            updateDrawerToggle();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        getSupportFragmentManager().addOnBackStackChangedListener(mBackStackChangedListener);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            public void onDrawerClosed(View view) {

            }

            public void onDrawerOpened(View drawerView) {

            }
        };

        toggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        addFragment(new DelayAutocompleteFragment(), getString(R.string.app_name));
        final Snackbar snackBar = Snackbar.make(drawer, "Start App", Snackbar.LENGTH_LONG);
        snackBar.setAction("Dismiss", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackBar.dismiss();
            }
        });
        snackBar.show();
        Log.e(getClass().getSimpleName(), "On Create");
        mSearchView = (MaterialSearchView) findViewById(R.id.searchView);
        mSearchView.setVoiceSearch(true);

    }

    @Override
    public void setTitle(CharSequence title) {
        super.setTitle(title);
        toolbar.setTitle(title);
    }


    @Override
    public void onBackPressed() {
        Fragment currentFragment = Utils.getCurrentFragment(this);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (currentFragment != null && currentFragment instanceof BrowserLayoutFragment
                && ((Backable) currentFragment).onBackPressed()) {
            Toast.makeText(this, "Fragment back call", Toast.LENGTH_LONG).show();
        } else if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
                getSupportFragmentManager().popBackStackImmediate();
                getSupportFragmentManager().beginTransaction().commit();
                try {
                    stackFragment.remove(stackFragment.size() - 1);
                    Fragment fragment = stackFragment
                            .get(stackFragment.size() - 1);
                    fragment.onResume();
                    if (getSupportActionBar() != null)
                        getSupportActionBar().setTitle(fragment.getTag());
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } else {
                showExitDialog();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        getSupportFragmentManager().removeOnBackStackChangedListener(mBackStackChangedListener);
        Log.e(getClass().getSimpleName(), "On Pause");
    }

    @Override
    protected void onResume() {
        super.onResume();
        getSupportFragmentManager().addOnBackStackChangedListener(mBackStackChangedListener);
        Log.e(getClass().getSimpleName(), "On Resume");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e(getClass().getSimpleName(), "On Stop");
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.e(getClass().getSimpleName(), "On Re Start");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e(getClass().getSimpleName(), "On Destroy");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.e(getClass().getSimpleName(), "Option Menu Created");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        Log.e(getClass().getSimpleName(), "Option Menu Prepare");
        return super.onPrepareOptionsMenu(menu);
    }

    protected void updateDrawerToggle() {
        if (toggle == null || getSupportActionBar() == null) {
            return;
        }
        boolean isRoot = getSupportFragmentManager().getBackStackEntryCount() <= 1;
        toggle.setDrawerIndicatorEnabled(isRoot);
        getSupportActionBar().setDisplayShowHomeEnabled(!isRoot);
        getSupportActionBar().setDisplayHomeAsUpEnabled(!isRoot);
        getSupportActionBar().setHomeButtonEnabled(!isRoot);
        if (isRoot) {
            toggle.syncState();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        String title = item.getTitle().toString();
        Fragment fragment;
        if (id == R.id.nav_autocomplete) {
            fragment = new DelayAutocompleteFragment();
        } else if (id == R.id.nav_gallery) {
            fragment = new ImageProcessFragment();
        } else if (id == R.id.nav_tracker) {
            fragment = new AplicatinTrackerFragment();
        } else if (id == R.id.nav_animation) {
            fragment = new AnimationActivity();
        } else if (id == R.id.nav_svg_animation) {
            fragment = new SVGFragment();
        } else if (id == R.id.nav_admin) {
            fragment = new DevicePolicyFragment();
        } else if (id == R.id.nav_browser) {
            fragment = new BrowserLayoutFragment();
        } else if (id == R.id.nav_cropper) {
            fragment = new CropFragment();
        } else {
            fragment = new DelayAutocompleteFragment();
        }

        getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, fragment)
                .commit();//.addToBackStack(title)
        setTitle(title);
        stackFragment.clear();
        stackFragment.add(fragment);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void showExitDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Are you sure you want to Exit?");
        alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public void addFragment(Fragment frm, String title) {
        getSupportFragmentManager().beginTransaction().add(R.id.frame_container, frm, title)
                .addToBackStack(title).commit();
        stackFragment.add(frm);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
        }

    }


}
