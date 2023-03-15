/*
   CODIGO ARDUINO PARA SISTEMA CHESSMATE

   Devs: Esteban Quintero Sotomayor & Jorge Alberto Davalos Sigala
*/

#include "SysConnection.h"
#include "SysSensors.h"
#include "SysMotors.h"

#include "Game.h"

#define MASTER true // MASTER
#define SLAVE false // SLAVE

SysConnection sConn; // System that connects and allows to tranfer data between app and arduino
SysSensors sSensors; // System which is checking countinously pieces on the board
SysMotors sMotors; // System which moves pieces underneath

Game game; // Object Game, here are some logic of chess and params to control the game

String kindOfConnection; // UNDEFINED, IS_LOCALIA_T, IS_LOCALPVP_T, IS_LOCALPVP, IS_REMOTE_T
/**
 * UNDEFINED: No game 
 * IS_LOCALPVP: Local pvp without playing with the board
 * IS_LOCALPVP_T: Local pvp playing with the board
 * IS_LOCALIA_T: Local ia playing with the board
 * IS_REMOTE_T: Remote playing pvp with the board
 */

int next; // to control the navigation between views in the app
boolean startNewGame; // Check if player is gonna start a game
long displayScan; // to control the serial display of scan

boolean largePath;
boolean sysMoved;

String x;
String y;

const boolean withMoveSystem = true;

boolean fromApp;

void setup()
{
  Serial.begin(9600);
  initializeSystems();
}

void loop()
{
  Serial.println(" ");
  Serial.print("LISTENING FOR COMMANDS...");
  Serial.print("   *Kind of connection: ");
  Serial.print(kindOfConnection);
  Serial.print("  *Next: ");
  Serial.println(next);
  commands(sConn.listenToSomeone());
}

void initializeSystems()
{
  sConn.start();
  sSensors.start();

  if(withMoveSystem)
  {
    sMotors.start();
  }

  resetGlobalValues();

  fromApp = false;

  Serial.println("*****   ALL SYSTEMS READY   *****");
}

