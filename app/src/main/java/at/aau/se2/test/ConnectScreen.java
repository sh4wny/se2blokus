package at.aau.se2.test;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.Connections;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Activity which handles the connection establishment.
 */
public class ConnectScreen extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        Connections.ConnectionRequestListener,
        Connections.MessageListener,
        Connections.EndpointDiscoveryListener,
        View.OnClickListener {

    //Network fields
    private GoogleApiClient apiClient;
    //private Connection connection;
    private static final long CONNECTION_TIME_OUT = 10000L;
    private boolean isHost;
    private boolean doHosting;
    private boolean isConnected;
    private String remoteHostEndpoint;
    private List<String> remotePeerEndpoints = new ArrayList<>();
    private HashMap<String, String> idNameMap = new HashMap<>();
    public int goneOn = 0;

    //Graphic fields
    private TextView actStatus;
    private TextView connectionButton;
    private TextView disconnectButton;
    private TextView startButton;

    private String username = "";
    private String hostName = "";
    //private static int participants = 0;
    private static final int FONT_SIZE_SMALL = 16;
    private static final int FONT_SIZE_LARGE = 19;

    private static final String[] MESSAGE_CODES = {"COLOROK-","FULLSCREEN","N-","NEWPLAYER-","REMOVE-","START","REMOVE-", "FINISHED-"};

    //TableView
    private TextView player1;
    private TextView player2;
    private TextView player3;
    private TextView player4;


    /**
     * oncreate Function called after the activity is launched.
     *
     * @param savedInstanceState the saved instance
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //debugging("onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connection);

        // Hide the status bar.
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);


        Intent i = getIntent();
        doHosting = Boolean.valueOf(i.getStringExtra("host"));
        apiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Nearby.CONNECTIONS_API)
                .build();
        //debugging("api erstellt");
        setupView();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        if (doHosting) {
            connectionButton.setText(R.string.connection_advertise_connection);
        } else {
            connectionButton.setText(R.string.connection_search_existing_game);
        }
    }

    /**
     * onStart method which connects the apiClient and asks the user for a name
     */
    @Override
    protected void onStart() {
        //debugging("onStart");
        super.onStart();
        apiClient.connect();
        //debugging("api verbunden");

        //Username dialog
        final EditText name = new EditText(this);
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        name.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setTitle(R.string.connection_select_username);
        builder.setView(name);
        builder.setPositiveButton("SET NAME", null);
        builder.setNegativeButton("GO BACK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final Intent intent = new Intent("at.aau.se2.test.STARTSCREEN");
                startActivity(intent);
            }
        });

        final AlertDialog dialog = builder.create();
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(name.getText().toString().trim().length() > 0){
                    username = name.getText().toString();
                    dialog.dismiss();
                }
                else{
                    Toast.makeText(getApplicationContext(), "Insert a name!", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });

    }

