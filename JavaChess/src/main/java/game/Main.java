package game;

import javax.swing.JFrame;

public class Main {
    public static void main(String[] args) {
    JFrame window = new JFrame("Simple Chess");
    //when we close the window we shut down the program
    window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    //can not resize the window
    window.setResizable(false);

    GamePanel gp = new GamePanel();
    window.add(gp);
    window.pack();

    //middle of the screen
    window.setLocationRelativeTo(null);
    //see it on the screen
    window.setVisible(true);


    gp.launchGame();

    }
}
