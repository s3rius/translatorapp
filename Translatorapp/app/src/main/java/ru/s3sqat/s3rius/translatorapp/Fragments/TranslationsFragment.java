package ru.s3sqat.s3rius.translatorapp.Fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
public class TranslationsFragment extends Fragment {

    ArrayList<Card> lastCards = new ArrayList<>();

    public TranslationsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        for (int i = 0, size = AllCards.cards.size(); i < size; i++) {
            lastCards.add(new Card(AllCards.cards.get(i)));
        }
        return inflater.inflate(R.layout.fragment_translations, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.allCards);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(manager);
        CardsAdapter adapter = new CardsAdapter(getContext());
        recyclerView.setAdapter(adapter);
    }

    @Override
    public boolean getUserVisibleHint() {
        return super.getUserVisibleHint();
    }
}
