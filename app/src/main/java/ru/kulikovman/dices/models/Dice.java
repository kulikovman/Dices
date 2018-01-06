package ru.kulikovman.dices.models;


public class Dice {
    private int mX;
    private int mY;
    private int number;
    private int view;

    public Dice(int x, int y, int number, int view) {
        mX = x;
        mY = y;
        this.number = number;
        this.view = view;
    }

    public int getX() {
        return mX;
    }

    public void setX(int x) {
        mX = x;
    }

    public int getY() {
        return mY;
    }

    public void setY(int y) {
        mY = y;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public int getView() {
        return view;
    }

    public void setView(int view) {
        this.view = view;
    }
}