void commands(String data)
{
  if (!startNewGame)
  {
    if(data == "IS_LOCALIA_T" || data == "IS_LOCALPVP_T" || data == "IS_LOCALPVP" || data == "IS_REMOTE_T")
    {
        kindOfConnection = data;
      
        if(data == "IS_LOCALPVP_T")
        {
            sConn.sendTo(MASTER, "R");
        }
        else
        {
            game.create();
            
            next = 0;
            startNewGame = true;
        }
  
        printStateGameSerial(data);
    }
    else if(data == "R")
    {
        sConn.sendTo(MASTER, data);
        Serial.println("Sending (R) as request to Master...");
    }
    else if(data == "CONFIRM")
    {
        game.create();
        
        next = 0;
        startNewGame = true;

        sConn.sendTo(MASTER, data);
        sConn.sendTo(SLAVE, "R");
        
        printMsgSerial("A NEW GAME HAS STARTED");
    }
    else if(data == "NO_DETECT")
    {
        resetGlobalValues();
        printNoActivitySerial();
    }
  }
  else
  {
    if (kindOfConnection == "IS_LOCALIA_T")
    {
      switch (next)
      {
        case 0:
          if (data == "NEW") 
          {
              preparingGame(true);
          }
          else if(data.charAt(0) == '$')
          {
              String listSavedPositions = data.substring(1);
              Serial.println(listSavedPositions);
              game.fillSavedPieces(listSavedPositions);
              preparingGame(false);
          }
          else if(data == "NO_DETECT")
          {
              resetGlobalValues();
              printNoActivitySerial();
          }
          else if(data == "DIE")
          {
              resetGlobalValues();
          }
          break;

        case 1:
          if(data != "NO_DETECT")
          {
              playFunction();
          }
          else
          {
              resetGlobalValues();
              printNoActivitySerial();
          }
          break;

        case 2:
          if(data != "NO_DETECT")
          {
              finalResponse();
          }
          else
          {
              resetGlobalValues();
              printNoActivitySerial();
          }
          break;
      }
    }
    else if (kindOfConnection == "IS_LOCALPVP" || kindOfConnection == "IS_LOCALPVP_T")
    {
      switch (next)
      {
        case 0:
          if(data != "NO_DETECT")
          {
              if (data.charAt(0) == 'S') // S@<idOpponent>@<opponentName>@ (SLAVE)
              {
                sConn.sendTo(MASTER, data);
                printStateGameSerial("SLAVES DOES PRESENCE");
              }
              else if (data.charAt(0) == 'M') // M@<idOpponent>@<opponentName>@ (MASTER) 
              {
                sConn.sendTo(SLAVE, data);
                printStateGameSerial("MASTER DOES PRESENCE");
              }
              else if (data == "PEER")
              {
                next++;
                game.setColorChoosen(false);
                printMsgSerial("PEER VIEW ===> NEXT VIEW");
              }
          }
          else
          {
              resetGlobalValues();
              printNoActivitySerial();
          }
          break;

        case 1:
          if(data != "NO_DETECT")
          {
              if (data == "W" || data == "B")
              {
                  if (!game.getColorChoosen())
                  {
                    sendDataToOther(data);
                    printStateGameSerial("COLOR CHOOSEN");
                    game.setColorChoosen(true);
                  }
              }
              else if (data == "OK")
              {
                  sendDataToOther(data);
              }
              else if (data == "COLOR")
              {
                  next++;
                  printMsgSerial("COLOR PIECES VIEW ===> NEXT VIEW");
              }
          }
          else
          {
              resetGlobalValues();
              printNoActivitySerial();
          }
          break;

        case 2:
          if(data != "NO_DETECT")
          {
              if (data == "NOSAVE" || data.charAt(0) == '@') 
              {
                  sendDataToOther(data);
                  printStateGameSerial("PREPARING GAME");
              }
              else if (data == "START")
              {
                  if(kindOfConnection == "IS_LOCALPVP")
                  {
                    next += 2;
                  }
                  else
                  {
                    next++;
                  }
                  
                  printMsgSerial("START GAME FRAGMENT ===> NEXT VIEW");
              }
              else if (data == "DIE") 
              {
                  sendDataToOther(data);
                  resetGlobalValues();
                  printMsgSerial("DIE FROM START GAME FRAGMENT");
              }  
          }
          else
          {
              resetGlobalValues();
              printNoActivitySerial();
          }
          break;

        case 3:
          if (data == "NEW") 
          {
              preparingGame(true);
          }
          else if(data == "NO_DETECT")
          {
              resetGlobalValues();
              printNoActivitySerial();
          }
          break;

        case 4:
          if(data != "NO_DETECT")
          {
            playFunction();
          }
          else
          {
              resetGlobalValues();
              printNoActivitySerial();
          }
          break;

        case 5:
          if(data != "NO_DETECT")
          {
              finalResponse();
          }
          else
          {
              resetGlobalValues();
              printNoActivitySerial();
          }
          break;
      }
    }
    else if (kindOfConnection == "IS_REMOTE_T")
    {
      switch (next)
      {
        case 0:
          if (data == "NEW") 
          {
            preparingGame(true);
          }
          else if(data == "NO_DETECT")
          {
            resetGlobalValues();
            printNoActivitySerial();
          }
          break;

        case 1:
          if(data != "NO_DETECT")
          {
            playFunction();
          }
          else
          {
            resetGlobalValues();
            printNoActivitySerial();
          }
          break;

        case 2:
          if(data != "NO_DETECT")
          {
              finalResponse();
          }
          else
          {
              resetGlobalValues();
              printNoActivitySerial();
          }
          break;
      }
    }
  }
}

// FUNCTIONS DE LA MUERTE

void preparingGame(boolean newGame)
{
    if(newGame)
    {
        printStateGameSerial("PREPARING BOARD OF GAME");
    }
    else
    {
        printStateGameSerial("PREPARING BOARD OF GAME SAVED");
    }

    String response;
    boolean startGame = false;
    sSensors.setLimitKeysIn(32);

    do
    {
        response = sConn.listenABitToSomeone();

        if (response != "nothing")
        {
            if(kindOfConnection == "IS_LOCALPVP_T" || kindOfConnection == "IS_LOCALPVP")
            {
                  if (response == "LOSE") 
                  {
                      if (sConn.getLastReply() == "MASTER")
                      {
                         sConn.sendTo(SLAVE, response);
                      }
                      else
                      {
                         sConn.sendTo(MASTER, response);
                      }
                      
                      next += 2;
                      startNewGame = false;
                      startGame = true;
          
                      printMsgSerial("LOSE FROM MAIN ACTIVITY (PREPARING GAME)");
                  }
                  else
                  {
                      startGame = false;
                  }
            }
            else
            {
                  if (response == "END") 
                  {
                      sConn.sendTo(MASTER, "OKEND");

                      next += 2;
                      startNewGame = false;
                      startGame = true;
          
                      printMsgSerial("END FROM MAIN ACTIVITY (PREPARING GAME)");
                  }
                  else
                  {
                      startGame = false;
                  }
            }
        }
        else
        {
            sSensors.fullScanSensors();

            String piecesDetected = sSensors.getStringKeysIn();
            String piecesNotDetected = sSensors.getStringKeysOut();

            if (!newGame && game.checkLegalSavedTable(piecesNotDetected))
            {
                startGame = true;
            }
            else if (newGame && game.checkLegalInitialTable(piecesNotDetected))
            {
                startGame = true;
            }
            else
            {
                startGame = false;
                String ilegalPieces = game.getListPieces();
                String param = "I" + ilegalPieces;
                sConn.sendTo(MASTER, param);

                if(kindOfConnection == "IS_LOCALPVP_T" || kindOfConnection == "IS_LOCALPVP")
                {
                    sConn.sendTo(SLAVE, param);
                }
                
                delay(3000);
            }
        }
    }
    while(!startGame);

    if(kindOfConnection == "IS_LOCALPVP_T" || kindOfConnection == "IS_LOCALPVP")
    {
        if(response != "LOSE")
        {
              delay(1000);
              sConn.sendTo(MASTER, "PLAY");
              sConn.sendTo(SLAVE, "PLAY");
              next++;

              printMsgSerial("MAIN ACTIVITY (PREPARING GAME) ===> NEXT VIEW");
        }
    }
    else
    {
        if(response != "END")
        {
              delay(1000);
              sConn.sendTo(MASTER, "PLAY");
              next++;

              printMsgSerial("MAIN ACTIVITY (PREPARING GAME) ===> NEXT VIEW");
        }
    }
}