/**
 * String actName = "";
 if (name.getText().toString().trim().length() > 0) {
 actName = name.getText().toString();
 } else {
 actName = "NoNameUser";
 }
 if (doHosting) {
 hostName = username;
 }
 //
 // debugging("start - "+ username + " + hostname "+ hostName);
 */
    /**
     * Ends the connection
     */
    /*@Override
    protected void onStop() {
        //debugging("onStop");
        super.onStop();

        //if( apiClient != null && apiClient.isConnected() ) {
        //    Nearby.Connections.stopAdvertising(apiClient);
        //    disconnect();
        //    apiClient.disconnect();
        //    finalizeDisconnection();
        //}
        //debugging("stop");
    }*/

    /**
     * Create fields to access/write from/to the graphic elements of the connectScreen.
     */
    private void setupView() {
        actStatus = (TextView) findViewById(R.id.text_status);
        connectionButton = (TextView) findViewById(R.id.button_connection);
        disconnectButton = (TextView) findViewById(R.id.button_disconnection);
        startButton = (TextView) findViewById(R.id.button_start);
        startButton.setVisibility(View.GONE);

        player1 = (TextView) findViewById(R.id.player1);
        player2 = (TextView) findViewById(R.id.player2);
        player3 = (TextView) findViewById(R.id.player3);
        player4 = (TextView) findViewById(R.id.player4);


        TextView playerName = (TextView) findViewById(R.id.button_playername);
        TextView logo = (TextView) findViewById(R.id.logo);

        Typeface font = Typeface.createFromAsset(getAssets(), "blocked.ttf");

        connectionButton.setTypeface(font);
        connectionButton.setTypeface(font);
        disconnectButton.setTypeface(font);
        startButton.setTypeface(font);
        player1.setTypeface(font);
        player2.setTypeface(font);
        player3.setTypeface(font);
        player4.setTypeface(font);
        playerName.setTypeface(font);
        logo.setTypeface(font);

        connectionButton.setTextSize(FONT_SIZE_LARGE);
        connectionButton.setTextSize(FONT_SIZE_LARGE);
        disconnectButton.setTextSize(FONT_SIZE_LARGE);
        startButton.setTextSize(FONT_SIZE_LARGE);
        playerName.setTextSize(FONT_SIZE_LARGE);

        logo.setTextSize(60);

        player1.setTextSize(FONT_SIZE_SMALL);
        player2.setTextSize(FONT_SIZE_SMALL);
        player3.setTextSize(FONT_SIZE_SMALL);
        player4.setTextSize(FONT_SIZE_SMALL);

        disconnectButton.setAlpha(.5f);
        disconnectButton.setClickable(false);
        setupButtons();
    }



    /**
     * Set button listeners.
     */
    private void setupButtons() {
        connectionButton.setOnClickListener(this);
        disconnectButton.setOnClickListener(this);
        startButton.setOnClickListener(this);
    }


    /**
     * Checks if the device has a (WIFI)-connection to participate in a game.
     *
     * @return true if it is connected, false if not
     */
    private boolean isConnectedToNetwork() {
        //debugging("isConnectedToNetwork");
        ConnectivityManager connManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (info != null && info.isConnectedOrConnecting()) {
            //debugging("mit netzwerk verbunden");
            return true;
        }
        //debugging("nicht verbunden");
        return false;
    }

    /**
     * Disconnects a device from the others. While a host-disconnection obviously breaks up the whole
     * connection, a normal peer-disconnection does not affect the other participants.
     */
    private void disconnect() {
        debugging("disconnect");
        if (!isConnectedToNetwork() || !isConnected)
            return;

        if (isHost) {
            idNameMap.clear();
            sendMessage("Shutting down host");
            Nearby.Connections.stopAdvertising(apiClient);
            Nearby.Connections.stopAllEndpoints(apiClient);
            actStatus.setText(R.string.status_not_connected);
            remotePeerEndpoints.clear();
            finalizeDisconnection();
            clearTableView();
        }
        else {
            if (TextUtils.isEmpty(remoteHostEndpoint)) {
                Nearby.Connections.stopDiscovery(apiClient, getString(R.string.service_id));
                return;
            }
            idNameMap.clear();
            sendMessage("Disconnecting");
            Nearby.Connections.disconnectFromEndpoint(apiClient, remoteHostEndpoint);
            remoteHostEndpoint = null;
            actStatus.setText(R.string.status_disconnected);
            finalizeDisconnection();
            clearTableView();
        }


    }

    /**
     * A host method. Advertises a connection to possible peers after checking it checks if the device has a WIFI-connection.
     */
    private void advertise() {
        if (!isConnectedToNetwork()) {
            actStatus.setText(R.string.status_please_connect_to_wifi);
            disconnectButton.setAlpha(0.5f);
            disconnectButton.setClickable(false);
            connectionButton.setAlpha(1f);
            connectionButton.setClickable(true);
            return;
        }

        idNameMap.put(Nearby.Connections.getLocalDeviceId(apiClient), username);
        String name = "Nearby Advertising";

        Nearby.Connections.startAdvertising(apiClient, name, null, CONNECTION_TIME_OUT, this).setResultCallback(new ResultCallback<Connections.StartAdvertisingResult>() {
            @Override
            public void onResult(Connections.StartAdvertisingResult result) {
                if (result.getStatus().isSuccess()) {
                    actStatus.setText(R.string.status_advertising);
                    idNameMap.put(Nearby.Connections.getLocalDeviceId(apiClient), username);
                    finalizeConnection();
                    setUpTableView();
                }
            }
        });
    }


    /**
     * A peer method. Searches for existing advertising hosts with the same serviceID ("bloxxid").
     * The onConnectionRequest method gets automatically called in the case a connection between a peer and a host would be possible.
     */
    private void discover() {
        //debugging("discover");
        if (!isConnectedToNetwork())
            return;
        String serviceID = getString(R.string.service_id);
        Nearby.Connections.startDiscovery(apiClient, serviceID, 10000L, this).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(Status status) {
                if (status.isSuccess()) {
                    actStatus.setText(R.string.status_discovering);
                }
            }
        });
    }

    @Override
    public void onConnected(Bundle bundle) {
        //debugging("onConnected");
    }

    /**
     * Sets up the table view and fills it with the information
     * contained in the idMap.
     *
     */
    private void setUpTableView(){
        clearTableView();
        if(isHost){
            idNameMap.put(Nearby.Connections.getLocalDeviceId(apiClient), username);
            player1.setText(username);
            int i = 2;

            for (Map.Entry<String, String> entry : idNameMap.entrySet())
            {
                if(!entry.getValue().equals(username)) {
                    String textViewPlayer = "player" + i;
                    int resPlayer = getResources().getIdentifier(textViewPlayer, "id", getPackageName());
                    ((TextView) findViewById(resPlayer)).setText(entry.getValue());
                    i++;
                }
            }

        }
        else{
            String[] rem = remoteHostEndpoint.split(":");
            String remoteHost = rem[0];
            player1.setText(idNameMap.get(remoteHost));
            int i = 2;
            for (Map.Entry<String, String> entry : idNameMap.entrySet())
            {
                if(!entry.getKey().equals(remoteHost)) {
                    String textViewPlayer = "player" + i;
                    int resPlayer = getResources().getIdentifier(textViewPlayer, "id", getPackageName());
                    ((TextView) findViewById(resPlayer)).setText(entry.getValue());
                    i++;
                }
            }
        }
    }

    /**
     * Clears the table view content.
     */
    private void clearTableView(){
        for (int i=1; i<=4; i++)
        {
                String textViewPlayer = "player" + i;
                int resPlayer = getResources().getIdentifier(textViewPlayer, "id", getPackageName());
                ((TextView) findViewById(resPlayer)).setText("");
        }
    }


    @Override
    public void onConnectionSuspended(int i) {
        apiClient.reconnect();
    }

    /**
     * The onConnectionRequest method gets fired each time there is one, but it is only really handled by the host of the connection.
     * The method tries to automatically accept the connection with the other device and subsequently inserts the new peer into the peerlist.
     *
     * @param remoteEndpointId   deviceID + number
     * @param remoteDeviceId     deviceID
     * @param remoteEndpointName deviceID of communication partner
     * @param payload
     */
    @Override
    public void onConnectionRequest(final String remoteEndpointId, final String remoteDeviceId, final String remoteEndpointName, final byte[] payload) {
        //debugging("onConnectionRequest");
        if (isHost) {
            //debugging("host trying to accept request");
            Nearby.Connections.acceptConnectionRequest(apiClient, remoteEndpointId, payload, this).setResultCallback(new ResultCallback<Status>() {
                @Override
                public void onResult(Status status) {
                    if (status.isSuccess()) {
                        if (!remotePeerEndpoints.contains(remoteEndpointId)) {
                            remotePeerEndpoints.add(remoteEndpointId);
                        }
                        String user = new String(payload, StandardCharsets.UTF_8);
                        //debugging("user: "+user);
                        debugging("name of partner" + remoteEndpointId);
                        idNameMap.put(remoteDeviceId, user);
                        //debugging("INFORMATION: "+ remoteEndpointId + ", "+ remoteDeviceId+", "+ remoteEndpointName);
                        for (Map.Entry<String, String> entry : idNameMap.entrySet()) {
                            sendMessage("NEWPLAYER-" + entry.getKey() + "-" + entry.getValue());
                        }
                        //sendMessage(username + " connected!");
                        //participants++;
                        //debugging("Request accepted NR: "+ idNameMap.size());
                        setUpTableView();
                        checkStartGame();

                    }
                }
            });
        } else {
            //debugging("no host - not accepting");
            Nearby.Connections.rejectConnectionRequest(apiClient, remoteEndpointId);
        }
    }

    /**
     *  Checks if a game start is possible. In case it it, there is a start button popping up
     *  at the hosts screen.
     */
    private void checkStartGame() {
        if (isHost) {
            if (idNameMap.size() > 1) {
                startButton.setVisibility(View.VISIBLE);
                startButton.setClickable(true);
            } else {
                startButton.setVisibility(View.GONE);
                startButton.setClickable(false);
            }
        }
    }

    /**
     * TODO: MUSS ANGEPASST WERDEN!!
     * Finallizes the connection through setting the parameters and telling the others
     * that this device is now part of the game.
     */
    private void finalizeConnection() {
        if (!isConnected) {
            isConnected = true;
        }

        String partString = listCurrentParticipants();
        sendMessage(partString);
        checkStartGame();
    }

    /**
     * TODO: MUSS ANGEPASST WERDEN!!
     * Finallizes the disconnection through setting the parameters and telling the others
     * that this device is now not part of the game anymore.
     */
    private void finalizeDisconnection() {
        if (isConnected) {
            isConnected = false;
        }
        String partString = listCurrentParticipants();
        sendMessage(partString);
        checkStartGame();

    }

    /**
     * TODO: Deprecated. Has to be either made in another function or adjusted.
     * @return
     */
    private String listCurrentParticipants() {
        String particip = "LIST-";
        for (Map.Entry<String, String> entry : idNameMap.entrySet()) {
            particip += entry.getValue() + "-" + entry.getKey() + "-\n";
        }
        return particip;
    }

    /**
     * Method to send a message from to all other participants.
     *
     * @param message text which is sent
     */
    private void sendMessage(String message) {
        if(!remotePeerEndpoints.isEmpty()) {
            if (isHost) {
                Nearby.Connections.sendReliableMessage(apiClient, remotePeerEndpoints, (message).getBytes());
            }
        }
        else {
            if (remoteHostEndpoint != null) {
                Nearby.Connections.sendReliableMessage(apiClient, remoteHostEndpoint, (message).getBytes());
            }
        }
    }

    /**
     * Automatically sends a connectionRequest to a host-device in case an existing
     * session is found (serviceID has to be the same). That connectionRequest contains
     * the users name. This connectionRequest is also automatically accepted by the host.
     *
     * @param endpointId   Id of the endpoint
     * @param deviceId     deviceId of the endpoint
     * @param serviceId    serviceId of the endpoint
     * @param endpointName name of the endpoint
     */
    @Override
    public void onEndpointFound(String endpointId, final String deviceId, final String serviceId, String endpointName) {

        byte[] payload = username.getBytes(StandardCharsets.UTF_8);
        Nearby.Connections.sendConnectionRequest(apiClient, deviceId, endpointId, payload, new Connections.ConnectionResponseCallback() {
            @Override
            public void onConnectionResponse(String s, Status status, byte[] bytes) {
                if (status.isSuccess()) {
                    idNameMap.put(Nearby.Connections.getLocalDeviceId(apiClient), username);
                    String text = R.string.status_connected_to + " " + hostName;
                    actStatus.setText(text);
                    Nearby.Connections.stopDiscovery(apiClient, serviceId);
                    remoteHostEndpoint = s;
                    finalizeConnection();
                    setUpTableView();

                } else {
                    String text = R.string.status_connection_to + hostName + R.string.status_connection_to_failed;
                    actStatus.setText(text);
                }
            }
        }, this);
    }

    /**
     * Finalizes the disconnection and resets the tableView.
     *
     * @param s String parameter
     */
    @Override
    public void onEndpointLost(String s) {
        dev("onEndpointLost");

        if(!Connection.getInstance().getColorScreen().equals(null)){
            dev("yeah");
            if(!Connection.getInstance().getFullscreenActivity().equals(null)){
                dev("fs");
                FullscreenActivity fs = Connection.getInstance().getFullscreenActivity();
                fs.endGame();
            }
            else{
                dev("cs");
                ColorScreen cs = Connection.getInstance().getColorScreen();
                cs.endGame();
            }
            if(isHost && !remotePeerEndpoints.equals(null)){
                sendMessage("ENDGAME");
            }
        }
        else{
            dev("cn");
            disconnectButton.performClick();
        }
        if (!isHost) {
            dev("onEndpointLost");
            finalizeDisconnection();
            if(!Connection.getInstance().getColorScreen().equals(null)) {
                clearTableView();
            }
        }


    }

    /**
     * Checks if a byte array contains numbers which are bigger than 20.
     *
     * @param b block-byte-array
     * @return true/false
     */
    private static boolean onlyNum(byte[] b) {

        for (byte x : b) {
            if (x > 20) {
                return false;
            }
        }
        return true;
    }


    /**
     * Method to handle an incomming message depending on a prefix and either processes it
     * whithin this activity or passes it to one of the other activities.
     * At the beginning, it is checked if the payload is "onlyNum", so it just contains numbers from 0-20.
     * It this onlyNum returns true this means that there is no String prefix in the payload and
     * that means it is a stone + additional information - message from the Fullscreenactivity.
     *
     * If the host receives a message, he has to send it to all other endpoints.
     *
     * @param endpointId sender
     * @param payload message which was sent
     * @param isReliable is it reliable or not
     */
    @Override
    public void onMessageReceived(String endpointId, byte[] payload, boolean isReliable) {
        //debugging("onMessageReceived");
        if (onlyNum(payload)) {
            deb("setStone");
            FullscreenActivity full = Connection.getInstance().getFullscreenActivity();
            full.onMessageReceived(endpointId, payload, isReliable);

        } else {
            String message = new String(payload);
            debugging("sender " + endpointId + ", message " + message);
            if (message.startsWith(MESSAGE_CODES[3])) {
                debugging("try to add new player to player list");
                String[] messArray = message.split("-");
                if (messArray.length == 3) {
                    String playerID = messArray[1];
                    String playerName = messArray[2];
                    idNameMap.put(playerID, playerName);
                    setUpTableView();
                } else {
                    debugging("message array has wrong format");
                }
                if (isHost) {
                    sendMessage(message);
                    debugging("send new player");
                }
            } else if (message.startsWith(MESSAGE_CODES[4])) {
                debugging("try remove player to player list");
                String[] messArray = message.split("-");
                if (messArray.length == 2) {
                    String playerID = messArray[1];
                    idNameMap.remove(playerID);
                    setUpTableView();
                } else {
                    debugging("message array has wrong format");
                }
                if (isHost) {
                    sendMessage(message);
                    debugging("remove player");
                }
            } else if (message.startsWith(MESSAGE_CODES[0])) {
                debugging("color is here");
                ColorScreen cs = Connection.getInstance().getColorScreen();
                cs.onMessageReceived(endpointId, payload, isReliable);
            } else if (message.startsWith(MESSAGE_CODES[5])) {
                debugging("try to start game");
                startButton.performClick();
            }
            else if (message.equals("GOON")) {
                FullscreenActivity full = Connection.getInstance().getFullscreenActivity();
                full.isItMyTurn(false, null);
            }
            /*else if (message.startsWith(MESSAGE_CODES[7])) {
                FullscreenActivity full = Connection.getInstance().getFullscreenActivity();
                String mess = message.split("-")[1];
                full.setFinished(mess);
                if(isHost){
                    sendMessage(mess);
                }
            }*/
            else if (message.startsWith(MESSAGE_CODES[2])) {
                debugging("startnum");
                debugging(message);
                String[] arr = message.split("-");
                String mess = arr[1];
                ColorScreen cs = Connection.getInstance().getColorScreen();
                cs.setTurnString(mess);
            }
            else if (message.startsWith(MESSAGE_CODES[1])) {
                debugging("start it!"+message);
                ColorScreen cs = Connection.getInstance().getColorScreen();
                cs.onMessageReceived(endpointId, payload, isReliable);
            }
            else if (message.startsWith("CLAIM-")){
                dev("i realized a claim");

                FullscreenActivity full = Connection.getInstance().getFullscreenActivity();
                full.claim(message.split("-")[1]);
                if(isHost){
                    sendMessage(message);
                }
            }
            else if (message.startsWith("WINNER-")){
                FullscreenActivity full = Connection.getInstance().getFullscreenActivity();
                full.winAdd(message.split("-")[1]);
                if(isHost){
                    sendMessage(message);
                }
            }
            else if (message.equals("ENDGAME")){
                onEndpointLost("s");
            }
            else {

                if (isHost) {
                    sendMessage(message);
                }
            }
        }
    }

    /**
     * Gets triggered at at the host if (Nearby.Connections.) disconnectFromEndpoint()
     * or stopAllEndpoints() are called at a device.
     *
     * @param s complete ID from disconnected partner
     */
    @Override
    public void onDisconnected(String s) {
        if (!isHost && isConnected) {
            disconnectButton.performClick();
            clearTableView();
        } else if (isHost && isConnected) {
            //debugging("... "+s);
            String id = "";
            if (s.contains(":")) {
                String[] idArr = s.split(":");
                id = idArr[0];
                idNameMap.remove(id);
            }
            sendMessage(MESSAGE_CODES[6] + id);
            checkStartGame();
            setUpTableView();
        }
    }

    /**
     * Method to handle connection failures.
     *
     * @param connectionResult Parameter containing the error message
     */
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        //debugging("no connection possible"+connectionResult);
        if (!isHost) {
            finalizeDisconnection();
        }
    }

    /**
     * onClick method for the different buttons.
     * button-connection-click:    evaluates if the current device is hosting or joining - and then decides
     * if it should advertise or discover a connection.
     * <p/>
     * button-disconnection-click: connection is shut down and if it is a host, the whole connection gets dissolved
     * <p/>
     * button-send-click:          sends a message to all other participants
     * <p/>
     * button-start-click:         progresses to the next view and pushes the same button at the other participants
     *
     * @param v View which is clicked
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_connection:
                disconnectButton.setAlpha(1f);
                disconnectButton.setClickable(true);
                connectionButton.setAlpha(0.5f);
                connectionButton.setClickable(false);
                if (doHosting) {
                    isHost = true;
                    advertise();
                    actStatus.setText(R.string.status_advertising + " " + username);
                } else {
                    isHost = false;
                    discover();
                    actStatus.setText(R.string.status_searching + " " + username);
                }
                break;

            case R.id.button_disconnection:
                if (isConnected) {
                    debugging("buttondisconnection");
                    disconnect();
                }
                disconnectButton.setAlpha(0.5f);
                disconnectButton.setClickable(false);
                connectionButton.setAlpha(1f);
                connectionButton.setClickable(true);
                actStatus.setText(R.string.status_disconnected);
                break;



            case R.id.button_start:
                if (isHost) {
                    sendMessage("START");
                    Nearby.Connections.stopAdvertising(apiClient);
                }
                //set the api in the Singleton
                Connection.getInstance().setApiClient(apiClient);
                Connection.getInstance().setRemotePeerEndpoints(remotePeerEndpoints);
                //chooseStoneColor();
                final Intent intent = new Intent("at.aau.se2.test.COLORSCREEN");
                intent.putExtra("map", idNameMap);
                intent.putExtra("host", isHost);
                intent.putExtra("hostEnd", remoteHostEndpoint);
                startActivity(intent);
                break;
            default:
                throw new ExceptionInInitializerError("onClick failure");
        }
    }


    /**
     * Helper method for debugging
     *
     * @param debMessage debug-message
     */
    private void debugging(String debMessage) {
        Log.d("tobiasho", debMessage);
    }

    private String output() {
        String output = "";
        for (Map.Entry key : idNameMap.entrySet()) {
            output += (key + " " + idNameMap.get(key)) + " ";
        }
        output += " - ANZ:" + idNameMap.size();
        return output;
    }

    private void deb(String debMessage) {
        Log.d("asdf", debMessage);
    }

    private void dev(String debMessage) {
        Log.d("asdfconn", debMessage);
    }

}