package ru.kulikovman.dices;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.Button;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import ru.kulikovman.dices.models.Dice;

public class BoardActivity extends AppCompatActivity {

    private Button mButton1, mButton2, mButton3, mButton4, mButtonColor;
    private ImageView mDice1, mDice2, mDice3, mDice4;
    private int mWidth, mHeight;

    private List<Dice> mDiceList = new ArrayList<>();
    private int mNumber;
    private String mColor = "w";
    private boolean isReadyForRoll;

    private SharedPreferences mSharedPref;
    private SoundPool mSoundPool;
    private int mRollDiceSound;

    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private ShakeDetector mShakeDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board);

        // Инициализируем вью элементы
        mButtonColor = findViewById(R.id.button_color);
        mButton1 = findViewById(R.id.button_1);
        mButton2 = findViewById(R.id.button_2);
        mButton3 = findViewById(R.id.button_3);
        mButton4 = findViewById(R.id.button_4);
        mDice1 = findViewById(R.id.dice_1);
        mDice2 = findViewById(R.id.dice_2);
        mDice3 = findViewById(R.id.dice_3);
        mDice4 = findViewById(R.id.dice_4);

        // Получаем SharedPreferences и восстанавливаем количество кубиков
        mSharedPref = getPreferences(Context.MODE_PRIVATE);
        mNumber = mSharedPref.getInt(getString(R.string.number_of_dice), 2);
        mColor = mSharedPref.getString(getString(R.string.color_of_dice), "w");

        // Инициализация SoundPool и ShakeDetector
        initSoundPool();
        initShakeDetector();

        // Получение границ координат для размещения кубиков
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int diceSize = convertDpToPx(140);
        int buttonPanelHeight = convertDpToPx(90);
        mWidth = displayMetrics.widthPixels - diceSize;
        mHeight = displayMetrics.heightPixels - diceSize - buttonPanelHeight - getStatusBarHeight();

        // Устанавливаем стиль кнопки цвета
        setStyleForColorButton();

        // Готовим доску и кидаем кубики
        selectButton(mNumber);
        prepareBoard(mNumber);
        dropDice(false);
    }

    @Override
    protected void onPause() {
        // Add the following line to unregister the Sensor Manager onPause
        mSensorManager.unregisterListener(mShakeDetector);

        super.onPause();

        // Сохраняем цвет и количество кубиков
        mSharedPref = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mSharedPref.edit();
        editor.putInt(getString(R.string.number_of_dice), mNumber);
        editor.putString(getString(R.string.color_of_dice), mColor);
        editor.apply();

        // Очищаем SoundPool
        clearSoundPool();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Создаем SoundPool
        initSoundPool();

        // Add the following line to register the Session Manager Listener onResume
        mSensorManager.registerListener(mShakeDetector, mAccelerometer, SensorManager.SENSOR_DELAY_UI);
    }

    private void initShakeDetector() {
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (mSensorManager != null) {
            mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }

        mShakeDetector = new ShakeDetector();
        mShakeDetector.setOnShakeListener(new ShakeDetector.OnShakeListener() {

            @Override
            public void onShake(int count) {
                // Действие при встряхивании устройства
                Log.d("log", "Обнаружена тряска - " + count);
                dropDice(true);
            }
        });
    }

    private void initSoundPool() {
        if (mSoundPool == null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                // Создаем SoundPool для Android API 21 и выше
                AudioAttributes attributes = new AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_GAME)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build();

                mSoundPool = new SoundPool.Builder()
                        .setAudioAttributes(attributes)
                        .build();
            } else {
                // Создаем SoundPool для старых версий Android
                mSoundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
            }

            // Получаем id звуковых файлов
            mRollDiceSound = mSoundPool.load(this, R.raw.roll_dice, 1);
        }
    }

    private void clearSoundPool() {
        mSoundPool.release();
        mSoundPool = null;
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

    private void prepareBoard(int number) {
        // Сначала скрываем все кубики (кроме первого)
        mDice2.setVisibility(View.GONE);
        mDice3.setVisibility(View.GONE);
        mDice4.setVisibility(View.GONE);

        // Показываем нужное количество кубиков
        if (number >= 2) {
            mDice2.setVisibility(View.VISIBLE);
        }

        if (number >= 3) {
            mDice3.setVisibility(View.VISIBLE);
        }

        if (number >= 4) {
            mDice4.setVisibility(View.VISIBLE);
        }
    }

    public void clickOnBoard(View view) {
        if (isReadyForRoll) {
            dropDice(true);
        }
    }

    public void clickNumberButton(View view) {
        // Получаем номер кнопки
        Button button = (Button) view;
        mNumber = Integer.parseInt(button.getText().toString());

        // Выделяем кнопку и готовим доску
        selectButton(button);
        prepareBoard(mNumber);
    }

    private void selectButton(Button button) {
        clearButtonSelection();
        button.setSelected(true);
    }

    private void selectButton(int number) {
        clearButtonSelection();

        if (number == 1) {
            mButton1.setSelected(true);
        } else if (number == 2) {
            mButton2.setSelected(true);
        } else if (number == 3) {
            mButton3.setSelected(true);
        } else if (number == 4) {
            mButton4.setSelected(true);
        }
    }

    private void clearButtonSelection() {
        mButton1.setSelected(false);
        mButton2.setSelected(false);
        mButton3.setSelected(false);
        mButton4.setSelected(false);
    }

    private void dropDice(boolean sound) {
        // Очищаем результеты прошлого броска
        mDiceList.clear();

        // Новый бросок
        Random random = new Random();

        boolean intersection = true;
        while (intersection) {
            for (int i = 0; i < 4; i++) {
                // Генерируем координаты и сторону кубика
                int x = random.nextInt(mWidth);
                int y = random.nextInt(mHeight);
                int diceNumber = 1 + random.nextInt(6); // от 1 до 6

                // Генерируем уникальный вид кубика
                int diceView = 1 + random.nextInt(7); // от 1 до 7
                boolean uniqueView = false;
                while (!uniqueView) {
                    uniqueView = true;
                    for (Dice dice : mDiceList) {
                        if (diceView == dice.getView()) {
                            uniqueView = false;
                            diceView = 1 + random.nextInt(7); // от 1 до 7
                            break;
                        }
                    }
                }

                // Добавляем новый кубик в список
                mDiceList.add(new Dice(x, y, diceNumber, diceView));
            }

            // Если есть пересечения, то начинаем заново
            if (isIntersection(mDiceList)) {
                mDiceList.clear();
            } else {
                intersection = false;
            }
        }

        // Вывод в консоль
        for (Dice dice : mDiceList) {
            Log.d("log", "Кубик: " + dice.getX() + " - " + dice.getY() + " | " + dice.getNumber() + " - " + dice.getView());
        }

        // Воспроизводим звук бросания кубиков
        if (sound) {
            if (mSoundPool == null) {
                initSoundPool();
            }
            mSoundPool.play(mRollDiceSound, 1, 1, 1, 0, 1);
        }

        // Размещаем кубики на поле
        moveDice(mDice1, mDiceList.get(0).getX(), mDiceList.get(0).getY());
        moveDice(mDice2, mDiceList.get(1).getX(), mDiceList.get(1).getY());
        moveDice(mDice3, mDiceList.get(2).getX(), mDiceList.get(2).getY());
        moveDice(mDice4, mDiceList.get(3).getX(), mDiceList.get(3).getY());

        // Назначаем картинки
        redrawDices();

        // Задержка для исключения ложного броска
        isReadyForRoll = false;
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                isReadyForRoll = true;
            }
        }, 1000); // 1 секунда
    }

    private void redrawDices() {
        if (!mDiceList.isEmpty()) {
            loadDiceImage(mDice1, mDiceList.get(0).getNumber(), mDiceList.get(0).getView());
            loadDiceImage(mDice2, mDiceList.get(1).getNumber(), mDiceList.get(1).getView());
            loadDiceImage(mDice3, mDiceList.get(2).getNumber(), mDiceList.get(2).getView());
            loadDiceImage(mDice4, mDiceList.get(3).getNumber(), mDiceList.get(3).getView());
        }
    }

    private boolean isIntersection(List<Dice> coordinates) {
        // Расстояние между центрами кубиков
        int dist = convertDpToPx(110);

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
        // Установка отступов слева и сверху
        MarginLayoutParams params = (MarginLayoutParams) view.getLayoutParams();
        params.leftMargin = x;
        params.topMargin = y;
        view.setLayoutParams(params);
    }

    private void loadDiceImage(ImageView dice, int number, int view) {
        // Формируем имя ресурса картинки с кубиком - dice_w_24
        String diceNumber = String.valueOf(number);
        String diceView = String.valueOf(view);
        String diceColor = getDiceColor();

        String diceImage = "dice_" + diceColor + "_" + diceNumber + diceView;

        // Получаем идентификатор и присваиваем картинку
        int diceImageId = getResources().getIdentifier(diceImage, "drawable", getPackageName());
        dice.setImageResource(diceImageId);
    }

    private int convertDpToPx(int dp) {
        return Math.round(dp * (getResources().getDisplayMetrics().xdpi / DisplayMetrics.DENSITY_DEFAULT));

    }

    private int convertPxToDp(int px) {
        return Math.round(px / (Resources.getSystem().getDisplayMetrics().xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    private void setStyleForColorButton() {
        switch (mColor) {
            case "w":
                mButtonColor.setBackgroundResource(R.drawable.top_color_button_white);
                break;
            case "b":
                mButtonColor.setBackgroundResource(R.drawable.top_color_button_black);
                break;
            case "r":
                mButtonColor.setBackgroundResource(R.drawable.top_color_button_red);
                break;
            case "random":
                mButtonColor.setBackgroundResource(R.drawable.top_color_button_random);
                break;
        }
    }

    public void changeColor(View view) {
        // Меняем цвет кубиков и кнопки
        switch (mColor) {
            case "w":
                mColor = "b";
                mButtonColor.setBackgroundResource(R.drawable.top_color_button_black);
                break;
            case "b":
                mColor = "r";
                mButtonColor.setBackgroundResource(R.drawable.top_color_button_red);
                break;
            case "r":
                mColor = "random";
                mButtonColor.setBackgroundResource(R.drawable.top_color_button_random);
                break;
            case "random":
                mColor = "w";
                mButtonColor.setBackgroundResource(R.drawable.top_color_button_white);
                break;
        }

        // Перерисовываем картинки
        redrawDices();
    }

    public String getDiceColor() {
        if (mColor.equals("random")) {
            // Возвращаем случайный цвет
            Random random = new Random();
            int number = 1 + random.nextInt(3); // от 1 до 3

            switch (number) {
                case 1:
                    return "w";
                case 2:
                    return "b";
                case 3:
                    return "r";
                default:
                    return "w";
            }
        } else {
            // Возвращаем установленный цвет
            return mColor;
        }
    }
}
