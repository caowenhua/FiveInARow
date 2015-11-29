package me.fiveinarow.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import me.fiveinarow.R;

/**
 * Created by caowenhua on 2015/11/29.
 */
public class MenuActivity extends Activity implements View.OnClickListener{

    private Button btn_create;
    private Button btn_join;
    private Button btn_quit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        btn_create = (Button) findViewById(R.id.btn_create);
        btn_join = (Button) findViewById(R.id.btn_join);
        btn_quit = (Button) findViewById(R.id.btn_quit);

        btn_create.setOnClickListener(this);
        btn_join.setOnClickListener(this);
        btn_quit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

    }
}
