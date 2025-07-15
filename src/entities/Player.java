package entities;

import audio.AudioPlayer;
import gameState.Gamestate;
import gameState.Playing;
import main.Game;
import utilz.LoadSave;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static utilz.Constants.ANI_SPEED;

import static utilz.Constants.Curve.STICK_HEIGHT;
import static utilz.Constants.Curve.STICK_WIDTH;
import static utilz.Constants.GRAVITY;
import static utilz.Constants.PlayerConstants.*;
import static utilz.HelpMethods.*;

public class Player extends Entity {
    
	private BufferedImage[][] animations;

    private boolean moving = false, attacking = false;
    private boolean right, left, jump;

    private int[][] lvlData;
    private float xDrawOffset = 38 * Game.SCALE;
    private float yDrawOffset =  15* Game.SCALE;

    protected int playerDamage;
    private double levelUpTimesIncrease = 1.1;
    
    public static int levelUpTime;

    // jumping / gravity

    private float jumpSpeed = -2.35f * Game.SCALE;
    private float fallSpeedAfterCollision = 0.5f * Game.SCALE;

    // Status BarUI
    private BufferedImage statusBarImg;

    private int statusBarWidth = (int) (192 * Game.SCALE);
    private int statusBarHeight = (int) (58 * Game.SCALE);
    private int statusBarX = (int) (10 * Game.SCALE);
    private int statusBarY = (int) (10 * Game.SCALE);

    // Health
    private int healthBarWith = (int) ((152) * Game.SCALE);
    private int healthBarHeigth = (int) (4 * Game.SCALE);
    private int healthBarXStart = (int) (34 * Game.SCALE);
    private int healthBarYStart = (int) (14 * Game.SCALE);

    // Stamina
    private int staminaBarWidth = (int) (103 * Game.SCALE);
    private int staminaBarHeight = (int) (3 * Game.SCALE);
    private int staminaBarXStart = (int) ((34 + 10) * Game.SCALE);
    private int staminaBarYStart = (int) ((14 + 25 - 5) * Game.SCALE);

    public static int expThatChange;

    private int healthWidth = healthBarWith;

    private int staminaWidth = staminaBarWidth;

    //Exp
    private int expBarWidth = (int)(32*10*Game.SCALE);
    private int expBarHeight = (int) (3*Game.SCALE);
    private int expBarYStart = (int) ((30) * Game.SCALE);
    private int expBarXStart = (int) (Game.GAME_WIDTH/2 - expBarWidth/2);
    private int expWidth = 0;
    // Khi levelUp = true thì hiện cửa sổ tăng sức mạnh bản thân
    private boolean levelUp;
    private boolean isShowLevelUp;

    private BufferedImage levelUpImg;
    private int lvlUpWidth=(int)(64*Game.SCALE);
    private int lvlUpHeight=(int)(64*Game.SCALE);




    // attackBox

    private int flipX = 0;
    private int flipW = 1;

    private boolean attackChecked;
    private Playing playing;

    private int tileY=0;

  //Chieu luot
    private boolean powerAttackActive;
    private int powerAttackTick;
    private int powerGrowSpeed=15;
    private int powerGrowTick;

    //Chieu ban
    private Stick stick;
    private boolean isThrow;
   private boolean sticking;
   private BufferedImage stickImg;
   private ArrayList<Stick> sticks=new ArrayList<>();
   private int stickDir =1;

    //Ulti
    private boolean ultiSkill = false;
    private int ultiAniSpeed = 10;

    //Run sound
    private boolean isStepSoundPlaying = false;

    //Chi so to len
    /*private int giantUp=0;
    private int firstWidth=width;
    private int firstHeight=height;*/