void playFunction()
{
  printStateGameSerial("INTO THE GAME | FINALLY PLAYING THE GAME");
  
  do
  {
    String response;
    String state;
  
    displayScan = 0;
    
    game.setLastState("nothing");

    do
    {
       sConn.clearParameters();
       response = sConn.listenABitToSomeone(); 

       if (response != "nothing")
       {
          if (response.charAt(0) == '@') // movement response of opponent
          {
              fromApp = true;
              
              Serial.print("Move: ");
              Serial.println(response);

              String s = response.substring(1);

              Serial.print("Move again: ");
              Serial.println(s);
        
              game.setLastMove(s); // Move: x, y, a, b, c, d


              String theCoord;

              if(game.getLastMove().getEat())
              {
                  largePath = true;
                  theCoord = getAsString(game.getLastMove().getXf(), game.getLastMove().getYf());
              }
              else if(game.getLastMove().getRook())
              {
                  largePath = true;
                  theCoord = getAsString(game.getLastMove().getXi(), game.getLastMove().getYi());
              }
              else
              {
                  largePath = false;
                  theCoord = getAsString(game.getLastMove().getXi(), game.getLastMove().getYi());
              }

              sysMoved = false;

              if(withMoveSystem)
              {
                x = "";
                y = "";

                x += Decoder::numberToLetter(theCoord.charAt(0)-'0');
                y += (theCoord.charAt(1)-'0')+1;

                Serial.println("===============================================");
                Serial.println(x);
                Serial.println(y);

                if(largePath)
                {
                  sMotors.toEatenPieceXY1(x, y);
                }
                else
                {
                  sMotors.initialMoveXY(x, y);
                }
              }
              else
              {
                printSysInMovSerial("A");
                delay(1000);
                printSysInStaticSerial("A");
              }

              String reply = "";
      
              do
              {
                    if(kindOfConnection == "IS_LOCALPVP_T" || kindOfConnection == "IS_LOCALPVP")
                    {
                        reply = "X";
                        reply += sSensors.getStringKeysIn();
                        sConn.sendTo(MASTER, reply);
                        sConn.sendTo(SLAVE, reply);
                    }
                    else
                    {
                        reply = "X";
                        reply += sSensors.getStringKeysIn();
                        sConn.sendTo(MASTER, reply);
                    }
                
                    reply = sConn.listenToSomeone();

                    if(reply != "fixedBoard" && reply != "NO_DETECT")
                    {
                        do
                        {
                            sSensors.fullScanSensors();
                            state = sSensors.stateTable();

                        } while (state == "?");
                    }
                          
              }while (reply != "fixedBoard" && reply != "NO_DETECT"); // Cycle until we know the opponent got the movement


              if(reply == "NO_DETECT")
              {
                  resetGlobalValues();
                  next = -1;
                  game.setLastState("finished");
                  break;
              }


              if(kindOfConnection != "IS_LOCALPVP")
              {
                  do
                  {
                       response = sConn.listenABitToSomeone();

                       if (response != "nothing")
                       {
                             if(kindOfConnection == "IS_LOCALPVP_T")
                             {
                                 if (response == "LOSE") 
                                 {
                                       sendDataToOther(response);
                                            
                                       game.setLastState("finished");
                                
                                       printMsgSerial("LOSE FROM MAIN ACTIVITY");
                                 }
                                 else
                                 {
                                       game.setLastState("nothing");
                                 }
                             }
                             else if(kindOfConnection == "IS_LOCALIA_T" || kindOfConnection == "IS_REMOTE_T")
                             {
                                 if (response == "END") 
                                 {
                                       sConn.sendTo(MASTER, "OKEND");
                                       game.setLastState("finished");
                              
                                       printMsgSerial("END FROM MAIN ACTIVITY");
                                 }
                                 else
                                 {
                                       game.setLastState("nothing");
                                 }
                             }
                       }
                       else
                       {
                             if(displayScan == 1000)
                             {
                                  Serial.println("--- SCANNING FROM APP ---");
                                  displayScan = 0;
                             }
                             else
                             {
                                  displayScan++;
                             }

                             sSensors.fullScanSensors();
                             state = sSensors.stateTable();

                             if (state != "?" || game.getLastState() == "firstBad")
                             {
                                  checkState(state, true);

                                  printMode();

                                  Serial.println("(FROM APP) Waiting for confirmation...");
                                                    
                                  response = sConn.listenToSomeone(); // here waiting for any confirmation 
                
                                  Serial.print("(FROM APP) The confirmation response: ");
                                  Serial.println(response);

                                  checkResponse(response, true);
                             }
                       }
                  }
                  while(game.getLastState() != "finished" && game.getLastState() != "moved");
              }
              else
              {
                  Serial.println("Simple IS_LOCALPVP mode");
                  game.setLastState("moved");
              }
          }
          else
          {
              if(kindOfConnection == "IS_LOCALPVP_T" || kindOfConnection == "IS_LOCALPVP")
              { 
                  if(response.equals("WIN") || response.equals("LOSE") || response.equals("DRAW"))
                  {
                       sendDataToOther(response);
                          
                       game.setLastState("finished");
                  }
              }
              else if(kindOfConnection == "IS_LOCALIA_T")
              {
                  if(response.equals("END"))
                  {
                       sConn.sendTo(MASTER, "OKEND");
                       game.setLastState("finished");
                  }
                  else if(response.equals("PAUSE"))
                  {
                       resetGlobalValues();
                       game.setLastState("paused");
                  
                       printMsgSerial("GAME PAUSED");
                  }
                  else if(response == "BACK" || response.substring(0, 3) == "BACK")
                  {
                       if(game.getLastState() != "bad")
                       {
                            game.setLastState("firstBad");
                       }
                  }
              }
              else if(kindOfConnection == "IS_REMOTE_T")
              {
                  if(response.equals("END"))
                  {
                       sConn.sendTo(MASTER, "OKEND");
                       game.setLastState("finished");
                  }
                  else if(response.equals("OVER"))
                  {
                       sConn.sendTo(MASTER, "OKEND");
                       game.setLastState("finished");
                  }
              }
          }
       }
       else
       {
           fromApp = false;

           if(kindOfConnection != "IS_LOCALPVP")
           {
                if(displayScan == 1000)
                {
                     Serial.println("--- SCANNING FROM BOARD ---");
                     displayScan = 0;
                }
                else
                {
                     displayScan++;
                }

                sSensors.fullScanSensors();
                state = sSensors.stateTable();

                if (state != "?" || game.getLastState() == "firstBad")
                {
                    checkState(state, false);

                    printMode();

                    Serial.println("(FROM BOARD) Waiting for confirmation...");
                                                    
                    response = sConn.listenToSomeone(); // here waiting for any confirmation 
                
                    Serial.print("(FROM BOARD) The confirmation response: ");
                    Serial.println(response);

                    checkResponse(response, false);
                }
           }
       }

    }
    while(game.getLastState() != "paused" && game.getLastState() != "finished" && game.getLastState() != "moved");

    printStateGameSerial("PLAYING OTHER TURN");
  }
  while(game.getLastState() != "paused" && game.getLastState() != "finished");

  if(game.getLastState() == "finished")
  {
      next++;
  }
  
}

