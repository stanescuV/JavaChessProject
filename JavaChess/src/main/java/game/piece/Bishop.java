package game.piece;

import game.GamePanel;

public class Bishop extends Piece{

    public Bishop(int color, int col, int row) {
        super(color, col, row);

        if(color == GamePanel.WHITE){
            image = getImage("/w-bishop");
        } else{
            image = getImage("/b-bishop");
        }
    }
}
