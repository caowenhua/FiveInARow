package me.fiveinarow.service;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import me.fiveinarow.bean.Piece;
import me.fiveinarow.common.P;

/**
 * Created by caowenhua on 2015/11/29.
 */
public class BlueToochServerService extends Service {

    private UUID bluetooth_uuid = UUID.fromString("D7437E5C-E841-723C-74DF-4158924F6B26");
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothServerSocket mServerSocket;
    private BluetoothSocket socket = null;
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
        bluetoothAdapter.setName("gameServer");
        bluetoothAdapter.enable();
        beginSearchTimer = new Timer();
        beginSearchTimer.schedule(new BeginSearchTask(), 500, 500);

        isWaiting = false;
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
    }

    private class AcceptThread extends Thread {
        public AcceptThread() {
            // Use a temporary object that is later assigned to mmServerSocket,
            try {
                // MY_UUID is the app's UUID string, also used by the client code
                BluetoothServerSocket tmp = bluetoothAdapter.listenUsingRfcommWithServiceRecord("FiveInARow", bluetooth_uuid);
                mServerSocket = tmp;
            } catch (IOException e) {
            }
        }

        public void run() {
            socket = null;
            // Keep listening until exception occurs or a socket is returned
            if(mServerSocket != null){
                while (true) {
                    try {
                        socket = mServerSocket.accept();
                    } catch (IOException e) {
                        Intent intent = new Intent("connectionServer");
                        intent.putExtra("connectionServer", false);
                        sendBroadcast(intent);
                        break;
                    }
                    // If a connection was accepted
                    if (socket != null) {
                        // Do work to manage the connection (in a separate thread)
                        //manageConnectedSocket(socket);
                        String s = socket.getRemoteDevice().getName() + "/n" + socket.getRemoteDevice().getAddress();
                        Intent intent = new Intent("connectionServer");
                        intent.putExtra("connectionServer", true);
                        sendBroadcast(intent);
                        new ReadThread().start();
                        break;
                    }
                    else{
                        Intent intent = new Intent("connectionServer");
                        intent.putExtra("connectionServer", false);
                        sendBroadcast(intent);
                    }
                }
            }
        }

        /** Will cancel the listening socket, and cause the thread to finish */
        public void cancel() {
            try {
                mServerSocket.close();
            } catch (IOException e) { }
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
        InputStream inputStream;
        byte[] buffer = new byte[1024];
        int bytes;
        @Override
        public void run() {
            super.run();
            try {
                inputStream = socket.getInputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            while (true){
                try {
                    if((bytes = inputStream.read(buffer)) > 0 )
                    {
                        byte[] buf_data = new byte[bytes];
                        for(int i=0; i<bytes; i++)
                        {
                            buf_data[i] = buffer[i];
                        }
                        String data = new String(buf_data);
                        isWaiting = false;
                        sendWaitingCast();
                        sendDataCast(data);
                    }
                } catch (IOException e) {
                    try {
                        inputStream.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                    finally{
                    }
                    break;
                }
            }
        }
    }

    private class BeginSearchTask extends TimerTask {
        @Override
        public void run() {
            if(bluetoothAdapter.isEnabled()){
                if(beginSearchTimer != null){
                    beginSearchTimer.cancel();
                }
                new AcceptThread().start();
            }
        }
    }

    private void sendWaitingCast(){
        Intent intent = new Intent("isWaiting");
        intent.putExtra("isWaiting", isWaiting);
        sendBroadcast(intent);
    }

    private void sendDataCast(String data){
        Intent intent = new Intent("data");
        intent.putExtra("data", data);
        sendBroadcast(intent);
    }
}
