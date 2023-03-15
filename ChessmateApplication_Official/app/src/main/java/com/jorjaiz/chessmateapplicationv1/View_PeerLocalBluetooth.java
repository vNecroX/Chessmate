package com.jorjaiz.chessmateapplicationv1;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.jorjaiz.chessmateapplicationv1.Bluetooth.BTCommunication;
import com.jorjaiz.chessmateapplicationv1.Classes.Constants;
import com.jorjaiz.chessmateapplicationv1.Firebase.FireQuery;
import com.jorjaiz.chessmateapplicationv1.Firebase.Firebase;
import com.jorjaiz.chessmateapplicationv1.Parameters.CP;
import com.jorjaiz.chessmateapplicationv1.Parameters.ParamsGame;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class View_PeerLocalBluetooth extends AppCompatActivity implements Constants, BTCommunication.OnBTCommunicationListener
{
    private Timer responseMaster;
    private Timer responseSlave;

    private Timer sendingRequest;

    private Timer inactiveTime;
    int counterInactiveTime;

    ProgressDialog progressDialog;

    ParamsGame pG;

    int idOponent;
    String oponentName;

    static boolean masterConnected;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        try
        {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_view__peer_local_bluetooth);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

            Log.w(TAG, "----------------------------------------------------------------------");
            Log.w(TAG, "VIEW_PEERLOCALBLUETOOTH onCreate");

            Bundle parameters = getIntent().getExtras();
            pG = parameters.getParcelable("pG");

            Log.i(TAG, "-- KIND OF LOCAL: " + pG.getKindOfLocal());

            waitingAnotherPlayer();

            responseMaster = new Timer();
            responseSlave = new Timer();
            inactiveTime = new Timer();
            sendingRequest = new Timer();

            masterConnected = false;

            findViewById(R.id.btnBluetooth).setBackgroundResource(R.drawable.animation_bluetooth);
            AnimationDrawable animBluetooth = (AnimationDrawable) findViewById(R.id.btnBluetooth).getBackground();
            animBluetooth.start();

            if(View_BluetoothDevice.fromPvPLocal)
            {
                Log.i(TAG, "- FROM PVP LOCAL");

                BTCommunication.getInstance().setListener(this);

                if(pG.getKindOfLocal() == IS_LOCALPVP)
                    if(pG.getKindPlayer() == SLAVE)
                        BTCommunication.getInstance().sendData("IS_LOCALPVP");

                if(pG.getKindOfLocal() == IS_LOCALPVP_T)
                    if(pG.getKindPlayer() == SLAVE)
                        BTCommunication.getInstance().sendData("IS_LOCALPVP_T");

                if(pG.getKindPlayer() == MASTER)
                    masterListening();
                else
                    slaveListening();

                checkInactiveTime();

                if(pG.getKindPlayer() == SLAVE)
                {
                    sendingRequest();

                    Handler hdlr = new Handler();
                    hdlr.postDelayed(
                            ()->
                            {
                                if(!masterConnected)
                                {
                                    Toast.makeText(getBaseContext(), "Anfitrion no fue encontrado.", Toast.LENGTH_LONG).show();

                                    inactiveTime.cancel();
                                    responseSlave.cancel();
                                    responseMaster.cancel();
                                    sendingRequest.cancel();

                                    Intent i = new Intent(getBaseContext(), View_MainInterface.class);
                                    i.addFlags(FLAG_ACTIVITY_NEW_TASK);
                                    i.putExtra("idPlayer", CP.get().getIdPlayer());
                                    i.putExtra("namePlayer", CP.get().getNamePlayer());
                                    getBaseContext().startActivity(i);
                                }

                            }, 20000
                    );
                }
            }
            else
            {
                Log.i(TAG, "- FROM ANOTHER");

                Log.i(TAG, "- FROM FRAGMENT");
                BTCommunication.getInstance().setListener(this);
                View_ConnectBoard.fromFragment = false;
                masterListening();
                checkInactiveTime();
            }
        }
        catch (Exception e)
        {
            Log.e(TAG, "VIEW_PEERLOCALBLUETOOTH onCreate exception " + e.toString());
        }
    }

    @Override
    public void onBackPressed()
    {
    }

    public void waitingAnotherPlayer()
    {
        progressDialog = new ProgressDialog(this);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setTitle("Esperando jugador...");
        progressDialog.setMessage("Disfrute la partida.");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
    }

    // LISTENERS

    public void masterListening()
    {
        final Handler handler = new Handler();
        responseMaster.schedule( new TimerTask()
                {
                    public void run()
                    {
                        handler.post(new Runnable()
                        {
                            public void run()
                            {
                                Log.i(TAG, "Master listening...");
                                BTCommunication.getInstance().listenForData();
                            }
                        });
                    }
                },0,100);
    }

    public void slaveListening()
    {
        final Handler handler = new Handler();
        responseSlave.schedule(new TimerTask()
        {
            public void run()
            {
                handler.post(new Runnable()
                {
                    public void run()
                    {
                        Log.i(TAG, "Slave listening...");
                        BTCommunication.getInstance().listenForData();
                    }
                });
            }
        },0,100);
    }

    public void sendingRequest()
    {
        final Handler handler = new Handler();
        sendingRequest.schedule(new TimerTask()
        {
            public void run()
            {
                handler.post(new Runnable()
                {
                    public void run()
                    {
                        BTCommunication.getInstance().sendData("R");
                    }
                });
            }
        },4000,4000);
    }

    public void checkInactiveTime()
    {
        counterInactiveTime = 0;

        final Handler handler = new Handler();
        inactiveTime.schedule(new TimerTask()
        {
            public void run()
            {
                handler.post(new Runnable()
                {
                    public void run()
                    {
                        Log.i(TAG, "INACTIVE TIME -  <" + counterInactiveTime + ">");
                        counterInactiveTime++;

                        if(counterInactiveTime >= 60)
                        {
                            if(counterInactiveTime == 70)
                            {
                                Toast.makeText(getBaseContext(), "Tiempo límite de espera alcanzado, intente de nuevo", Toast.LENGTH_LONG).show();

                                inactiveTime.cancel();
                                responseSlave.cancel();
                                responseMaster.cancel();
                                sendingRequest.cancel();

                                Intent i = new Intent(getBaseContext(), View_MainInterface.class);
                                i.addFlags(FLAG_ACTIVITY_NEW_TASK);
                                i.putExtra("idPlayer", CP.get().getIdPlayer());
                                i.putExtra("namePlayer", CP.get().getNamePlayer());
                                getBaseContext().startActivity(i);
                            }
                            else if((70-counterInactiveTime)%2 != 0)
                            {
                                Toast.makeText(getBaseContext(), 70-counterInactiveTime+"", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
            }
        },0,1000);
    }

    // BLUETOOTH COMMUNICATION IMPLEMENTATIONS

    @Override
    public void bluetoothReply(String data)
    {
        if(!data.equals(""))
        {
            if(pG.getKindPlayer() == MASTER)
            {
                if(data.equals("R"))
                {
                    Log.w(TAG, "CONNECTION WITH THE OTHER IS READY!");
                    BTCommunication.getInstance().sendData("CONFIRM");
                }
                else if(data.equals("CONFIRM"))
                {
                    BTCommunication.getInstance().sendData("M@"+CP.get().getIdPlayer()+"@"+CP.get().getNamePlayer()+"@");
                }
                else if(data.charAt(0) == 'S')
                {
                    Log.w(TAG, "--- SLAVE CAPTIONED!");

                    ArrayList<String> aL = new ArrayList<>();

                    for(int i=0; i<data.length(); i++)
                    {
                        if(data.charAt(i) == '@')
                        {
                            String aux = "";

                            for(int j=i+1; j<data.length(); j++)
                            {
                                if(data.charAt(j) == '@')
                                {
                                    break;
                                }
                                else
                                {
                                    aux += data.charAt(j);
                                }
                            }

                            aL.add(aux);
                        }
                    }

                    idOponent = Integer.parseInt(aL.get(0));
                    oponentName = aL.get(1);

                    BTCommunication.getInstance().sendData("PEER");

                    responseSlave.cancel();
                    progressDialog.cancel();

                    gotoPlay();
                }
            }
            else
            {
                if(data.equals("R"))
                {
                    Log.w(TAG, "CONNECTION WITH THE OTHER IS READY!");
                    sendingRequest.cancel();
                }
                else if(data.charAt(0) == 'M')
                {
                    Log.w(TAG, "--- MASTER CAPTIONED!");

                    masterConnected = true;

                    ArrayList<String> aL = new ArrayList<>();

                    for(int i=0; i<data.length(); i++)
                    {
                        if(data.charAt(i) == '@')
                        {
                            String aux = "";

                            for(int j=i+1; j<data.length(); j++)
                            {
                                if(data.charAt(j) == '@')
                                {
                                    break;
                                }
                                else
                                {
                                    aux += data.charAt(j);
                                }
                            }

                            aL.add(aux);
                        }
                    }

                    idOponent = Integer.parseInt(aL.get(0));
                    oponentName = aL.get(1);

                    BTCommunication.getInstance().sendData("S@"+CP.get().getIdPlayer()+"@"+CP.get().getNamePlayer()+"@");

                    responseMaster.cancel();
                    progressDialog.cancel();

                    Handler hdlr = new Handler();
                    hdlr.postDelayed(
                            () ->
                            {
                                gotoPlay();

                            }, 2000
                    );
                }
            }
        }
    }

    // GO TO PLAY

    public void gotoPlay()
    {
        pG.setIdOponent(idOponent);
        pG.setOponentName(oponentName);
        pG.setKeyComunnic("");
        pG.setDifficulty(NULL);

        responseSlave.cancel();
        responseMaster.cancel();
        inactiveTime.cancel();
        sendingRequest.cancel();

        Intent intentBluetooth = new Intent(this, View_PiecesColor_Bluetooth.class);
        intentBluetooth.putExtra("pG", pG);
        startActivity(intentBluetooth);
    }
}
