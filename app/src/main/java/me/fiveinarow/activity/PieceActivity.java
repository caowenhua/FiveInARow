package me.fiveinarow.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.TextView;

import me.fiveinarow.R;
import me.fiveinarow.bean.Piece;
import me.fiveinarow.service.BlueToochClientService;
import me.fiveinarow.service.BlueToochServerService;
import me.fiveinarow.widget.GobangView;

/**
 * Created by caowenhua on 2015/11/29.
 */
public class PieceActivity extends Activity implements GobangView.OnClickChessListener{

    private TextView tv_tip;
    private TextView tv_object;
    private GobangView view_gobang;

    private boolean isBlack;
    private boolean isWaiting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_piece);

        tv_tip = (TextView) findViewById(R.id.tv_tip);
        tv_object = (TextView) findViewById(R.id.tv_object);
        view_gobang = (GobangView) findViewById(R.id.view_gobang);
        view_gobang.setOnClickChessListener(this);

        isBlack = getIntent().getBooleanExtra("isBlack", true);
        isWaiting = !isBlack;
        if(isBlack){
            tv_object.setText("黑子");
        }
        else{
            tv_object.setText("白子");
        }

        refreshStatus();

    }

    private void refreshStatus() {
        if(isWaiting){
            tv_tip.setText("等待对方下子..");
        }
        else{
            tv_tip.setText("轮到您下子..");
        }

        view_gobang.setIsCanClick(!isWaiting);
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter("isWaiting");
        registerReceiver(waitingReceiver, intentFilter);
        IntentFilter dataFilter = new IntentFilter("data");
        registerReceiver(dataReceiver, dataFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(waitingReceiver);
        unregisterReceiver(dataReceiver);
    }


    @Override
    public void onClickChess(int row, int column) {
        Piece piece = new Piece();
        piece.setRow(row);
        piece.setColumn(column);
        piece.setIsBlack(isBlack);
        if(isBlack){
            Intent intent = new Intent(this, BlueToochServerService.class);
            intent.putExtra("piece", piece);
            startService(intent);
        }
        else{
            Intent intent = new Intent(this, BlueToochClientService.class);
            intent.putExtra("piece", piece);
            startService(intent);
        }
    }

    private BroadcastReceiver waitingReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            isWaiting = intent.getBooleanExtra("isWaiting", true);
            refreshStatus();
        }
    };

    private BroadcastReceiver dataReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String data = intent.getStringExtra("data");
            Piece piece = new Piece(data);
            view_gobang.addPiece(piece);
        }
    };
}
