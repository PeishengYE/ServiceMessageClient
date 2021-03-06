package com.radioyps.arrayadaptertest;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ArrayAdapterTest extends AppCompatActivity {

    TextView mCallbackText;

    private static final String TAG = "ArrayAdapterTest";

    static final int MSG_REGISTER_CLIENT = 1;
    /**
     * Command to the service to unregister a client, ot stop receiving callbacks
     * from the service.  The Message's replyTo field must be a Messenger of
     * the client as previously given with MSG_REGISTER_CLIENT.
     */
    static final int MSG_UNREGISTER_CLIENT = 2;

    /**
     * Command to service to set a new value.  This can be sent to the
     * service to supply a new value, and will be sent by the service to
     * any registered clients with the new value.
     */
    static final int MSG_SET_VALUE = 3;
    static final int MSG_SHOW_MESG = 4;

    private static  boolean mIsBound = false;
    private Messenger mService;

    private static final String REMOTE_INTENT = "com.radioyps.apidemos.app.MessengerService";
    /**
     * Target we publish for clients to send messages to IncomingHandler.
     */
    private  static IncomingHandler mHandler =null ;
    private  Messenger mMessenger = null;
    private static  Context mContext = null;


    /**
     * Handler of incoming messages from service.
     */
    class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_SET_VALUE:
                    mCallbackText.setText("Received from service: " + msg.arg1);
                    Log.i(TAG, "IncomingHandler()>> got new Value: " + msg.arg1);
                    break;
                case MSG_SHOW_MESG:
                    String messg = (String)msg.obj;
                    mCallbackText.setText(messg);
                    Log.i(TAG, "IncomingHandler()>> got new Mesg: " + messg);
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }



    /**
     * Class for interacting with the main interface of the service.
     */
    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // This is called when the connection with the service has been
            // established, giving us the service object we can use to
            // interact with the service.  We are communicating with our
            // service through an IDL interface, so get a client-side
            // representation of that from the raw service object.
            mService = new Messenger(service);
            mCallbackText.setText("Attached.");
            Log.i(TAG, "onServiceConnected()>> getting Messenge from the remote side");

            // We want to monitor the service for as long as we are
            // connected to it.
            try {
                Log.i(TAG, "onServiceConnected()>> register client");
                Message msg = Message.obtain(null,
                        MSG_REGISTER_CLIENT);
                msg.replyTo = mMessenger;
                mService.send(msg);

                // Give it some value as an example.
                Log.i(TAG, "onServiceConnected()>> send new value");
                msg = Message.obtain(null,
                        MSG_SET_VALUE, this.hashCode(), 0);
                mService.send(msg);
            } catch (RemoteException e) {
                // In this case the service has crashed before we could even
                // do anything with it; we can count on soon being
                // disconnected (and then reconnected if it can be restarted)
                // so there is no need to do anything here.
            }

            // As part of the sample, tell the user what happened.
            Toast.makeText(ArrayAdapterTest.this, R.string.remote_service_connected,
                    Toast.LENGTH_SHORT).show();
        }

        public void onServiceDisconnected(ComponentName className) {
            // This is called when the connection with the service has been
            // unexpectedly disconnected -- that is, its process crashed.
            mService = null;
            mCallbackText.setText("Disconnected.");

            // As part of the sample, tell the user what happened.
            Toast.makeText(ArrayAdapterTest.this, R.string.remote_service_disconnected,
                    Toast.LENGTH_SHORT).show();
        }
    };

    void doBindService() {
        Intent apidemoIntent = null;
        apidemoIntent = new Intent(REMOTE_INTENT);

        apidemoIntent.setClassName("com.radioyps.messengerservice", "com.radioyps.messengerservice.MessengerService");
        mCallbackText.setText("Binding.");

        try {
            bindService(apidemoIntent, mConnection, Context.BIND_AUTO_CREATE);
            mIsBound = true;
        }catch (Exception e){
            e.printStackTrace();
            Log.e(TAG, " binding error ");
            mCallbackText.setText("Failed on Binding.");
        }


    }

    void doUnbindService() {
        if (mIsBound) {
            // If we have received the service, and hence registered with
            // it, then now is the time to unregister.
            if (mService != null) {
                try {
                    Message msg = Message.obtain(null,
                            MSG_UNREGISTER_CLIENT);
                    msg.replyTo = mMessenger;
                    mService.send(msg);
                } catch (RemoteException e) {
                    // There is nothing special we need to do if the service
                    // has crashed.
                }
            }

            // Detach our existing connection.
            unbindService(mConnection);
            mIsBound = false;
            mCallbackText.setText("Unbinding.");
        }
    }
    // END_INCLUDE(bind)

    /**
     * Standard initialization of this activity.  Set up the UI, then wait
     * for the user to poke it before doing anything.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.messenger_service_binding);
        mHandler = new IncomingHandler();
        mMessenger = new Messenger(mHandler);

        // Watch for button clicks.
        Button button = (Button)findViewById(R.id.bind);
        button.setOnClickListener(mBindListener);
        button = (Button)findViewById(R.id.unbind);
        button.setOnClickListener(mUnbindListener);
        button = (Button)findViewById(R.id.send_button);
        button.setOnClickListener(mConnectServer);

        button = (Button)findViewById(R.id.turn_off_screen);
        button.setOnClickListener(mTurnOffScreen);

        button = (Button)findViewById(R.id.trun_on_screen);
        button.setOnClickListener(mTurnOnScreen);

        button = (Button)findViewById(R.id.send_file);
        button.setOnClickListener(mSendingFile);

        mCallbackText = (TextView)findViewById(R.id.callback);
        mCallbackText.setText("Not attached.");
        mContext = ArrayAdapterTest.this;


    }

    private View.OnClickListener mBindListener = new View.OnClickListener() {
        public void onClick(View v) {
            doBindService();
        }
    };

    private View.OnClickListener mUnbindListener = new View.OnClickListener() {
        public void onClick(View v) {
            doUnbindService();
        }
    };

    private  View.OnClickListener mConnectServer  = new View.OnClickListener(){
        public void onClick(View v){
         String params[] =    new String[]{"hello"};
         ConnectToServer task = new ConnectToServer();
            task.execute(params);
        }
    };

    private  View.OnClickListener mTurnOnScreen  = new View.OnClickListener(){
        public void onClick(View v){
            String params[] =    new String[]{CommonConstants.MSG_TURN_SCREEN_ON};
            ConnectToServer task = new ConnectToServer();
            task.execute(params);
        }
    };


    private  View.OnClickListener mTurnOffScreen  = new View.OnClickListener(){
        public void onClick(View v){
            String params[] =    new String[]{CommonConstants.MSG_TURN_SCREEN_OFF};
            ConnectToServer task = new ConnectToServer();
            task.execute(params);
        }
    };

    private  View.OnClickListener mSendingFile  = new View.OnClickListener(){
        public void onClick(View v){
            String params[] =    new String[]{"Sending File 123"};
            makeToastOnScreen("Trying sending file");
            ReadFileWriteSocket task = new ReadFileWriteSocket();
            task.execute(params);
        }
    };
    @Override
    protected void onDestroy() {
        super.onDestroy();
        doUnbindService();
    }

    public static void makeToastOnScreen(String mesg){
        Message.obtain(mHandler, MSG_SHOW_MESG,mesg ).sendToTarget();
    }
}

