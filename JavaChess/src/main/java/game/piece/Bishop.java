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

    @Override
    public boolean canMove(int targetCol, int targetRow) {
        if(isWithinBoard(targetCol, targetRow) && !isSameSquare(targetCol, targetRow)){
            if(Math.abs(targetCol - preCol) == Math.abs(targetRow - preRow)) {
                if(isValidSquare(targetCol, targetRow) && !pieceIsOnDiagonalLine(targetCol,targetRow)){
                    return true;
                }

            }
        }
    return false;
    }
}
