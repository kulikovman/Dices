package ru.kulikovman.dices;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.view.View;
import android.widget.ImageView;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private ImageView mLeftDice, mRightDice;
    private int mLeftNumber, mRightNumber, mLeftView, mRightView;

    private SharedPreferences mSharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Инициализируем наши ImageView с кубиками
        mRightDice = (ImageView) findViewById(R.id.dice_right);
        mLeftDice = (ImageView) findViewById(R.id.dice_left);

        // Получаем SharedPreferences
        mSharedPref = getPreferences(Context.MODE_PRIVATE);

        // Восттанавливаем значения переменных
        mLeftNumber = mSharedPref.getInt(getString(R.string.left_dice_number), 1);
        mLeftView = mSharedPref.getInt(getString(R.string.left_dice_view), 2);
        mRightNumber = mSharedPref.getInt(getString(R.string.right_dice_number), 2);
        mRightView = mSharedPref.getInt(getString(R.string.right_dice_view), 1);

        // Загружаем картинки кубиков
        loadDiceImage(mLeftDice, mLeftNumber, mLeftView);
        loadDiceImage(mRightDice, mRightNumber, mRightView);
        Log.d("myLog", "Загрузили картинки кубиков");
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Сохраняем значения кубиков
        // При первом запуске кубики получают значения по умолчанию
        // поэтому не надо проверять на null
        mSharedPref = getPreferences(Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = mSharedPref.edit();
        editor.putInt(getString(R.string.left_dice_number), mLeftNumber);
        editor.putInt(getString(R.string.left_dice_view), mLeftView);
        editor.putInt(getString(R.string.right_dice_number), mRightNumber);
        editor.putInt(getString(R.string.right_dice_view), mRightView);
        editor.apply();

        Log.d("myLog", "Сохранили значения кубиков: " + mLeftNumber + " | " + mRightNumber);
        Log.d("myLog", "Сохранили вид кубиков: " + mLeftView + " | " + mRightView);

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
        // Вид кубика не должен совпадать с предыдущим видом
        // Вид правого кубика не должен совпадать с видом левого кубика
        int leftViewInt = mLeftView;
        int rightViewInt = mRightView;

        do {
            mLeftView = 1 + random.nextInt(7);
        } while (mLeftView == leftViewInt);

        do {
            mRightView = 1 + random.nextInt(7);
        } while (mRightView == mLeftView || mRightView == rightViewInt);
        //Log.d("myLog", "Значения кубиков: " + mLeftNumber + "-" + mLeftView + " | " + mRightNumber + "-" + mRightView);

        // Загружаем картинки для кубиков
        loadDiceImage(mLeftDice, mLeftNumber, mLeftView);
        loadDiceImage(mRightDice, mRightNumber, mRightView);
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
