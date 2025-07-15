package main;

import javax.swing.*;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.Sphere;

import static main.Game.gameEnd;
import static main.Game.GAME_HEIGHT;
import static main.Game.GAME_WIDTH;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.io.File;

public class GameWindow {
    private static GameWindow instance;
    private final JFrame jframe;
    private GamePanel gamePanel;
    private JFXPanel videoPanel;
    private Media media;
    private MediaPlayer mediaPlayer;
    private MediaView mediaView ;

    private GameWindow() {
        jframe = new JFrame("BlackMythWukong");
        jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jframe.setLayout(new BorderLayout());
        jframe.setSize(GAME_WIDTH, GAME_HEIGHT);
        jframe.setResizable(false);
    }

    public static GameWindow getInstance() {
        if (instance == null) {
            instance = new GameWindow();
        }
        return instance;
    }

    public void setGamePanel(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
        jframe.add(gamePanel);
        jframe.pack();
        jframe.setLocationRelativeTo(null);
        gamePanel.setFocusable(true);
        gamePanel.requestFocusInWindow();
        jframe.setVisible(true);

        jframe.addWindowFocusListener(new WindowFocusListener() {
            @Override
            public void windowGainedFocus(WindowEvent e) {
            }

            @Override
            public void windowLostFocus(WindowEvent e) {
                gamePanel.getGame().windowFocusLost();
            }
        });
    }

    public void switchToGame() {
        SwingUtilities.invokeLater(() -> {
            jframe.remove(videoPanel);               // Xoá panel video
            jframe.add(gamePanel, BorderLayout.CENTER);  // Thêm lại gamePanel

            gamePanel.setFocusable(true);            // Cho phép focus
            gamePanel.requestFocusInWindow();        // Focus lại phím
            jframe.revalidate();
            jframe.repaint();
        });
    }


    public void switchToVideo(JFrame jFrame, String videoPath) {
        System.out.println("Switching to video mode...");
        System.out.println("Video path: " + videoPath);

        // Kiểm tra đường dẫn video
        if (!videoPath.startsWith("file:")) {
            File file = new File(videoPath);
            if (!file.exists()) {
                System.out.println("Video file not found: " + videoPath);
                JOptionPane.showMessageDialog(jframe, "Video file not found", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            videoPath = file.toURI().toString();
        }

        videoPanel = new JFXPanel();
        videoPanel.setPreferredSize(new Dimension(GAME_WIDTH, GAME_HEIGHT));
        videoPanel.setSize(GAME_WIDTH, GAME_HEIGHT);

        final String finalVideoPath = videoPath;
        Platform.runLater(() -> {
            try {
                // System.out.println("Attempting to play video from: " + finalVideoPath);
                
                this.media = new Media(finalVideoPath);
                this.mediaPlayer = new MediaPlayer(media);
                this.mediaView = new MediaView(mediaPlayer);

                // Thiết lập tỷ lệ và kích thước cho MediaView
                mediaView.setPreserveRatio(false);
                mediaView.setFitWidth(GAME_WIDTH);
                mediaView.setFitHeight(GAME_HEIGHT);
                mediaView.setVisible(true);

                // Tạo container cho MediaView với background đen
                StackPane root = new StackPane(mediaView);
                Scene scene = new Scene(root, GAME_WIDTH, GAME_HEIGHT);
                
                videoPanel.setScene(scene);
                videoPanel.setSize(Game.GAME_WIDTH, Game.GAME_HEIGHT);
                videoPanel.setVisible(true);
                // Xóa panel game cũ và thêm video panel
                // jframe.removeAll();
                // jframe.add(videoPanel, BorderLayout.CENTER);
                // jframe.revalidate();
                // jframe.repaint();

                SwingUtilities.invokeLater(() -> {
                    jframe.getContentPane().removeAll(); // xoá toàn bộ các panel cũ
                    jframe.getContentPane().add(videoPanel, BorderLayout.CENTER);
                    jframe.invalidate();   // Đánh dấu lại layout
                    jframe.validate();     // Bố trí lại layout
                    jframe.repaint();      // Vẽ lại toàn bộ giao diện
                });

                // Phát video
                mediaPlayer.setOnReady(() -> {
                    Platform.runLater(() -> {
                        try {
                            mediaPlayer.seek(javafx.util.Duration.ZERO);
                            mediaPlayer.play();
                            System.out.println("Video playback started successfully");
                        } catch (Exception e) {
                            System.out.println("Error during playback start: " + e.getMessage());
                            e.printStackTrace();
                        }
                    });
                });

                // Xử lý khi video kết thúc
                mediaPlayer.setOnEndOfMedia(() -> {
                    Platform.runLater(() -> {
                        if (mediaPlayer != null) {
                            mediaPlayer.dispose();
                        }
                        switchToGame();
                    });
                });

            } catch (Exception ex) {    
                ex.printStackTrace();
                System.out.println("Detailed error: " + ex.getMessage());
                Platform.runLater(() -> {
                    JOptionPane.showMessageDialog(jframe, "Cannot play video: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                });
            }
        });
    }

    public JFXPanel getGamePanel() {
        return gamePanel;
    }

    public JFrame getJFrame() {
        return jframe;
    }
}
