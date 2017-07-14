package ru.s3sqat.s3rius.translatorapp;

/**
 * Created by s3rius on 13.07.17.
 */

public class Card {
    private String text;
    private String translate;
    private String directionFrom;
    private String directionTo;
    private boolean mark;

    public Card() {

    }

    public Card(String text, String translate, String directionFrom, String directionTo, boolean mark) {
        this.text = text;
        this.translate = translate;
        this.directionFrom = directionFrom;
        this.directionTo = directionTo;
        this.mark = mark;
    }

    public Card(Card card) {
        text = card.getText();
        translate = card.getTranslate();
        directionFrom = card.getDirectionFrom();
        directionTo = card.getDirectionTo();
        mark = card.isMark();
    }

    public String getTranslate() {
        return translate;
    }

    public void setTranslate(String translate) {
        this.translate = translate;
    }

    public String getDirectionFrom() {
        return directionFrom;
    }

    public void setDirectionFrom(String directionFrom) {
        this.directionFrom = directionFrom;
    }

    public String getDirectionTo() {
        return directionTo;
    }

    public void setDirectionTo(String directionTo) {
        this.directionTo = directionTo;
    }

    public boolean isMark() {
        return mark;
    }

    public void setMark(boolean mark) {
        this.mark = mark;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void changeMark() {
        mark = !mark;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Card) {
            Card object = (Card) obj;
            if (text.equals(object.getText()) &&
                    translate.equals(object.getTranslate()) &&
                    directionFrom.equals(object.getDirectionFrom()) &&
                    directionTo.equals(object.getDirectionTo()) &&
                    mark == object.isMark())
                return true;
        }
        return false;
    }
}
