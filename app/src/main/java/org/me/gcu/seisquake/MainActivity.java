/**
 *  NAME - IKEOLUWA AJIBOLA ODUKUDU
 *  MATRIC NO - S1702414
 **/

package org.me.gcu.seisquake;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.me.gcu.seisquake.views.TimelineFragment;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class MainActivity extends AppCompatActivity{

    TimelineFragment timelineFragment = new TimelineFragment();
    private Fragment fragment = new Fragment();

    private SparseArray<androidx.fragment.app.Fragment.SavedState> savedStateSparseArray =
            new SparseArray<androidx.fragment.app.Fragment.SavedState>();

    private int currentSelectItemId = R.id.navigation_timeline;

    private String SAVED_STATE_SEARCH_KEY = "SearchKey";
    private String SAVED_STATE_CURRENT_TAB_KEY = "CurrentTabKey";
    private String SAVED_STATE_MAP_KEY = "MapKey";
    private String SAVED_TIMELINE_KEY = "TimelineKey";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(savedInstanceState !=null){
            Log.e("MyTag", "test saved");
            savedStateSparseArray = savedInstanceState.getSparseParcelableArray(SAVED_TIMELINE_KEY);
            currentSelectItemId = savedInstanceState.getInt(SAVED_STATE_CURRENT_TAB_KEY);
        }

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_timeline:
                        swapFragments(item.getItemId(), "TimelineFragment" );
                        break;
                    case R.id.navigation_search:
                        swapFragments(item.getItemId(), "SearchFragment");
                        break;
                    case R.id.navigation_map_view:
                        swapFragments(item.getItemId(), "MapFragment");
                        break;
                }
                return false;
            }
        });

        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_timeline, R.id.navigation_search, R.id.navigation_map_view).
                build();

        NavController navController = Navigation.findNavController(this, R.id.navHostFragment);

        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(bottomNavigationView, navController);

    }

    public static void triggerRebirth(Context context, Intent nextIntent) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("Restarting_Application", nextIntent);
        context.startActivity(intent);
        if (context instanceof Activity) {
            ((Activity) context).finish();
        }

        Runtime.getRuntime().exit(0);
    }

    @Override
    public void onBackPressed() {
        for (androidx.fragment.app.Fragment fragment : getSupportFragmentManager().getFragments()) {
            if(fragment != null && fragment.isVisible()){
                if(fragment.getChildFragmentManager().getBackStackEntryCount() > 0){
                    fragment.getChildFragmentManager().popBackStack();
                }
            }
        }
        super.onBackPressed();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSparseParcelableArray(SAVED_TIMELINE_KEY, savedStateSparseArray);
        outState.putInt(SAVED_STATE_CURRENT_TAB_KEY, currentSelectItemId);
    }

    private void swapFragments(int resId, String key){
        if(getSupportFragmentManager().findFragmentByTag(key) == null){
            savedFragmentState(resId);
            createFragment(key, resId);
        }
    }

    private void createFragment(String key, int resId) {
        timelineFragment = TimelineFragment.newInstance(key);
        timelineFragment.setInitialSavedState(savedStateSparseArray.get(resId));
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.navigation_timeline, timelineFragment, key).commit();
    }

    private void savedFragmentState(int resId) {
        androidx.fragment.app.Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.navigation_timeline);
        if (currentFragment != null){
            savedStateSparseArray.put(currentSelectItemId, getSupportFragmentManager().saveFragmentInstanceState(currentFragment));
        }
//        Log.e("MyTag", "test saved");
        currentSelectItemId = resId;
    }

}