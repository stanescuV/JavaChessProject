package game;
import game.piece.*;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class GamePanel extends JPanel implements Runnable{
    //mettre toutes les constantes dans un seul fichier
    public static final int WIDTH = 1100;
    public static final int HEIGHT = 800;
    private final int FPS = 60;
    private Thread gameThread;
    private Board board;
    private Mouse mouse;

    //PIECES
    public static ArrayList<Piece> pieces = new ArrayList<>();
    public static ArrayList<Piece> simPieces = new ArrayList<>();
    ArrayList<Piece> promoPieces = new ArrayList<>();
    //piece that the player is holding
    // Shift + F6 pour tout renommer
    private Piece activePiece, checkingPiece;
    public static Piece castlingP;


    //COLOR
    public static final int WHITE = 0;
    public static final int BLACK = 1;
    int currentColor = WHITE;

    //BOOLEANS
    boolean canMove;
    boolean validSquare;
    boolean promotion;
    boolean gameOver;
    boolean stalemate;


    //constructor
    public GamePanel(){
        //si jamais je veux plusieurs Boards
        board = new Board();
        mouse = new Mouse();
        setPreferredSize(new Dimension(WIDTH,HEIGHT));
        setBackground(Color.black);
        setPieces();
        copyPieces(pieces, simPieces);
        addMouseMotionListener(mouse);
        addMouseListener(mouse);

    }


    //adding pieces to the arrayList
    public void setPieces(){

        //Todo boucle pour les pions faire avec le stream
        //WHITE PIECES
        pieces.add(new Pawn(WHITE,0,6));
        pieces.add(new Pawn(WHITE,1,6));
        pieces.add(new Pawn(WHITE,2,6));
        pieces.add(new Pawn(WHITE,3,6));
        pieces.add(new Pawn(WHITE,4,6));
        pieces.add(new Pawn(WHITE,5,6));
        pieces.add(new Pawn(WHITE,6,6));
        pieces.add(new Pawn(WHITE,7,6));
        pieces.add(new Rook(WHITE,0,7));
        pieces.add(new Rook(WHITE,7,7));
        pieces.add(new Knight(WHITE,1,7));
        pieces.add(new Knight(WHITE,6,7));
        pieces.add(new Bishop(WHITE,5,7));
        pieces.add(new Bishop(WHITE,2,7));
        pieces.add(new Queen(WHITE,3,7));
        pieces.add(new King(WHITE,4,7));

        //BLACK PIECES
        pieces.add(new Pawn(BLACK,0,1));
        pieces.add(new Pawn(BLACK,1,1));
        pieces.add(new Pawn(BLACK,2,1));
        pieces.add(new Pawn(BLACK,3,1));
        pieces.add(new Pawn(BLACK,4,1));
        pieces.add(new Pawn(BLACK,5,1));
        pieces.add(new Pawn(BLACK,6,1));
        pieces.add(new Pawn(BLACK,7,1));
        pieces.add(new Rook(BLACK,7,0));
        pieces.add(new Rook(BLACK,0,0));
        pieces.add(new Knight(BLACK,1,0));
        pieces.add(new Knight(BLACK,6,0));
        pieces.add(new Bishop(BLACK,5,0));
        pieces.add(new Bishop(BLACK,2,0));
        pieces.add(new Queen(BLACK,3,0));
        pieces.add(new King(BLACK,4,0));
    }

    private void copyPieces(ArrayList<Piece> source, ArrayList<Piece> target){
        target.clear();
        for (int i = 0 ; i < source.size(); i++){
            target.add(source.get(i));
        }
    }





    public void launchGame(){
        gameThread = new Thread(this);
        gameThread.start();
    }
    @Override // GAME LOOP
    public void run() {
        double drawInterval = 1_000_000_000/FPS;
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;

        while(gameThread != null){
            currentTime = System.nanoTime();
            delta += (currentTime - lastTime)/drawInterval;
            lastTime = currentTime;

            if(delta >= 1){
                update();
                repaint();
                delta--;
            }
        }
    }
    //will handle all the updates
    private void update(){

        if(promotion){
            promoting();

        } else if(!gameOver && !stalemate) {
            ///// MOUSE BUTTON PRESSED /////
            if(mouse.pressed){
                if(activePiece==null){
                    // If the activeP is null, check if u can pick up a piece
                    for (Piece piece : simPieces){
                        // If the mouse is on an ally piece, pick it up as the activeP
                        if(piece.color == currentColor &&
                                piece.col == mouse.x/Board.SQUARE_SIZE &&
                                piece.row == mouse.y/Board.SQUARE_SIZE) {
                            activePiece = piece;
                        }
                    }
                }
                else {
                    //If the player is holding a piece, simulate the move
                    simulate();
                }
            }
            ///MOUSE BUTTON RELEASED///
            if(!mouse.pressed){
                if(activePiece != null){
                    if(validSquare){
                        ///MOVE CONFIRMED
                        //update the piece list in case a piece has been captured and removed during the simulation
                        copyPieces(simPieces, pieces);
                        activePiece.updatePosition();
                        if(castlingP != null){
                            castlingP.updatePosition();
                        }

                        if(isKingInCheck() && isCheckmate()){
                            gameOver = true;
                        } else if(isStalemate() && !isKingInCheck()){
                            stalemate= true;
                        }
                        else { // the game is still going on
                            if(canPromote()){
                                promotion = true;
                            } else{
                                changePlayer();
                            }
                        }


                    }
                    else
                    {
                        // the move is not valid so reset everything
                        copyPieces(pieces, simPieces);
                        activePiece.resetPosition();
                        activePiece = null;
                    }
                }
            }
        }


    }

    private void simulate(){

        canMove = false;
        validSquare=false;
        // reset the piece list in every loop
        //this is basically for restoring the removed piece during the simulation
        copyPieces(pieces, simPieces);

        //reset the castling piece's position
        if(castlingP!= null){
            castlingP.col = castlingP.preCol;
            castlingP.x = castlingP.getX(castlingP.col);
            castlingP = null;

        }

    //Todo comment ça fonctionne vraiment
        // If a piece is being held, update its position
        activePiece.x = mouse.x - Board.HALF_SQUARE_SIZE;
        activePiece.y = mouse.y - Board.HALF_SQUARE_SIZE;
        activePiece.col = activePiece.getCol(activePiece.x);
        activePiece.row = activePiece.getRow(activePiece.y);

        //Check if the pieces is hovering over a reachable square
        if(activePiece.canMove(activePiece.col, activePiece.row)){
            canMove = true;

            if(activePiece.hittingP != null ){
                simPieces.remove(activePiece.hittingP.getIndex());
            }
            checkCastling();


            if(!isIllegal(activePiece) && !opponentCanCaptureKing()){
            validSquare = true;
            }
        }

    }

    private boolean isIllegal(Piece king){
        if(king.type == Type.KING){
            for(Piece piece: simPieces){
                if(piece != king && piece.color != king.color && piece.canMove(king.col, king.row)){
                    return true;
                }
            }
        }

        return false;
    }

    private boolean opponentCanCaptureKing(){
        Piece king = getKing(false);
        for(Piece piece : simPieces){
            if(piece.color != king.color && piece.canMove(king.col, king.row)){
                return true;
            }
        }
        return false;
    }
    private boolean isKingInCheck(){
        Piece king = getKing(true);

        if(activePiece.canMove(king.col, king.row)){
            checkingPiece = activePiece;
            return true;
        } else{
            checkingPiece = null;
        }

        return false;
    }

    private Piece getKing(boolean opponent){
        Piece king = null;
        for(Piece piece: simPieces){
            if(opponent){
                if(piece.type == Type.KING && piece.color != currentColor){
                    king = piece;
                }
            }else {
                if(piece.type == Type.KING && piece.color == currentColor){
                    king = piece;
                }
            }
        }
        return king;
    }
    private void checkCastling(){
        if(castlingP != null){
            if(castlingP.col == 0) {
                castlingP.col += 3;
            }
            else if(castlingP.col == 7){
                castlingP.col -= 2;
            }
            castlingP.x = castlingP.getX(castlingP.col);
        }
    }

    private boolean isStalemate(){
        int count = 0;
        //count the number of pieces
        for(Piece piece : simPieces){
            if(piece.color != currentColor){
                count ++;
            }
        }
        // only king is left
        if(count == 1){
            if(kingCanMove(getKing(true))==false){
                return true;
            }
        }
        return false;
    }

    private boolean isCheckmate(){
        Piece king = getKing(true);
        if(kingCanMove(king)){
            return false;
        }
        else {
            // but you still have a chance
            // check if you can block the attack with your piece

            // check the position of the cheking piece and the king in check
            int colDiff = Math.abs(checkingPiece.col - king.col);
            int rowDiff = Math.abs(checkingPiece.row - king.row);

            if(colDiff == 0){
                //The checking piece is attacking vertically
                if(checkingPiece.row< king.row){
                    //the checking piece is above the king
                    for(int row = checkingPiece.row; row<king.row; row++){
                        for(Piece piece : simPieces){
                            if(piece != king && piece.color != currentColor && piece.canMove(checkingPiece.col, row)){
                                return false;
                            }
                        }
                    }
                }
                if(checkingPiece.row> king.row){
                    // the checking piece is below
                    for(int row = checkingPiece.row; row>king.row; row--){
                        for(Piece piece : simPieces){
                            if(piece != king && piece.color != currentColor && piece.canMove(checkingPiece.col, row)){
                                return false;
                            }
                        }
                    }

                }
            } else if(rowDiff == 0){
                //The checking piece is attacking horizontally
                //Checking piece is on the left
                if(checkingPiece.col< king.col){
                    for(int col = checkingPiece.col; col<king.col; col++){
                        for(Piece piece : simPieces){
                            if(piece != king && piece.color != currentColor && piece.canMove(col, checkingPiece.row)){
                                return false;
                            }
                        }
                    }
                }

                //Checking piece is on the right
                if(checkingPiece.col>king.col){
                    for(int col = checkingPiece.col; col>king.col; col--){
                        for(Piece piece : simPieces){
                            if(piece != king && piece.color != currentColor && piece.canMove(col, checkingPiece.row)){
                                return false;
                            }
                        }
                    }
                }
            } else if(colDiff == rowDiff){
                // The checking piece is attacking diagonally
                //piece is attacking above king
                if(checkingPiece.row < king.row){
                    // upper left
                    if(checkingPiece.col < king.col){
                        for(int col = checkingPiece.col, row = checkingPiece.row; col<king.col ; col++ , row++){
                            for(Piece piece : simPieces){
                                if(piece != king && piece.color != currentColor && piece.canMove(col,row)){
                                    return false;
                                }
                            }
                        }

                    }
                    // upper right
                    if(checkingPiece.col > king.col){
                        for(int col = checkingPiece.col, row = checkingPiece.row; col>king.col ; col-- , row++){
                            for(Piece piece : simPieces){
                                if(piece != king && piece.color != currentColor && piece.canMove(col,row)){
                                    return false;
                                }
                            }
                        }
                    }

                }
                //piece is attacking below king
                if(checkingPiece.row > king.row){
                    //lower left
                    if(checkingPiece.col < king.col){
                        for(int col = checkingPiece.col, row = checkingPiece.row; col<king.col ; col++ , row--){
                            for(Piece piece : simPieces){
                                if(piece != king && piece.color != currentColor && piece.canMove(col,row)){
                                    return false;
                                }
                            }
                        }
                    }
                    //lower right
                    if(checkingPiece.col > king.col){
                        for(int col = checkingPiece.col, row = checkingPiece.row; col>king.col ; col-- , row--){
                            for(Piece piece : simPieces){
                                if(piece != king && piece.color != currentColor && piece.canMove(col,row)){
                                    return false;
                                }
                            }
                        }
                    }

                }
            }
        }
        return true;
    }
    private boolean kingCanMove(Piece king){

        // simulate if there is any square where the king can move

        if(isValidMove(king, 0,1)){
            return true;
        }

        if(isValidMove(king, 0,-1)){
            return true;
        }
        if(isValidMove(king, -1,0)){
            return true;
        }
        if(isValidMove(king, -1,1)){
            return true;
        }
        if(isValidMove(king, -1,-1)){
            return true;
        }
        if(isValidMove(king, 1,1)){
            return true;
        }
        if(isValidMove(king, 1,0)){
            return true;
        }
        if(isValidMove(king, 1,-1)){
            return true;
        }
    return false;
    }
    private boolean isValidMove(Piece king, int colPlus, int rowPlus){
        boolean isValidMove = false;

        //update the king s position for a second

        king.col += colPlus;
        king.row += rowPlus;

        if(king.canMove(king.col, king.row)){
            if(king.hittingP != null){
                simPieces.remove(king.hittingP.getIndex());
            }
            if(!isIllegal(king)){
                isValidMove = true;
            }
        }
        king.resetPosition();
        copyPieces(pieces, simPieces);
        return isValidMove;
    }
    private void changePlayer(){
        if(currentColor == WHITE){
            currentColor = BLACK;
            //reset black s two stepped status
            for(Piece piece : pieces) {
                if(piece.color == BLACK){
                    piece.twoStepped = false;
                }
            }
        } else{
            currentColor = WHITE;
            //reset white s two stepped status
            for(Piece piece : pieces) {
                if(piece.color == WHITE){
                    piece.twoStepped = false;
                }
            }
        }
        activePiece = null;
    }


    private  boolean canPromote(){
        if(activePiece.type == Type.PAWN){
            if(currentColor == WHITE && activePiece.row == 0 || currentColor == BLACK && activePiece.row==7){
                promoPieces.clear();
                promoPieces.add(new Rook(currentColor,9,2));
                promoPieces.add(new Knight(currentColor,9,3));
                promoPieces.add(new Bishop(currentColor,9,4));
                promoPieces.add(new Queen(currentColor,9,5));
                return true;
            }
        }
        return false;
    }

    private void promoting() {
        if(mouse.pressed){
            for(Piece piece : promoPieces){
                if(piece.col == mouse.x/Board.SQUARE_SIZE && piece.row == mouse.y/Board.SQUARE_SIZE){
                    switch(piece.type){
                        case ROOK: simPieces.add(new Rook(currentColor, activePiece.col, activePiece.row));break;
                        case KNIGHT: simPieces.add(new Knight(currentColor, activePiece.col, activePiece.row));break;
                        case BISHOP: simPieces.add(new Bishop(currentColor, activePiece.col, activePiece.row));break;
                        case QUEEN: simPieces.add(new Queen(currentColor, activePiece.col, activePiece.row));break;
                        default: break;
                    }
                    simPieces.remove(activePiece.getIndex());
                    copyPieces(simPieces, pieces);
                    activePiece = null;
                    promotion = false;
                    changePlayer();
                }
            }
        }

    }
    //used to draw objects in the panel
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D)g;

        //BOARD
        board.draw(g2);

        //PIECES
        for(Piece p : simPieces){
            p.draw(g2);
        }

        if(activePiece != null){
            if(canMove){
                if(isIllegal(activePiece) || opponentCanCaptureKing()){
                    g2.setColor(Color.red);
                    // todo Alpha Composite 34 : 45
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f));
                    g2.fillRect(activePiece.col*Board.SQUARE_SIZE, activePiece.row*Board.SQUARE_SIZE, Board.SQUARE_SIZE,Board.SQUARE_SIZE);
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
                } else {
                    g2.setColor(Color.white);
                    // todo Alpha Composite 34 : 45
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f));
                    g2.fillRect(activePiece.col*Board.SQUARE_SIZE, activePiece.row*Board.SQUARE_SIZE, Board.SQUARE_SIZE,Board.SQUARE_SIZE);
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
                }


            }
            //Draw the active piece in the end so it won't be hidden by the board or the colored square
            activePiece.draw(g2);
        }

        // STATUS MESSAGES
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setFont(new Font ("Book Antiqua", Font.PLAIN, 40));
        g2.setColor(Color.white);

        if(promotion){
            g2.drawString("Promote to:", 840, 150);
            for(Piece piece: promoPieces){
                g2.drawImage(piece.image, piece.getX(piece.col), piece.getY(piece.row),Board.SQUARE_SIZE,Board.SQUARE_SIZE, null);
            }
        }else {
            if(currentColor == WHITE){
                g2.drawString("White's turn", 840, 550);
                if(checkingPiece != null && checkingPiece.color == BLACK){
                    g2.setColor(Color.red);
                    g2.drawString("The King", 840, 650);
                    g2.drawString("is in check!", 840, 700);
                }
            } else {
                g2.drawString("Black's Turn", 840, 250);
                if(checkingPiece != null && checkingPiece.color == WHITE){
                    g2.setColor(Color.red);
                    g2.drawString("The King", 840, 100);
                    g2.drawString("is in check!", 840, 150);
                }
            }
            if(gameOver){
                String s = "";
                if(currentColor == WHITE){
                    s = "White Wins";
                } else {
                    s = "Black Wins";
                }
                g2.setFont(new Font("Arial", Font.PLAIN, 90));
                g2.setColor(Color.green);
                g2.drawString(s,200,420);
            }
            if(stalemate){
                g2.setFont(new Font("Arial", Font.PLAIN, 90));
                g2.setColor(Color.green);
                g2.drawString("Stalemate",200,420);
            }

        }


    }

}
