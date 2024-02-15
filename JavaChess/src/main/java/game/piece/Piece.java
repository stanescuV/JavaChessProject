package game.piece;

import game.GamePanel;

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
    public Piece hittingP;

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

    public int getIndex(){
        for(int index = 0 ; index < GamePanel.simPieces.size(); index++){
            if(GamePanel.simPieces.get(index) == this){
                return index;
            }
        }
        return 0;
    }

    public void updatePosition(){
        x = getX(col);
        y = getY(row);
        preCol = getCol(x);
        preRow = getRow(y);
    }

    public void resetPosition(){
        col = preCol;
        row = preRow;
        x = getX(col);
        y = getY(row);

    }
    public boolean canMove(int targetCol, int targetRow){
        return false;
    }

    public boolean isWithinBoard(int targetCol, int targetRow){
        if(targetCol>= 0 && targetCol <= 7 && targetRow >= 0 && targetRow <= 7){
            return true;
        }
        return false;
    }

    public boolean isSameSquare(int targetCol, int targetRow){
        if(targetCol == preCol && targetRow == preRow){
            return true;
        }
        return false;
    }

    public Piece getHittingP(int targetCol, int targetRow){
       for(Piece piece : GamePanel.simPieces){
           if(piece.col == targetCol && piece.row == targetRow && piece != this){
               return piece;
           }
       }
       return null;
    }

    public boolean isValidSquare(int targetCol, int targetRow){
        hittingP = getHittingP(targetCol, targetRow);

        if(hittingP ==null){ // the square is free
            return true;
        } else { // the square is occupied
            if(hittingP.color != this.color){ // If the color is different, it can be captured
                return true;
            } else{
                hittingP = null;
            }

        }

        return false;
    }

    public boolean pieceIsOnStraightLine(int targetCol, int targetRow){

        //when the piece is moving to the left
        for(int col = preCol-1; col> targetCol ; col--){
            for(Piece piece : GamePanel.simPieces){
                if(piece.col == col && piece.row == targetRow){
                    hittingP = piece;
                    return true;
                }
            }
        }
        //when the piece is moving to the right
        for(int col = preCol+1; col< targetCol ; col++){
            for(Piece piece : GamePanel.simPieces){
                if(piece.col == col && piece.row == targetRow){
                    hittingP = piece;
                    return true;
                }
            }
        }
        //when the piece is moving to the down
        for(int row = preRow+1; row < targetRow ; row++){
            for(Piece piece : GamePanel.simPieces){
                if(piece.col == targetCol && piece.row == row){
                    hittingP = piece;
                    return true;
                }
            }
        }
        //when the piece is moving to the up
        for(int row = preRow-1; row > targetRow ; row--){
            for(Piece piece : GamePanel.simPieces){
                if(piece.col == targetCol && piece.row == row){
                    hittingP = piece;
                    return true;
                }
            }
        }

        return false;
    }

    public boolean pieceIsOnDiagonalLine(int targetCol, int targetRow){
        if(targetRow<preRow){
            //Up left
            for(int col = preCol-1; col> targetCol; col--){
                int diff = Math.abs(col-preCol);
                for(Piece piece: GamePanel.simPieces){
                    if(piece.col == col && piece.row == preRow - diff){
                        hittingP = piece;
                        return true;
                    }
                }
            }
            //Up right
            for(int col = preCol+1; col < targetCol; col++){
                int diff = Math.abs(col-preCol);
                for(Piece piece: GamePanel.simPieces){
                    if(piece.col == col && piece.row == preRow - diff){
                        hittingP = piece;
                        return true;
                    }
                }
            }

        }
        if(targetRow > preRow){

            //Down left
            for(int col = preCol-1; col> targetCol; col--){
                int diff = Math.abs(col-preCol);
                for(Piece piece: GamePanel.simPieces){
                    if(piece.col == col && piece.row == preRow + diff){
                        hittingP = piece;
                        return true;
                    }
                }
            }
            //Down right
            for(int col = preCol+1; col< targetCol; col++){
                int diff = Math.abs(col-preCol);
                for(Piece piece: GamePanel.simPieces){
                    if(piece.col == col && piece.row == preRow + diff){
                        hittingP = piece;
                        return true;
                    }
                }
            }
        }
        return false;
    }
    public void draw(Graphics2D g2) {
        g2.drawImage(image, x, y, SQUARE_SIZE, SQUARE_SIZE, null);
    }
}