void checkState(String state, boolean fromApp)
{
    if(game.getLastState() == "firstBad" || game.getLastState() == "bad")
    {
        if(fromApp)
        {
            if(game.getLastState() == "firstBad")
            {
                game.setLastState("bad");
            }
            
            checkIfMoveSysMov();
        }
        else
        {
            if(game.getLastState() == "firstBad")
            {
                game.setLastState("bad");
            }

            String param = "B" + sSensors.getStringKeysIn();
            sConn.sendTo(MASTER, param);

            if(kindOfConnection == "IS_LOCALPVP_T")
            {
                sConn.sendTo(SLAVE, param);
            }
        }
    }
    else
    {
         if (state == "exceeded" || state == "missing")
         {
             sConn.sendTo(MASTER, "E"); 

             if(kindOfConnection == "IS_LOCALPVP_T")
             {
                  sConn.sendTo(SLAVE, "E");
             }
         }
         else if (state == "minus")
         {
            if(fromApp)
            {
                checkIfMoveSysMov();
            }
            else
            {
                String param = "B" + sSensors.getStringKeysIn();
                sConn.sendTo(MASTER, param);

                if(kindOfConnection == "IS_LOCALPVP_T")
                {
                    sConn.sendTo(SLAVE, param);
                }
            }
         }
         else if (state == "more")
         {
              String param = "B" + sSensors.getStringKeysIn();
              sConn.sendTo(MASTER, param);

              if(kindOfConnection == "IS_LOCALPVP_T")
              {
                   sConn.sendTo(SLAVE, param);
              }
         }
         else if (state == "moving")
         {
            if(fromApp)
            {
                checkIfMoveSysMov();
            }
            else
            {
                String auxMove = "M";
                auxMove += sSensors.getStringKeysLeft();
                auxMove += sSensors.getStringKeysPut();
                sConn.sendTo(MASTER, auxMove);

                if(kindOfConnection == "IS_LOCALPVP_T")
                {
                    sConn.sendTo(SLAVE, auxMove);
                }
            }
         }
         else if(state == "??")
         {
                if(fromApp)
                {
                    if(game.getLastState() == "firstBad")
                    {
                        game.setLastState("bad");
                    }
                    
                    checkIfMoveSysMov();
                }
                else
                {
                    if(game.getLastState() == "firstBad")
                    {
                        game.setLastState("bad");
                    }

                    String param = "B" + sSensors.getStringKeysIn();
                    sConn.sendTo(MASTER, param);

                    if(kindOfConnection == "IS_LOCALPVP_T")
                    {
                        sConn.sendTo(SLAVE, param);
                    }
                }
         }
    }
}