    public Player(float x, float y, int width, int height, Playing playing) {
        super(x, y, width, height);
        this.playing = playing;
        this.state = IDLE;
        this.maxHealth = 100;
        this.currentHealth = maxHealth;
        this.Shield = 0;
        this.maxShield = 8;
        this.maxStamina = 1000000000;
        this.currentStamina = maxStamina;
        this.currentExp = 0;
        this.maxExp = 100;
        this.walkSpeed = Game.SCALE * 1.0f;



        // this.playerDamage = 10;
        this.levelUp=false;
        loadAnimations();
        loadImgs();
        initHitbox(15, 27);
        initAttackBox();
        expThatChange = 0;
    }

    public void setSpawn(Point spawn) {
        this.x = spawn.x ;
        this.y = spawn.y;
        hitbox.x = x ;
        hitbox.y = y ;
    }

     void initAttackBox() {
        attackBox = new Rectangle2D.Float(hitbox.x + hitbox.width, y , (int) (30 * Game.SCALE), (int) (20 * Game.SCALE));
        resetAttackBox();

    }

    private void resetAttackBox() {
        if (flipW == 1)
            setAttackBoxOnRightSide();
        else
            setAttackBoxOnLeftSide();
    }

    private void setAttackBoxOnLeftSide() {
        attackBox.x = hitbox.x - hitbox.width - (int) (Game.SCALE * 10);
    }

    private void setAttackBoxOnRightSide() {
        attackBox.x = hitbox.x + hitbox.width - (int) (Game.SCALE * 5);
    }


    /*private void updateBiggerAttackBox(){
        if (right||(powerAttackActive&&flipW==1))
        attackBox = new Rectangle2D.Float(hitbox.x + hitbox.width, y , (int) ((30+getGiantUp()) * Game.SCALE), (int) (20 * Game.SCALE));

        else if (left||(powerAttackActive&&flipW==-1))
            attackBox = new Rectangle2D.Float(hitbox.x - hitbox.width, y , (int) ((30+getGiantUp()) * Game.SCALE), (int) (20 * Game.SCALE));
    }*/

  /*  private void updateBiggerHitBox(){
        hitbox = new Rectangle2D.Float(x, y, (int) (width * Game.SCALE), (int) (height * Game.SCALE));
    }*/

   /* private void updateBiggerPlayer(){
        this.width=firstWidth+getGiantUp();
        this.height=firstHeight+getGiantUp();

    }*/

    /*public void setGiantUp(int x){
        giantUp+=x;
    }

    public int getGiantUp(){
        return giantUp;
    }*/

