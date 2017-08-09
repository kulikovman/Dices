package ru.kulikovman.dices;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private ImageView mLeftDice, mRightDice;

    private int mLeftNumber = 1;
    private int mLeftView = 1;
    private int mRightNumber = 2;
    private int mRightView = 2;

    public static final String PREFERENCES = "dice_settings";
    public static final String LEFT_DICE_NUMBER = "left_dice_number";
    public static final String RIGHT_DICE_NUMBER = "right_dice_number";
    public static final String LEFT_DICE_VIEW = "left_dice_view";
    public static final String RIGHT_DICE_VIEW = "right_dice_view";
    private SharedPreferences mSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRightDice = (ImageView) findViewById(R.id.dice_right);
        mLeftDice = (ImageView) findViewById(R.id.dice_left);

        // Получаем SharedPreferences
        mSettings = getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);

        // Если сохранено значение кубиков, по получаем его
        if (mSettings.contains(LEFT_DICE_NUMBER) && mSettings.contains(RIGHT_DICE_NUMBER)) {
            mLeftNumber = mSettings.getInt(LEFT_DICE_NUMBER, 1);
            mRightNumber = mSettings.getInt(RIGHT_DICE_NUMBER, 1);
            Log.d("myLog", "Восстановили значения кубиков");
        }

        // Если сохранен вид кубиков, то получаем его
        if (mSettings.contains(LEFT_DICE_VIEW) && mSettings.contains(RIGHT_DICE_VIEW)) {
            mLeftView = mSettings.getInt(LEFT_DICE_VIEW, 1);
            mRightView = mSettings.getInt(RIGHT_DICE_VIEW, 1);
            Log.d("myLog", "Восстановили вид кубиков");
        }

        // Загружаем картинки для кубиков
        loadDiceImage(mLeftDice, mLeftNumber, mLeftView);
        loadDiceImage(mRightDice, mRightNumber, mRightView);
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Создаем SharedPreferences и если переменные не пустые, то сохраняем их
        SharedPreferences.Editor editor = mSettings.edit();
        if (mLeftNumber != 0 && mRightNumber != 0) {
            editor.putInt(LEFT_DICE_NUMBER, mLeftNumber);
            editor.putInt(RIGHT_DICE_NUMBER, mRightNumber);
        }
        if (mLeftView != 0 && mRightView != 0) {
            editor.putInt(LEFT_DICE_VIEW, mLeftView);
            editor.putInt(RIGHT_DICE_VIEW, mRightView);
        }
        editor.apply();
        Log.d("myLog", "Сохранили значения кубиков: " + mLeftNumber + " | " + mRightNumber);
    }

    public void onClick(View view) {
        // Воспроизводим звук бросания кубиков
        MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.roll_dice);
        mediaPlayer.start();

        // Получаем значение кубиков
        Random random = new Random();
        mLeftNumber = 1 + random.nextInt(6);
        mRightNumber = 1 + random.nextInt(6);

        // Получаем вид кубиков
        int leftViewInt = mLeftView;
        int rightViewInt = mRightView;

        do {
            mLeftView = 1 + random.nextInt(7);
        } while (mLeftView == leftViewInt);

        do {
            mRightView = 1 + random.nextInt(7);
        } while (mRightView == mLeftView || mRightView == rightViewInt);
        Log.d("myLog", "Значения кубиков: " + mLeftNumber + "-" + leftViewInt + " | " + mRightNumber + "-" + rightViewInt);

        // Загружаем картинки для кубиков
        loadDiceImage(mLeftDice, mLeftNumber, mLeftView);
        loadDiceImage(mRightDice, mRightNumber, mRightView);


        /*// Переводим полученные значения в строки
        String leftNumber = String.valueOf(mLeftNumber);
        String rightNumber = String.valueOf(mRightNumber);
        String leftView = String.valueOf(mLeftView);
        String rightView = String.valueOf(mRightView);

        // Формируем имя картинки с кубиком - dice_1_01
        String leftImage = "dice_" + leftNumber + "_0" + leftView;
        String rightImage = "dice_" + rightNumber + "_0" + rightView;
        Log.d("myLog", "Имя ресурса: " + leftImage + " | " + rightImage);

        // Присваеваем новые картинки с кубиками
        int eftNumber = getResources().getIdentifier(leftImage, "drawable", getPackageName());
        int ightNumber = getResources().getIdentifier(rightImage, "drawable", getPackageName());
        Log.d("myLog", "Идентификатор ресурса: " + mLeftNumber + " | " + mRightNumber);

        mLeftDice.setImageResource(eftNumber);
        mRightDice.setImageResource(ightNumber);*/
    }

    private void loadDiceImage(ImageView dice, int number, int view) {
        // Переводим значение кубика и его вид в строки
        String diceNumber = String.valueOf(number);
        String diceView = String.valueOf(view);

        // Формируем имя картинки с кубиком - dice_1_01
        String diceImage = "dice_" + diceNumber + "_0" + diceView;

        // Получаем идентификатор картинки
        int diceImageId = getResources().getIdentifier(diceImage, "drawable", getPackageName());

        // Присваиваем ImageView новую картинку кубика
        dice.setImageResource(diceImageId);
    }
}
