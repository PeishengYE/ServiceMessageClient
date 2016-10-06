package com.radioyps.arrayadaptertest;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ArrayAdapterTest extends AppCompatActivity {

    TextView mCallbackText;

    private static final String TAG = "ArrayAdapterTest";

    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_array_adapter_test);
        /*
        ListView listView = (ListView)findViewById(R.id.list_view_layout);
        TextView list_item_textview = (TextView)findViewById(R.id.)
         */


        String[] data = {
                "data 1", "data 2", "data 3",
                "data 1", "data 2", "data 3",
                "data 1", "data 2", "data 3",
                "data 1", "data 2", "data 3",
                "data 1", "data 2", "data 3",
                "data 1", "data 2", "data 3",
                "data 1", "data 2", "data 3",
                "data 1", "data 2", "data 3",
                "data 1", "data 2", "data 3",
                "data 1", "data 2", "data 3",
                "data 1", "data 2", "data 3",
                "data 1", "data 2", "data 3",
                "data 1", "data 2", "data 3",
                "data 1", "data 2", "data 3",
                "data 1", "data 2", "data 3",
                "data 1", "data 2", "data 3"
        };

        /* convert string array to array list */
        List<String> listData = new ArrayList<String>(Arrays.asList(data));

        /* create a new adapter according the list view layout and text view */
        ArrayAdapter listAdapter = new ArrayAdapter<String> (
            getBaseContext(),
            R.layout.list_item_text_view,
            R.id.list_item_text_view_inside_layout,
            listData
        );



        ListView listView = (ListView)findViewById(R.id.list_view);
        listView.setAdapter(listAdapter);
        /* set adapter to the list layout */




    }




    /**
     * Handler of incoming messages from service.
     */
    class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MessengerService.MSG_SET_VALUE:
                    mCallbackText.setText("Received from service: " + msg.arg1);
                    Log.i(TAG, "IncomingHandler()>> got new Value: " + msg.arg1);
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

    /**
     * Target we publish for clients to send messages to IncomingHandler.
     */
    final Messenger mMessenger = new Messenger(new IncomingHandler());

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
                        MessengerService.MSG_REGISTER_CLIENT);
                msg.replyTo = mMessenger;
                mService.send(msg);

                // Give it some value as an example.
                Log.i(TAG, "onServiceConnected()>> send new value");
                msg = Message.obtain(null,
                        MessengerService.MSG_SET_VALUE, this.hashCode(), 0);
                mService.send(msg);
            } catch (RemoteException e) {
                // In this case the service has crashed before we could even
                // do anything with it; we can count on soon being
                // disconnected (and then reconnected if it can be restarted)
                // so there is no need to do anything here.
            }

            // As part of the sample, tell the user what happened.
            Toast.makeText(Binding.this, R.string.remote_service_connected,
                    Toast.LENGTH_SHORT).show();
        }

        public void onServiceDisconnected(ComponentName className) {
            // This is called when the connection with the service has been
            // unexpectedly disconnected -- that is, its process crashed.
            mService = null;
            mCallbackText.setText("Disconnected.");

            // As part of the sample, tell the user what happened.
            Toast.makeText(Binding.this, R.string.remote_service_disconnected,
                    Toast.LENGTH_SHORT).show();
        }
    };

    void doBindService() {
        // Establish a connection with the service.  We use an explicit
        // class name because there is no reason to be able to let other
        // applications replace our component.
        bindService(new Intent(Binding.this,
                MessengerService.class), mConnection, Context.BIND_AUTO_CREATE);
        mIsBound = true;
        mCallbackText.setText("Binding.");
    }

    void doUnbindService() {
        if (mIsBound) {
            // If we have received the service, and hence registered with
            // it, then now is the time to unregister.
            if (mService != null) {
                try {
                    Message msg = Message.obtain(null,
                            MessengerService.MSG_UNREGISTER_CLIENT);
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

        // Watch for button clicks.
        Button button = (Button)findViewById(R.id.bind);
        button.setOnClickListener(mBindListener);
        button = (Button)findViewById(R.id.unbind);
        button.setOnClickListener(mUnbindListener);

        mCallbackText = (TextView)findViewById(R.id.callback);
        mCallbackText.setText("Not attached.");
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
}
}