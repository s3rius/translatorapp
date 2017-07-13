package ru.s3sqat.s3rius.translatorapp.Fragments;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pair;
import android.text.Editable;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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
import ru.s3sqat.s3rius.translatorapp.AllCards;
import ru.s3sqat.s3rius.translatorapp.Card;
import ru.s3sqat.s3rius.translatorapp.EditText2;
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
    EditText2 textBox;
    TextView translation;
    SharedPreferences sPrefs;
    ImageView bookmark;
    LinearLayout dictionary;
    boolean linked;

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
        textBox = (EditText2) view.findViewById(R.id.textToTranslate);
        translation = (TextView) view.findViewById(R.id.translation);
        sPrefs = getActivity().getPreferences(Context.MODE_PRIVATE);
        bookmark = (ImageView) view.findViewById(R.id.bookmarkTranslate);
        dictionary = (LinearLayout) view.findViewById(R.id.dictionaryLayout);
        RelativeLayout layout = (RelativeLayout) view.findViewById(R.id.focusLayout);
        layout.setFocusable(true);
        layout.setFocusableInTouchMode(true);
//        layout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (textBox.hasFocus()) {
//                    textBox.clearFocus();
//                }
//            }
//        });
//        layout.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                if (hasFocus) {
//                    if (textBox.hasFocus()) {
//                        textBox.clearFocus();
//                    }
//                }
//            }
//        });
        linked = false;
        translation.setVisibility(View.GONE);
        bookmark.setVisibility(View.GONE);
        bookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int markedIndex = AllCards.changeMark(textBox.getText().toString(), requestFrom, requestTo);
                if (markedIndex != -1) {
                    if (AllCards.cards.get(markedIndex).isMark()) {
                        bookmark.setImageDrawable(getResources().getDrawable(R.drawable.bookmark_yellow, null));
                    } else {
                        bookmark.setImageDrawable(getResources().getDrawable(R.drawable.bookmark, null));
                    }
                } else {
                    Toast.makeText(getContext(), getString(R.string.cantMark), Toast.LENGTH_SHORT).show();
                }
            }
        });
        swapLangsListener();
        getLanguages();
        setTranslateListeners();
        setFocusListener();
    }

    public TextView createLink(TextView targetTextView, String completeString,
                               ArrayList<String> partsToClick) {

        SpannableString spannableString = new SpannableString(completeString);

        // make sure the String is exist, if it doesn't exist
        // it will throw IndexOutOfBoundException
        for (final String partToClick : partsToClick) {
            int startPosition = completeString.indexOf(partToClick);
            int endPosition = startPosition + partToClick.length();
            spannableString.setSpan(new ClickableSpan() {
                                        @Override
                                        public void onClick(View widget) {
                                            addCard();
                                            int swap = spinnerFrom.getSelectedItemPosition();
                                            spinnerFrom.setSelection(spinnerTo.getSelectedItemPosition());
                                            spinnerTo.setSelection(swap);
                                            linked = true;
                                            textBox.setText(partToClick);
                                        }

                                        @Override
                                        public void updateDrawState(TextPaint ds) {
                                            super.updateDrawState(ds);
                                            int linkColor = ContextCompat.getColor(getActivity(), R.color.blumine);
                                            ds.setColor(linkColor);
                                            ds.setUnderlineText(false);
                                        }

                                    }, startPosition, endPosition,
                    Spanned.SPAN_INCLUSIVE_EXCLUSIVE);

        }

        targetTextView.setText(spannableString);
        targetTextView.setMovementMethod(LinkMovementMethod.getInstance());

        return targetTextView;
    }

    private void setFocusListener() {
        textBox.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    Toast.makeText(getContext(), "Unfocused", Toast.LENGTH_SHORT).show();
                    if (!isEmptyOrNull(textBox.getText().toString())) {
                        addCard();
                    }
                } else {
                    Toast.makeText(getContext(), "Focused", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void addCard() {
        AllCards.addCard(new Card(textBox.getText().toString().trim(),
                translation.getText().toString().trim(),
                requestFrom,
                requestTo,
                false));
    }

    private void swapLangsListener() {
        ImageView imageView = (ImageView) getView().findViewById(R.id.swapImg);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int swap = spinnerFrom.getSelectedItemPosition();
                spinnerFrom.setSelection(spinnerTo.getSelectedItemPosition());
                spinnerTo.setSelection(swap);
                if (!isEmptyOrNull(translation.getText().toString())) {
                    textBox.setText(translation.getText().toString());
                }
            }
        });
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
        params.put("options", AllCards.getAutodetect());
        translateConnect.post(getString(R.string.translate), params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                final String response = new String(responseBody);
                JSONObject object;
                String translate = "";
                String detected = "";
                try {
                    object = new JSONObject(response);
                    JSONArray text = object.getJSONArray("text");
                    detected = object.getJSONObject("detected").getString("lang");
                    for (int i = 0; i < text.length(); i++) {
                        translate += text.getString(i);
                    }
                    translation.setText(translate);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (AllCards.isAutoswap()) {
                    if (!detected.equals(requestFrom)) {
                        spinnerFrom.setSelection(requestLangs.indexOf(detected));
                        return;
                    }
                }
                addCard();
                if (AllCards.isUseDictionary()) {
                    if (!linked) {
                        RequestParams requestParams = new RequestParams();
                        requestParams.put("key", getString(R.string.dictionaryKey));
                        requestParams.put("lang", lastRequest);
                        requestParams.put("text", s.toString());
                        requestParams.put("ui", locale);
                        requestParams.put("flags", AllCards.getFlags());
                        dictConnect.get(getString(R.string.lookUp), requestParams, new AsyncHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                try {
                                    refillDictionaryView(new String(responseBody));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                                if (dictionary.getChildCount() > 0)
                                    dictionary.removeAllViews();
                            }
                        });
                    } else
                        linked = false;
                }
                translation.setVisibility(View.VISIBLE);
                bookmark.setVisibility(View.VISIBLE);
                int index = AllCards.indexOf(textBox.getText().toString(), requestFrom, requestTo);
                if (index != -1) {
                    if (AllCards.cards.get(index).isMark()) {
                        bookmark.setImageDrawable(getResources().getDrawable(R.drawable.bookmark_yellow, null));
                    } else {
                        bookmark.setImageDrawable(getResources().getDrawable(R.drawable.bookmark, null));
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                translation.setVisibility(View.GONE);
                bookmark.setVisibility(View.GONE);
                if (dictionary.getChildCount() > 0)
                    dictionary.removeAllViews();
            }
        });
    }

    private void refillDictionaryView(String s) throws JSONException {
        JSONObject body = new JSONObject(s);
        JSONArray def = body.getJSONArray("def");
        if (dictionary.getChildCount() > 0)
            dictionary.removeAllViews();
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        for (int i = 0, defLength = def.length(); i < defLength; i++) {
            View titleView = inflater.inflate(R.layout.dictionary_title, null);
            JSONObject object = def.getJSONObject(i);
            TextView title = (TextView) titleView.findViewById(R.id.dictionaryTitle);
            if (i == 0) {
                String first = object.getString("text");
                String next = "";
                if (object.has("ts"))
                    next = " <font color='#9e9d9d'>[" + object.getString("ts") + "]</font>";
                title.setText(Html.fromHtml(first + next));
            } else {
                if (object.getString("text").equals(def.getJSONObject(0).getString("text")))
                    title.setVisibility(View.GONE);
                else {
                    String first = object.getString("text");
                    String next = "";
                    if (object.has("ts"))
                        next = " <font color='#9e9d9d'>[" + object.getString("ts") + "]</font>";
                    title.setText(Html.fromHtml(first + next));
                }
            }
            TextView pos = (TextView) titleView.findViewById(R.id.dictionaryTitlePos);
            if (object.has("pos"))
                pos.setText(object.getString("pos"));
            else
                pos.setVisibility(View.GONE);
            dictionary.addView(titleView);
            JSONArray tr = object.getJSONArray("tr");
            for (int j = 0, trLength = tr.length(); j < trLength; j++) {
                String dictTr = "";
                JSONObject trJson = tr.getJSONObject(j);
                View trView = inflater.inflate(R.layout.dictionary_tr, null);
                TextView count = (TextView) trView.findViewById(R.id.dictionaryCount);
                count.setText(String.valueOf(j + 1));
                TextView syn = (TextView) trView.findViewById(R.id.syn);
                dictTr += trJson.getString("text");
                if (trJson.has("gen"))
                    dictTr += " " + trJson.getString("gen");
                ArrayList<String> links = new ArrayList<>();
                links.add(trJson.getString("text"));
                if (trJson.has("syn")) {
                    JSONArray synJ = trJson.getJSONArray("syn");
                    for (int k = 0, synJSize = synJ.length(); k < synJSize; k++) {
                        if (k == 0) {
                            dictTr += ", ";
                        }
                        JSONObject synJs = synJ.getJSONObject(k);
                        links.add(synJs.getString("text"));
                        dictTr += synJs.getString("text");
                        if (synJs.has("gen"))
                            dictTr += " " + synJs.getString("gen");
                        if (i != synJ.length() - 1)
                            dictTr += ", ";
                    }
                }
                createLink(syn, dictTr, links);
                TextView mean = (TextView) trView.findViewById(R.id.mean);
                if (trJson.has("mean")) {
                    String meanText = "(";
                    JSONArray meanings = trJson.getJSONArray("mean");
                    for (int k = 0, meanSize = meanings.length(); k < meanSize; k++) {
                        meanText += meanings.getJSONObject(k).getString("text");
                        if (k != meanSize - 1) {
                            meanText += ", ";
                        }
                    }
                    meanText += ")";
                    mean.setText(meanText);
                } else {
                    mean.setVisibility(View.GONE);
                }
                TextView exTextView = (TextView) trView.findViewById(R.id.ex);
                if (trJson.has("ex")) {
                    JSONArray exArray = trJson.getJSONArray("ex");
                    String exString = "";
                    for (int k = 0, exArrayLength = exArray.length(); k < exArrayLength; k++) {
                        JSONObject exObject = exArray.getJSONObject(k);
                        exString += exObject.getString("text");
                        if (exObject.has("tr")) {
                            exString += " - " + exObject.getJSONArray("tr").getJSONObject(0).getString("text") + "\n";
                        } else {
                            exString += "\n";
                        }
                    }
                    exTextView.setText(exString);
                } else {
                    exTextView.setVisibility(View.GONE);
                }
                dictionary.addView(trView);
            }
        }
    }


    public void getLanguages() {
        RequestParams params = new RequestParams();
        final ArrayList<Pair<String, String>> languagesPairs = new ArrayList<>();
        params.put("key", getString(R.string.translateKey));
        params.put("ui", locale);
        translateConnect.post(getString(R.string.getLangs), params, new AsyncHttpResponseHandler() {

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
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
                if (!isEmptyOrNull(textBox.getText().toString())) {
                    translate(textBox.getText());
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinnerTo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                requestTo = requestLangs.get(position);
                if (!isEmptyOrNull(textBox.getText().toString())) {
                    translate(textBox.getText());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public boolean isEmptyOrNull(CharSequence sequence) {
        return sequence == null || sequence.toString().trim().isEmpty();
    }

}