    public void update() {
        updateHealthBar();
        updateStaminaBar();
        playerUpdateLevel(levelUp);

        //updateIsShowLvlUp(isShowLevelUp);
        //System.out.println(maxHealth + " " + maxStamina + " "+ playerDamage);

            updateDirStick();

        if (currentHealth <= 0) {

//            if (state != DEAD) {
//                state = DEAD;
//                aniTick = 0;
//                aniIndex = 0;
//                playing.setPlayerDying(true);
//                playing.getGame().getAudioPlayer().playEffect(AudioPlayer.DIE);
//            } else if (aniIndex == GetSpriteAmount(DEAD) - 1 && aniTick >= ANI_SPEED - 1) {
//                playing.setGameOver(true);
//                playing.getGame().getAudioPlayer().stopSong();
//                playing.getGame().getAudioPlayer().playEffect((AudioPlayer.GAMEOVER));
//            } else {
//                updateAnimationTick();
//
//                //fall if in air
//                if (inAir)
//                    if (CanMoveHere(hitbox.x, hitbox.y + airSpeed, hitbox.width, hitbox.height, lvlData)) {
//                        hitbox.y += airSpeed;
//                        airSpeed += GRAVITY;
//                    } else
//                        inAir = false;
//            }

 //           return;

        	if ( playing.CountRev() == 0) {

	            if (state != DEAD) {
	                state = DEAD;
	                aniTick = 0;
	                aniIndex = 0;
	                playing.setPlayerDying(true);
	                playing.getGame().getAudioPlayer().playEffect(AudioPlayer.DIE);
	            } else if (aniIndex == GetSpriteAmount(DEAD) - 1 && aniTick >= ANI_SPEED - 1) {
	                playing.setGameOver(true);
	                playing.getGame().getAudioPlayer().stopSong();
	                playing.getGame().getAudioPlayer().playEffect((AudioPlayer.GAMEOVER));
	            } else {
	                updateAnimationTick();

	                //fall if in air
	                if (inAir)
	                    if (CanMoveHere(hitbox.x, hitbox.y + airSpeed, hitbox.width, hitbox.height, lvlData)) {
	                        hitbox.y += airSpeed;
	                        airSpeed += GRAVITY;
	                    } else
	                        inAir = false;

	            }
        	}
        	else {
            		playing.setSpawn();
            		currentHealth = maxHealth;
            		playing.setCountRev(playing.CountRev() - 1);
            		
        	}

	            return;

        }

            updateExpBar();

         //updateBiggerPlayer();
         //updateBiggerHitBox();
        //updateBiggerAttackBox();
        updateAttackBox();
        updateStick(lvlData);
        updatePlayerShoot(this);

            if (state == HIT) {
                if (aniIndex <= GetSpriteAmount(state) - 3)
                    pushBack(pushBackDir, lvlData, 1.25f);
                updatePushBackDrawOffset();
            } else
                updatePos();

            if(moving) {
                tileY = (int) (hitbox.y / Game.TILES_SIZE);
                if(powerAttackActive){
                    powerAttackTick++;
                    if(powerAttackTick>=35){
                        powerAttackTick=0;
                        powerAttackActive=false;
                    }
                }
                checkTrapTouched();
                checkObjectTouched();



            }





        if (attacking||powerAttackActive||ultiSkill)
            checkAttack();

        updateAnimationTick();
        setAnimation();

    }
    
    private void checkTrapTouched() {
		playing.checkTrapTouched(this);

	}

	private void checkObjectTouched() {
    	playing.checkObjectTouched(hitbox);
    }




    public void playerUpdateLevel(boolean levelUp){
        if(levelUp){
            maxHealth *= levelUpTimesIncrease;
            maxStamina *= levelUpTimesIncrease;
            setPlayerDamage((int)(levelUpTimesIncrease*getPlayerDamage()));
            levelUpTime += 1;
        }
        resetBooleanLevelUp();

    }

    private void checkAttack() {
        if(!(isUltiSkill())){
            if (attackChecked || aniIndex != 1 )
                return;
            attackChecked = true;
    
            if(powerAttackActive)
                attackChecked=false;
    

    
            playing.checkEnemyHit(attackBox);
            playing.getGame().getAudioPlayer().playAttackSound();
            playing.checkObjectHit(attackBox);
        } else {
            if(aniIndex>=6 && !attackChecked){
                attackChecked = true;
                if(powerAttackActive)
                    attackChecked=false;
                playing.checkEnemyHit(attackBox);
                playing.getGame().getAudioPlayer().playAttackSound();
            }
        }

    }

    private void updateAttackBox() {
        if (right && left) {
            if (flipW == 1) {
                setAttackBoxOnRightSide();
            } else {
                setAttackBoxOnLeftSide();
            }

        } else if (right || (powerAttackActive && flipW == 1))
            setAttackBoxOnRightSide();
        else if (left || (powerAttackActive && flipW == -1))
            setAttackBoxOnLeftSide();

        attackBox.y = hitbox.y + (Game.SCALE * 10);

    }

    private void updateHealthBar() {
        if(currentHealth>=maxHealth){
            currentHealth = maxHealth;
        }
        healthWidth = (int) ((currentHealth / (float) maxHealth) * healthBarWith);
    }

