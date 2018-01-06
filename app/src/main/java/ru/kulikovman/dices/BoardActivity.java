package ru.kulikovman.dices;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
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
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import ru.kulikovman.dices.models.Dice;

public class BoardActivity extends AppCompatActivity {

    private Button mButton1, mButton2, mButton3, mButton4;
    private FrameLayout mBoard;
    private ImageView mDice1, mDice2, mDice3, mDice4;
    private int mWidth, mHeight;

    private int mNumber;

    private SharedPreferences mSharedPref;
    private SoundPool mSoundPool;
    private int mRollDiceSound;

    // The following are used for the shake detection
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private ShakeDetector mShakeDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board);

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

        // Получаем SharedPreferences и восстанавливаем количество кубиков
        mSharedPref = getPreferences(Context.MODE_PRIVATE);
        mNumber = mSharedPref.getInt(getString(R.string.number_of_dice), 1);

        // Создаем SoundPool
        createSoundPool();

        // Получение границ координат для размещения кубиков
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        mWidth = displayMetrics.widthPixels - convertDpToPx(140);
        mHeight = displayMetrics.heightPixels - convertDpToPx(140 + 100) - getStatusBarHeight();

        // Готовим доску и кидаем кубики
        selectButton(mNumber);
        prepareBoard(mNumber);
        dropDices(false);

        // ShakeDetector initialization
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (mSensorManager != null) {
            mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }

        mShakeDetector = new ShakeDetector();
        mShakeDetector.setOnShakeListener(new ShakeDetector.OnShakeListener() {

            @Override
            public void onShake(int count) {
				/*
				 * The following method, "handleShakeEvent(count):" is a stub //
				 * method you would use to setup whatever you want done once the
				 * device has been shook.
				 */

                Log.d("log", "Обнаружена тряска - " + count);
                //handleShakeEvent(count);
            }
        });
    }

    private void handleShakeEvent(int count) {

    }

    @Override
    protected void onPause() {
        // Add the following line to unregister the Sensor Manager onPause
        mSensorManager.unregisterListener(mShakeDetector);

        super.onPause();
        Log.d("myLog", "Запущен onPause");

        // Сохраняем количество кубиков
        mSharedPref = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mSharedPref.edit();
        editor.putInt(getString(R.string.number_of_dice), mNumber);
        editor.apply();

        // Очищаем SoundPool
        clearSoundPool();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("myLog", "Запущен onResume");

        // Создаем SoundPool
        createSoundPool();

        // Add the following line to register the Session Manager Listener onResume
        mSensorManager.registerListener(mShakeDetector, mAccelerometer,	SensorManager.SENSOR_DELAY_UI);
    }

    private void dropDices(boolean sound) {
        List<Dice> dices = new ArrayList<>();
        Random random = new Random();

        boolean intersection = true;
        while (intersection) {
            for (int i = 0; i < 4; i++) {
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

            // Если есть пересечения, то начинаем заново
            if (isIntersection(dices)) {
                dices.clear();
            } else {
                intersection = false;
            }
        }

        // Вывод в консоль
        for (Dice dice : dices) {
            Log.d("log", "Кубик: " + dice.getX() + " - " + dice.getY() +
                    " | " + dice.getNumber() + " - " + dice.getView());
        }

        // Воспроизводим звук бросания кубиков
        if (sound) {
            mSoundPool.play(mRollDiceSound, 1, 1, 1, 0, 1);
        }

        // Размещаем кубики на поле
        Dice dice = dices.get(0);
        moveDice(mDice1, dice.getX(), dice.getY());
        loadDiceImage(mDice1, dice.getNumber(), dice.getView());

        dice = dices.get(1);
        moveDice(mDice2, dice.getX(), dice.getY());
        loadDiceImage(mDice2, dice.getNumber(), dice.getView());

        dice = dices.get(2);
        moveDice(mDice3, dice.getX(), dice.getY());
        loadDiceImage(mDice3, dice.getNumber(), dice.getView());

        dice = dices.get(3);
        moveDice(mDice4, dice.getX(), dice.getY());
        loadDiceImage(mDice4, dice.getNumber(), dice.getView());
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

    private void loadDiceImage(ImageView dice, int number, int view) {
        // Формируем имя картинки с кубиком - dice_1_01
        String diceNumber = String.valueOf(number);
        String diceView = String.valueOf(view);
        String diceImage = "dice_" + diceNumber + "_0" + diceView;

        // Получаем идентификатор и присваиваем картинку
        int diceImageId = getResources().getIdentifier(diceImage, "drawable", getPackageName());
        dice.setImageResource(diceImageId);
    }

    private void moveDice(View view, int x, int y) {
        // Установка отступов слева и сверху
        MarginLayoutParams params = (MarginLayoutParams) view.getLayoutParams();
        params.leftMargin = x;
        params.topMargin = y;
        view.setLayoutParams(params);
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
        // Сначала скрываем все кубики
        mDice2.setVisibility(View.INVISIBLE);
        mDice3.setVisibility(View.INVISIBLE);
        mDice4.setVisibility(View.INVISIBLE);

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
        dropDices(true);
    }

    public void selectNumberCubes(View view) {
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

    @SuppressWarnings("deprecation")
    private void createSoundPool() {
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

            Log.d("myLog", "Создали SoundPool");

            // Получаем id звуковых файлов
            loadIdSounds();
        }
    }

    private void loadIdSounds() {
        mRollDiceSound = mSoundPool.load(this, R.raw.roll_dice, 1);

        Log.d("myLog", "Получили id звуковых файлов");
    }

    private void clearSoundPool() {
        mSoundPool.release();
        mSoundPool = null;

        Log.d("myLog", "SoundPool очищен");
    }

    private int convertDpToPx(int dp) {
        return Math.round(dp * (getResources().getDisplayMetrics().xdpi / DisplayMetrics.DENSITY_DEFAULT));

    }

    private int convertPxToDp(int px) {
        return Math.round(px / (Resources.getSystem().getDisplayMetrics().xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }
}
