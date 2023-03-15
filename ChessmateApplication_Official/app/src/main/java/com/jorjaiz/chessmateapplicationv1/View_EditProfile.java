package com.jorjaiz.chessmateapplicationv1;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.jorjaiz.chessmateapplicationv1.Classes.Constants;
import com.jorjaiz.chessmateapplicationv1.Database.MySQL;
import com.jorjaiz.chessmateapplicationv1.Database.Query;
import com.jorjaiz.chessmateapplicationv1.Firebase.FireQuery;
import com.jorjaiz.chessmateapplicationv1.Firebase.Firebase;
import com.jorjaiz.chessmateapplicationv1.Parameters.CP;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

public class View_EditProfile extends AppCompatActivity implements Constants, Query.OnResponseDatabase,
        FireQuery.OnResponseFireQuery
{
    EditText eTUserName, eTUserMail, eTUserPsw0, eTUserPsw1;
    Spinner sPNation;
    boolean changeName, changeNation, changeMail, changePassword;
    String userName, nation, mail, password;

    ArrayList<String> playersList;
    boolean touched = false;
    String purpose;
    int idPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view__edit_profile);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Log.w(TAG, "----------------------------------------------------------------------");
        Log.w(TAG, "VIEW_EDIT PROFILE ON CREATE");

        try
        {
            eTUserName = findViewById(R.id.etUserName);
            eTUserMail = findViewById(R.id.etUserMail);
            eTUserPsw0 = findViewById(R.id.etUserPsw0);
            eTUserPsw1 = findViewById(R.id.etUserPsw1);
            sPNation = findViewById(R.id.spNation);

            Bundle parameters = getIntent().getExtras();
            purpose = parameters!=null? parameters.getString("case") : null;

            if(purpose.equals("createAccount"))
            {
                idPlayer = 0;

                findViewById(R.id.btnSave).setOnClickListener(view -> createAccount());

                EditText eTTitle = findViewById(R.id.etEditProfile);
                eTTitle.setText("Crear Cuenta");
                eTTitle.setTextColor(getResources().getColor(R.color.colorTurquoise0));

                MySQL.query(this).getNations();
                MySQL.query(this).getPlayersList(idPlayer);

            }
            else
            {
                idPlayer = CP.get().getIdPlayer();
                findViewById(R.id.btnSave).setOnClickListener(view -> saveChanges());
                MySQL.query(this).getPersonalData(idPlayer);
            }

            eTUserName.addTextChangedListener(new TextWatcher()
            {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after)
                {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count)
                {
                    if((purpose.equals("createAccount") && s.toString().equals(""))
                        || (purpose.equals("editProfile") && s.toString().equals(userName)))
                    {
                        changeName = false;
                    }
                    else
                    {
                        changeName = true;
                    }
                }

                @Override
                public void afterTextChanged(Editable s)
                {
                }
            });

            eTUserMail.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after)
                {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count)
                {
                    if((purpose.equals("createAccount") && s.toString().equals(""))
                            || (purpose.equals("editProfile")&& s.toString().equals(mail)))
                    {
                        changeMail = false;
                    }
                    else
                    {
                        changeMail = true;
                    }
                }

                @Override
                public void afterTextChanged(Editable s)
                {
                }
            });

            eTUserPsw0.addTextChangedListener(new TextWatcher()
            {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after)
                {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count)
                {
                    if((purpose.equals("createAccount") && s.toString().equals(""))
                        || (purpose.equals("editProfile") && s.toString().equals(password)))
                    {
                        changePassword = false;
                        eTUserPsw1.setText("");
                        eTUserPsw1.setEnabled(false);
                        eTUserPsw1.setVisibility(View.GONE);
                    }
                    else
                    {
                        changePassword = true;
                        eTUserPsw1.setEnabled(true);
                        eTUserPsw1.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void afterTextChanged(Editable s)
                {
                }
            });

            sPNation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
            {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
                {
                    if((purpose.equals("createAccount") && sPNation.getSelectedItem().toString().equals(" "))
                            || (purpose.equals("editProfile") && sPNation.getItemAtPosition(position).toString().equals(nation)))
                    {
                        changeNation = false;
                    }
                    else
                    {
                        changeNation = true;
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent)
                {

                }
            });
        }
        catch(Exception e)
        {
            Log.e(TAG, "Exception VIEW_EDITPROFILE onCreate: " + e.toString());
        }
    }

    @Override
    public void onBackPressed()
    {
        if(purpose.equals("createAccount"))
        {
            Intent i = new Intent(this, View_ApplicationStart.class);
            this.startActivity(i);
        }
        else
        {
            Intent i = new Intent(this, View_MainInterface.class);
            i.putExtra("idPlayer", CP.get().getIdPlayer());
            i.putExtra("namePlayer", CP.get().getNamePlayer());
            this.startActivity(i);
        }
    }

    public void saveChanges()
    {
        try
        {
            if(changeName || changeMail || changeNation || changePassword)
            {
                if(eTUserPsw0.getText().toString().equals(eTUserPsw1.getText().toString()) || !changePassword)
                {
                    if(!touched)
                    {
                        touched = true;

                        MySQL.query(this).setPersonalData(sPNation.getSelectedItem().toString(),
                                eTUserName.getText().toString(), eTUserMail.getText().toString(),
                                eTUserPsw0.getText().toString(), idPlayer);
                    }
                }
                else
                {
                    Toast.makeText(this, "Contraseñas no coinciden", Toast.LENGTH_SHORT).show();
                }
            }
            else
            {
                Toast.makeText(this, "Usted no ha modificado nada", Toast.LENGTH_SHORT).show();
            }
        }
        catch(Exception e)
        {
            Log.e(TAG, "Exception VIEW_EDITPROFILE saveChanges: " + e.toString());
        }
    }

    public void createAccount()
    {
        try
        {
            if(changeName && changeMail && changeNation && changePassword)
            {
                if(userNameExists())
                {
                    Toast.makeText(this, "Lo sentimos, Nombre de usuario ya existe, ingresar otro", Toast.LENGTH_LONG).show();
                }
                else
                {
                    if(eTUserPsw0.getText().toString().equals(eTUserPsw1.getText().toString()))
                    {
                        if(!touched)
                        {
                            touched = true;

                            MySQL.query(this).createAccount(sPNation.getSelectedItem().toString(),
                                    eTUserName.getText().toString(), eTUserMail.getText().toString(), eTUserPsw0.getText().toString());
                        }
                    }
                    else
                    {
                        Toast.makeText(this, "Contraseñas no coinciden", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            else
            {
                Toast.makeText(this, "Favor de llenar/seleccionar todos los campos", Toast.LENGTH_SHORT).show();
            }
        }
        catch(Exception e)
        {
            Log.e(TAG, "Exception VIEW_EDITPROFILE createAccount: " + e.toString());
        }
    }

    public boolean userNameExists()
    {
        for (String s: playersList)
            if(s.equals(eTUserName.getText().toString()))
                return true;

        return false;
    }

    // DATABASE IMPLEMENTATIONS

    // MYSQL

    @Override
    public void getResponseDB(HashMap<String, JSONArray> data, String purpose)
    {
        Log.w(TAG, ">>>>> PURPOSE: " + purpose);

        if(data==null)
        {
            Log.e(TAG, "FATAL ERROR (DATA NULL) VIEW_EDITPROFILE getResponseDB");
        }
        else
        {
            if(data.containsKey("DATA"))
            {
                try
                {
                    JSONArray jA = null;

                    if(data.get("DATA")!=null)
                        jA = data.get("DATA");

                    switch(purpose)
                    {
                        case "getPersonalData":
                            for(int i = 0 ; i < jA.length() ; i++)
                            {
                                JSONObject object = jA.getJSONObject(i);

                                userName = object.getString("userName");
                                mail = object.getString("mail");
                                nation = object.getString("nation");
                                password = object.getString("password");

                                eTUserName.setText(userName);
                                eTUserMail.setText(mail);
                                eTUserPsw0.setText(password);

                                changeName = changeMail = changeNation = changePassword = false;

                                if(this.purpose.equals("editProfile"))
                                    CP.get().setNamePlayer(userName);

                                MySQL.query(this).getNations();
                            }
                            break;

                        case "getNations":
                            ArrayList<String> nationsList = new ArrayList<>();

                            if(this.purpose.equals("createAccount"))
                                nationsList.add(" ");

                            for(int i = 0 ; i < jA.length() ; i++)
                            {
                                JSONObject object = jA.getJSONObject(i);
                                nationsList.add(object.getString("nation"));
                            }

                            ArrayAdapter<String> adapter =
                                    new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, nationsList);
                            sPNation.setAdapter(adapter);

                            if(this.purpose.equals("createAccount"))
                            {
                                changeNation = false;
                                eTUserPsw0.setText("");
                            }
                            else
                            {
                                for(int x=0; x < sPNation.getAdapter().getCount(); x++)
                                    if(sPNation.getAdapter().getItem(x).toString().contains(nation))
                                        sPNation.setSelection(x);
                            }
                            break;

                        case "getPlayersList":
                            playersList = new ArrayList<>();

                            for(int i=0; i < jA.length() ; i++)
                            {
                                JSONObject object = jA.getJSONObject(i);
                                playersList.add(object.getString("userName"));
                            }
                            break;

                        case "createAccount":
                            Toast.makeText(this, "Cuenta creada con éxito", Toast.LENGTH_SHORT).show();

                            Intent i = new Intent(this, View_ApplicationStart.class);
                            this.startActivity(i);
                            break;

                        case "setPersonalData":
                            Firebase.query(this).sendNamePlayer(idPlayer, eTUserName.getText().toString());

                            MySQL.query(this).getPersonalData(idPlayer);

                            touched = false;

                            Toast.makeText(this, "Datos de cuenta actualizados con éxito", Toast.LENGTH_SHORT).show();
                            break;

                        default:

                    }
                }
                catch (JSONException e)
                {
                    Log.e(TAG, "Exception VIEW_EDITPROFILE getResponseDB: " + e.toString());
                }
            }
            else if(data.containsKey("ERROR"))
            {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                Fragment_ErrorWithServer frag = new Fragment_ErrorWithServer();
                transaction.replace(R.id.fragmentPlace, frag);
                transaction.commit();
            }
        }
    }

    // FIREBASE

    @Override
    public void getResponseFireQuery(HashMap<String, Object> data, String purpose)
    {
        switch(purpose)
        {
            case "sendNamePlayer":
                break;
        }
    }



}
