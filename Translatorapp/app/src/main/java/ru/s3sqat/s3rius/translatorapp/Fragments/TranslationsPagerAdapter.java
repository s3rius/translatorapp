package ru.s3sqat.s3rius.translatorapp.Fragments;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import ru.s3sqat.s3rius.translatorapp.R;

/**
 * Created by s3rius on 13.07.17.
 */

public class TranslationsPagerAdapter extends FragmentStatePagerAdapter {
    Context context;

    public TranslationsPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment tab = null;
        switch (position) {
            case 0:
                tab = new TranslationsFragment();
                break;
            case 1:
                tab = new MarkedTranslationsFragment();
                break;
        }
        return tab;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        String title;
        switch (position) {
            case 0:
                title = context.getResources().getString(R.string.translations);
                break;
            case 1:
                title = context.getResources().getString(R.string.bookmarks);
                break;
        }
        return super.getPageTitle(position);
    }
}
