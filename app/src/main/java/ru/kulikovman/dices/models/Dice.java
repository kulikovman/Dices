package ru.kulikovman.dices.models;


public class Dice {
    private int mX;
    private int mY;

    public Dice(int x, int y) {
        mX = x;
        mY = y;
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
}
