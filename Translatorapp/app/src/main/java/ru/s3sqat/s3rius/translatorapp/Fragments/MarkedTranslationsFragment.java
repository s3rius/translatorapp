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
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.markedCards);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(AllCards.bookmarkAdapter);
    }

//    @Override
//    public boolean getUserVisibleHint() {
//        if (super.getUserVisibleHint()) {
//            if (AllCards.cards.size() == lastCards.size()) {
//                for (int i = 0; i < lastCards.size(); i++) {
//                    Card newCard = AllCards.cards.get(i);
//                    Card lastCard = lastCards.get(i);
//                    if (!newCard.equals(lastCard)) {
//                        adapter.notifyItemChanged(i);
//                    }
//                }
//                for (int i = 0, size = AllCards.cards.size(); i < size; i++) {
//                    lastCards.add(new Card(AllCards.cards.get(i)));
//                }
//            }
//        } else {
//
//        }
//        return super.getUserVisibleHint();
//    }

//    @Override
//    public void setUserVisibleHint(boolean isVisibleToUser) {
//        if (isVisibleToUser) {
//            if (AllCards.cards.size() == lastCards.size()) {
//                for (int i = 0, size = lastCards.size(); i < size; i++) {
//                    Card newCard = AllCards.cards.get(i);
//                    Card lastCard = lastCards.get(i);
//                    if (!newCard.equals(lastCard)) {
//                        adapter.notifyItemChanged(i);
//                        lastCards.clear();
//                        for (int j = 0; j < size; j++) {
//                            lastCards.add(new Card(AllCards.cards.get(j)));
//                        }
//                        break;
//                    }
//                }
//
//            } else {
//                for (int i = 0, size = lastCards.size(); i < size; i++) {
//                    Card newCard = AllCards.cards.get(i);
//                    Card lastCard = lastCards.get(i);
//                    if (!newCard.equals(lastCard)) {
//                        adapter.notifyItemRemoved(i);
//                        lastCards.clear();
//                        for (int j = 0; j < size; j++) {
//                            lastCards.add(new Card(AllCards.cards.get(j)));
//                        }
//                        break;
//                    }
//                }
//            }
//        }
//        super.setUserVisibleHint(isVisibleToUser);
//    }

}
