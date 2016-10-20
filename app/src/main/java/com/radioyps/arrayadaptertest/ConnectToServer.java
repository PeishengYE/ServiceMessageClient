package com.radioyps.arrayadaptertest;

import android.os.AsyncTask;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

/**
 * Created by developer on 20/10/16.
 */

    public class ConnectToServer extends AsyncTask<String, Void, String> {

    private static final String LOG_TAG = ConnectToServer.class.getName();

    @Override
    protected String doInBackground(String... params) {


        Socket socket = null;
        String stringReceived = "";
        String cmd = params[0];

        try {

            Log.i(LOG_TAG, "sending message: " + cmd);
            socket = new Socket(CommonConstants.IP_ADDR, CommonConstants.connectPort);
            socket.setSoTimeout(CommonConstants.SOCKET_TIMEOUT);

            ByteArrayOutputStream byteArrayOutputStream =
                    new ByteArrayOutputStream(1024);

            byte[] buffer = new byte[1024];

            int bytesRead;
            InputStream inputStream = socket.getInputStream();
            OutputStream outputStream = socket.getOutputStream();


            outputStream.write(cmd.getBytes());
            outputStream.flush();

//
//            while ((bytesRead = inputStream.read(buffer)) != -1) {
//                byteArrayOutputStream.write(buffer, 0, bytesRead);
//                stringReceived += byteArrayOutputStream.toString("UTF-8");
//            }
//            outputStream.close();
//            inputStream.close();


        } catch (ConnectException e) {
            e.printStackTrace();
//                String errorStr = getConnectionError(e);


        } catch (UnknownHostException e) {
            e.printStackTrace();


        } catch (SocketTimeoutException e) {

            e.printStackTrace();


        } catch (IOException e) {
            e.printStackTrace();


        } finally {

            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


        Log.d(LOG_TAG, "sendCmd()>> reply with " + stringReceived);
            return stringReceived;


    }
}


