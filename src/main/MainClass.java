package main;

import java.io.File;
import java.util.Timer;

import javax.swing.SwingUtilities;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;

import static main.Game.gameEnd;

public class MainClass {
    public static void main(String[] args) {
        
        SwingUtilities.invokeLater(() -> {
            new JFXPanel();
            Platform.setImplicitExit(false);
            Game game = new Game(); 
            GamePanel gamePanel = new GamePanel(game);
            GameWindow gameWindow = GameWindow.getInstance();
            gameWindow.setGamePanel(gamePanel);
    
            game.update();
    
            // game.setGameEndListener(() -> {
            //     File videoFile = new File("resources/LastGameScene.mp4");
            //     // String videoPath = "D:/Download _ D/GameWukong/javaProject/LastGame'sScene.mp4";
            //     String videoPath = videoFile.toURI().toString();
            //     // File file = new File(videoPath);
            //     // String videoURI = file.toURI().toString();
            //     // File file = new File("LastGameScene.mp4");
            //     // String videoPath = file.toURI().toString();
                
            //     gameWindow.switchToVideo(gameWindow.getJFrame(), videoPath); // Chuyển sang video
            // });
            // // game.update();
        });
        // gameWindow.repaint();
    }

}
