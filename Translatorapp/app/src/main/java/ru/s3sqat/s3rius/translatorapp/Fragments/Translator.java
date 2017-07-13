package ru.s3sqat.s3rius.translatorapp.Fragments;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;
import ru.s3sqat.s3rius.translatorapp.MainActivity;
import ru.s3sqat.s3rius.translatorapp.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class Translator extends Fragment {

    ViewGroup container;
    String locale;
    AsyncHttpClient translateConnect = new AsyncHttpClient();
    AsyncHttpClient dictConnect = new AsyncHttpClient();
    ArrayList<String> languages;
    ArrayList<String> requestLangs;
    Spinner spinnerFrom;
    Spinner spinnerTo;
    int startTranslateTo;
    String requestFrom;
    String requestTo;
    String lastRequest;
    EditText textBox;
    TextView translation;
    SharedPreferences sPrefs;
    ImageView bookmark;
    ScrollView dictionary;

    public Translator() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        this.container = container;
        return inflater.inflate(R.layout.fragment_translator, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        locale = Locale.getDefault().getLanguage();
        textBox = (EditText) view.findViewById(R.id.textToTranslate);
        translation = (TextView) view.findViewById(R.id.translation);
        sPrefs = ((MainActivity) getActivity()).getPreferences(Context.MODE_PRIVATE);
        bookmark = (ImageView) view.findViewById(R.id.bookmarkTranslate);
        dictionary = (ScrollView) view.findViewById(R.id.dictionary);
        translation.setVisibility(View.GONE);
        bookmark.setVisibility(View.GONE);
        getLanguages();
        setTranslateListeners();
    }

    private void setTranslateListeners() {
        textBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                translate(s);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void translate(final CharSequence s) {
        RequestParams params = new RequestParams();
        params.put("key", getString(R.string.translateKey));
        params.put("text", s.toString());
        lastRequest = requestFrom + "-" + requestTo;
        params.put("lang", lastRequest);
        int autoDetect = sPrefs.getInt("autoDetect", 1);
        params.put("options", autoDetect);
        translateConnect.post(getString(R.string.translate), params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                final String response = new String(responseBody);
                JSONObject object;
                String translate = "";
                try {
                    object = new JSONObject(response);
                    JSONArray text = object.getJSONArray("text");
                    for (int i = 0; i < text.length(); i++) {
                        translate += text.getString(i);
                    }
                    translation.setText(translate);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                boolean useDictionary = sPrefs.getBoolean("useDictionary", true);
                if (useDictionary) {
                    RequestParams requestParams = new RequestParams();
                    requestParams.put("key", getString(R.string.dictionaryKey));
                    requestParams.put("lang", lastRequest);
                    requestParams.put("text", s.toString());
                    dictConnect.get(getString(R.string.lookUp), requestParams, new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                            refillDictionaryView(new String(responseBody));
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                            dictionary.setVisibility(View.GONE);
                        }
                    });
                }
                translation.setVisibility(View.VISIBLE);
                bookmark.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                translation.setVisibility(View.GONE);
                bookmark.setVisibility(View.GONE);
            }
        });
    }

    private void refillDictionaryView(String s) {

    }

    public void getLanguages() {
        RequestParams params = new RequestParams();
        final ArrayList<Pair<String, String>> languagesPairs = new ArrayList<>();
        params.put("key", getString(R.string.translateKey));
        params.put("ui", locale);
        final boolean[] ret = {false};
        translateConnect.post(getString(R.string.getLangs), params, new AsyncHttpResponseHandler() {

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                ret[0] = false;
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String responce = new String(responseBody);
                try {
                    JSONObject jsonObject = new JSONObject(responce);
                    JSONObject langs = jsonObject.getJSONObject("langs");
                    Iterator<String> keys = langs.keys();
                    while (keys.hasNext()) {
                        String key = keys.next();
                        languagesPairs.add(new Pair<>(key, langs.getString(key)));
                    }
                    Collections.sort(languagesPairs, new Comparator<Pair<String, String>>() {
                        @Override
                        public int compare(Pair<String, String> o1, Pair<String, String> o2) {
                            return o1.second.compareTo(o2.second);
                        }
                    });
                    normalizeLangs(languagesPairs);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void normalizeLangs(ArrayList<Pair<String, String>> languagesPairs) {
        languages = new ArrayList<>();
        requestLangs = new ArrayList<>();
        for (Pair<String, String> langPair :
                languagesPairs) {
            if (langPair.first.equals(locale))
                startTranslateTo = languages.size();
            requestLangs.add(langPair.first);
            languages.add(langPair.second);
        }
        fillSpinners();
    }

    private void fillSpinners() {
        spinnerFrom = (Spinner) getView().findViewById(R.id.spinnerFrom);
        spinnerTo = (Spinner) getView().findViewById(R.id.spinnerTo);
        final ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(
                getContext(), R.layout.spinner_item, languages);
        spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_item);
        spinnerFrom.setAdapter(spinnerArrayAdapter);
        spinnerTo.setAdapter(spinnerArrayAdapter);
        spinnerTo.setSelection(startTranslateTo);
        spinnerFrom.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                requestFrom = requestLangs.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinnerTo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                requestTo = requestLangs.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
}
