package game;
import game.piece.*;

import javax.swing.*;
import java.awt.*;
import java.sql.Array;
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
    //piece that the player is holding
    // Shift + F6 pour tout renommer
    private Piece activePiece;


    //COLOR
    public static final int WHITE = 0;
    public static final int BLACK = 1;
    PieceColor currentColor = PieceColor.WHITE;


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

        if(mouse.pressed == false){
            if(activePiece != null){
                activePiece.updatePosition();
                activePiece = null;
            }
        }

    }

    private void simulate(){
    //Todo comment ça fonctionne vraiment
        // If a piece is being held, update its position
        activePiece.x = mouse.x - Board.HALF_SQUARE_SIZE;
        activePiece.y = mouse.y - Board.HALF_SQUARE_SIZE;
        activePiece.col = activePiece.getCol(activePiece.x);
        activePiece.row = activePiece.getRow(activePiece.y);
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
            g2.setColor(Color.white);
            // todo Alpha Composite 34 : 45
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f));
            g2.fillRect(activePiece.col*Board.SQUARE_SIZE, activePiece.row*Board.SQUARE_SIZE, Board.SQUARE_SIZE,Board.SQUARE_SIZE);
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));

            //Draw the active piece in the end so it won't be hidden by the board or the colored square
            activePiece.draw(g2);
        }
    }

}
