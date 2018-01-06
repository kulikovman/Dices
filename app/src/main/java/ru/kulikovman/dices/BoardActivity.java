package ru.kulikovman.dices;

import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
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

        // Получение границ координат для размещения кубиков
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        mWidth = displayMetrics.widthPixels - convertDpToPx(140);
        mHeight = displayMetrics.heightPixels - convertDpToPx(140 + 100) - getStatusBarHeight();

        // Тестирование
        /*Log.d("log", "Размер поля: " + mWidth + " x " + mHeight);
        Log.d("log", "status bar: " + getStatusBarHeight() + "px");
        Log.d("log", "140dp = : " + convertDpToPx(140) + "px");
        Log.d("log", "240dp = : " + convertDpToPx(240) + "px");
        Log.d("log", "350dp = : " + convertDpToPx(350) + "px");
        Log.d("log", "326dp = : " + convertDpToPx(326) + "px");*/


        dropDices(4);
    }

    private void dropDices(int number) {
        List<Dice> dices = new ArrayList<>();
        Random random = new Random();

        int counter = 0;
        boolean intersection = true;

        while (intersection) {
            for (int i = 0; i < number; i++) {
                // Генерируем координаты и сторону кубика
                int x = random.nextInt(mWidth);
                int y = random.nextInt(mHeight);
                int diceNumber = 1 + random.nextInt(6);

                // Генерируем уникальный вид кубика
                int diceView = 1 + random.nextInt(7);
                boolean uniqueView = false;
                while (!uniqueView) {
                    uniqueView = true;
                    for (Dice dice : dices) {
                        if (diceView == dice.getView()) {
                            uniqueView = false;
                            diceView = 1 + random.nextInt(7);
                            break;
                        }
                    }
                }

                // Добавляем новый кубик в список
                dices.add(new Dice(x, y, diceNumber, diceView));
            }

            counter++;
            if (counter % 1000 == 0) {
                Log.d("log", "Счетчик: " + counter);
            }

            // Если есть пересечения, то начинаем заново
            if (isIntersection(dices)) {
                dices.clear();
            } else {
                intersection = false;
            }
        }

        for (Dice dice : dices) {
            Log.d("log", "Кубик: " + dice.getX() + " - " + dice.getY() +
                    " | " + dice.getNumber() + " - " + dice.getView());
        }

        // Размещаем кубики на поле
        Dice dice = dices.get(0);
        moveDice(mDice1, dice.getX(), dice.getY());
        loadDiceImage(mDice1, dice.getNumber(), dice.getView());

        if (number >= 2) {
            dice = dices.get(1);
            moveDice(mDice2, dice.getX(), dice.getY());
            loadDiceImage(mDice2, dice.getNumber(), dice.getView());
        }

        if (number >= 3) {
            dice = dices.get(2);
            moveDice(mDice3, dice.getX(), dice.getY());
            loadDiceImage(mDice3, dice.getNumber(), dice.getView());
        }

        if (number >= 4) {
            dice = dices.get(3);
            moveDice(mDice4, dice.getX(), dice.getY());
            loadDiceImage(mDice4, dice.getNumber(), dice.getView());
        }
    }

    private void loadDiceImage(ImageView dice, int number, int view) {
        // Формируем имя картинки с кубиком - dice_1_01
        String diceNumber = String.valueOf(number);
        String diceView = String.valueOf(view);
        String diceImage = "dice_" + diceNumber + "_0" + diceView;

        // Получаем идентификатор и присваиваем картинку
        int diceImageId = getResources().getIdentifier(diceImage, "drawable", getPackageName());
        dice.setImageResource(diceImageId);
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

    private void moveDice(View view, int x, int y) {
        MarginLayoutParams params = (MarginLayoutParams) view.getLayoutParams();
        params.leftMargin = x;
        params.topMargin = y;
        view.setLayoutParams(params);
    }

    public void clickOnBoard(View view) {
        dropDices(4);
    }

    public int getStatusBarHeight() {
        // Получение высоты статус-бара
        int statusBarHeight = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            statusBarHeight = getResources().getDimensionPixelSize(resourceId);
        }
        return statusBarHeight;
    }

    private int convertDpToPx(int dp) {
        return Math.round(dp * (getResources().getDisplayMetrics().xdpi / DisplayMetrics.DENSITY_DEFAULT));

    }

    private int convertPxToDp(int px) {
        return Math.round(px / (Resources.getSystem().getDisplayMetrics().xdpi / DisplayMetrics.DENSITY_DEFAULT));
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