void checkResponse(String response, boolean fromApp)
{
    if(response == "bad") // when something wrong occurs
    {
        if(game.getLastState() != "bad")
        {
             game.setLastState("firstBad");
        }
    }
    else if(response == "first" || response == "second" || response == "finished") // when the movement is correct and we are not seeing any problem
    {
        if (response == "second")
        {
            game.setLastState("moved");
        }
        else if (response == "finished")
        {
            game.setLastState("finished");
        }
        else
        {
            game.setLastState("moving");
        }
    
        if(response == "finished" || response == "second")
        {
            if((kindOfConnection == "IS_LOCALPVP_T" && !fromApp) || kindOfConnection == "IS_REMOTE_T")
            {
                String reply;
      
                do
                {
                    reply = sConn.listenToSomeone();
                                                    
                }while (reply.charAt(0) != '@' && reply != "NO_DETECT"); // Cycle until we know the opponent got the movement

                sendDataToOther(reply);

                if(reply == "NO_DETECT")
                {
                    resetGlobalValues();
                    printNoActivitySerial();
                    next = -1;
                    game.setLastState("finished");

                    if(withMoveSystem && fromApp)
                    {
                        recalibrateSystem();
                    }
                }
            }
            
            printMsgSerial("- MOVE DONE -");
        }
    }
    else if(response == "rooked")
    {
        game.setLastState("nothing");
    }
    else if(response == "fixedApp")
    {
        game.setLastState("moved"); 

        if(kindOfConnection == "IS_LOCALPVP" || kindOfConnection == "IS_LOCALPVP_T" || kindOfConnection == "IS_REMOTE_T")
        {
            String reply;
      
            do
            {
                reply = sConn.listenToSomeone();
                                                    
            }while (reply.charAt(0) != '@' && reply != "NO_DETECT"); // Cycle until we know the opponent got the movement

            sendDataToOther(reply);

            if(reply == "NO_DETECT")
            {
                resetGlobalValues();
                printNoActivitySerial();
                next = -1;
                game.setLastState("finished");

                if(withMoveSystem && fromApp)
                {
                    recalibrateSystem();
                }
            }
        }
    }
    else if(response == "fixedSystem")
    {
        game.setLastState("moved"); 

        if(withMoveSystem)
        {
            String theCoord = getAsString(game.getLastMove().getXf(), game.getLastMove().getYf());

            x = "";
            y = "";

            x += Decoder::numberToLetter(theCoord.charAt(0)-'0');
            y += (theCoord.charAt(1)-'0')+1;

            sMotors.currentToCenterPositionXY(x, y);

            sMotors.recalibrate();

            sMotors.recal();

            //sMotors.calibrate();
        }
        else
        {
            printSysInMovSerial("AMONOS AL ORIGEN");
            delay(1000);
            printSysInStaticSerial("AMONOS AL ORIGEN");
        }


        sConn.sendTo(MASTER, "N");

        if(kindOfConnection == "IS_LOCALPVP_T")
        {
            sConn.sendTo(SLAVE, "N");

            String reply;
      
            do
            {
                reply = sConn.listenToSomeone();
                                                    
            }while (reply.charAt(0) != '@' && reply != "NO_DETECT"); // Cycle until we know the opponent got the movement

            sendDataToOther(reply);

            if(reply == "NO_DETECT")
            {
                resetGlobalValues();
                printNoActivitySerial();
                next = -1;
                game.setLastState("finished");

                if(withMoveSystem && fromApp)
                {
                    recalibrateSystem();
                }
            }
        }
    }
    else if(response == "fixedBoard" || response == "again" || response == "eaten")
    {
        game.setLastState("nothing");
       
        if(withMoveSystem && (game.getLastMove().getEat() || game.getLastMove().getRook()) && !largePath && sysMoved)
        {
            sysMoved = false;
            //recalibrateSystem();
        }
    }
    else if(response == "NO_DETECT")
    {
        resetGlobalValues();
        printNoActivitySerial();
        next = -1;
        game.setLastState("finished");

        if(withMoveSystem && fromApp)
        {
            recalibrateSystem();
        }
    }
    else if(response == "BACK" || response.substring(0, 3) == "BACK")
    {
        if(game.getLastState() != "bad")
        {
            game.setLastState("firstBad");
        }
    }
    else if(response == "PAUSE")
    {
        resetGlobalValues();
        game.setLastState("paused");
                  
        printMsgSerial("GAME PAUSED");
    }
    else if(response.equals("END"))
    {
        sConn.sendTo(MASTER, "OKEND");
        game.setLastState("finished");
    }
    else if(response.equals("OVER"))
    {
        sConn.sendTo(MASTER, "OKEND");
        game.setLastState("finished");
    }
    else
    {
        if(kindOfConnection == "IS_LOCALPVP_T")
        {
            if(response.charAt(0) == '@')
            {
                 sendDataToOther(response);
                 
                 if(game.getLastState() != "bad")
                 {
                      game.setLastState("firstBad");
                 }
            }
        }
        else if(kindOfConnection == "IS_REMOTE_T")
        {
            if(response.charAt(0) == '@')
            {
                 Serial.println(response);
                 game.setLastMove(response); // Move: x, y, a, b, c, d
            
                
                 // move xyz system (este no le hagas caso esteban, gracias)


                 game.setLastState("firstBad");
            
                 printMsgSerial("- MOVE DONE -");
                              
            }
        }
        else
        {
            if(game.getLastState() != "bad")
            {
                game.setLastState("firstBad");
            }
        }
    }
}

