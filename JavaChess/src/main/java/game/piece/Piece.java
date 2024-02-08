package game.piece;
import game.Board;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class Piece {
    public BufferedImage image;
    public int x,y;
    public int col, row, preCol, preRow;
    public int color;

    public Piece(int color, int col, int row){
        this.color = color;
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
        return col * Board.SQUARE_SIZE;
    }

    public int getY(int row) { return row * Board.SQUARE_SIZE; }


    public void draw(Graphics2D g2) {
        g2.drawImage(image, x, y, Board.SQUARE_SIZE, Board.SQUARE_SIZE, null);
    }
}
