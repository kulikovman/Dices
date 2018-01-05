package ru.kulikovman.dices;

import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import ru.kulikovman.dices.models.Dice;

public class BoardActivity extends AppCompatActivity {

    private Button mButton1, mButton2, mButton3, mButton4;
    private FrameLayout mBoard;
    private ImageView mDice1, mDice2, mDice3, mDice4;
    private int mWidth, mHeight;
    private int mX, mY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_board);

        Log.d("log", "Запущен onCreate в BoardActivity");

        // Инициализируем вью элементы
        mBoard = findViewById(R.id.board);
        mButton1 = findViewById(R.id.button_1);
        mButton2 = findViewById(R.id.button_2);
        mButton3 = findViewById(R.id.button_3);
        mButton4 = findViewById(R.id.button_4);
        mDice1 = findViewById(R.id.dice_1);
        mDice2 = findViewById(R.id.dice_2);
        mDice3 = findViewById(R.id.dice_3);
        mDice4 = findViewById(R.id.dice_4);

        // Получаем границы поля для кубиков
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        mWidth = displayMetrics.widthPixels - convertDpToPx(140);
        mHeight = displayMetrics.heightPixels - convertDpToPx(140 + 100);

        /*Log.d("log", "Размер поля: " + mWidth + " x " + mHeight);
        Log.d("log", "140dp = : " + convertDpToPx(140) + "px");
        Log.d("log", "240dp = : " + convertDpToPx(240) + "px");
        Log.d("log", "350dp = : " + convertDpToPx(350) + "px");*/


        //moveToRandomPosition(mDice1);

        dropDices(4);
    }

    /*public int convertDpToPx(int valueInDp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, valueInDp,
                getResources().getDisplayMetrics());
    }*/

    public int getPixelsFromDPs(int dp) {
        return (int) (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics()));

    }

    private int convertDpToPx(int dp) {
        return Math.round(dp * (getResources().getDisplayMetrics().xdpi / DisplayMetrics.DENSITY_DEFAULT));

    }

    private int convertPxToDp(int px) {
        return Math.round(px / (Resources.getSystem().getDisplayMetrics().xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }


    private void dropDices(int number) {
        List<Dice> coordinates = new ArrayList<>();
        Random random = new Random();

        int counter = 0;
        boolean intersection = true;

        while (intersection) {
            for (int i = 0; i < number; i++) {
                int x = random.nextInt(666);
                int y = random.nextInt(1211);
                coordinates.add(new Dice(x, y));
            }

            counter++;
            if (counter % 1000 == 0) {
                Log.d("log", "Счетчик: " + counter);
            }

            if (isIntersection(coordinates)) {
                coordinates.clear();
            } else {
                intersection = false;
            }
        }

        for (Dice dice : coordinates) {
            Log.d("log", "Координаты: " + dice.getX() + " - " + dice.getY());
        }

        moveDice(mDice1, coordinates.get(0).getX(), coordinates.get(0).getY());
        moveDice(mDice2, coordinates.get(1).getX(), coordinates.get(1).getY());
        moveDice(mDice3, coordinates.get(2).getX(), coordinates.get(2).getY());
        moveDice(mDice4, coordinates.get(3).getX(), coordinates.get(3).getY());

    }

    private boolean isIntersection(List<Dice> coordinates) {
        // Расстояние между центрами кубиков
        int dist = convertDpToPx(130);

        // Проверка пересечений
        for (Dice d1 : coordinates) {
            for (Dice d2 : coordinates) {
                if (!d1.equals(d2)) {
                    int dX = Math.abs(d1.getX() - d2.getX());
                    int dY = Math.abs(d1.getY() - d2.getY());

                    if (dX < dist && dY < dist) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    private void moveToRandomPosition(ImageView dice) {
        // Получаем случайные координаты кубика
        Random random = new Random();
        mX = 1 + random.nextInt(mWidth);
        mY = 1 + random.nextInt(mHeight);

        Log.d("log", "Координаты: " + mX + " x " + mY);

        moveDice(dice, mX, mY);
    }


    private void moveDice(View view, int x, int y) {
        MarginLayoutParams params = (MarginLayoutParams) view.getLayoutParams();
        params.topMargin = y;
        params.leftMargin = x;
        view.setLayoutParams(params);
        view.invalidate();
        view.requestLayout();
    }

    public void dropDice(View view) {
        dropDices(4);
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