    private void updateStaminaBar() {
        // hàm cập nhật stamina
        staminaWidth = (int) ((currentStamina / (float) maxStamina) * staminaBarWidth);

        powerGrowTick++;
        if(powerGrowTick>=powerGrowSpeed){
            powerGrowTick=0;
        }

       /* curveGrowTick++;
        if(curveGrowTick>=curveGrowSpeed){
            curveGrowTick=0;
        }*/

    }

    private void updateExpBar(){
        expWidth = (int) ((currentExp / (float) maxExp)*expBarWidth);
    }

    public void render(Graphics g, int xlvlOffset) {
    
    	g.drawImage(animations[state][aniIndex], (int) ((hitbox.x - xDrawOffset) - xlvlOffset + flipX),
                (int) (hitbox.y - yDrawOffset), (int) (width * flipW * 1.5), (int) (height * 1.5), null);

        drawUI(g);
        //  drawAttackHitbox(g, xlvlOffset);
        //  drawHitbox(g, xlvlOffset);
    	
    }
    

    public void drawLvlUp(Graphics g,int xlvlOffset){
        if(isShowLevelUp) {
            g.drawImage(levelUpImg, (int) (hitbox.x - 30)- xlvlOffset, (int) (hitbox.y - 60), lvlUpWidth, lvlUpHeight, null);
            resetIsShowLevelUp();
        }
    }





    private void drawUI(Graphics g) {
        g.drawImage(statusBarImg, statusBarX, statusBarY, statusBarWidth, statusBarHeight, null);
        g.setColor(Color.red);
        if(healthWidth>=healthBarWith)
            healthWidth = healthBarWith;
        g.fillRect(healthBarXStart + statusBarX, healthBarYStart + statusBarY, healthWidth, healthBarHeigth);
        g.setColor(Color.YELLOW);
        g.fillRect(staminaBarXStart + statusBarX, staminaBarYStart + statusBarY, staminaWidth, staminaBarHeight);
        g.setColor(Color.LIGHT_GRAY);
        g.fillRect(expBarXStart, expBarYStart, expBarWidth , expBarHeight);
        g.setColor(Color.GREEN);
        g.fillRect(expBarXStart, expBarYStart, expWidth, expBarHeight);

    }



    private void updatePos() {
        moving = false;

        if (jump)
            jump();
        if (!inAir) {
            if(!powerAttackActive) {
                if ((!left && !right) || (right && left)) {
                    stopStepSound();
                    return;
                }

            }
        }
        float xSpeed = 0;

	if(left&&!right) {
	
			xSpeed -= walkSpeed;
            
			 flipX = (int)(width*1.5);
			 flipW = -1;
	}
	
	 if(right&&!left) {
        
		 xSpeed += walkSpeed;
        
	     flipX = 0;
	     flipW = 1;
	 }

     if(powerAttackActive){
         if((!left&&!right)||(left&&right)){
             if(flipW==-1){
                 xSpeed=-walkSpeed;
             }
             else
                 xSpeed=walkSpeed;
         }
         xSpeed*=3;
     }
	 if(!inAir)
	     if(!IsEntityOnFloor(hitbox,lvlData))
	         inAir=true;

        if (inAir&&!powerAttackActive) {
            if (CanMoveHere(hitbox.x, hitbox.y + airSpeed, hitbox.width, hitbox.height, lvlData)) {
                hitbox.y += airSpeed;
                airSpeed += GRAVITY;
                updateXPos(xSpeed);
            } else {
                hitbox.y = GetEntityYPosUnderRoofOrAboveFloor(hitbox, airSpeed);
                if (airSpeed > 0)
                    resetInAir();
                else
                    airSpeed = fallSpeedAfterCollision;
                updateXPos(xSpeed);
            }
        } else {
                    
            updateXPos(xSpeed);
        }
        moving = true;
        if (!inAir && (xSpeed != 0)) {
            playStepSound(); // Gọi phương thức phát âm thanh lặp
        } else if (xSpeed == 0){
            stopStepSound(); // Nếu không di chuyển, dừng âm thanh
        }
        
        

    }

