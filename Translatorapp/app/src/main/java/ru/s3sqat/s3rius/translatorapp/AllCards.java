package ru.s3sqat.s3rius.translatorapp;

import android.content.Context;
import android.content.SharedPreferences;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import ru.s3sqat.s3rius.translatorapp.Fragments.BookmarkAdapter;
import ru.s3sqat.s3rius.translatorapp.Fragments.CardsAdapter;

/**
 * Created by s3rius on 13.07.17.
 */

public class AllCards {
    public static ArrayList<Card> cards;
    public static BookmarkAdapter bookmarkAdapter;
    public static CardsAdapter cardsAdapter;
    public static boolean detectHint;
    private static boolean useDictionary;
    private static ObjectMapper mapper = new ObjectMapper();
    private static SharedPreferences sharedPreferences;
    private static int autodetect;
    private static boolean autoswap;
    private static boolean familyFilter;
    private static boolean morpho;
    private static boolean posFilter;
    private static int flags;
    private static Context context;

    public static boolean isDetectHint() {
        return detectHint;
    }

    public static void setDetectHint(boolean detectHint) {
        AllCards.detectHint = detectHint;
        saveFlags();
    }

    public static Context getContext() {
        return context;
    }

    public static void setContext(Context context) {
        AllCards.context = context;
        sharedPreferences = ((MainActivity) context).getPreferences(Context.MODE_PRIVATE);
    }

    private static void saveCards() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        try {
            editor.putString("cards", mapper.writeValueAsString(cards));
            editor.apply();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    private static void saveFlags() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("morpho", morpho);
        editor.putBoolean("autoSwap", autoswap);
        editor.putBoolean("useDictionary", useDictionary);
        editor.putBoolean("familyFilter", familyFilter);
        editor.putBoolean("morpho", morpho);
        editor.putBoolean("posFilter", posFilter);
        editor.putBoolean("detectHint", detectHint);
        editor.apply();
    }

    static void loadValues() {
        cards = new ArrayList<>();
        if (sharedPreferences.contains("cards")) {
            try {
                cards = mapper.readValue(sharedPreferences.getString("cards", ""), new TypeReference<ArrayList<Card>>() {
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        autodetect = sharedPreferences.getInt("autoDetect", 1);
        autoswap = sharedPreferences.getBoolean("autoSwap", true);
        useDictionary = sharedPreferences.getBoolean("useDictionary", true);
        familyFilter = sharedPreferences.getBoolean("familyFilter", true);
        morpho = sharedPreferences.getBoolean("morpho", true);
        posFilter = sharedPreferences.getBoolean("posFilter", false);
        detectHint = sharedPreferences.getBoolean("detectHint", true);
        createMask();
        bookmarkAdapter = new BookmarkAdapter();
        cardsAdapter = new CardsAdapter();
    }

    public static void notifyBookmarkChanged(int position) {
        bookmarkAdapter.notifyItemChanged(position);
    }

    public static void notifyBookmarkRemoved(int position) {
        bookmarkAdapter.notifyItemRemoved(position);
    }

    public static void notifyCardChanged(int position) {
        cardsAdapter.notifyItemChanged(position);
    }

    public static void notifyCardRemoved(int position) {
        cardsAdapter.notifyItemRemoved(position);
    }

    private static void createMask() {
        flags = 0;
        if (familyFilter)
            flags += 1;
        if (morpho)
            flags += 4;
        if (posFilter)
            flags += 8;
    }

    public static boolean isFamilyFilter() {
        return familyFilter;
    }

    public static void setFamilyFilter(boolean familyFilter) {
        AllCards.familyFilter = familyFilter;
        saveFlags();
        createMask();
    }

    public static boolean isMorpho() {
        return morpho;
    }

    public static void setMorpho(boolean morpho) {
        AllCards.morpho = morpho;
        saveFlags();
        createMask();
    }

    public static boolean isPosFilter() {
        return posFilter;
    }

    public static void setPosFilter(boolean posFilter) {
        AllCards.posFilter = posFilter;
        saveFlags();
        createMask();
    }

    public static int getFlags() {
        return flags;
    }

    public static void addCard(Card card) {
        boolean swapped = false;
        for (int i = 0, size = cards.size(); i < size; i++) {
            Card c = cards.get(i);
            if (c.getText().equals(card.getText())) {
                if (c.getDirectionFrom().equals(card.getDirectionFrom()) && c.getDirectionTo().equals(card.getDirectionTo())) {
                    swapped = true;
                    putCardOnTop(i);
                    break;
                }
            }
        }
        if (!swapped) {
            cards.add(0, card);
            saveCards();
        }
    }

    private static void putCardOnTop(int index) {
        Collections.swap(cards, 0, index);
        saveCards();
    }

    public static void removeCard(int index) {
        cards.remove(index);
        saveCards();
    }

    public static void clearCards() {
        cards.clear();
        saveCards();
    }

    public static void setMark(String text, String directionFrom, String directionTo, boolean mark) {
        for (int i = 0, size = cards.size(); i < size; i++) {
            Card card = cards.get(i);
            if (card.getText().equals(text) &&
                    card.getDirectionFrom().equals(directionFrom) &&
                    card.getDirectionTo().equals(directionTo)) {
                cards.get(i).setMark(mark);
                break;
            }
        }
        saveCards();
    }

    public static int getAutodetect() {
        return autodetect;
    }

    public static boolean isAutoswap() {
        return autoswap;
    }

    public static void setAutoswap(boolean autoswap) {
        AllCards.autoswap = autoswap;
        saveFlags();
    }

    public static boolean isUseDictionary() {
        return useDictionary;
    }

    public static void setUseDictionary(boolean useDictionary) {
        AllCards.useDictionary = useDictionary;
        saveFlags();
    }

    public static void changeMark(int position) {
        cards.get(position).changeMark();
        saveCards();
    }

    public static int changeMark(String text, String directionFrom, String directionTo) {
        int index = indexOf(text, directionFrom, directionTo);
        if (index != -1) {
            cards.get(index).changeMark();
            saveCards();
        }
        return index;
    }

    public static int indexOf(String text, String directionFrom, String directionTo) {
        int index = -1;
        for (int i = 0, size = cards.size(); i < size; i++) {
            Card card = cards.get(i);
            if (card.getText().equals(text) &&
                    card.getDirectionFrom().equals(directionFrom) &&
                    card.getDirectionTo().equals(directionTo)) {
                index = i;
                break;
            }
        }
        return index;
    }

    public static boolean isMarked(String text, String directionFrom, String directionTo) {
        boolean marked = false;
        for (int i = 0, size = cards.size(); i < size; i++) {
            Card card = cards.get(i);
            if (card.getText().equals(text) &&
                    card.getDirectionFrom().equals(directionFrom) &&
                    card.getDirectionTo().equals(directionTo)) {
                marked = true;
                break;
            }
        }
        return marked;
    }
}
