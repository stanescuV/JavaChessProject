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
}