void printMode()
{
    if(kindOfConnection == "IS_LOCALPVP_T")
    {
        Serial.println("---( MODE: IS_LOCALPVP_T )---");
    }
    else if(kindOfConnection == "IS_LOCALIA_T")
    {
        Serial.println("---( MODE: IS_LOCALIA_T  )---");
    }
    else if(kindOfConnection == "IS_REMOTE_T")
    {
        Serial.println("---( MODE: IS_REMOTE_T )---");
    }
}

void finalResponse()
{
    String response;
    boolean ok = false;

    printStateGameSerial("IN FRAGMENT GAME FINISHED");

    do
    {
          response = sConn.listenToSomeone();

          if(response == "ASK" || response == "CANCEL")
          {
                sendDataToOther(response);
          }
          else if(response == "AGAIN") 
          {
                if(kindOfConnection == "IS_LOCALPVP" || kindOfConnection == "IS_LOCALPVP_T")
                {
                    sendDataToOther(response);
                }
                else
                {
                    next = 0;
                    ok = true;
                    startNewGame = true;
      
                    printMsgSerial("ANOTHER GAME AGAIN");
                }
          }
          else if(response == "OKAGAIN") 
          {
                next = 1;
                ok = true;
                game.setColorChoosen(false);

                printMsgSerial("OK, PLAY AGAIN");
          }
          else if(response == "FINISH")
          {
                if(kindOfConnection == "IS_LOCALPVP" || kindOfConnection == "IS_LOCALPVP_T")
                {
                    sendDataToOther(response);
                }
                
                resetGlobalValues();
                ok = true;
  
                printMsgSerial("GAME HAS TOTALLY FINISH");
          }
          else if(response == "NO_DETECT")
          {
                resetGlobalValues();
                printNoActivitySerial();
                ok = true;

                if(withMoveSystem && fromApp)
                {
                    recalibrateSystem();
                }
          }
          else if(response == "again" || response.substring(0, 4) == "again" || response.substring(0, 5) == "again")
          {
                resetGlobalValues();
                printNoActivitySerial();
                ok = true;
          }
          
    }
    while(!ok);
}

void resetGlobalValues()
{
    kindOfConnection = "UNDEFINED";
    next = 0;
    startNewGame = false;
}

void sendDataToOther(String data)
{
    if (sConn.getLastReply() == "MASTER")
    {
        sConn.sendTo(SLAVE, data);
    }
    else
    {
        sConn.sendTo(MASTER, data);
    }
}

void printMsgSerial(String msg)
{
    Serial.println(" ");
    Serial.println("--------------------------------------------------------------------");
    Serial.println(msg);
    Serial.println("--------------------------------------------------------------------");
}

void printStateGameSerial(String msg)
{
    Serial.println(" ");
    Serial.print(">>> >>> >>> >>> >>> >>>  STATE OF GAME: ");
    Serial.println(msg);
    Serial.println(" ");
}

