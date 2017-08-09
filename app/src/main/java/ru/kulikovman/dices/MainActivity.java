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

    private ImageView mDiceLeft, mDiceRight;
    private int mLeftImageId, mRightImageId;

    public static final String PREFERENCES = "dice_settings";
    public static final String LEFT_DICE_IMAGE = "left_dice_image";
    public static final String RIGHT_DICE_IMAGE = "right_dice_image";
    private SharedPreferences mSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDiceRight = (ImageView) findViewById(R.id.dice_right);
        mDiceLeft = (ImageView) findViewById(R.id.dice_left);

        // Получаем сохраненное состояние кубиков
        mSettings = getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
        if (mSettings.contains(LEFT_DICE_IMAGE) && mSettings.contains(RIGHT_DICE_IMAGE)) {
            int leftDiceViewId = getResources().getIdentifier("dice_1_01", "drawable", getPackageName());
            int rightDiceViewId = getResources().getIdentifier("dice_1_02", "drawable", getPackageName());
            mDiceLeft.setImageResource(mSettings.getInt(LEFT_DICE_IMAGE, leftDiceViewId));
            mDiceRight.setImageResource(mSettings.getInt(RIGHT_DICE_IMAGE, rightDiceViewId));
            Log.d("myLog", "Восстановили значения кубиков");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Если в переменных что-то есть, то сохраняем их в SharedPreferences
        if (mLeftImageId != 0 && mRightImageId != 0) {
            SharedPreferences.Editor editor = mSettings.edit();
            editor.putInt(LEFT_DICE_IMAGE, mLeftImageId);
            editor.putInt(RIGHT_DICE_IMAGE, mRightImageId);
            editor.apply();
            Log.d("myLog", "Сохранили значения кубиков: " + mLeftImageId + " | " + mRightImageId);
        }
    }

    public void onClick(View view) {
        // Воспроизводим звук бросания кубиков
        MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.roll_dice);
        mediaPlayer.start();

        // Получаем значение кубиков и номер картинки
        Random random = new Random();
        int leftNumberInt = 1 + random.nextInt(6 - 1 + 1);
        int rightNumberInt = 1 + random.nextInt(6 - 1 + 1);

        int leftViewInt = 1 + random.nextInt(7 - 1 + 1);
        int rightViewInt;

        do {
            rightViewInt = 1 + random.nextInt(7 - 1 + 1);
        } while (leftViewInt == rightViewInt);
        Log.d("myLog", "Значения кубиков: " + leftNumberInt + "-" + leftViewInt + " | " + rightNumberInt + "-" + rightViewInt);

        // Переводим полученные значения в строки
        String leftNumber = String.valueOf(leftNumberInt);
        String rightNumber = String.valueOf(rightNumberInt);
        String leftView = String.valueOf(leftViewInt);
        String rightView = String.valueOf(rightViewInt);

        // Формируем имя картинки с кубиком - dice_1_01
        String leftImage = "dice_" + leftNumber + "_0" + leftView;
        String rightImage = "dice_" + rightNumber + "_0" + rightView;
        Log.d("myLog", "Имя ресурса: " + leftImage + " | " + rightImage);

        // Присваеваем новые картинки с кубиками
        mLeftImageId = getResources().getIdentifier(leftImage, "drawable", getPackageName());
        mRightImageId = getResources().getIdentifier(rightImage, "drawable", getPackageName());
        Log.d("myLog", "Идентификатор ресурса: " + mLeftImageId + " | " + mRightImageId);

        mDiceLeft.setImageResource(mLeftImageId);
        mDiceRight.setImageResource(mRightImageId);



    }
}
