package ru.s3sqat.s3rius.translatorapp.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import ru.s3sqat.s3rius.translatorapp.AllCards;
import ru.s3sqat.s3rius.translatorapp.Card;
import ru.s3sqat.s3rius.translatorapp.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class MarkedTranslationsFragment extends Fragment {


    private ArrayList<Card> lastCards = new ArrayList<>();

    public MarkedTranslationsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        for (int i = 0, size = AllCards.cards.size(); i < size; i++) {
            lastCards.add(new Card(AllCards.cards.get(i)));
        }
        return inflater.inflate(R.layout.fragment_marked_translations, container, false);
    }

    @Override
    public boolean getUserVisibleHint() {
        return super.getUserVisibleHint();

    }
}
