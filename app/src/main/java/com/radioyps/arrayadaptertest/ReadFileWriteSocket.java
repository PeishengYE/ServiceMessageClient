package com.radioyps.arrayadaptertest;




        import android.os.AsyncTask;
        import android.os.Environment;
        import android.util.Log;
        import android.widget.Toast;

        import java.io.BufferedInputStream;
        import java.io.ByteArrayOutputStream;
        import java.io.File;
        import java.io.FileInputStream;
        import java.io.IOException;
        import java.io.InputStream;
        import java.io.OutputStream;
        import java.net.ConnectException;
        import java.net.InetSocketAddress;
        import java.net.Socket;
        import java.net.SocketTimeoutException;
        import java.net.UnknownHostException;

/**
 * Created by developer on 31/10/16.
 */

public class ReadFileWriteSocket extends AsyncTask<String, Void, String>  {

    private static final String LOG_TAG = ReadFileWriteSocket.class.getName();
    final File file = new File(Environment.getExternalStorageDirectory(), CommonConstants.EXTERNAL_FILENAME);

@Override
protected String doInBackground(String... params) {


        Socket socket = null;
        String stringReceived = "";

        /* FIXME exception not caught ?*/
        int fileLength = (int)file.length();
        byte[] readFileBuffer = new byte[fileLength];
        try {

        Log.i(LOG_TAG, "sending file : " + CommonConstants.EXTERNAL_FILENAME );

        socket = new Socket();
        socket.connect(new InetSocketAddress(CommonConstants.SENDING_FILE_SERVER_IP_ADDR,
                CommonConstants.SENDING_FILE_connectPort), 2*1000);
        socket.setSoTimeout(CommonConstants.SOCKET_TIMEOUT);
        OutputStream socketOutputStream = socket.getOutputStream();
            InputStream fileInputStream = null;
            int totalBytesRead = 0;
            fileInputStream = new BufferedInputStream(new FileInputStream(file));
            while (totalBytesRead < fileLength){
                int bytesRemaining = fileLength - totalBytesRead;
                int bytesRead = fileInputStream.read(readFileBuffer, totalBytesRead, bytesRemaining);
                if(bytesRead > 0){
                    totalBytesRead = totalBytesRead + bytesRead;
                }
            }

            socketOutputStream.write(readFileBuffer);
            socketOutputStream.flush();
            Log.i(LOG_TAG, "finish sending file : " + CommonConstants.EXTERNAL_FILENAME );
            ArrayAdapterTest.makeToastOnScreen("Send successfully!");

        } catch (ConnectException e) {
        e.printStackTrace();
//                String errorStr = getConnectionError(e);


        } catch (UnknownHostException e) {
        e.printStackTrace();


        } catch (SocketTimeoutException e) {
            ArrayAdapterTest.makeToastOnScreen("Socket Timeout");
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
