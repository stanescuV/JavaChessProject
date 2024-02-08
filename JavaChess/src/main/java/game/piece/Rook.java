package game.piece;

import game.GamePanel;

public class Rook extends Piece{

    public Rook(int color, int col, int row) {
        super(color, col, row);

        if(color == GamePanel.WHITE){
            image = getImage("/w-rook");
        } else{
            image = getImage("/b-rook");
        }
    }
}
