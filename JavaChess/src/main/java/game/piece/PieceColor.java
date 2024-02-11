package game.piece;

public enum PieceColor {
    WHITE,
    BLACK;

    public static PieceColor fromValue(int color) {
        for(PieceColor p : values()){

            if(color == 0 ){
                return WHITE;
            } else if (color == 1){
                return BLACK;
            }
        }
        throw new IllegalArgumentException();
    }
}
