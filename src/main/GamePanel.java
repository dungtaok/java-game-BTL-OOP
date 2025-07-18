package main;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;
import input.KeyboardInputs;
import input.MouseInputs;
import javafx.embed.swing.JFXPanel;
import ui.UI;

import static main.Game.GAME_HEIGHT;
import static main.Game.GAME_WIDTH;

public class GamePanel extends JFXPanel {

    private MouseInputs mouseInputs;
    private Game game;

    public GamePanel(Game game) {
        mouseInputs = new MouseInputs(this);
        this.game = game;
        setPanelSize();
        addKeyListener(new KeyboardInputs(this));
        addMouseListener(mouseInputs);
        addMouseMotionListener(mouseInputs);
    }

    private void setPanelSize() {
        Dimension size = new Dimension(GAME_WIDTH, GAME_HEIGHT);
        setPreferredSize(size);
    }


    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        game.render(g);
     
    }

    public Game getGame() {
        return game;
    }

    // public void switchToVideo(String videoPath){
    //     setVisible(false);
    //     GameWindow.getInstance().switchToVideo(videoPath);
    // }

    // public void switchToGame(){
    //     setVisible(true);
    //     GameWindow.getInstance().switchToGame();
    // }

}


