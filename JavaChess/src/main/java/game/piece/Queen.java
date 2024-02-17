package game.piece;

import game.GamePanel;
import game.Type;

public class Queen extends Piece{

    public Queen(int color, int col, int row) {
        super(color, col, row);
        type = Type.QUEEN;

        if(color == GamePanel.WHITE){
            image = getImage("/w-queen");
        } else{
            image = getImage("/b-queen");
        }
    }

    @Override
    public boolean canMove(int targetCol, int targetRow) {
        if(isWithinBoard(targetCol, targetRow) && !isSameSquare(targetCol, targetRow)){
            //vertical and horizontal
            if(targetCol == preCol || targetRow == preRow){
                if(isValidSquare(targetCol, targetRow) && !pieceIsOnStraightLine(targetCol,targetRow)){
                    return true;
                }
            }
            // diagonal
            if(Math.abs(targetCol - preCol) == Math.abs(targetRow - preRow)){
                if(isValidSquare(targetCol, targetRow) && !pieceIsOnDiagonalLine(targetCol, targetRow)){
                    return true;
                }
            }
        }
        return false;
    }
}
