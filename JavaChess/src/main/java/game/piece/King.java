package game.piece;

import game.GamePanel;

public class King extends Piece{
     public King(int color, int col, int row) {
        super(color, col, row);

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
        }
         return false;
    }
}
