package ui;

import gameState.Gamestate;
import gameState.Playing;
import main.Game;

import java.awt.*;
import java.awt.event.KeyEvent;

public class GameOverOverLay {
    private Playing playing;
    public GameOverOverLay(Playing playing){
        this.playing=playing;
    }
    public void draw(Graphics g){
        g.setColor(new Color(0,0,0,200));
        g.fillRect(0,0, Game.GAME_WIDTH,Game.GAME_HEIGHT);

        g.setColor(Color.white);
        g.drawString("Gane Over",Game.GAME_WIDTH/2,150);
        g.drawString("Press esc to enter Menu",Game.GAME_WIDTH/2,300);

    }
    public void keyPressed(KeyEvent e){
        if(e.getKeyCode()==KeyEvent.VK_ESCAPE){
            playing.resetAll();
            Gamestate.state=Gamestate.MENU;
        }

    }
}
