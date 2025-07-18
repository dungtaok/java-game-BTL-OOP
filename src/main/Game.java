package main;

import java.awt.*;
import java.io.File;

import audio.AudioPlayer;
import gameState.GameOptions;
import gameState.Gamestate;
import gameState.Menu;

import gameState.Playing;
import ui.AudioOptions;
import utilz.LoadSave;

public class Game implements Runnable {
	
	public static boolean gameEnd = false;
    private GameWindow gameWindow;
    private GamePanel gamePanel;
    private Thread gameThread;
    private final int FPS_SET = 120;
    private final int UPS_SET = 200;
    private int frames;
    

    private Playing playing;
    private Menu menu;
    private GameOptions gameOptions;
    private AudioOptions audioOptions;
    private AudioPlayer audioPlayer;

    private Runnable gameEndListener;

    public final static int TILES_DEFAULT_SIZE = 32;
    public final static float SCALE = 1.85f;
    public final static int TILES_IN_WIDTH = 26;
    public final static int TILES_IN_HEIGHT = 16;
    public final static int TILES_SIZE = (int) (TILES_DEFAULT_SIZE * SCALE);
    public final static int GAME_WIDTH = TILES_SIZE * TILES_IN_WIDTH;
    public final static int GAME_HEIGHT = TILES_SIZE * TILES_IN_HEIGHT;

    public Game() {
        LoadSave.GetAllLevels();
        System.out.println(GAME_WIDTH);
        System.out.println(GAME_HEIGHT);
        initClasses();

        gamePanel = new GamePanel(this);
        gameWindow = GameWindow.getInstance();
        gameWindow.setGamePanel(gamePanel);
        gamePanel.setFocusable(true);
        gamePanel.requestFocus(true);

        startGameLoop();

    }

    private void initClasses() {
        audioOptions = new AudioOptions(this);
        audioPlayer=new AudioPlayer();
        menu = new Menu(this);
        playing = new Playing(this);
        gameOptions=new GameOptions(this);
    }

    private void startGameLoop() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    public void update() {
        if(gameEnd!=true){
            switch (Gamestate.state) {
                case MENU:
                    menu.update();
                    break;
                case PLAYING:
                    playing.update();
                    break;
                case OPTIONS:
                    gameOptions.update();
                    break;
                case QUIT:
                default:
                    System.exit(0);
                    break;
    
            }
        }

        if(gameEnd){
            File videoFile = new File("res/LastGameScene.mp4");
            // String videoPath = "D:/Download _ D/GameWukong/javaProject/LastGame'sScene.mp4";
            String videoPath = videoFile.toURI().toString();
            gameWindow.switchToVideo(gameWindow.getJFrame(), videoPath);
            if(gameEndListener != null){
                gameEndListener.run();
            }
            System.out.println("HHHHHHHHHHHHHHHHHHHHHHHHHH");
            gameEnd = false;
        }
    }

    public void render(Graphics g) {
        switch (Gamestate.state) {
            case MENU:
                menu.draw(g);
                break;
            case PLAYING:
                playing.draw(g);
                break;
            case OPTIONS:
                gameOptions.draw(g);
                break;
            default:
                break;
        }
    }

    @Override
    public void run() {

        double timePerFrame = 1000000000.0 / FPS_SET;
        double timePerUpdate = 1000000000.0 / UPS_SET;

        long previousTime = System.nanoTime();

        int frames = 0;
        int updates = 0;
        long lastCheck = System.currentTimeMillis();

        double deltaU = 0;
        double deltaF = 0;

        while (true) {
            long currentTime = System.nanoTime();

            deltaU += (currentTime - previousTime) / timePerUpdate;
            deltaF += (currentTime - previousTime) / timePerFrame;
            previousTime = currentTime;

            if (deltaU >= 1) {
                update();
                updates++;
                deltaU--;
            }

            if (deltaF >= 1) {
                gamePanel.repaint();
                frames++;
                
                deltaF--;
            }

            if (System.currentTimeMillis() - lastCheck >= 1000) {
                lastCheck = System.currentTimeMillis();
                this.frames = frames;
                System.out.println("FPS: " + frames + " | UPS: " + updates);
                playing.restoreStaminaDefault(); //Hôì mana sau 1s
                frames = 0;
                updates = 0;

            }
        }

    }

    public void setGameEndListener(Runnable listener) {
        this.gameEndListener = listener;
    }    

    public void windowFocusLost() {
        if (Gamestate.state == Gamestate.PLAYING)
            playing.getPlayer().resetDirBooleans();
    }

    public Menu getMenu() {
        return menu;
    }

    public Playing getPlaying() {
        return playing;
    }
    public GameOptions getGameOptions(){
        return gameOptions;
    }
    public AudioOptions getAudioOptions(){
        return audioOptions;
    }

    public int getFPS() {
    	return frames;
    }
    
    public AudioPlayer getAudioPlayer() {
        return audioPlayer;
    }

    public boolean isGameEnd(){
        return gameEnd;
    }

    public void setGameEnd(boolean a){
        this.gameEnd = a;
    }
}
