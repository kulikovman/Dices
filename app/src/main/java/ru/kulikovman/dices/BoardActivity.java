package ru.kulikovman.dices;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.Random;

public class BoardActivity extends AppCompatActivity {

    //private ViewFlipper mFlipper;
    //private Button mButton1, mButton2, mButton3, mButton4;
    private LinearLayout mBoard;
    private ImageView mDiceOne;
    private int mWidth, mHeight, mX, mY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_board);

        Log.d("log", "Запущен onCreate в BoardActivity");

        //mFlipper = findViewById(R.id.cubes_container);
        /*mButton1 = findViewById(R.id.button_1);
        mButton2 = findViewById(R.id.button_2);
        mButton3 = findViewById(R.id.button_3);
        mButton4 = findViewById(R.id.button_4);*/

        mBoard = findViewById(R.id.board);
        mDiceOne = findViewById(R.id.dice_1);



        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        mWidth = displayMetrics.widthPixels - convertDpToPx(140);
        mHeight = displayMetrics.heightPixels - convertDpToPx(140 + 100);

        moveToRandomPosition(mDiceOne);

        Log.d("log", "Размер: " + mWidth + " x " + mHeight);


    }

    private void moveToRandomPosition(ImageView dice) {
        // Получаем случайные координаты кубика
        Random random = new Random();
        mX = 1 + random.nextInt(mWidth);
        mY = 1 + random.nextInt(mHeight);

        Log.d("log", "Координаты: " + mX + " x " + mY);

        setMargin(dice, mX, mY);
    }

    public int convertDpToPx(int valueInDp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, valueInDp,
                getResources().getDisplayMetrics());
    }

    private void setMargin(View view, int x, int y) {
        MarginLayoutParams params = (MarginLayoutParams) view.getLayoutParams();
        params.topMargin = y;
        params.leftMargin = x;
        view.setLayoutParams(params);
    }


    @Override
    protected void onResume() {
        super.onResume();

        Log.d("log", "Запущен onResume в BoardActivity");

        /*mWidth = mBoard.getWidth();
        mHeight = mBoard.getHeight();

        Log.d("log", "Размер: " + mWidth + " x " + mHeight);*/
    }

    public void dropDice(View view) {
        moveToRandomPosition(mDiceOne);
    }

    /*public void selectNumberCubes(View view) {
        int id = view.getId();
        selectButton(view);

        switch (id) {
            case R.id.button_1:
                mFlipper.setDisplayedChild(0);
                break;
            case R.id.button_2:
                mFlipper.setDisplayedChild(1);
                break;
            case R.id.button_3:
                mFlipper.setDisplayedChild(2);
                break;
            case R.id.button_4:
                mFlipper.setDisplayedChild(3);
                break;
        }
    }*/

    /*private void selectButton(View view) {
        mButton1.setSelected(false);
        mButton2.setSelected(false);
        mButton3.setSelected(false);
        mButton4.setSelected(false);

        view.setSelected(true);
    }*/


}
