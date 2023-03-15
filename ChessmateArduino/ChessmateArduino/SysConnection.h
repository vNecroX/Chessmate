#include <AltSoftSerial.h>
#include <SoftwareSerial.h>

                     //(Rx, Tx);
AltSoftSerial white; //(48, 46);
SoftwareSerial black(13, 12);

class SysConnection
{
  private:

    String data;

    String dataWhite;
    String dataBlack;
 
    char dWhite;
    char dBlack;

    boolean reading;

    String lastReply;

  public:

    SysConnection()
    {
    }

    void start()
    {
      clearParameters();
      white.begin(9600);
      black.begin(9600);

      Serial.println("SysConnection ready");
    }

    void clearParameters()
    {
      reading = false;
      dataWhite = "";
      dataBlack = "";
      data = "";
      dWhite = ' ';
      dBlack = ' ';
    }

    // Which player was the last to send something
    
    String getLastReply()
    {
      return lastReply;
    }

    // Listening whoever or to an specific player

    String listenToSomeone()
    {
      reading = false;
      data = "";
      dWhite = ' ';
      dBlack = ' ';

      long cc = 0;
      long ccc = 0;
      
      while(true)
      {
         if(cc == 400000)
         {
           Serial.println("Listening for commands..."); 
           cc = 0;
         }
         else
         {
           cc++;
         }

         if(ccc == 3000000)
         {
           data = "NO_DETECT";
           ccc = 0;
           break;
         }
         else
         {
           ccc++;
         }
                
         if(white.available() > 0)
         {
             delay(100);
             dWhite = white.read();   
             data += dWhite;
             reading = true;
             lastReply = "MASTER";
             Serial.flush();
             white.flush();
         }
         else if(black.available() > 0)
         {
             delay(100);
             dBlack = black.read();    
             data += dBlack;
             reading = true;
             lastReply = "SLAVE";
             Serial.flush();
             black.flush();
         }
         else
         {
             if(reading)
             {
                 break;
             }
         }
      }

      Serial.println("");
      Serial.print("<---- DATA LISTENED FROM ");
      Serial.print(lastReply);
      Serial.print(": ");
      Serial.println(data);
      Serial.println("");
      return data;
    }

    String listenABitToSomeone()
    {
      reading = false;
      data = "";
        
      while(true)
      {
         if(white.available() > 0)
         {
             delay(100);
             dWhite = white.read();    
             data += dWhite;    
             reading = true;
             lastReply = "MASTER";
             Serial.flush();
             white.flush();
         }
         else if(black.available() > 0)
         {
             delay(100);  
             dBlack = black.read();    
             data += dBlack; 
             reading = true;
             lastReply = "SLAVE";
             Serial.flush();
             black.flush();
         }
         else
         {
             break;
         }
      }

      if(reading)
      {
          Serial.println("");
          Serial.print("<---- DATA LISTENED A BIT FROM ");
          Serial.print(lastReply);
          Serial.print(": ");
          Serial.println(data);
          Serial.println("");
          return data;
      }
      else
      {
          return "nothing";
      }
    }

    // Send data

    void sendTo(boolean isWhite, String s)
    {
      Serial.println("");
      Serial.print("----> DATA SENTED TO ");
          
      if(isWhite)
      {
        Serial.print("MASTER");
        Serial.print(": ");
        Serial.println(s);
        Serial.println("");
        white.print(s);
      }
      else
      {
        Serial.print("SLAVE");
        Serial.print(": ");
        Serial.println(s);
        Serial.println("");
        black.print(s);
      }
    }

    void sendToBoth(String s)
    {
      Serial.println("");
      Serial.print("----> DATA SENTED TO BOTH: ");
      Serial.println(s);
      Serial.println("");
        
      white.print(s);
      black.print(s);
    }
};