    private void playStepSound() {
        if (!isStepSoundPlaying) {
            playing.getGame().getAudioPlayer().playEffect(AudioPlayer.RUN); // Gọi phương thức loop
            isStepSoundPlaying = true; // Đặt cờ
        }
    }

    public void stopStepSound() {
        if (isStepSoundPlaying ) {
            playing.getGame().getAudioPlayer().stopEffect(AudioPlayer.RUN); // Dừng âm thanh
            isStepSoundPlaying = false; // Đặt lại cờ
        }
    }

    private void jump() {
        if (inAir)
            return;
        stopStepSound();
        playing.getGame().getAudioPlayer().playEffect(AudioPlayer.JUMP);
        inAir = true;
        airSpeed = jumpSpeed;
    }

    private void resetInAir() {
        inAir = false;
        airSpeed = 0;
    }

    private void updateXPos(float xSpeed) {
        if (CanMoveHere(hitbox.x + xSpeed, hitbox.y, hitbox.width, hitbox.height, lvlData)) {
            
            hitbox.x += xSpeed;
        } else {
            hitbox.x = GetEntityXPosNextToWall(hitbox, xSpeed);
            
            if(powerAttackActive){
                powerAttackActive=false;
                powerAttackTick=0;

            }
        }
    }

    public void changeHealth(int value) {

        currentHealth += value;
        if (value < 0) {
            playing.getGame().getAudioPlayer().playEffect(AudioPlayer.HIT);
        }
        if (value > 0) {
            playing.getGame().getAudioPlayer().playEffect(AudioPlayer.HEAL_MANA);
        }
        if (currentHealth <= 0) {
            currentHealth = 0;
            // gameOver();
        } else if (currentHealth >= maxHealth) {
            currentHealth = maxHealth;
        }
    }
    

	public void kill(int value) {
    	 currentHealth -= value;
         if (currentHealth <= 0) {
             currentHealth = 0;
             // gameOver();
         } else if (currentHealth >= maxHealth) {
             currentHealth = maxHealth;
         }
    }

    public void changeStamina(int value) {
        // thay đổi stamina
        currentStamina += value;
        if (currentStamina <= 0) {
            currentStamina = 0;
        } else if (currentStamina >= maxStamina) {
            currentStamina = maxStamina;
        }
    }

    public void changeExp(int value) {
        currentExp += value;
        if(currentExp >= maxExp){
            levelUp = true;
            isShowLevelUp=true;
            playing.getGame().getAudioPlayer().playEffect((AudioPlayer.LEVEL_UP));//Lvl up effect
            currentExp -= maxExp;
            maxExp = (int) (1.2*maxExp);
        }
    }

    public void updateGame() {
        updatePos();
        updateAnimationTick();
        setAnimation();

    }

    private void setAnimation() {
        int startAni = state;
        if (moving)
            state = RUNNING;

        else
            state = IDLE;
        if (inAir) {
            if (airSpeed < 0)
                state = JUMP;
            else
                state = FALLING;
        }

        if(powerAttackActive){
            state=ATTACK;
            aniIndex=1;
            aniTick=0;
            return;
        }



        if (attacking) {
            state = ATTACK;
            if (startAni != ATTACK) {
                aniIndex = 1;
                aniTick = 0;
                return;

            }
        }

        if (isThrow) {
            state = THROW;
            if (startAni != THROW) {
                aniIndex = 1;
                aniTick = 0;
                return;

            }
        }
        if (ultiSkill){
            state = ULTI;
            if(startAni != ULTI){
                aniIndex = 1;
                aniTick = 0;
                return;
            }
        }






        if (startAni != state)
            resetAniTick();

    }



    public void resetExp(){
        
    }

    private void resetAniTick() {
        aniIndex = 0;
        aniTick = 0;
    }

