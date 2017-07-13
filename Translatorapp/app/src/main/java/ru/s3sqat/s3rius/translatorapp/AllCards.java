package ru.s3sqat.s3rius.translatorapp;

import android.content.SharedPreferences;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by s3rius on 13.07.17.
 */

public class AllCards {
    public static ArrayList<Card> cards;
    private static boolean useDictionary;
    private static ObjectMapper mapper = new ObjectMapper();
    private static SharedPreferences sharedPreferences;
    private static int autodetect;
    private static boolean autoswap;
    private static boolean familyFilter;
    private static boolean morpho;
    private static boolean posFilter;
    private static int flags;
    public static int pops;

    public static void saveCards() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        try {
            editor.putString("cards", mapper.writeValueAsString(cards));
            editor.apply();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    public static void setSharedPreferences(SharedPreferences sharedPreferences) {
        AllCards.sharedPreferences = sharedPreferences;
    }

    public static void loadValues() {
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
        createMask();
    }

    public static void createMask() {
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
        createMask();
    }

    public static boolean isMorpho() {
        return morpho;
    }

    public static void setMorpho(boolean morpho) {
        AllCards.morpho = morpho;
        createMask();
    }

    public static boolean isPosFilter() {
        return posFilter;
    }

    public static void setPosFilter(boolean posFilter) {
        AllCards.posFilter = posFilter;
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

    public static boolean isUseDictionary() {
        return useDictionary;
    }

    public static void changeMark(int position) {
        cards.get(position).changeMark();
        saveCards();
    }

    public static int changeMark(String text, String directionFrom, String directionTo) {
        int index = indexOf(text, directionFrom, directionTo);
        cards.get(index).changeMark();
        saveCards();
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
