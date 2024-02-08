package game.piece;
import game.GamePanel;

public class Pawn extends Piece {
    public Pawn(int color, int col, int row){
        super(color,col,row);

        if(color == GamePanel.WHITE){
            image = getImage("/w-pawn");
        } else{
            image = getImage("/b-pawn");
        }
    }

}
