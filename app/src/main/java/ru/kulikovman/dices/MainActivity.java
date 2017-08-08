package ru.kulikovman.dices;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private ImageView mDiceLeft, mDiceRight;
    private String mLeftNumber, mRightNumber, mLeftView, mRightView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDiceRight = (ImageView) findViewById(R.id.dice_right);
        mDiceLeft = (ImageView) findViewById(R.id.dice_left);
    }

    public void onClick(View view) {
        // Получаем значение кубиков и номер картинки
        Random random = new Random();
        int leftNumber = 1 + random.nextInt(6 - 1 + 1);
        int rightNumber = 1 + random.nextInt(6 - 1 + 1);

        int leftView = 1 + random.nextInt(7 - 1 + 1);
        int rightView;

        do {
            rightView = 1 + random.nextInt(7 - 1 + 1);
        } while (leftView == rightView);
        Log.d("myLog", "Значения кубиков: " + leftNumber + "-" + leftView + " | " + rightNumber + "-" + rightView);

        // Переводим полученные значения в строки
        mLeftNumber = String.valueOf(leftNumber);
        mRightNumber = String.valueOf(rightNumber);
        mLeftView = String.valueOf(leftView);
        mRightView = String.valueOf(rightView);

        // Формируем имя картинки с кубиком - dice_1_01
        String leftImage = "dice_" + mLeftNumber + "_0" + mLeftView;
        String rightImage = "dice_" + mRightNumber + "_0" + mRightView;
        Log.d("myLog", "Имя ресурса: " + leftImage + " | " + rightImage);

        // Присваеваем новые картинки с кубиками
        int leftImageId = getResources().getIdentifier(leftImage, "drawable", getPackageName());
        int rightImageId = getResources().getIdentifier(rightImage, "drawable", getPackageName());
        Log.d("myLog", "Идентификатор ресурса: " + leftImageId + " | " + rightImageId);

        mDiceLeft.setImageResource(leftImageId);
        mDiceRight.setImageResource(rightImageId);



    }
}
