package me.fiveinarow.activity;

import android.app.Activity;
import android.os.Bundle;

import me.fiveinarow.R;

/**
 * Created by caowenhua on 2015/11/29.
 */
public class PieceActivity extends Activity {

    private boolean isBlack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_piece);

        isBlack = getIntent().getBooleanExtra("isBlack", true);
    }
}