    private void updateAnimationTick() {
        aniTick++;
        if(isUltiSkill()==true){
            if (aniTick >= ultiAniSpeed) {
                aniTick = 0;
                aniIndex++;
                if (aniIndex >= GetSpriteAmount(state)) {
                    aniIndex = 0;
                    attacking = false;
                    isThrow=false;
                    sticking=false;
                    ultiSkill = false;
                    attackChecked = false;
                }
            }
        } else {
            if (aniTick >= ANI_SPEED) {
                aniTick = 0;
                aniIndex++;
                if (aniIndex >= GetSpriteAmount(state)) {
                    aniIndex = 0;
                    attacking = false;
                    isThrow=false;
                    sticking=false;
                    ultiSkill = false;
                    attackChecked = false;
                }
    
            }
        }
    }

    public boolean isRight() {
        return right;
    }

    public boolean isLeft() {
        return this.left;
    }

    public void setRight(boolean right) {
        this.right = right;
    }

    public void setLeft(boolean left) {
        this.left = left;
    }

    public void resetDirBooleans() {

        right = false;
        left = false;

    }

    public void setAttacking(boolean attacking) {
        if(attacking == true)
            this.ultiSkill = false;
        this.attacking = attacking;
    }

    private void loadAnimations() {
        BufferedImage img = LoadSave.GetSpriteAtlas(LoadSave.PLAYER_ATLAS);
        animations = new BufferedImage[9][8];
        for (int j = 0; j < animations.length; j++)
            for (int i = 0; i < animations[j].length; i++)
                animations[j][i] = img.getSubimage(i * 64, j * 40, 64, 40);

        statusBarImg = LoadSave.GetSpriteAtlas(LoadSave.STATUS_BAR);
        levelUpImg=LoadSave.GetSpriteAtlas(LoadSave.LEVEL_UP_IMG);

    }
    
    public void loadLvlData(int[][] lvlData) {
        this.lvlData = lvlData;
        if (!IsEntityOnFloor(hitbox, lvlData))
            inAir = true;
    }

    public void setJump(boolean jump) {
        this.jump = jump;
    }

    public void resetAll() {
        resetDirBooleans();
        inAir = false;
        attacking = false;
        ultiSkill = false;
        powerAttackActive=false;
        for(Stick st:sticks){
            if(st.isActive()){
                st.setActive(false);
            }
        }
        moving = false;
        state = IDLE;
        currentHealth = maxHealth;
        currentStamina = maxStamina;
        flipX = 0;
        flipW = 1;
        hitbox.x = x;
        hitbox.y = y;
        if (!IsEntityOnFloor(hitbox, lvlData))
            inAir = true;

        while(expThatChange > 0){
            if(expThatChange > currentExp){
                expThatChange-=currentExp;
                maxExp = (int) (maxExp/1.2);
                currentExp = maxExp;
            } else if (currentExp >= expThatChange) {
                currentExp -= expThatChange;    
                expThatChange -= expThatChange;
            }
        }

        while(levelUpTime > 0){
            maxHealth = (int) Math.ceil(maxHealth/levelUpTimesIncrease);
            maxStamina = (int) Math.ceil(maxStamina/levelUpTimesIncrease);
            playerDamage = (int) Math.ceil(playerDamage/levelUpTimesIncrease);
            levelUpTime -= 1;
        }

    }

    public int getMaxHealth() {
        return maxHealth;
    }   

    public void setCurrentStamina(int x){
        this.currentStamina = x;
    }

    public int getMaxStamina(){
        return maxStamina;
    }

    public int getCurrentStamina(){
        return currentStamina;
    }

    public void setLevelUp(boolean a){
        this.levelUp = a;
    }

    public boolean isLevelUp(){
        return levelUp;
    }

    public void setCurrentExp(int exp){
        this.currentExp = exp;
    }

    public int getCurrentExp(){
        return currentExp;
    }
    
    public Rectangle2D.Float getHitbox() {
        return hitbox;
    }

