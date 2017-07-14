package ru.s3sqat.s3rius.translatorapp.Fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;

import ru.s3sqat.s3rius.translatorapp.AllCards;
import ru.s3sqat.s3rius.translatorapp.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends Fragment {


    public SettingsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Switch autodetect = (Switch) view.findViewById(R.id.autDetectSwitch);
        autodetect.setChecked(AllCards.isAutoswap());
        Switch dictionary = (Switch) view.findViewById(R.id.dictionarySwitch);
        dictionary.setChecked(AllCards.isUseDictionary());
        Switch family = (Switch) view.findViewById(R.id.familySwitch);
        family.setChecked(AllCards.isFamilyFilter());
        Switch correspondence = (Switch) view.findViewById(R.id.correspondenceSwitch);
        correspondence.setChecked(AllCards.isPosFilter());
        Switch morphoSwitch = (Switch) view.findViewById(R.id.morphoSwitch);
        morphoSwitch.setChecked(AllCards.isMorpho());
        autodetect.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                AllCards.setAutoswap(isChecked);
            }
        });
        dictionary.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                AllCards.setUseDictionary(isChecked);
            }
        });
        family.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                AllCards.setFamilyFilter(isChecked);
            }
        });
        correspondence.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                AllCards.setPosFilter(isChecked);
            }
        });
        morphoSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                AllCards.setMorpho(isChecked);
            }
        });
    }
}
