package game.piece;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

import static game.Board.HALF_SQUARE_SIZE;
import static game.Board.SQUARE_SIZE;


public class Piece {
    public BufferedImage image;
    public int x,y;
    public int col, row, preCol, preRow;
    public PieceColor color;

    public Piece(int color, int col, int row){
        this.color = PieceColor.fromValue(color);
        this.col = col;
        this.row = row;
        x = getX(col);
        y = getY(row);
        preCol = col;
        preRow = row;
    }

    public BufferedImage getImage(String imagePath){
        BufferedImage image = null;

        try {
            image = ImageIO.read(getClass().getResourceAsStream(imagePath+".png"));
        } catch (IOException e){
            e.printStackTrace();
        }
        return image;
    }

    //je ne peux pas import BOARD CLASS game.Board.Square_Size au lieu de 100
    public int getX(int col) {
        return col * SQUARE_SIZE;
    }

    public int getY(int row) { return row * SQUARE_SIZE; }

    public int getCol(int x){
        return (x + HALF_SQUARE_SIZE)/ SQUARE_SIZE;
    }

    public int getRow(int y){
        return (y + HALF_SQUARE_SIZE)/ SQUARE_SIZE;
    }

    public void updatePosition(){
        x = getX(col);
        y = getY(row);
        preCol = getCol(x);
        preRow = getRow(y);
    }



    public void draw(Graphics2D g2) {
        g2.drawImage(image, x, y, SQUARE_SIZE, SQUARE_SIZE, null);
    }
}
