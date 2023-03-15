#include "Move.h"
#include "Decoder.h"

class Game
{
  private:

    Move lastMove; // the last move done in the game

    String lastState; // 

    boolean colorChoosen; // If someone choose a color already

    String piecesOfWhite; // True list of White pieces
    String piecesOfBlack; // True list of Black pieces
    String piecesSaved; // True list of pieces saved
    String listPieces; // False (aux) list of pieces detected

  public:

    Game()
    {
      
    }

    void create()
    {
      initGameParameters();
      fillInitialPieces();
    }

    void initGameParameters()
    {
      piecesOfWhite = "";
      piecesOfBlack = "";
      listPieces = "";
      piecesSaved = "";

      lastState = "";

      colorChoosen = false;
    }

    // Fill initial positions of table

    void fillInitialPieces()
    {
      for(int i=0; i<2; i++)
        for(int j=0; j<8; j++)
          piecesOfWhite += (String)j+(String)i;
          
      for(int i=6; i<8; i++)
        for(int j=0; j<8; j++)
          piecesOfBlack += (String)j+(String)i;
    }

    void fillSavedPieces(String listSaved)
    {
      piecesSaved = listSaved;
    }

    // Getters

    String getListPieces()
    {
        return listPieces;
    }

    boolean getColorChoosen()
    {
      return colorChoosen;
    }

    Move getLastMove()
    {
        return lastMove;
    }

    String getLastState()
    {
      return lastState;
    }

    // Setters

    void setColorChoosen(boolean c)
    {
      colorChoosen = c;
    }

    void setLastMove(String s)
    {
      lastMove = Decoder::decodeStringToMove(s);
    }

    void setLastState(String state)
    {
      lastState = state;
    }



    // Validations

    // For a new game

    boolean checkLegalInitialTable(String stringPieces)
    {
        Serial.println("CHECK LEGAL INITIAL TABLE");
        
        listPieces = stringPieces;
      
        String list;

        int numberKeys = listPieces.length();
      
        for(int i=0; i<piecesOfWhite.length(); i+=2)
        {
            for(int j=0; j<numberKeys; j+=2)
            {
                if(String(piecesOfWhite.charAt(i))+String(piecesOfWhite.charAt(i+1)) == String(listPieces.charAt(j))+String(listPieces.charAt(j+1)))
                {
                    list += String(piecesOfWhite.charAt(i))+String(piecesOfWhite.charAt(i+1));
                    break;
                }
            } 
        }
        
        for(int i=0; i<piecesOfBlack.length(); i+=2)
        {
            for(int j=0; j<numberKeys; j+=2)
            {
                if(String(piecesOfBlack.charAt(i))+String(piecesOfBlack.charAt(i+1)) == String(listPieces.charAt(j))+String(listPieces.charAt(j+1)))
                {
                     list += String(piecesOfBlack.charAt(i))+String(piecesOfBlack.charAt(i+1));
                     break;
                }
            } 
        }        

        if(list.length()/2 == 0)
        {
            listPieces = "";
            Serial.println("GOOD GOOD GOOD GOOD GOOD GOOD GOOD GOOD - ALL INIT PIECES ARE LEGAL");
            return true;
        }
        else
        {
            listPieces = list;
            Serial.println("BAD BAD BAD BAD BAD BAD BAD BAD BAD BAD - INIT PIECES BAD POSITIONED");
            return false;
        }
    }

    // For a saved game

    boolean checkLegalSavedTable(String stringPieces)
    {
        Serial.println("CHECK LEGAL SAVED TABLE");
        
        listPieces = stringPieces;
      
        String list;

        int numberKeys = listPieces.length();
      
        for(int i=0; i<piecesSaved.length(); i+=2)
        {
            for(int j=0; j<numberKeys; j+=2)
            {
                if(String(piecesSaved.charAt(i))+String(piecesSaved.charAt(i+1)) == String(listPieces.charAt(j))+String(listPieces.charAt(j+1)))
                {
                    list += String(piecesSaved.charAt(i))+String(piecesSaved.charAt(i+1));
                    break;
                }
            } 
        }
        
        if(list.length()/2 == 0)
        {
            listPieces = "";
            Serial.println("GOOD SAVED GOOD SAVED GOOD GOOD GOOD GOOD - ALL INIT SAVED PIECES ARE LEGAL");
            return true;
        }
        else
        {
            listPieces = list;
            Serial.println("BAD SAVED BAD SAVED BAD BAD BAD BAD BAD BAD - INIT SAVED PIECES BAD POSITIONED");
            return false;
        }
    }
    
};