    public int getTileY(){
        return tileY;
    }

    public void powerAttack() {
        if(powerAttackActive)
            return;
        if(currentStamina>=60){
            powerAttackActive=true;
            changeStamina(-60);
        }
    }

	public void renderIDLE(Graphics g, int xLvlOffset) {
		g.drawImage(animations[IDLE][aniIndex], (int) ((hitbox.x - xDrawOffset) - xLvlOffset + flipX),
                (int) (hitbox.y - yDrawOffset), (int) (width * flipW * 1.5), (int) (height * 1.5), null);
        drawUI(g);
		
	}
	
    public void updateIDLE() {
   	 aniTick++;
        if (aniTick >= ANI_SPEED) {
            aniTick = 0;
            aniIndex++;
            if (aniIndex >= GetSpriteAmount(IDLE)) {
                aniIndex = 0;
                attacking = false;
                attackChecked = false;
            }

        }
   }

    ////////Ban STICK

    public void shootStick(){
        if(sticking)
            return;
        if(currentStamina>=GetStamina(THROW)) {
            this.setIsThrow(true);
            this.setPlayerDamage(GetPlayerDamage(THROW));
            sticking=true;
            playing.getGame().getAudioPlayer().playEffect(AudioPlayer.THROW);
            changeStamina(-GetStamina(THROW));
        }
    }
    private void loadImgs() {
        stickImg= LoadSave.GetSpriteAtlas(LoadSave.STICK_IMG);
    }

    public void drawSticks(Graphics g,int xLvlOffset){
        for(Stick st:sticks) {
            if (st.isActive())
                g.drawImage(stickImg, (int) (st.getHitbox().x - xLvlOffset), (int) (st.getHitbox().y), STICK_WIDTH, STICK_HEIGHT, null);
           // st.drawStickHitbox(g,xLvlOffset);
        }

    }



    public void updateStick(int[][] lvlData) {
        for(Stick st:sticks) {
            if (st.isActive()) {
                st.updatePos();

                if (playing.isStickHitEnemy(st.getHitbox())) {
                    playing.checkEnemyHit(st.getHitbox());
                    st.setActive(false);
                } else if (IsStickHittingLevel(st, lvlData))
                    st.setActive(false);
            }
        }
    }


    private void updatePlayerShoot(Player player){
        if(player.isSticking()) {
            reloadStick(player);
            player.resetSticking();
        }

    }


    public void reloadStick(Player player){
        sticks.add(new Stick((int) player.getHitbox().x,(int)player.getHitbox().y,stickDir));
    }

    private void updateDirStick(){
        if(this.left){
            stickDir =-1;
        }
        else if(this.right){
            stickDir=1;
        }
    }

    public void setIsThrow(boolean throwing) {
        if(attacking==true||isUltiSkill()) {
            isThrow = false;
        }
        else isThrow=throwing;
    }

    public void setPlayerDamage(int a){
        this.playerDamage = a;
    }

    public int getPlayerDamage(){
        return playerDamage;
    }
   private void resetIsShowLevelUp() {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.schedule(() -> {
            isShowLevelUp = false;
            //System.out.println("Level up Img has been reset!");
            scheduler.shutdown(); // Dừng Scheduler sau khi thực hiện
        }, 2, TimeUnit.SECONDS); // 2 giây
    }

    private void resetBooleanLevelUp(){
        this.levelUp=false;
    }

    public boolean getIsShowLvlUp(){
        return isShowLevelUp;
    }

    public boolean isSticking(){
        return sticking;
    }
    public void resetSticking(){
        this.sticking=false;
    }

    public void setUltiSkill(boolean ulti){
        if(ulti == true){
            attacking = false;
        } 
        this.ultiSkill = ulti;
    }

    public boolean isUltiSkill(){
        return ultiSkill;
    }
    public Stick getStick(){
        return stick;
    }


}