void printNoActivitySerial()
{
    Serial.println(" ");
    Serial.println("--------------------------------------------------------------------");
    Serial.println("NO ACTIVITY HERE... ... ... ...");
    Serial.println("--------------------------------------------------------------------");
}

void printSysInMovSerial(String msg)
{
    Serial.println(" ");
    Serial.println(">_>_>_>_>_>_>_>_>_>_>_>_>_>_>_>_>_>_>_>_>_>_>_>_>_>_>_>_>_>_>_>");
    Serial.print("SYSTEM IN MOVEMENT | Recorrido: ");
    Serial.println(msg);
    Serial.println(">_>_>_>_>_>_>_>_>_>_>_>_>_>_>_>_>_>_>_>_>_>_>_>_>_>_>_>_>_>_>_>");
}

void printSysInStaticSerial(String msg)
{
    Serial.println(" ");
    Serial.println("----------------------------------------------------------------");
    Serial.print("SYSTEM IN STATIC | Recorrido: ");
    Serial.println(msg);
    Serial.println("----------------------------------------------------------------");
}

boolean checkIfPieceMoved(String listPositions, String coord)
{
    Serial.println("CHECK IF PIECES IS MOVED");
      
    for(int i=0; i<listPositions.length(); i+=2)
    {
        if(String(listPositions.charAt(i))+String(listPositions.charAt(i+1)) == coord)
        {
            return true;
        }
    }

    return false;
}

String getAsString(int x, int y)
{
    return (String)x+(String)y;
}




