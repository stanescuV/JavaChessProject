package game.piece;

import game.GamePanel;
import game.Type;

public class King extends Piece{
     public King(int color, int col, int row) {
        super(color, col, row);
        type = Type.KING;

        if(color == GamePanel.WHITE){
            image = getImage("/w-king");
        } else{
            image = getImage("/b-king");
        }
    }

    @Override
    public boolean canMove(int targetCol, int targetRow) {
         // on verifie si le mouvement est sur la table
        if (isWithinBoard(targetCol, targetRow)) {
            //on verifie si le mouvement est réglementaire
            if(
                    // mouvement haut, dorite, gauche, bas
                    Math.abs(targetCol - preCol) + Math.abs(targetRow-preRow) == 1
                    // mouvement en diagonale d une case
                    || Math.abs(targetCol- preCol) * Math.abs(targetRow - preRow) == 1){

                if (isValidSquare(targetCol, targetRow)) {
                return true;
                }
            }

            //CASTLING
            if(!moved){
                // right castling
                if(targetCol==preCol+2 && targetRow == preRow && !pieceIsOnStraightLine(targetCol,targetRow)){
                    for(Piece piece : GamePanel.simPieces){
                        if(piece.col == preCol+3 && piece.row == preRow && !piece.moved){
                            GamePanel.castlingP = piece;
                            return true;
                        }
                    }
                }
                //left castling
                if(targetCol == preCol-2 && targetRow == preRow && !pieceIsOnStraightLine(targetCol, targetRow)){
                    Piece[] p = new Piece[2];
                    for (Piece piece : GamePanel.simPieces){
                        if(piece.col == preCol-3 && piece.row == targetRow){
                            p[0] = piece;
                        }
                        if(piece.col == preCol-4 && piece.row ==targetRow){
                            p[1] = piece;
                        }
                        if(p[0]==null && p[1]!=null  && !p[1].moved){
                            GamePanel.castlingP = p[1];
                            return true;
                        }
                    }
                }


            }
        }
         return false;
    }
}
