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

    @Override
    public boolean canMove(int targetCol, int targetRow) {
        if(isWithinBoard(targetCol,targetRow) && !isSameSquare(targetCol,targetRow)){
            //rook can move as long as either its col or row is the same
            if(targetCol == preCol || targetRow == preRow){
                if(isValidSquare(targetCol,targetRow) && !pieceIsOnStraightLine(targetCol,targetRow)){
                    return true;
                }
            }
        }

        return false;

    }
}
