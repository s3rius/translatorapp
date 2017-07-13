package ru.s3sqat.s3rius.translatorapp;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import ru.s3sqat.s3rius.translatorapp.Fragments.TabsFragment;
import ru.s3sqat.s3rius.translatorapp.Fragments.Translator;

public class MainActivity extends AppCompatActivity {


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragment;
            android.support.v4.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            FragmentManager manager = getSupportFragmentManager();
            Fragment currentFragment = manager.findFragmentById(R.id.fragment_container);
            AllCards.pops = 0;
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    if (!(currentFragment instanceof Translator)) {
                        fragment = new Translator();
                        transaction.replace(R.id.fragment_container, fragment);
                        transaction.commit();
                    }
                    return true;
                case R.id.navigation_dashboard:
                    if (!(currentFragment instanceof TabsFragment)) {
                        fragment = new TabsFragment();
                        transaction.replace(R.id.fragment_container, fragment);
                        if (currentFragment instanceof Translator) {
                            transaction.addToBackStack("translator");
                            AllCards.pops = AllCards.pops + 1;
                        }
                        transaction.commit();
                    }
                    return true;
                case R.id.navigation_notifications:
                    return true;
            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        android.support.v4.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, new Translator());
        transaction.commit();
        AllCards.setSharedPreferences(getPreferences(MODE_PRIVATE));
        AllCards.loadValues();
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

}
