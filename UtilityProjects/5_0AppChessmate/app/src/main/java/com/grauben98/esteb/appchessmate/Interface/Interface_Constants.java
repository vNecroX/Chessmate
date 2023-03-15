package com.grauben98.esteb.appchessmate.Interface;

import com.grauben98.esteb.appchessmate.Fragment.Fragment_Chat;
import com.grauben98.esteb.appchessmate.View_BluetoothDummy;
import com.grauben98.esteb.appchessmate.View_ConnectBoard;
import com.grauben98.esteb.appchessmate.View_GI_PvPOnline;
import com.grauben98.esteb.appchessmate.View_MainConfiguration;
import com.grauben98.esteb.appchessmate.View_MainInterface;
import com.grauben98.esteb.appchessmate.View_PeerLocalBluetooth;

public interface Interface_Constants
{
    String emitter = "y";
    String receptor = "x";
    String cas = "SeeAllMessages";
    String cas1 = "UnseenMessages";

    String url = "https://estudiantescrazys.000webhostapp.com/See_Messages.php";
    String URL_DATA = url + "?emitter=" + emitter; //Here put your user name from previous activities.

    String c = "getConfiguration";
    String cSetAllConfiguration = "setAllConfiguration";
    String cSetBoardConfiguration = "setBoardConfiguration";
    String idUser = "1";

    String URL = "https://estudiantescrazys.000webhostapp.com/MainPHP_WEB.php";
    String URL_DATABASE = URL + "?case=" + c + "&idUser=" + idUser; //Here put obtained user ID from previous activities.

    String TAG = View_GI_PvPOnline.class.getSimpleName(); // For Log
    String TAG1 = Fragment_Chat.class.getSimpleName(); // For Log
    String TAG2 = View_BluetoothDummy.class.getSimpleName(); // For Log
    String TAG3 = View_MainInterface.class.getSimpleName(); // For Log
    String TAG4 = View_MainConfiguration.class.getSimpleName(); // For Log
    String TAG5 = View_PeerLocalBluetooth.class.getSimpleName(); // For Log
    String TAG6 = View_ConnectBoard.class.getSimpleName(); // For Log
}
