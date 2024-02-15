package game.piece;

import game.GamePanel;

public class Knight extends Piece{

    public Knight(int color, int col, int row) {
        super(color, col, row);

        if(color == GamePanel.WHITE){
            image = getImage("/w-knight");
        } else{
            image = getImage("/b-knight");
        }
    }

    @Override
    public boolean canMove(int targetCol, int targetRow) {
        if(isWithinBoard(targetCol,targetRow)){
            //knight can move col row 1:2 or 2:1
            if(Math.abs(targetCol-preCol)*Math.abs(targetRow-preRow)==2){
                if(isValidSquare(targetCol,targetRow)){
                    return true;
                }
            }
        }
        return false;
    }
}
