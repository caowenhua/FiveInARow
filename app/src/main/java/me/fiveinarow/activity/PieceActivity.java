package me.fiveinarow.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.PowerManager;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import me.fiveinarow.R;
import me.fiveinarow.bean.Piece;
import me.fiveinarow.common.P;
import me.fiveinarow.service.BlueToochClientService;
import me.fiveinarow.service.BlueToochServerService;
import me.fiveinarow.widget.GobangView;
import me.fiveinarow.widget.TwoButtonDialog;

/**
 * Created by caowenhua on 2015/11/29.
 */
public class PieceActivity extends Activity implements GobangView.OnClickChessListener, GobangView.OnChessWinListener{

    private TextView tv_tip;
    private TextView tv_object;
    private GobangView view_gobang;

    private boolean isBlack;
    private boolean isWaiting;

    private PowerManager.WakeLock wl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_piece);

        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wl = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "MyTag");

        tv_tip = (TextView) findViewById(R.id.tv_tip);
        tv_object = (TextView) findViewById(R.id.tv_object);
        view_gobang = (GobangView) findViewById(R.id.view_gobang);
        view_gobang.setOnClickChessListener(this);
        view_gobang.setOnChessWinListener(this);

        isBlack = getIntent().getBooleanExtra("isBlack", true);
        isWaiting = !isBlack;
        if(isBlack){
            tv_object.setText("黑子");
        }
        else{
            tv_object.setText("白子");
        }

        view_gobang.setIsBlackChess(isBlack);

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
        IntentFilter endFilter = new IntentFilter("GAMEOVER");
        registerReceiver(endReceiver, endFilter);

        wl.acquire();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(waitingReceiver);
        unregisterReceiver(dataReceiver);
        unregisterReceiver(endReceiver);

        wl.release();
    }


    @Override
    public void onClickChess(int row, int column, boolean isAddSuccess) {
        if(isAddSuccess){
            Piece piece = new Piece();
            piece.setRow(row);
            piece.setColumn(column);
            piece.setIsBlack(isBlack);
            if(isBlack){
                Intent intent = new Intent(this, BlueToochServerService.class);
                intent.putExtra(P.OP, P.CHESS_DOWN);
                intent.putExtra("piece", piece);
                startService(intent);
            }
            else{
                Intent intent = new Intent(this, BlueToochClientService.class);
                intent.putExtra(P.OP, P.CHESS_DOWN);
                intent.putExtra("piece", piece);
                startService(intent);
            }
        }
        else{
            Toast.makeText(this, "已有子，请重下", Toast.LENGTH_SHORT).show();
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

    private BroadcastReceiver endReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(PieceActivity.this, "对方已退出游戏，游戏结束", Toast.LENGTH_SHORT).show();
            finish();
        }
    };

    @Override
    public void onBackPressed() {
        TwoButtonDialog.Builder builder = new TwoButtonDialog.Builder(this);
        builder.setTitle("退出").setContentText("确定退出游戏?").setRightClickListener(new TwoButtonDialog.OnDialogButtonClickListener() {
            @Override
            public void onDialogButtonClick(View view) {
                Intent intent = new Intent(PieceActivity.this, BlueToochClientService.class);
                intent.putExtra(P.OP, P.OVER_GAME);
                startService(intent);
                finish();
            }
        }).setIsNeedToShow(true).build();

    }

    @Override
    public void onChessWin(boolean isBlackWin) {
        if(isBlackWin == isBlack){
            Toast.makeText(this, "你赢了", Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(this, "你输了", Toast.LENGTH_SHORT).show();
        }
        view_gobang.restartChess();
        view_gobang.setIsCanClick(true);
    }
}
