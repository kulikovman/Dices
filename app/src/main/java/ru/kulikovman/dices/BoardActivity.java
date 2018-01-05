package ru.kulikovman.dices;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ViewFlipper;

public class BoardActivity extends AppCompatActivity {

    private ViewFlipper mFlipper;
    private Button mButton1, mButton2, mButton3, mButton4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board);

        mFlipper = findViewById(R.id.cubes_container);
        mButton1 = findViewById(R.id.button_1);
        mButton2 = findViewById(R.id.button_2);
        mButton3 = findViewById(R.id.button_3);
        mButton4 = findViewById(R.id.button_4);
    }


    public void selectNumberCubes(View view) {
        int id = view.getId();

        switch (id) {
            case R.id.button_1:
                pressButton(view);
                mFlipper.setDisplayedChild(0);
                break;
            case R.id.button_2:
                pressButton(view);
                mButton2.setPressed(true);
                mFlipper.setDisplayedChild(1);
                break;
            case R.id.button_3:
                break;
            case R.id.button_4:
                break;
        }
    }

    private void pressButton(View view) {
        mButton1.setPressed(false);
        mButton2.setPressed(false);
        mButton3.setPressed(false);
        mButton4.setPressed(false);

        //view.setPressed(true);
    }


}