void checkIfMoveSysMov()
{
    if(game.getLastMove().getEat() || game.getLastMove().getRook())
    {
        Serial.println("+++ EAT OR ROOK");

        if(largePath)
        {
            Serial.println("LARGE  PATH");

            if(game.getLastMove().getEat())
            {
                Serial.println("+++ EAT");

                String s = getAsString(game.getLastMove().getXf(), game.getLastMove().getYf());
                String ss = sSensors.getStringKeysLeft();

                String auxS = s;

                if(checkIfPieceMoved(ss, s))
                {
                    if(withMoveSystem)
                    {
                        x = "";
                        y = "";

                        x += Decoder::numberToLetter(s.charAt(0)-'0');
                        y += (s.charAt(1)-'0')+1;
                        
                        //sMotors.eatenPieceXY(x, y);
                        sMotors.toEatenPieceXY2();
                    }
                    else
                    {
                        printSysInMovSerial("B");
                        delay(1000);
                        printSysInStaticSerial("B");
                    }
                    

                    s = getAsString(game.getLastMove().getXi(), game.getLastMove().getYi());

                    if(withMoveSystem)
                    {
                        x = "";
                        y = "";

                        x += Decoder::numberToLetter(s.charAt(0)-'0');
                        y += (s.charAt(1)-'0')+1;

                        sMotors.eaterPieceXY(x, y);
                    }
                    else
                    {
                        printSysInMovSerial("C");
                        delay(1000);
                        printSysInStaticSerial("C");
                    }

                    largePath = false;
                    sysMoved = true;
                }
            }
            else if(game.getLastMove().getRook())
            {
                Serial.println("+++ ROOK");

                String s = getAsString(game.getLastMove().getXf(), game.getLastMove().getYf());
                String ss = sSensors.getStringKeysLeft();

                if(checkIfPieceMoved(ss, s))
                {
                    if(withMoveSystem)
                    {
                        x = "";
                        y = "";

                        x += s.charAt(0);
                        y += s.charAt(1);

                        sMotors.eatenPieceXY(x, y);
                    }
                    else
                    {
                        printSysInMovSerial("B");
                        delay(1000);
                        printSysInStaticSerial("B");
                    }

                    s = getAsString(game.getLastMove().getXi(), game.getLastMove().getYi());

                    if(withMoveSystem)
                    {
                        x = "";
                        y = "";

                        x += s.charAt(0);
                        y += s.charAt(1);

                        sMotors.eaterPieceXY(x, y);
                    }
                    else
                    {
                        printSysInMovSerial("C");
                        delay(1000);
                        printSysInStaticSerial("C");
                    }

                    largePath = false;
                    sysMoved = true;
                }
            }

            String s = "X";
            s += sSensors.getStringKeysIn();
            sConn.sendTo(MASTER, s);

            if(kindOfConnection == "IS_LOCALPVP_T")
            {
                sConn.sendTo(SLAVE, s);
            }
        }
        else
        {
            Serial.println("SHORT  PATH");

            if(!sysMoved)
            {
                if(game.getLastMove().getEat())
                {
                    Serial.println("--- EAT OR ROOK");

                    String s = getAsString(game.getLastMove().getXi(), game.getLastMove().getYi());
                    String ss = sSensors.getStringKeysLeft();

                    if(checkIfPieceMoved(ss, s))
                    {
                        if(withMoveSystem)
                        {
                            String f = getAsString(game.getLastMove().getXf(), game.getLastMove().getYf());

                            x = "";
                            y = "";

                            x += Decoder::numberToLetter(f.charAt(0)-'0');
                            y += (f.charAt(1)-'0')+1;

                            sMotors.eaterToEatenXY(x, y);
                        }
                        else
                        {
                            printSysInMovSerial("D");
                            delay(1000);
                            printSysInStaticSerial("D");
                        }
                        

                        String s = "Y";
                        s += sSensors.getStringKeysIn();
                        sConn.sendTo(MASTER, s);

                        if(kindOfConnection == "IS_LOCALPVP_T")
                        {
                            sConn.sendTo(SLAVE, s);
                        }

                        sysMoved = true;
                    }
                    else
                    {
                        String s = "X";
                        s += sSensors.getStringKeysIn();
                        sConn.sendTo(MASTER, s);

                        if(kindOfConnection == "IS_LOCALPVP_T")
                        {
                            sConn.sendTo(SLAVE, s);
                        }
                    }
                }
                else if(game.getLastMove().getRook())
                {
                    Serial.println("--- EAT OR ROOK");

                    String s = getAsString(game.getLastMove().getXf(), game.getLastMove().getYf());
                    String ss = sSensors.getStringKeysLeft();

                    if(checkIfPieceMoved(ss, s))
                    {
                        if(withMoveSystem)
                        {
                            x = "";
                            y = "";

                            x += s.charAt(0);
                            y += s.charAt(1);

                            sMotors.eaterToEatenXY(x, y);
                        }
                        else
                        {
                            printSysInMovSerial("D");
                            delay(1000);
                            printSysInStaticSerial("D");
                        }
                        

                        String s = "Y";
                        s += sSensors.getStringKeysIn();
                        sConn.sendTo(MASTER, s);

                        if(kindOfConnection == "IS_LOCALPVP_T")
                        {
                            sConn.sendTo(SLAVE, s);
                        }

                        sysMoved = true;
                    }
                    else
                    {
                        String s = "X";
                        s += sSensors.getStringKeysIn();
                        sConn.sendTo(MASTER, s);

                        if(kindOfConnection == "IS_LOCALPVP_T")
                        {
                            sConn.sendTo(SLAVE, s);
                        }
                    }           
                }
                else
                {
                    String s = "X";
                    s += sSensors.getStringKeysIn();
                    sConn.sendTo(MASTER, s);

                    if(kindOfConnection == "IS_LOCALPVP_T")
                    {
                        sConn.sendTo(SLAVE, s);
                    }
                }
            }
            else
            {
                String s = "Y";
                s += sSensors.getStringKeysIn();
                sConn.sendTo(MASTER, s);

                if(kindOfConnection == "IS_LOCALPVP_T")
                {
                    sConn.sendTo(SLAVE, s);
                }
            }
        }
    }
    else
    {
        Serial.println("+++ NORMAL");

        String s = getAsString(game.getLastMove().getXi(), game.getLastMove().getYi());
        String ss = sSensors.getStringKeysLeft();

        if(!sysMoved)
        {
            if(checkIfPieceMoved(ss, s))
            {
                if(withMoveSystem)
                {
                    String f = getAsString(game.getLastMove().getXf(), game.getLastMove().getYf());

                    x = "";
                    y = "";

                    x += Decoder::numberToLetter(f.charAt(0)-'0');
                    y += (f.charAt(1)-'0')+1;

                    sMotors.finalMoveXY(x, y);
                }
                else
                {
                    printSysInMovSerial("B");
                    delay(1000);
                    printSysInStaticSerial("B");
                }


                String s = "Y";
                s += sSensors.getStringKeysIn();
                sConn.sendTo(MASTER, s);

                if(kindOfConnection == "IS_LOCALPVP_T")
                {
                    sConn.sendTo(SLAVE, s);
                }

                sysMoved = true;
            }
            else
            {
                String s = "X";
                s += sSensors.getStringKeysIn();
                sConn.sendTo(MASTER, s);

                if(kindOfConnection == "IS_LOCALPVP_T")
                {
                    sConn.sendTo(SLAVE, s);
                }
            }
        }
        else
        {
            String s = "Y";
            s += sSensors.getStringKeysIn();
            sConn.sendTo(MASTER, s);

            if(kindOfConnection == "IS_LOCALPVP_T")
            {
                sConn.sendTo(SLAVE, s);
            }
        }
    }
}

void recalibrateSystem()
{
    String theCoord = getAsString(game.getLastMove().getXf(), game.getLastMove().getYf());
            
    x = "";
    y = "";

    x += Decoder::numberToLetter(theCoord.charAt(0)-'0');
    y += (theCoord.charAt(1)-'0')+1;

    sMotors.currentToCenterPositionXY(x, y);

    sMotors.recalibrate();

    sMotors.recal();
}
