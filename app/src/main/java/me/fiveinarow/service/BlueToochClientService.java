package me.fiveinarow.service;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import me.fiveinarow.bean.Piece;
import me.fiveinarow.common.P;

/**
 * Created by caowenhua on 2015/11/29.
 */
public class BlueToochClientService extends Service {

    private UUID bluetooth_uuid = UUID.fromString("D7437E5C-E841-723C-74DF-4158924F6B26");
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothSocket socket;
    private BluetoothDevice device;
    private Timer beginSearchTimer;

    private boolean isWaiting;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        bluetoothAdapter.enable();
        //开始搜索
        beginSearchTimer = new Timer();
        beginSearchTimer.schedule(new BeginSearchTask(), 500, 500);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent != null){
            String op = intent.getStringExtra(P.OP);
            if(op != null){
                if(op.equals(P.CHESS_DOWN)){
                    Piece piece = (Piece) intent.getSerializableExtra("piece");
                    OutputThread thread = new OutputThread(piece);
                    thread.start();
                    isWaiting = true;
                    sendWaitingCast();
                }
                else if(op.equals(P.REQUEST_STATUS)){
                    sendWaitingCast();
                }
            }
        }
        return super.onStartCommand(intent, flags, startId);
//        device = bluetoothAdapter.getRemoteDevice(address);
    }

    private class BeginSearchTask extends TimerTask {
        @Override
        public void run() {
            if(bluetoothAdapter.isEnabled()){
                if(beginSearchTimer != null){
                    beginSearchTimer.cancel();
                }
                bluetoothAdapter.startDiscovery();
            }
        }
    }

    private class ClientThread extends Thread{
        @Override
        public void run() {
            super.run();
            try {
                bluetoothAdapter.cancelDiscovery();
                //创建一个Socket连接：只需要服务器在注册时的UUID号
                socket = device.createRfcommSocketToServiceRecord(bluetooth_uuid);
                socket.connect();
            }
            catch (IOException e)
            {
            }
        }
    }


    class OutputThread extends Thread{
        Piece piece;
        public OutputThread(Piece piece) {
            this.piece = piece;
        }
        @Override
        public void run() {
            super.run();
            try {
                OutputStream os = socket.getOutputStream();
                os.write(piece.toString().getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    class ReadThread extends Thread{
        @Override
        public void run() {
            super.run();
            while (true){
//                    socket.getInputStream().
            }
        }
    }

    private void sendWaitingCast(){
        Intent intent = new Intent("isWaiting");
        intent.putExtra("isWaiting", isWaiting);
        sendBroadcast(intent);
    }

    private class CheckBluetoothStatusTask extends TimerTask{
        @Override
        public void run() {
//            Intent i = new Intent(Parameters.BLUETOOTHSTATUS_BROADCAST);
//            i.putExtra(Parameters.BLUETOOTHSTATUS, bluetoothAdapter.isEnabled());
//            sendBroadcast(i);
        }
    }
}
