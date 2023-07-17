import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class JumpQuest extends PApplet {

final static float SPEED = 5;
final static float TILE_SCALE = 50.0f/128;
final static float TILE_SIZE = 50;
final static float SPRITE_SIZE = 50;
final static float GRAVITY = 0.6f;
final static float JUMPSPEED = 14;

float TOP_MARGIN = height * 0.2f;
float BOT_MARGIN = height * 0.8f;
float RIGHT_MARGIN = width * 0.6f;
float LEFT_MARGIN = width * 0.4f;

final static int IDLE = 0;
final static int RIGHT_FACING = 1;
final static int LEFT_FACING = 2;
final static int IDLE_RIGHT = 3;
final static int IDLE_LEFT = 4;

final static int playerwidth = 88;
final static int playerheight = 66;

final static float WIDTH = SPRITE_SIZE *16;
final static float HEIGHT = SPRITE_SIZE *12;
final static float GROUND_LEVEL = HEIGHT - SPRITE_SIZE;


//declare global variables
//backgrounds
Background bg;
PImage img, bgImg;

//gfx
PImage blood, pistolGun, shotgunGun, assaultGun, hud;
boolean bloodShown;
int bloodOpacity;
boolean lowAmmoSwitch;
int lowAmmoOpacity;

//tiles
PImage crate, brick, platform, botScaff;
//arraylist of objects
ArrayList<Sprite> platforms, coins, enemies, bullets, exits, enemyBullets, scrolls, boundaryBlocks, traps;

//player variables
Player player;
float centreX, centreY;
float changeX, changeY;
boolean jumped, keyUp, keyDown, keyLeft, keyRight, keyEnter, keyJ;
int viewX, viewY;
int state = 0;
boolean keyReleased = false;
boolean pressed = false;

//menu 
MenuButton[] buttons;
Menu menu;
Credits credit;
int levelNum = 1;
PFont gameFont;
boolean loading = true;
int loadingbar;
int barWidth  = 10;
boolean mouseReleased;

//ingame
Exit car;
Dog dog;
Shop shop;
boolean shopToggle;
boolean isGameOver;
int numCoins;
int damageCounter;
boolean damaged;
int score;
int enemiesKilled;
boolean died, resetIndex;
int maxNum;
int timeExited;
boolean playerDraw, hasExited, allowKeys, shootCooldown;
int shootTimer;
int finalOpacity;


//initialize them in setup()
public void setup() {
  
  menu = new Menu();
  credit = new Credits();
  shop = new Shop();
  bg = new Background();

  imageMode(CENTER);
  brick = loadImage("tiles/brick.png");
  botScaff = loadImage("tiles/botScaff.png");
  platform = loadImage("tiles/platform.png");
  blood = loadImage("gfx/blood.png");
  pistolGun = loadImage("gfx/pistolGun.png");
  shotgunGun = loadImage("gfx/shotgunGun.png");
  assaultGun = loadImage("gfx/assaultRifle.png");
  player = new Player("spritesheets/SpriteSheet.png", 1);
  dog = new Dog("spritesheets/dog.png", 1);
  hud = loadImage("gfx/HUD.png");

  gameFont = createFont("5x5.ttf", 20);
  textFont(gameFont);

  TOP_MARGIN = height * 0.3f;
  BOT_MARGIN = height * 0.2f;
  RIGHT_MARGIN = width * 0.4f;
  LEFT_MARGIN = width * 0.2f;

  centreX = width/2;
  centreY = GROUND_LEVEL;
  //player.setBottom(GROUND_LEVEL);
  player.centreX = width/2;
  viewX = 0;
  viewY = 0;

  platforms = new ArrayList<Sprite>();
  coins = new ArrayList<Sprite>();
  enemies = new ArrayList<Sprite>();
  bullets = new ArrayList<Sprite>();
  exits = new ArrayList<Sprite>();
  enemyBullets = new ArrayList<Sprite>();
  scrolls =  new ArrayList<Sprite>();
  boundaryBlocks =  new ArrayList<Sprite>();
  traps = new ArrayList<Sprite>();

  menu.display();
  playerDraw = true;
  allowKeys = true;
  finalOpacity = 0;
  isGameOver = false;
  menu.controlsScreen = false;
  menu.levelScreen = false;
  menu.creditsScreen = false;
  mouseReleased = false;


  createPlatforms("maps/map1.csv");
}






//-------------------------- MAIN METHODS ------------------------\\
//modify and update them in draw()
public void draw() {

  if (loading) {
    loadingScreen();
  } else {
    if (!menu.display) {
      background(12, 40, 64);
      bg.drawParallax();
      scroll();
      checkMovements();
      bloodOpacity-= 10;
      displayAll();
      playDeath();
      if (player.index <= player.dieRight.length-1 && !died && !shopToggle) {
        player.updateAni();
      }
      if (levelNum == 4) {
        dogDisplay();
        checkEnding();
      }

      if (shopToggle);
      shop.update();

      if (!isGameOver && !shopToggle) {
        resolvePlatformCollisions(player, platforms);
        updateAll();
        checkAll();
      } else {
      }
    } else {
      menu.update();
      menu.display();
    }
  }
}

public void updateAll() {
  score =(round((isMax(viewX)/10)/10.0f)*10) + enemiesKilled*50;
  for (Sprite enemy : enemies) {
    enemy.update();
    ((Animated)enemy).updateAni();
    resolveXPlatformCollisionsEnemy((Enemy)enemy, boundaryBlocks);
  }

  for (Sprite coin : coins)
    ((Animated)coin).updateAni();

  for (Sprite bullet : bullets) {
    ((Animated)bullet).update();
  }

  for (Sprite bullet : enemyBullets) {
    ((Animated)bullet).update();
  }

  for (Sprite scroll : scrolls) {
    scroll.update();
  }
  car.update();
  car.updateAni();
}

public int isMax(int num) {
  if (num > maxNum) {
    maxNum = num;
  }
  return maxNum;
}
//----------------------------------------------------------\\











//------------------------ DISPLAY METHODS --------------------\\

public void displayAll() {
  for (Sprite platform : platforms) {
    platform.display();
  }

  for (Sprite coin : coins) {
    coin.display();
  }

  for (Sprite bullet : bullets) {
    bullet.display();
  }

  if (!enemyBullets.isEmpty())
    for (Sprite bullet : enemyBullets) {
      bullet.display();
    }
  for (Sprite scroll : scrolls) {
    scroll.display();
  }

  for (Sprite enemy : enemies)
    enemy.display();

  if (playerDraw)
    player.display();
  car.display();
  fill(255);
  textSize(40);
  displayHUD();
  player.showLives();
  if (shopToggle)
    shop.display();

  if (isGameOver) {
    gameOverDisplay();
  }
}

public void dogDisplay() {
  dog.update();
  dog.updateAni();
  dog.display();
}

public void gameOverDisplay() {
  fill(255);
  textSize(30);
  text("GAME OVER", viewX + width/2, viewY + height/2);
  if (player.lives <= 0)
    text("Press SPACE to restart", viewX + width/2, viewY + height/2 + 100);
  else
    text("You win!", viewX + width/2, viewY + height/2 + 100);
}

public void lowAmmoDisplay() {
  if (player.bulletsLeft <= 5) {
    textSize(50);
    fill(255, 255, 255, lowAmmoOpacity);
    text("LOW AMMO", width/2 + viewX, height/3 + viewY);
    if (lowAmmoOpacity < 0 || lowAmmoOpacity > 255)
      lowAmmoSwitch = !lowAmmoSwitch;

    if (lowAmmoSwitch)
      lowAmmoOpacity +=5;

    if (!lowAmmoSwitch)
      lowAmmoOpacity -=5;
  }
}


public void playDeath() {
  if (isGameOver) {
    changeX = 0;
    changeY = 0;
    if (!resetIndex) {
      player.index = 0;
      resetIndex = true;
    }
    if (player.index >= player.dieRight.length-1) {
      died = true;
      numCoins = 0;
    }
  }
}

public void displayHUD() {
  image(hud, viewX + width/2, viewY + hud.height/2, width, hud.height);
  text(player.bulletsLeft, viewX + width*20/21 + 10, viewY + 110);
  text("0-" + levelNum, viewX + width*6/10, viewY + 90);
  text(score, viewX + width*4/10 - 50, viewY + 50);
  fill(255, 215, 0);
  text("$" + numCoins, viewX + width * 30/32, viewY + 70);
  textSize(15);
  fill(255);
  if (player.gun == "pistol") {
    image(pistolGun, viewX + width * 17/21 + 10, viewY + 90);
    text("PISTOL", viewX + width * 17/21 + 10, viewY + 110);
  } else if (player.gun == "shotgun") {
    image(shotgunGun, viewX + width * 17/21 + 5, viewY + 90);
    text("SHOTGUN", viewX + width * 17/21 + 8, viewY + 110);
  } else if (player.gun == "assault") {
    image(assaultGun, viewX + width * 17/21 + 5, viewY + 90);
    text("ASSAULT", viewX + width * 17/21 + 8, viewY + 110);
  }
  lowAmmoDisplay();
}

//-----------------------------------------------------------------\\













//-------------- RESET METHODS -------------------\\
public void resetAll() {
  isGameOver = false;
  platforms.clear();
  enemies.clear();
  coins.clear();
  bullets.clear();
  enemyBullets.clear();
  boundaryBlocks.clear();
  traps.clear();
  exits.clear();
  scrolls.clear();
  finalOpacity = 0;
  player.lives = 18;
  enemiesKilled = 0;
  score = 0;
  maxNum = 0;
  playerDraw = true;
  hasExited = false;
  allowKeys = true;
  died = false;
}

public void resetKeys() {
  keyUp = false;
  keyDown = false;
  keyRight = false;
  keyLeft = false;
}

//-----------------------------------------------------\\













//--------------------CHECK METHODS--------------------------\\

public void checkAll() {
  collectCoins();
  checkGuns();
  checkEnemy();
  checkBullets();
  checkScrolls();
  if (!hasExited) {
    checkDeath();
  }
  checkNextLevel();
}

//checks whether the enemy has been hit, the player has been hit, or the bullet hits the terrain, and removes the bullets
public void checkBullets() {
  for (Sprite platform : platforms) {
    ArrayList<Sprite> bulletList = checkCollisionList(platform, bullets);
    for (Sprite bullet : bulletList)
      bullets.remove(bullet);
  }

  ArrayList<Sprite> bulletList = checkCollisionList(player, enemyBullets);
  for (Sprite b : bulletList) {
    enemyBullets.remove(b);
    player.lives--;
    damaged = true;
  }

  for (Sprite platform : platforms) {
    ArrayList<Sprite> enemyBulletList = checkCollisionList(platform, enemyBullets);
    for (Sprite bullet : enemyBulletList)
      enemyBullets.remove(bullet);
  }
}


//checks whether an enemy has been hit, or killed
public void checkEnemy() {
  ArrayList<Sprite> bulletsToRemove = new ArrayList<Sprite>();
  for (Sprite bullet : bullets) {
    ArrayList<Sprite> enemyList = checkCollisionList(bullet, enemies);
    for (Sprite enemy : enemyList) {
      ((Enemy)enemy).hitAmount++;
      if (((Enemy)enemy).hitAmount >= ((Enemy)enemy).hitCap) {
        enemies.remove(enemy);
      }

      bulletsToRemove.add(bullet);
      enemiesKilled++;
    }
  }
  bullets.removeAll(bulletsToRemove);
}

//checks whether the coins should be increased based on player colliding with them
public void collectCoins() {
  ArrayList<Sprite> coinList = checkCollisionList(player, coins);
  if (coinList.size() > 0) {
    for (Sprite coin : coinList) {
      numCoins++;
      coins.remove(coin);
    }
  }
}

//checks whether the scrolls should be displayed based on player colliding with them
public void checkScrolls() {
  ArrayList<Sprite> scrollList = checkCollisionList(player, scrolls);
  if (scrollList.size() > 0) {
    for (Sprite scroll : scrollList) {
      ((Scrolls)scroll).displayText();
    }
  }
}

//method increases the level number each time the player collides with the extract car
public void checkNextLevel() {
  if (isCollide(player, car)) {
    allowKeys = false;
    damaged = false;
    player.centreX = car.centreX;
    player.centreY = car.centreY;
    if (!hasExited) {
      timeExited = frameCount;
      hasExited = true;
    }
    hidePlayer();
    if (timeExited + 140 <= frameCount) {
      levelNum++;
      loadLevel();
    } else {
      car.centreX+= 3;
      player.centreX = car.centreX;
      player.centreY = car.centreY;
    }
  }
}


public void checkEnding() {
  if (levelNum == 4) {
    if (player.centreX >= dog.centreX) { 
      allowKeys = false;
      resetKeys();
    }
    if (isCollide(player, dog)) {    //if collides with dog, start credits
      hasExited = true;
      allowKeys = false;
      resetKeys();
      finalOpacity+= 1;
      fill(0, 0, 0, finalOpacity);
      rectMode(CENTER);
      rect(viewX + width/2, viewY + height/2, width, height);
      rectMode(CORNER);
      isGameOver = true;
    }
  }

  if (finalOpacity > 300) {    //once the transition has finished, reset and send to credits
    menu.buttons[3].setClicked(false);
    isGameOver = false;
    levelNum = 1;
    resetAll();
    player = new Player("spritesheets/SpriteSheet.png", 1);
    credit.creditY = height + 20;
    createPlatforms("maps/map_credits.csv");
    player.setBottom(GROUND_LEVEL);
    player.centreX = width/2;
    viewX = 0;
    viewY = 0;
    credit.creditY = height + 20;
    menu.display = true;
    mouseReleased = false;
    menu.creditsScreen = true;
    menu.levelScreen = false;
    menu.controlsScreen = false;
  }
}

//checks whether the player has died every frame, updates the lives, displays the hit animation, and keeps track of damage delay
public void checkDeath() {
  boolean collideEnemy = false;
  ArrayList<Sprite> enemyList = checkCollisionList(player, enemies);
  for (Sprite enemy : enemyList) {
    if (isCollide(player, enemy))
      collideEnemy = true;
  }


  boolean fallOffCliff = player.getBottom() > GROUND_LEVEL + TILE_SIZE;
  if (damaged) {
    if (!bloodShown) {
      bloodOpacity = 255;
      bloodShown = true;
    }
    shakeScreen();
    damageCounter++;
    if (damageCounter > 30) {
      damageCounter = 0;
      damaged = false;
      bloodShown = false;
    }
  }
  if (collideEnemy && !damaged && player.lives > 0) {
    player.lives--;
    damaged = true;
  }
  if (fallOffCliff)
    player.lives = 0;

  if (player.lives  <= 0) {
    allowKeys  = false;
    isGameOver = true;
  } else {
    //if not dead
  }
}

//updates the players chosen gun, and checks whether he can shoot each frame (shoot cooldown)
public void checkGuns() {
  if (shootCooldown)
    shootTimer++;
  if (shootTimer >=  30) {
    shootTimer = 0;
    shootCooldown = false;
  }
  if (!keyDown && !isGameOver && (mousePressed || keyJ)) {
    if ((player.gun == "pistol" && !shootCooldown && keyJ) || keyJ && menu.creditsScreen && !shootCooldown  && player.bulletsLeft > 0) {
      keyJ = false;
      player.shoot();
      shootCooldown = true;
      player.hasShot = true;
    }
    if (player.gun == "shotgun"  && !shootCooldown && player.bulletsLeft > 4) {
      player.shoot();
      shootCooldown = true;
      player.hasShot = true;
    }
    if (player.gun == "assault"  && frameCount % 10 == 0 && player.bulletsLeft > 0) {
      player.shoot();
      player.hasShot = true;
    }
  }
}

//-----------------------------------------------------------\\






//--------------------------------- GFX -------------------------------\\
//method used to hide player in cases such as death, transitions, or entity overlapping
public void hidePlayer() {
  playerDraw = false;
}

//creates the map based on the level number.
public void loadLevel() {
  resetAll();
  if (levelNum <= 4) {
    createPlatforms("maps/map" + levelNum + ".csv");
    player.setBottom(GROUND_LEVEL);
    player.centreX = width/2;
    viewX = 0;
    viewY = 0;
  } else {
    isGameOver = true;
  }
}

//displays a loading screen where the bar displays progress loaded
public void loadingScreen() {
  PImage loading = loadImage("backgrounds/loading.png");
  image(loading, width/2, height/2, width, height);
  fill(255);
  if (frameCount % 10 < random(6, 10))
    barWidth += random(30);
  rect(25, height*8/10-10, barWidth, 50);

  if (barWidth >= width-100)
    this.loading = false;
}

//moves the screen based on player position
public void scroll() {
  float rightBound = viewX + width - RIGHT_MARGIN;
  if (player.getRight() > rightBound)
    viewX += player.getRight() - rightBound;

  float leftBound = viewX + LEFT_MARGIN;
  if (player.getLeft() < leftBound && viewX > 0)
    viewX -= leftBound - player.getLeft();

  float topBound = viewY + TOP_MARGIN;
  if (player.getTop() < topBound)
    viewY -= topBound - player.getTop();

  float botBound = viewY + height - BOT_MARGIN;
  if (player.getBottom() > botBound && viewY + player.getBottom() < botBound)
    viewY += player.getBottom() - botBound;

  translate(-viewX, -viewY);
}

//testing purposes only
public void drawHitbox(Sprite s) {
  rect(s.getLeft(), s.getTop(), s.getRight() - s.getLeft(), s.getBottom() - s.getTop());
}

//moves the screen when the player is damaged, displays blood
public void shakeScreen() {
  if (damageCounter < 8) {
    if (damageCounter % 2 == 0 ) {
      viewX -=3;
      viewY -=3;
    } else {
      viewX +=3;
      viewY +=3;
    }
  }
  imageMode(CORNER);
  tint(255, 255, 255, bloodOpacity);
  image(blood, viewX, viewY + hud.height);
  noTint();
  imageMode(CENTER);
}
//----------------------------------------------------------\\






//--------------------- PLATFORM COLLISIONS ------------------\\

//checks intersection between two sprites
public boolean isCollide(Sprite s1, Sprite s2) {
  return s1.getLeft() < (s2.getRight()) &&
    (s1.getRight()) > s2.getLeft() &&
    s1.getTop() < (s2.getBottom()) &&
    (s1.getBottom()) > s2.getTop();
}

//returns the most recent collision within a list of sprites.
public ArrayList<Sprite> checkCollisionList(Sprite s, ArrayList<Sprite> list) {
  ArrayList<Sprite> collisionList = new ArrayList<Sprite>();
  for (Sprite sprite : list) {
    if (isCollide(s, sprite))
      collisionList.add(sprite);
  }
  return collisionList;
}


//returns whether the sprite is on a platform.
public boolean isOnPlatforms(Sprite s, ArrayList<Sprite> tiles) {
  s.centreY += 5;
  ArrayList<Sprite> collisionList = checkCollisionList(s, tiles);
  s.centreY -= 5;
  if (collisionList.size() > 0) {
    return true;
  } else
    return false;
}

//method checks y axis collisions and deals with gravity.
public void resolvePlatformCollisions(Sprite s, ArrayList<Sprite> tiles) {
  s.changeY += GRAVITY;
  s.centreY += s.changeY;
  ArrayList<Sprite> collisionList = checkCollisionList(s, tiles);
  if (collisionList.size() > 0) {
    Sprite collided = collisionList.get(0);

    if (collided.fn.equals("tiles/spikes.png") && s == player)
      player.lives = 0;
    if (s.changeY > 0) {
      s.setBottom(collided.getTop());
      jumped = false;
    } else if (s.changeY < 0) {
      s.setTop(collided.getBottom());
    }
    s.changeY = 0;
  }
  resolveXPlatformCollisions(s, tiles);
}

//method checks whether the player is hitting any tiles on the x axis, and fixes it. Also checks spike collision.
public void resolveXPlatformCollisions(Sprite s, ArrayList<Sprite> tiles) {
  s.centreX += s.changeX;
  ArrayList<Sprite> collisionList = checkCollisionList(s, tiles);
  if (collisionList.size() > 0) {
    Sprite collided = collisionList.get(0);
    if (collided.fn.equals("tiles/spikes.png") && s == player)
      player.lives = 0;
    if (s.changeX > 0) {
      s.setRight(collided.getLeft());
    } else if (s.changeX < 0) {
      s.setLeft(collided.getRight());
    }
    s.changeX = 0;
  }
}

//seperate method checks invisible "boundary blocks" which do not effect the player
public void resolveXPlatformCollisionsEnemy(Enemy s, ArrayList<Sprite> tiles) {
  s.centreX += s.changeX;
  ArrayList<Sprite> collisionList = checkCollisionList(s, tiles);
  if (collisionList.size() > 0) {
    Sprite collided = collisionList.get(0);
    if (s.changeX > 0) {
      s.setRight(collided.getLeft());
      if (s.direction != RIGHT_FACING) {
        changeX *= -1;
        s.direction = RIGHT_FACING;
      }
    } else if (s.changeX < 0) {
      s.setLeft(collided.getRight() + abs(s.changeX));
      if (s.direction != LEFT_FACING) {
        changeX *= -1;
        s.direction = LEFT_FACING;
      }
    }
  }
}

//-----------------------------------------------------------------------\\








//----------------- INPUTS ------------------\\
public void mouseMoved() {
  mouseReleased = false;
}

public void mouseReleased() {
  if (menu.display && !menu.creditsScreen && !menu.levelScreen)
    menu.buttons[1].setClicked(true);
  if (menu.display && !menu.creditsScreen && !menu.levelScreen)
    menu.buttons[3].setClicked(true);

  mouseReleased = true;
}

public void mouseClicked() {
  if (!keyDown && (!isGameOver && player.gun == "pistol" && !shootCooldown && !shopToggle) || keyJ && menu.creditsScreen) {
    player.shoot(); 
    shootCooldown = true; 
    player.hasShot = true;
  }
  if (shopToggle)
    for (int i = 0; i < shop.buttons.length; i++)
      shop.buttons[i].setClicked(true);
}

//run whenever a key is pressed
public void keyPressed() {
  if (allowKeys) {
    pressed = true; 

    if (keyCode == RIGHT || key == 'd')
      keyRight = true; 
    if (keyCode == LEFT || key == 'a')
      keyLeft = true; 
    if ((keyCode == UP || key == 'w' || keyCode == 32))
      keyUp = true; 

    if (keyCode == DOWN || key == 's') {
      keyDown = true; 
      keyRight = false; 
      keyLeft = false; 
      keyUp = false;
    }

    if (key == 'b')
      shopToggle = !shopToggle; 

    if (key == 'j' && !keyDown)
      keyJ = true;
  }

  if (keyCode == 32 && isGameOver || keyCode == ENTER && isGameOver)
    setup(); 

  if (keyCode == ENTER)
    keyEnter = true;

  if (key == ESC) {
    menu.display = true; 
    key = 0;
  }
}

//called when a key is released
public void keyReleased() {
  keyReleased = true; 
  if (keyCode == RIGHT || key == 'd')
    keyRight = false; 
  else if (keyCode == LEFT || key == 'a')
    keyLeft = false; 
  else if (keyCode == UP || key == 'w' || keyCode == 32)
    keyUp = false; 
  else if (keyCode == DOWN || key == 's')
    keyDown = false; 
  if (keyCode == ENTER)
    keyEnter = false; 
  if (key == 'j')
    keyJ = false; 
  if (key == ESC)
    key = ESC;
}

//every frame checks what key is pressed and moves player accordingly, prevents the keyboard input delay and deals with dual inputs
public void checkMovements() {
  if (!keyDown) {
    if (keyRight)
      player.changeX = SPEED; 
    if (keyLeft)
      player.changeX = -SPEED; 
    if (keyUp && !jumped && isOnPlatforms(player, platforms)) {
      player.changeY = -JUMPSPEED; 
      jumped = true;
    }
  }

  if ((!keyRight && !keyLeft) || (keyRight && keyLeft))
    player.changeX = 0;
}

//--------------------------------------------------\\









//method creates the world maps using a csv file. Takes in a table of values in rows and columns. For each row and column, places a tile/enemy/entity based on the index on that row/column. 
//the position of the tile placed is based on that row and column. This allows easy map editing and large map creations with multiple sprites and tile types.
public void createPlatforms(String filename) {
  String[] lines = loadStrings(filename); 
  for (int row = 0; row < lines.length; row++) {
    String[] values = split(lines[row], ","); 
    for (int col = 0; col < values.length; col++) {
      if (values[col].equals("1")) {

        //tile creation
        Sprite tile = new Sprite("tiles/brick.png", TILE_SCALE); 
        tile.centreX = TILE_SIZE/2 + col*TILE_SIZE; 
        tile.centreY = TILE_SIZE/2 + row*TILE_SIZE; 
        platforms.add(tile); 
        boundaryBlocks.add(tile); 
        credit.creditPlatforms.add(tile);
      } else if (values[col].equals("2")) {
        Sprite tile = new Sprite("tiles/botScaff.png", TILE_SCALE); 
        tile.centreX = TILE_SIZE/2 + col*TILE_SIZE; 
        tile.centreY = TILE_SIZE/2 + row*TILE_SIZE; 
        platforms.add(tile); 
        boundaryBlocks.add(tile); 
        credit.creditPlatforms.add(tile);
      } else if (values[col].equals("3")) {
        Sprite tile = new Sprite("tiles/platform.png", TILE_SCALE); 
        tile.centreX = TILE_SIZE/2 + col*TILE_SIZE; 
        tile.centreY = row*TILE_SIZE + TILE_SIZE/4; 
        platforms.add(tile); 
        boundaryBlocks.add(tile);
      } else if (values[col].equals("4")) {
        Coin coin = new Coin("gfx/coin.png", TILE_SCALE); 
        coin.centreX = TILE_SIZE/2 + col*TILE_SIZE; 
        coin.centreY = TILE_SIZE/2 + row*TILE_SIZE; 
        coins.add(coin); 




        //enemy creation
      } else if (values[col].equals("6")) {
        float bLeft = col* SPRITE_SIZE; 
        float bRight = bLeft + 6 * SPRITE_SIZE; 
        Enemy enemy = new Enemy("spritesheets/enemies.png", 1, bLeft, bRight, "normal"); 
        enemy.centreX = 80/2 + col*TILE_SIZE; 
        enemy.centreY = 90/2 + 10 + row*TILE_SIZE - TILE_SIZE; 
        enemies.add(enemy);
      } else if (values[col].equals("7")) {
        float bLeft = col* SPRITE_SIZE; 
        float bRight = bLeft + 6 * SPRITE_SIZE; 
        Enemy enemy = new Enemy("spritesheets/melee.png", 1, bLeft, bRight, "melee"); 
        enemy.centreX = 80/2 + col*TILE_SIZE; 
        enemy.centreY = 90/2 + 10 + row*TILE_SIZE - TILE_SIZE; 
        enemies.add(enemy);
      } else if (values[col].equals("8")) {
        float bLeft = col* SPRITE_SIZE; 
        float bRight = bLeft + 6 * SPRITE_SIZE; 
        Enemy enemy = new Enemy("spritesheets/heavy.png", 1.5f, bLeft, bRight, "heavy"); 
        enemy.centreX = 80/2 + col*TILE_SIZE; 
        enemy.centreY = 90/2 + 10 + row*TILE_SIZE - TILE_SIZE; 
        enemies.add(enemy); 



        //tile creation
      } else if (values[col].equals("10")) {
        Sprite tile = new Sprite("tiles/bin.png", TILE_SCALE); 
        tile.centreX = TILE_SIZE/2 + col*TILE_SIZE; 
        tile.centreY = TILE_SIZE/2 + row*TILE_SIZE; 
        platforms.add(tile); 
        boundaryBlocks.add(tile);
      } else if (values[col].equals("11")) {
        Sprite tile = new Sprite("tiles/platform.png", TILE_SCALE); 
        tile.centreX = TILE_SIZE/2 + col*TILE_SIZE; 
        tile.centreY = row*TILE_SIZE + TILE_SIZE - TILE_SIZE/4; 
        platforms.add(tile); 
        boundaryBlocks.add(tile);
      } else if (values[col].equals("12")) {
        Sprite tile = new Sprite("tiles/topScaff.png", TILE_SCALE); 
        tile.centreX = TILE_SIZE/2 + col*TILE_SIZE; 
        tile.centreY = TILE_SIZE/2 + row*TILE_SIZE; 
        platforms.add(tile); 
        boundaryBlocks.add(tile);
      } else if (values[col].equals("13")) {
        Sprite tile = new Sprite("tiles/greybrick.png", TILE_SCALE); 
        tile.centreX = TILE_SIZE/2 + col*TILE_SIZE; 
        tile.centreY = TILE_SIZE/2 + row*TILE_SIZE; 
        platforms.add(tile); 
        boundaryBlocks.add(tile);
      } else if (values[col].equals("14")) {
        Sprite tile = new Sprite("tiles/greenbrick.png", TILE_SCALE); 
        tile.centreX = TILE_SIZE/2 + col*TILE_SIZE; 
        tile.centreY = TILE_SIZE/2 + row*TILE_SIZE; 
        platforms.add(tile); 
        boundaryBlocks.add(tile);
      } else if (values[col].equals("15")) {
        Sprite tile = new Sprite("tiles/botWall.png", TILE_SCALE); 
        tile.centreX = TILE_SIZE/2 + col*TILE_SIZE; 
        tile.centreY = TILE_SIZE/2 + row*TILE_SIZE; 
        platforms.add(tile); 
        boundaryBlocks.add(tile);
      } else if (values[col].equals("16")) {
        Sprite tile = new Sprite("tiles/midWall.png", TILE_SCALE); 
        tile.centreX = TILE_SIZE/2 + col*TILE_SIZE; 
        tile.centreY = TILE_SIZE/2 + row*TILE_SIZE; 
        platforms.add(tile); 
        boundaryBlocks.add(tile);
      } else if (values[col].equals("17")) {
        Sprite tile = new Sprite("tiles/topWall.png", TILE_SCALE); 
        tile.centreX = TILE_SIZE/2 + col*TILE_SIZE; 
        tile.centreY = TILE_SIZE/2 + row*TILE_SIZE; 
        platforms.add(tile); 
        boundaryBlocks.add(tile);
      } else if (values[col].equals("18")) {
        Sprite tile = new Sprite("tiles/spikes.png", TILE_SCALE); 
        tile.centreX = TILE_SIZE/2 + col*TILE_SIZE; 
        tile.centreY = row*TILE_SIZE + TILE_SIZE - TILE_SIZE/4; 
        platforms.add(tile); 
        boundaryBlocks.add(tile);
        traps.add(tile);
      }
      //text box creation
      else if (values[col].equals("100")) {
        float bTop = row*SPRITE_SIZE; 
        float bBot = bTop + 28; 
        Scrolls scroll = new Scrolls("gfx/scroll.png", TILE_SCALE, bTop, bBot, "John Wick, *EXCOMMUNICADO*"); 
        scroll.centreX = SPRITE_SIZE/2 + col*TILE_SIZE; 
        scroll.centreY = SPRITE_SIZE/2 - 20 + row*TILE_SIZE; 
        scrolls.add(scroll);
      } else if (values[col].equals("101")) {
        float bTop = row*SPRITE_SIZE; 
        float bBot = bTop + 28; 
        Scrolls scroll = new Scrolls("gfx/scroll.png", TILE_SCALE, bTop, bBot, "There must be another way up"); 
        scroll.centreX = SPRITE_SIZE/2 + col*TILE_SIZE; 
        scroll.centreY = SPRITE_SIZE/2 - 20 + row*TILE_SIZE; 
        scrolls.add(scroll);
      } else if (values[col].equals("102")) {
        float bTop = row*SPRITE_SIZE; 
        float bBot = bTop + 28; 
        Scrolls scroll = new Scrolls("gfx/scroll.png", TILE_SCALE, bTop, bBot, "You'll need more guns... Lots of guns. Use 'B'"); 
        scroll.centreX = SPRITE_SIZE/2 + col*TILE_SIZE; 
        scroll.centreY = SPRITE_SIZE/2 - 20 + row*TILE_SIZE; 
        scrolls.add(scroll);
      } else if (values[col].equals("103")) {
        float bTop = row*SPRITE_SIZE; 
        float bBot = bTop + 28; 
        Scrolls scroll = new Scrolls("gfx/scroll.png", TILE_SCALE, bTop, bBot, "No wife, no dog, no home. You have nothing John."); 
        scroll.centreX = SPRITE_SIZE/2 + col*TILE_SIZE; 
        scroll.centreY = SPRITE_SIZE/2 - 20 + row*TILE_SIZE; 
        scrolls.add(scroll);
      } else if (values[col].equals("104")) {
        float bTop = row*SPRITE_SIZE; 
        float bBot = bTop + 28; 
        Scrolls scroll = new Scrolls("gfx/scroll.png", TILE_SCALE, bTop, bBot, "Vengeance is all you have left."); 
        scroll.centreX = SPRITE_SIZE/2 + col*TILE_SIZE; 
        scroll.centreY = SPRITE_SIZE/2 - 20 + row*TILE_SIZE; 
        scrolls.add(scroll);
      } else if (values[col].equals("110")) {
        float bTop = row*SPRITE_SIZE; 
        float bBot = bTop + 28; 
        Scrolls scroll = new Scrolls("gfx/scroll.png", TILE_SCALE, bTop, bBot, "Nothing is here, go back"); 
        scroll.centreX = SPRITE_SIZE/2 + col*TILE_SIZE; 
        scroll.centreY = SPRITE_SIZE/2 - 20 + row*TILE_SIZE; 
        scrolls.add(scroll);
      } else if (values[col].equals("555")) {
        Sprite tile = new Sprite("tiles/invisible.png", TILE_SCALE); 
        tile.centreX = TILE_SIZE/2 + col*TILE_SIZE; 
        tile.centreY = TILE_SIZE/2 + row*TILE_SIZE; 
        boundaryBlocks.add(tile); 


        //exit creation
      } else if (values[col].equals("300")) {
        car = new Exit("tiles/car.png", TILE_SCALE); 
        car.centreX = TILE_SIZE/2 + col*TILE_SIZE; 
        car.centreY = TILE_SIZE/2 + row*TILE_SIZE;
      } else if (values[col].equals("301")) {
        dog = new Dog("spritesheets/dog.png", TILE_SCALE); 
        dog.centreX = TILE_SIZE/2 + col*TILE_SIZE; 
        dog.centreY = TILE_SIZE/2 + row*TILE_SIZE;
      }
    }
  }
}
class Animated extends Sprite {
  PImage[] currentImages;
  PImage[] standIdle;
  PImage[] standLeft;
  PImage[] standRight;
  PImage[] moveLeft;
  PImage[] moveRight;
  PImage[] shootRight;
  PImage[] shootLeft;
  PImage[] dieLeft;
  PImage[] dieRight;
  PImage[] crouchLeft;
  PImage[] crouchRight;

  int direction, lastDirection;
  int index;
  int frame;
  final int aniSpeed = 7;

  public static final int IDLE = 0;
  public static final int RUN = 1;
  public static final int ATTACK = 2;
  public static final int SHOOTUP = 3;
  public static final int CROUCH = 4;
  public static final int DIE = 5;

  public Animated(String filename, float scale) {
    super(filename, scale);
    direction = IDLE;
    index = 0;
    frame = 0;
  }

  public void updateAni() {
    frame++;
    if (frame % aniSpeed == 0) {
      selectDirection();
      selectCurrentImages();
      advanceToNextImage();
    }
  }

  public void selectDirection() {
    if (changeX > 0) {
      direction = RIGHT_FACING;
      lastDirection = RIGHT_FACING;
    } else if (changeX < 0) {
      direction = LEFT_FACING;
      lastDirection = LEFT_FACING;
    } else 
    direction = IDLE;
  }

  public void selectCurrentImages() {
    if (direction == RIGHT_FACING) {
      currentImages = moveRight;
    } else if (direction == LEFT_FACING) {
      currentImages = moveLeft;
    } else
      currentImages = standIdle;
  }

  public void advanceToNextImage() {
    index++;
    if (index >= currentImages.length) {
      index = 0;
    }
    if (keyDown && (currentImages == crouchLeft || currentImages == crouchRight) && index == 0) {
    } else
      img = currentImages[index];
  }

  public int getAniFrame() {
    int aniFrame = (frameCount / aniSpeed) % getSpriteAmount(state);
    return aniFrame;
  }

  public int getSpriteAmount(int playerAction) {
    switch (playerAction) {
    case 0:    //idle
    case 1:    //atttack
      return 5;
    case 2:    //run    //jump
    case 4: 
      return 3;  //falling

    case 3:
      return 4;

    case 5:    //die
      return 6;


    default:
      return 1;
    }
  }


  public void drawSprites(PImage[][] sheet, float x, float y, float w, float h, int state, int aniFrame) {
    if (direction == LEFT_FACING) {    //if the character swaps directions
      pushMatrix();
      translate(x, height - y - h);    //move him by one character
      scale(-1, 1);    //inverse the sprite
      image(sheet[state][aniFrame], 0, 0);    //redraw
      popMatrix();
    } else if (direction == RIGHT_FACING) {
      //img = sheet[state][aniFrame];
      image(sheet[state][aniFrame], x, height - y - h);
    }
  }
}
public class Background {
  PImage bg1, bg2, bg3;
  PImage firstbg1, firstbg2, firstbg3;
  PImage secondbg1, secondbg2, secondbg3;
  public Background() {
    firstbg1 = loadImage("backgrounds/bg3.png");
    firstbg2 = loadImage("backgrounds/bg2.png");
    firstbg3 = loadImage("backgrounds/bg1.png");
    secondbg1 = loadImage("backgrounds/secondBG3.png");
    secondbg2 = loadImage("backgrounds/secondBG2.png");
    secondbg3 = loadImage("backgrounds/secondBG1.png");
    bg1 = firstbg1;
    bg2 = firstbg2;
    bg3 = firstbg3;
  }


  public void drawParallax() {
    if (levelNum % 2 ==  0) {
      background(8, 0, 0);
      bg1 = secondbg1;
      bg2 = secondbg2;
      bg3 = secondbg3;
    } else {
      background(12, 40, 64);
      bg1 = firstbg1;
      bg2 = firstbg2;
      bg3 = firstbg3;
    }
    //if the first backgrounds x is greater than the width of the screen, create a copy at that x + width;
    int bg1x = -(int)(screenPos((int)0, 0, 3) % bg1.width);
    int bg1y = -(int)(screenYPos(0, 1));
    copy(bg1, (int)(bg1x), bg1y, bg1.width, bg1.height, 0, 0, (int)(bg1.width), bg1.height);
    int bg1x2 = (int)(bg1.width) - bg1x;
    if (bg1x2 < width) {
      copy(bg1, 0, bg1y, bg1.width, bg1.height, (int)(bg1x2), 0, (int)(bg1.width), bg1.height);
    }


    int bg2x = -(int)(screenPos((int)0, 0, 2) % bg2.width);
    int bg2y = -(int)(screenYPos(0, 1)) ;
    copy(bg2, (int)(bg2x), bg2y, bg2.width, bg2.height, 0, 0, (int)(bg2.width), bg2.height);
    int bg2x2 = (int)(bg1.width) - bg2x;
    if (bg2x2 < width) {
      copy(bg2, 0, bg2y, bg2.width, bg2.height, (int)(bg2x2), 0, (int)(bg2.width), bg2.height);
    }


    int bg3x = -(int)(screenPos((int)0, 0, 1) % bg3.width);
    int bg3y = -(int)(screenYPos(0, 1));
    copy(bg3, (int)(bg3x), bg3y, bg3.width, bg3.height, 0, 0, (int)(bg3.width), bg3.height);
    int bg3x2 = (int)(bg3.width) - bg3x;
    if (bg3x2 < width) {
      copy(bg3, 0, bg3y, bg3.width, bg3.height, (int)(bg3x2), 0, (int)(bg3.width), bg3.height);
    }
  }

  public float screenPos(int x, int y, int z) {
    return (x - viewX)/z;
  }
  public float screenYPos(int y, int  z) {
    return  (y - viewY)/z;
  }

  public void imageScale(PImage toScale, float scale) {
    toScale.resize(PApplet.parseInt(toScale.width*scale), PApplet.parseInt(toScale.height*scale));
  }

  public void imageDouble(PImage toScale) {
    imageScale(toScale, 2.0f);
  }
}
class Bullet extends Animated {
  float size;
  float x;
  float y;
  int dir;
  float vel;
  float yvel;
  boolean hasCollided;


  public Bullet(String filename, float scale, int direction, float speed, float ySpeed, float size, Sprite entity) {
    super(filename, scale);
    this.size = size;
    standIdle = new PImage[14];
    PImage bulletImg = loadImage("gfx/bullet.png");
    hasCollided =  false;
    centreY = entity.centreY  - entity.h/3.2f;

    this.vel = speed;
    this.yvel = ySpeed;
    super.w = 5;
    super.h = 5;

    standIdle[0] = bulletImg;
    currentImages  = standIdle;

    if (direction  == RIGHT_FACING) {
      centreX = entity.getRight();
      dir = 1;
    }
    if (direction == LEFT_FACING) {
      centreX = entity.getLeft();
      dir = -1;
    }
  }

  public void update() {
    move();
  }

  public void move() {
    centreX += vel*dir;
    centreY += yvel;
  }

  public void setCollided(boolean b) {
  }
}
public class Coin extends Animated {
  public Coin(String filename, float scale) {
    super(filename, scale);
    standIdle = new PImage[14];
    PImage coinsImg = loadImage("spritesheets/coins.png");
    super.w = 25;
    super.h = 25;
    for (int i = 0; i < standIdle.length; i++) {
      standIdle[i] = coinsImg.get(i*171, 0, 171, 171);
      currentImages = standIdle;
    }
  }
}
class Controls {
  PImage text, back, title, titleLight, trademark, buildings, bg, arrow;
  MenuButton exit;
  Controls() {
    back = loadImage("menu/controlsScreen.png");
    buildings = loadImage("backgrounds/buildings.png");
    bg = loadImage("backgrounds/BG.png");
    arrow = loadImage("gfx/arrow.png");
    arrow = mirrorX(arrow);
    exit = new MenuButton(50, 50, arrow.width, arrow.height, arrow);
  }

  public PImage mirrorX(PImage src) {
    PImage dst = createImage(src.width, src.height, ARGB);
    src.loadPixels();
    dst.loadPixels();
    for (int y = 0; y < src.height; y++ ) {
      for (int x = 0; x < src.width; x++ ) {
        dst.pixels[y*src.width+x] = src.pixels[y*src.width+src.width-x-1];
      }
    }
    dst.updatePixels();
    return(dst);
  } 

  public void imageScale(PImage toScale, float scale) {
    toScale.resize(PApplet.parseInt(toScale.width*scale), PApplet.parseInt(toScale.height*scale));
  }

  public void update() {
    display();

    if (key == ESC || exit.getIsReleased()) {
      menu.setSelect(3);
      menu.controlsScreen = false;
      mouseReleased =  false;

      key = 0;
    }
  }

  public void display() {
    background(0, 0, 60);
    image(bg, width/2, height/2, bg.width, bg.height);
    image(buildings, width/2, height/2 + height/5, width, buildings.height);
    image(back, width/2, height/2, back.width, back.height);
    exit.draws();
  }
}
class Credits {
  Enemy enemy1, enemy2;
  MenuButton exit;
  PImage text, back, title, titleLight, trademark, buildings, bg, arrow, creditsBox;
  ArrayList<Sprite> creditPlatforms;
  ArrayList<Sprite> creditEnemies;
  boolean enemiesSet = false;
  float creditY = height + 20;

  public Credits() {
    buildings = loadImage("backgrounds/buildings.png");
    creditsBox = loadImage("menu/creditsBox.png");
    bg = loadImage("backgrounds/BG.png");
    arrow = loadImage("gfx/arrow.png");
    arrow = mirrorX(arrow);
    exit = new MenuButton(65, 65, arrow.width, arrow.height, arrow);
    creditPlatforms = new ArrayList<Sprite>();
    creditEnemies = new ArrayList<Sprite>();
    enemy1 = new Enemy("spritesheets/melee.png", 1, 0, width, "melee");
    enemy2 = new Enemy("spritesheets/melee.png", 1, 0, width, "melee");
  }

  public PImage mirrorX(PImage src) {
    PImage dst = createImage(src.width, src.height, ARGB);
    src.loadPixels();
    dst.loadPixels();
    for (int y = 0; y < src.height; y++ ) {
      for (int x = 0; x < src.width; x++ ) {
        dst.pixels[y*src.width+x] = src.pixels[y*src.width+src.width-x-1];
      }
    }
    dst.updatePixels();
    return(dst);
  } 

  public void imageScale(PImage toScale, float scale) {
    toScale.resize(PApplet.parseInt(toScale.width*scale), PApplet.parseInt(toScale.height*scale));
  }

  public void update() {
    display();

    if (key == ESC || exit.getIsReleased()) {
      menu.setSelect(4);
      menu.creditsScreen = false;
      mouseReleased =  false;
      key = 0;
    }

    if (player.centreX + player.changeX < 0 || player.centreX  + player.changeX> width) {
      player.changeX = 0;
    }
    this.checkEnemies();

    if (!enemiesSet) {
      setEnemy1();
      setEnemy2();
      enemiesSet = true;
    }
  }

  public void setEnemy1() {
    enemy1 = new Enemy("spritesheets/melee.png", 1, 0, width, "melee");
    enemy1.changeX = 3;
    enemy1.centreY = 505;
    enemy1.centreX = 0 - 30;
    creditEnemies.add(enemy1);
  }

  public void setEnemy2() {
    enemy2 = new Enemy("spritesheets/melee.png", 1, 0, width, "melee");
    enemy2.changeX = -3;
    enemy2.centreY = 505;
    enemy2.centreX = width + 30;
    creditEnemies.add(enemy2);
  }

  public void drawCredits() {
    fill(0, 0, 0, 50);
    rect(0, 0, width, height);
    String[] lines = loadStrings("menu/credits.txt");
    fill(255);
    textSize(20);
    creditY -= 0.5f;
    for (int i = 0; i < lines.length; i++) {

      text(lines[i], width/2, creditY + 30 * i);
    }
    fill(0, 0, 60);
    rectMode(CENTER);
    rect(width/2, 0, 480, 70);
    rectMode(CORNER);
    image(creditsBox, width/2, creditsBox.height/2 + 20);
  }


  public void display() {

    player.bulletsLeft = 50;
    background(0, 0, 60);
    image(bg, width/2, height/2, bg.width, bg.height);
    image(buildings, width/2, height/2 + height/5, width, buildings.height);

    player.updateAni();
    resolvePlatformCollisions(player, platforms);
    checkMovements();
    player.display();
    checkGuns();

    for (Sprite bullet : bullets) {
      bullet.display();
      bullet.update();
    }

    for (Sprite platform : creditPlatforms) {
      platform.display();
    }

    if (!creditEnemies.isEmpty())
      for (Sprite enemy : creditEnemies) {
        enemy.update();
        ((Enemy)enemy).updateAni();
        enemy.display();
        if ((enemy.centreX > width + 50 || enemy.centreX < 0 - 50) && creditEnemies.get(0) != enemy)
          //creditEnemies.remove(enemy);
          println();
      }
    if (random(1000) < 5) {
      setEnemy1();
    }
    if (random(1000) < 5) {
      setEnemy2();
    }
    drawCredits();
    exit.draws();
  }

  public void checkEnemies() {
    ArrayList<Sprite> bulletsToRemove = new ArrayList<Sprite>();
    for (Sprite bullet : bullets) {
      ArrayList<Sprite> enemyList = checkCollisionList(bullet, creditEnemies);
      for (Sprite enemy : enemyList) {
        creditEnemies.remove(enemy);
        bulletsToRemove.add(bullet);
      }
    }
    bullets.removeAll(bulletsToRemove);

    for (int i = 0; i < creditEnemies.size(); i++)
      if (creditEnemies.get(i).centreX < 0 - 30 || creditEnemies.get(i).centreX > width + 30)
        creditEnemies.remove(creditEnemies.get(i));
  }
}
public class Dog extends Animated {
  public Dog(String filename, float scale) {
    super(filename, scale);
    standIdle = new PImage[8];
    PImage dogImg = loadImage("spritesheets/dog.png");
    super.w = 57;
    super.h = 45;
    for (int i = 0; i < standIdle.length; i++) {
      standIdle[i] = mirrorX(dogImg.get(i*57, 0, 57, 45));
      currentImages = standIdle;
    }
  }
}
public class Enemy extends Animated {
  float boundaryLeft, boundaryRight;
  float attackDistance;
  float viewLength;
  String type;
  int hitAmount;
  int hitCap;
  boolean hasShot, allowShot;
  int shootTimer;

  public Enemy(String filename, float scale, float bLeft, float bRight, String type) {
    super(filename, scale);
    moveLeft = new PImage[4];
    moveRight = new PImage[4];
    standIdle = new PImage[6];
    standRight = new PImage[6];
    standLeft = new PImage[6];
    dieLeft = new PImage[3];
    dieRight = new PImage[3];

    attackDistance = TILE_SIZE;
    hitAmount = 0;
    super.w = 80;
    super.h = 90;
    this.type = type;
    changeX = 2;

    PImage enemiesImg = loadImage("spritesheets/enemies.png");
    PImage meleeImg = loadImage("spritesheets/melee.png");
    PImage heavyImg = loadImage("spritesheets/heavy.png");

    if (this.type == "normal") {
      viewLength = 10;
      hitCap = 1;
      for (int i = 0; i < standIdle.length; i++) {
        standIdle[i] = enemiesImg.get(i*155 + 50, 115 +20, 80, 90);
      }
      for (int i = 0; i < moveRight.length; i++) {
        moveRight[i] = enemiesImg.get(i*155 + 50, +25, 80, 90);
        moveLeft[i] = mirrorX(moveRight[i]);
      }
      for (int i = 0; i < dieRight.length; i++) {
        dieRight[i] = enemiesImg.get(i*155 + 50, +25, 90, 90);
        dieLeft[i] = mirrorX(dieRight[i]);
      }
    } else if (this.type == "melee") {
      changeX = 3;
      viewLength = 8;
      hitCap = 1;
      for (int i = 0; i < standIdle.length; i++) {
        standIdle[i] = meleeImg.get(i*155 + 50, 0, 80, 118);
      }
      for (int i = 0; i < moveRight.length; i++) {
        moveRight[i] = meleeImg.get(i*155 + 50, 0, 80, 118);
        moveLeft[i] = mirrorX(moveRight[i]);
      }
      for (int i = 0; i < dieRight.length; i++) {
        dieRight[i] = enemiesImg.get(i*155 + 50, +25, 90, 90);
        dieLeft[i] = mirrorX(dieRight[i]);
      }
    } else if (this.type == "heavy") {
      changeX = 1;
      viewLength = 8;
      hitCap = 3;
      for (int i = 0; i < standIdle.length; i++) {
        standRight[i] = heavyImg.get(i*155 + 50, 116, 80, 118);
        standLeft[i] = mirrorX(standRight[i]);
        standIdle[i] = heavyImg.get(i*155 + 50, 116, 80, 118);
      }
      for (int i = 0; i < moveRight.length; i++) {
        moveRight[i] = heavyImg.get(i*155 + 50, 0, 80, 118);
        moveLeft[i] = mirrorX(moveRight[i]);
      }
    }

    currentImages = moveRight;
    direction = RIGHT_FACING;
    boundaryLeft = bLeft;
    boundaryRight = bRight;
  }



  public void update() {
    super.update();
    if (type.equals("normal")) {
      if (getLeft() <= boundaryLeft && !canSeePlayer(player)) {
        setLeft(boundaryLeft);
        changeX *= -1;
        direction = RIGHT_FACING;
      } else if (getRight() >= boundaryRight && !canSeePlayer(player)) {
        setRight(boundaryRight);
        changeX *= -1;
        direction = LEFT_FACING;
      }

      if (canSeePlayer(player) && isPlayerCloseForAttack(player)) {
        turnTowardsPlayer(player);
        if (frameCount % 15 == 0) {
          if (player.centreX < centreX && direction == LEFT_FACING || (player.centreX > centreX && direction == RIGHT_FACING)) {
            Bullet bullet = new Bullet("gfx/bullet.png", 1.0f, (int)direction, 7.0f, 0, 1.0f, this);
            enemyBullets.add(bullet);
          }
        }
      }
    }


    if (type.equals("heavy")) {
      if (getLeft() <= boundaryLeft && !canSeePlayer(player)) {
        setLeft(boundaryLeft);
        changeX = 1;
        direction = RIGHT_FACING;
      } else if (getRight() >= boundaryRight && !canSeePlayer(player)) {
        setRight(boundaryRight);
        changeX = -1;
        direction = LEFT_FACING;
      }
      if (canSeePlayer(player) && isPlayerCloseForAttack(player)) {
        turnTowardsPlayer(player);
        if (frameCount % 100 == 0) {
          if (player.centreX < centreX && direction == LEFT_FACING || (player.centreX > centreX && direction == RIGHT_FACING)) {
            for (int i = 0; i < 7; i++) {
              Bullet bullet = new Bullet("gfx/bullet.png", 1.0f, (int)direction, 7.0f, 1*random(-2, 2), 1.0f, this);
              enemyBullets.add(bullet);
            }
            hasShot = true;
            if (!allowShot) {
              shootTimer = frameCount;
              allowShot = true;

              if (direction == RIGHT_FACING) {
                changeX = 0;
                standIdle = standRight;
              }
              if (direction == LEFT_FACING) {
                changeX = 0;
                standIdle = standLeft;
              }
            }
          }
        } 
        if (hasShot && shootTimer + 50 <= frameCount) {
          if (direction == LEFT_FACING)
            changeX = -1;
          if (direction == RIGHT_FACING)
            changeX = 1;
          allowShot = false;
          hasShot = false;
          shootTimer = 0;
        }
      }
    }



    if (type.equals("melee")) {
      if (canSeePlayer(player) && isPlayerCloseForAttack(player)) {
        if (!menu.creditsScreen) {
          turnTowardsPlayer(player);
          if (direction == LEFT_FACING)
            changeX = -3;
          if (direction == RIGHT_FACING)
            changeX = 3;
        } else if (!canSeePlayer(player)) {
          changeX = 0.001f;
        }
      }
    }
  }



  protected void turnTowardsPlayer(Player player) {
    if (centreX < player.getRight() && centreX > player.getLeft()) {
      changeX = 0.0001f;
    } else if (player.getRight() > centreX && direction != RIGHT_FACING) {
      changeX *= -1;

      direction = RIGHT_FACING;
    } else if (player.getLeft() < centreX && direction != LEFT_FACING) {
      changeX *= -1;
      direction = LEFT_FACING;
    }
  }

  protected boolean canSeePlayer(Player player) {
    int playerTileY = (int) (player.centreY / TILE_SIZE);
    int tileY = (int) (centreY / TILE_SIZE);
    if (playerTileY == tileY) {
      if (isPlayerInRange(player)) {
        return true;
      }
    }
    return false;
  }

  protected boolean isPlayerInRange(Player player) {
    int absValue = (int) Math.abs(player.centreX - centreX);
    return absValue <= attackDistance * viewLength;
  }

  protected boolean isPlayerCloseForAttack(Player player) {
    int absValue = (int) Math.abs(player.centreX - centreX);
    return (absValue <= attackDistance * viewLength);
  }
}
public class Exit extends Animated {
  public Exit(String filename, float scale) {
    super(filename, scale);
    standIdle = new PImage[1];
    PImage img = loadImage(filename);
    standIdle[0]  = img;
    currentImages = standIdle;
  }
}
class LevelSelect {
  MenuButton buttons[] = new MenuButton[4];
  PImage l1, l2, l3, exitButton, bg;

  public LevelSelect() {
    l1 = loadImage("menu/l1.png");
    l2 = loadImage("menu/l2.png");
    l3 = loadImage("menu/l3.png");
    exitButton = loadImage("menu/exitButton.png");
    bg = loadImage("backgrounds/levelSelect.png");

    buttons[0] = new MenuButton(width*2/7, height/2, l1.width, l1.height, l1);
    buttons[1] = new MenuButton(width/2, height/2, l2.width, l2.height, l2);
    buttons[2] = new MenuButton(width*5/7, height/2, l3.width, l3.height, l3);
    buttons[3] = new MenuButton(width/2, height*3/4, exitButton.width, exitButton.height, exitButton);
  }

  public void display() {
    for (int i = 0; i < 4; i++) {
      menu.buttons[i].setClicked(false);
    }

    image(bg, width/2, height/2);
    for (int i = 0; i < buttons.length; i++) {
      buttons[i].draws();
    }
  }

  public void update() {
    display();
    
    if (buttons[0].getIsReleased()) {    //play
      levelNum = 1;
      loadLevel();

      menu.display = false;
      menu.levelScreen = false;
      buttons[0].setPressed(false);
      mouseReleased =  false;
    }

    if (buttons[1].getIsReleased()) {    //level select
      levelNum = 2;
      loadLevel();

      menu.display = false;
      menu.levelScreen = false;
      buttons[1].setPressed(false);
      mouseReleased =  false;
    }

    if (buttons[2].getIsReleased()) {    //controls
      levelNum = 3;
      loadLevel();
      mouseReleased =  false;

      menu.display = false;
      menu.levelScreen = false;
      buttons[2].setPressed(false);
      mouseReleased =  false;
    }

    if (key == ESC || this.buttons[3].getIsReleased()) {    //credits
      menu.setSelect(2);
      menu.levelScreen = false;
      menu.display = true;
      buttons[3].setPressed(false);
      mouseReleased = false;
      key = 0;
    }
  }
}
class Menu {
  Controls control;
  LevelSelect levelSelect;
  Credits credit;
  MenuButton buttons[] = new MenuButton[4];

  PImage play, level, credits, avatar, select, jump, quest, bg, buildings, enter, back, car, car2, controls;

  int pos = 1;
  int bgScale;
  int bgX  = 0, bgX2 = width;
  int carX, carX2;

  float rand1, rand2;
  float r1, g1, b1;
  float r2, g2, b2;
  float selectY;

  boolean display = true;
  boolean select1, select2, select3, select4;
  boolean controlsScreen = false;
  boolean levelScreen = false;
  boolean creditsScreen = false;

  Menu() {
    control = new Controls();
    levelSelect = new LevelSelect();
    credit = new Credits();
    play = loadImage("gfx/PLAY.png");
    level = loadImage("gfx/LEVEL.png");
    credits = loadImage("gfx/CREDITS.png");
    controls = loadImage("gfx/CONTROLS.png");
    avatar = loadImage("menu/avatar.png");
    select = loadImage("gfx/select.png");
    jump = loadImage("menu/jumpquest.png");
    bg = loadImage("backgrounds/BG.png");
    buildings = loadImage("backgrounds/buildings.png");
    enter  = loadImage("gfx/enter.png");
    back = loadImage("gfx/back.png");
    car = loadImage("tiles/car.png");
    selectY = height *2/5;
    car2 = mirrorX(car);
    imageScale(play, 0.3f);
    imageScale(level, 0.3f);
    imageScale(controls, 0.3f);
    imageScale(credits, 0.3f);
    imageScale(select, 0.8f);

    r1 = random(255);
    g1 = random(255);
    b1 = random(255);

    r2 = random(255);
    g2 = random(255);
    b2 = random(255);
    
    buttons[0] = new MenuButton(width/2, height * 2/5, play.width, play.height, play);
    buttons[1] = new MenuButton(width/2, height * 2.5f/5, level.width, level.height, level);
    buttons[2] = new MenuButton(width/2, height * 3/5, controls.width, controls.height, controls);
    buttons[3] = new MenuButton(width/2, height * 3.5f/5, credits.width, credits.height, credits);
  }

  public void imageScale(PImage toScale, float scale) {
    toScale.resize(PApplet.parseInt(toScale.width*scale), PApplet.parseInt(toScale.height*scale));
  }

  public void display() {
    if (levelScreen) {
      levelSelect.update();
    } else if (controlsScreen) {
      control.update();
    } else if (creditsScreen) {
      credit.update();
    } else {
      background(0, 0, 60);
      wallpaper();

      for (int i = 0; i < buttons.length; i++) {
        if (i+1 == pos && frameCount % 20 < 8) {
        } else
          buttons[i].draws();
      }

      image(avatar, width * 4/5, height - avatar.height/2, avatar.width, avatar.height);
      image(select, width * 2/7, selectY, select.width, select.height);
      image(jump, width/2, height/ 6, jump.width*2/3, jump.height*2/3);

      if ((frameCount % 40) < 20)
        image(enter, width/2, height * 9/10, enter.width*2, enter.height*2);    //flashes "press enter"
    }
  }


  public void wallpaper() {
    image(bg, width/2, height/2, bg.width, bg.height);
    image(buildings, bgX, height/2 + height/5, width, buildings.height);
    image(buildings, bgX2, height/2 + height/5, width, buildings.height);
    noStroke();
    fill(0, 0, 60);
    rect(0, height*15/16, width, height/18);
    stroke(1);
    fill(25);
    rect(0, height*8/9, width, height/18);
    bgX--;
    bgX2--;

    if (bgX+width/2 <= 0)
      bgX = width + width/2;
    if (bgX2+width/2<= 0)
      bgX2 = width+width/2;

    drawCars();
  }


//draws random cars at random intervals with random colours in the menu screen
  public void drawCars() {
    carX+= 5 + rand1;
    carX2-= 7 + rand2;

    if (random(1000) < 10 && carX > width) {    
      carX = 0-car.width;
      rand1 = random(10);

      r1 = random(255);
      g1 = random(255);
      b1 = random(255);
    }
    tint(r1, g1, b1);
    image(car, carX, height*8/9, car.width/3, car.height/3);

    if (random(1000) < 10 && carX2 < 0) {
      carX2 = width+car.width;
      rand2 = random(12);

      r2 = random(255);
      g2 = random(255);
      b2 = random(255);
    }
    tint(r2, g2, b2);
    image(car2, carX2, height*9/10, car.width/3, car.height/3);

    tint(255);
  }

//mirrors the sprite image
  public PImage mirrorX(PImage src) {
    PImage dst = createImage(src.width, src.height, ARGB);
    src.loadPixels();
    dst.loadPixels();
    for (int y = 0; y < src.height; y++ ) {
      for (int x = 0; x < src.width; x++ ) {
        dst.pixels[y*src.width+x] = src.pixels[y*src.width+src.width-x-1];
      }
    }
    dst.updatePixels();
    return(dst);
  }

  public void setSelect(int position) {
    selectY = height* (2 + ((position -1) * 0.5f))/5;    //equation for getting the selection position.
  }

  public void update() {


    select1 = false;
    select2 = false;
    select3 = false;
    if (pressed) {
      pressed = false;
      
      if (keyUp)    //sets the select icon to the buttons heights
        selectY -= height* .5f/5;
      if (selectY < height * 2/5)
        selectY = height * 3.5f/5;
      if (keyDown)
        selectY += height*.5f/5;
      if (selectY > height * 3.5f/5)
        selectY = height * 2/5;
    }

    if (selectY == height*2/5)     //sets the selection number to the corresponding position (eg. if hovering over play, position = 1, if level select, pos =2)
      pos = 1;
    if (selectY == height*2.5f/5)
      pos = 2;
    if (selectY == height*3/5)
      pos = 3;
    if (selectY == height*3.5f/5)
      pos = 4;

    if (keyEnter) {    //if enter is clicked, set the icon position to that button
      if (pos == 1)
        select1 = true;
      if (pos == 2)
        select2 = true;
      if (pos == 3)
        select3 = true;
      if (pos == 4)
        select4 = true;
    }
    if (keyCode == ESC)
      key = 0;

    if (buttons[0].getIsReleased() || select1 ) {    //if play is pressed, reset all, and create a new player, load game
      resetAll();
      levelNum = 1;
      player = new Player("spritesheets/SpriteSheet.png", 1);
      numCoins = 0;
      player.bulletsLeft = 14;
      loadLevel();
      display = false;
      mouseReleased =  false;
      buttons[0].setPressed(false);
    }

    if ((buttons[1].getIsReleased() || select2 ) && !levelScreen && !creditsScreen) {    //if level select is pressed, reset all, display levels.
      buttons[1].mouseIsPressed = false;
      levelScreen = true;
      levelSelect.display();
      player.bulletsLeft = 14;

      buttons[1].getIsPressed();
      mouseReleased =  false;
      println("0");
    }

    if ((buttons[2].getIsReleased() || select3) && !controlsScreen) {    //if controls is pressed, set the screen to controls.
      controlsScreen = true;
      control.display();
      player.bulletsLeft = 14;

      mouseReleased =  false;
      buttons[2].setPressed(false);
    }

    if ((buttons[3].getIsReleased() || select4) && !creditsScreen) {    //if credits is pressed, create a "minigame" with a new character and randomly spawning enemies.
      buttons[3].setClicked(false);
      resetAll();
      player = new Player("spritesheets/SpriteSheet.png", 1);
      credit.creditY = height + 20;
      createPlatforms("maps/map_credits.csv");
      player.setBottom(GROUND_LEVEL);
      player.centreX = width/2;
      viewX = 0;
      viewY = 0;
      select4 = false;
      creditsScreen = true;
      credit.display();
      mouseReleased =  false;
    }
  }
}
class MenuButton {
  //_________________________________________________ variables

  float posX, posY;
  float x, y;
  int wScale, hScale;
  String text;
  boolean mouseInside;
  boolean mouseClicked, mouseIsPressed;
  PImage buttonImg, buttonPressedImg;
  int colour;
  boolean mouseHasReleased;


  //_________________________________________________ constructor

  MenuButton (float x, float y, float w, float h, String t) {
    wScale = round(w);
    hScale = round(h);
    posX = x;
    posY = y;
    text = t;

    buttonImg = loadImage("menu/Button.png");
    buttonImg.resize(wScale, hScale);
    buttonPressedImg = loadImage("menu/ButtonPressed.png");
    buttonPressedImg.resize(wScale, hScale);
  }

  MenuButton (float x, float y, float w, float h, PImage img) {
    posX = x;
    posY = y;
    this.x = x;
    this.y = y;

    wScale = round(w);
    hScale = round(h);
    text = " ";
    buttonImg = img;
    buttonImg.resize(wScale, hScale);
    buttonPressedImg = img;
    buttonPressedImg.resize(wScale, hScale);
    colour = color(255, 255, 255);
  }

  //_________________________________________________ functionality

  public void draws() {
    mouseHasReleased = mouseReleased;
    //colour = color(255, 255, 255);
    if (mouseX >= x - wScale/2 && mouseX <= x - wScale/2 + wScale && mouseY >= y - hScale/2 && mouseY <= y + hScale/2) 
      mouseInside = true;
    else
      mouseInside = false;

    if (mouseInside) {
      noStroke();
      fill(colour, 100);
      image(buttonPressedImg, posX, posY);
      rectMode(CENTER);
      rect(posX, posY, wScale, hScale);
      rectMode(CORNER);
      fill(255);
      textSize(50);
      textAlign(CENTER, CENTER);
      text(text, posX + wScale/2, posY + hScale/2);
    } else {
      image(buttonImg, posX, posY);
      fill(0);
      textSize(50);
      textAlign(CENTER, CENTER);
      text(text, posX + wScale/2, posY + hScale/2);
    }
  }


  //_________________________________________________ get functions
  public void setHighlight(int colour) {
    this.colour = colour;
  }


  public boolean getIsPressed() {
    if (mouseInside && mousePressed && mouseReleased) {
      this.mouseIsPressed = true;
      return mouseIsPressed;
    } else {
      this.mouseIsPressed = false;
      return mouseIsPressed;
    }
  }

  public boolean getIsReleased() {
    if (mouseInside && mouseReleased) {
      return true;
    } else {
      return false;
    }
  }

  public void setPressed(boolean b) {
    this.mouseIsPressed = b;
  }

  public void setClicked(boolean b) {
    if (mouseInside)
      this.mouseClicked = b;
  }

  public boolean getIsClicked() {
    if (mouseInside && mouseClicked)
      return true;

    return false;
  }
}
public class Player extends Animated {
  final int rows = 11;
  final int cols = 7;

  int bulletsLeft;
  int  lives;
  String gun;
  float bulletVel;
  boolean hasShot;
  boolean onPlatform, inPlace;

  public Player(String filename, float scale) {
    super(filename, scale);
    PImage playerSheet = loadImage("spritesheets/SpriteSheet.png");
    moveLeft = new PImage[5];
    moveRight = new PImage[5];
    standIdle = new PImage[5];
    standRight = new PImage[5];
    standLeft = new PImage[5];
    dieRight = new PImage[7];
    dieLeft = new PImage[7];
    crouchRight = new PImage[2];
    crouchLeft = new PImage[2];
    shootRight = new PImage[3];
    shootLeft = new PImage[3];

    lives = 66;
    bulletsLeft = 20;
    direction = RIGHT_FACING;
    onPlatform =  false;
    inPlace = true;
    gun = "pistol";
    bulletVel = 7;
    super.w = 70;
    super.h = 90;

  //loading each type of animation
    for (int i = 0; i < standIdle.length; i++) {
      standRight[i] = playerSheet.get((int)(i*155 + 50), (int)(25), (int)75, (int)88);
      standLeft[i] = mirrorX(standRight[i]);
    }

    for (int i = 0; i < moveRight.length; i++) {
      moveRight[i] = playerSheet.get((int)(i*155 + 50 ), (int)(115+25), (int)75, (int)88);
      moveLeft[i] = mirrorX(moveRight[i]);
    }

    for (int i = 0; i < dieRight.length; i++) {
      dieRight[i] = playerSheet.get((int)(i*155 + 50), (int)(5*115+25), (int)105, (int)88);
      dieLeft[i] = mirrorX(dieRight[i]);
    }

    for (int i = 1; i < crouchRight.length; i++) {
      crouchRight[i] = playerSheet.get((int)(i * 155 + 50), (int)(4*115 + 30), 56, 68);
      crouchLeft[i] = mirrorX(crouchRight[i]);
    }

    for (int i = 0; i < shootRight.length; i++) {
      shootRight[i] = playerSheet.get((int)(i * 155 + 50), (int)(2*115 + 25), 88, 88);
      shootLeft[i] = mirrorX(shootRight[i]);
    }
  }

//overrides the regular sprite class
  @Override
    public void updateAni() {
    onPlatform = isOnPlatforms(this, platforms);
    inPlace = changeX == 0 && changeY == 0;
    super.updateAni();
  }

  @Override
    public void selectDirection() {
    if (keyRight) {
      direction = RIGHT_FACING;
    } else if (keyLeft) {
      direction = LEFT_FACING;
    }
  }

  @Override
    public void  selectCurrentImages() {
    //each animation has different hitbox sizes, and animations.
    if (direction == RIGHT_FACING) {
      if (lives <= 0) { 
        super.w = 101;
        super.h = 61;
        currentImages = dieRight;
      } else if (hasShot && bulletsLeft > 0) {
        super.w = 71;
        super.h = 88;
        currentImages = shootRight;
        if (player.index >= shootRight.length-1)
          hasShot = false;
      } else if (keyDown) {
        super.w = 56;
        super.h = 60;
        currentImages =  crouchRight;
      } else if (inPlace) {
        super.w = 71;
        super.h = 88;
        currentImages = standRight;
      } else {
        super.w = 71;
        super.h = 86;
        currentImages = moveRight;
      }
    } else if (direction == LEFT_FACING) {
      if (lives <= 0) { 
        super.w = 101;
        super.h = 61;
        currentImages = dieLeft;
      } else if (hasShot && bulletsLeft > 0) {
        super.w = 71;
        super.h = 88;
        currentImages = shootLeft;
        if (player.index >= shootRight.length-1)
          hasShot = false;
      } else if (keyDown) {
        super.w = 56;
        super.h = 60;
        currentImages =  crouchLeft;
      } else if (inPlace) {
        super.w = 71;
        super.h = 88;
        currentImages = standLeft;
      } else {
        super.w = 71;
        super.h = 86;
        currentImages = moveLeft;
      }
    }
  }

  //determines the gun being used, and player shoots accordingly, uses ammoLeft
  public void  shoot() {
    if (gun == "shotgun" && bulletsLeft >= 4) {
      bulletsLeft -= 4;
      for (int i = 0; i < 7; i++) {
        bulletVel = 6.0f;
        Bullet bullet = new Bullet("gfx/bullet.png", 1.0f, (int)direction, bulletVel, 1*random(-2, 2), 1.0f, this);
        bullets.add(bullet);
      }
    } else if (gun == "pistol") {
      if (bulletsLeft > 0) {
        bulletVel = 7.0f;
        Bullet bullet = new Bullet("gfx/bullet.png", 1.0f, (int)direction, bulletVel, 0, 1.0f, this);
        bullets.add(bullet);
        bulletsLeft--;
      }
    } else if (gun == "assault") {
      if (bulletsLeft > 0) {
        bulletVel = 9.0f;
        Bullet bullet = new Bullet("gfx/bullet.png", 1.0f, (int)direction, bulletVel, 0, 1.0f, this);
        bullets.add(bullet);
        bulletsLeft--;
      }
    }
  }

//lives of the player displayed as bars
  public void showLives() {
    fill(0, 232, 216);
    for (int i = 0; i < lives; i++) {
      noStroke();
      rect(viewX + 118 + (i * 9.37f), viewY + 78, 7, 23);
    }
  }

  public void setState() {

    if (inPlace) {
      state = IDLE;
    } else if (mousePressed) {
      state = ATTACK;
    } else {
      state = RUN;
    }
  }
}
public class Scrolls extends Animated {
  float boundaryTop, boundaryBot;
  String text;
  public Scrolls(String filename, float scale, float bTop, float bBot, String text) {
    super(filename, scale);
    boundaryTop = bTop;
    boundaryBot = bBot;
    standIdle = new PImage[1];
    changeY = 0.2f;
    this.text = text;
    PImage scrollImg = loadImage("gfx/scroll.png");
    super.w = 25;
    super.h = 25;
    for (int i = 0; i < standIdle.length; i++) {
      standIdle[i] = scrollImg;
      currentImages = standIdle;
    }
  }

  public void update() {
    super.update();
    if (getBottom() > boundaryBot) {
      setBottom(boundaryBot);
      changeY *= -1;
    }

    if (getTop() < boundaryTop) {
      setTop(boundaryTop);
      changeY *= -1;
    }
  }

  //if the player is standing on a scroll, display the corresponding text
  public void  displayText() {
    textSize(25);
    float strWidth = textWidth(text);
    float strAscent = textAscent();
    float strDescent = textDescent();
    float strHeight = strAscent + strDescent;
    float tW = strWidth + 20;
    fill(0, 0, 0, 150);
    rect(centreX - tW/2, player.getTop() - 50 - strHeight/4, tW, strHeight);
    fill(255);

    text(text, centreX, player.getTop() - 50);
  }
}
class Shop {
  MenuButton buttons[] = new MenuButton[5];
  MenuButton mb;
  PImage pistol, shotgun, assault, health, ammo;
  PImage shop;
  int pistolX, pistolY, pistolCost;
  int shotgunX, shotgunY, shotgunCost;
  int assaultX, assaultY, assaultCost;
  int healthX, healthY, healthCost;
  int ammoX, ammoY, ammoCost;

  public Shop() {
    //variables for the shop items
    pistol = loadImage("gfx/PISTOL.png");
    shotgun = loadImage("gfx/SHOTGUN.png");
    assault = loadImage("gfx/ASSAULT.png");
    health = loadImage("gfx/HEALTH.png");
    ammo = loadImage("gfx/AMMO.png");
    pistolX =  + width*2/7;
    pistolY = + height * 2/5;
    pistolCost = 0;

    shotgunX =  + width/2;
    shotgunY =  + height * 2/5;
    shotgunCost = 10;

    assaultX =  + width*5/7;
    assaultY =  + height * 2/5;
    assaultCost = 20;

    healthX =  + width*9/23;
    healthY = (int)( + height * 3.5f/5);
    healthCost = 5;

    ammoX =  + width*14/23;
    ammoY =  (int)( + height * 3.5f/5);
    ammoCost = 1;

    buttons[0] = new MenuButton(pistolX, pistolY, pistol.width, pistol.height, pistol);
    buttons[1] = new MenuButton(shotgunX, shotgunY, shotgun.width, shotgun.height, shotgun);
    buttons[2] = new MenuButton(assaultX, assaultY, assault.width, assault.height, assault);
    buttons[3] = new MenuButton(healthX, healthY, health.width, health.height, health);
    buttons[4] = new MenuButton(ammoX, ammoY, ammo.width, ammo.height, ammo);
  }

  public void display() {    //displays each shop item, and the corresponding price
    for (int i = 0; i < buttons.length; i++) {
      buttons[i].draws();
    }
    textMode(CORNER);
    textSize(25);
    fill(255);
    text("$" + pistolCost, viewX +pistolX, viewY +pistolY);
    text("$" + shotgunCost, viewX +shotgunX, viewY +shotgunY);
    text("$" + assaultCost, viewX+ assaultX, viewY +assaultY);
    text("$" + healthCost, viewX +healthX, viewY +healthY + 5);
    text("$" + ammoCost, viewX +ammoX, viewY + ammoY + 5);
    //image(shop, viewX + width/2, viewY + height/2);
  }

  public void update() {
    //updates each button on the shop
    buttons[0].posX = viewX + width*2/7;
    buttons[0].posY = viewY + height * 2/5;

    buttons[1].posX = viewX + width/2;
    buttons[1].posY = viewY + height * 2/5;

    buttons[2].posX =  viewX + width*5/7;
    buttons[2].posY = viewY + height * 2/5;

    buttons[3].posX = viewX + width*9/23;
    buttons[3].posY = (viewY + height * 3.5f/5);

    buttons[4].posX = viewX + width*14/23;
    buttons[4].posY = (viewY + height * 3.5f/5);

    //checks if the player can afford or already purchased items in shop
    if (buttons[0].getIsClicked() ) {
      player.gun = "pistol";
      buttons[0].setClicked(false);
    }

    if (buttons[1].getIsClicked()) {
      if (numCoins >= shotgunCost && player.gun != "shotgun") {
        player.gun = "shotgun";
        numCoins -= shotgunCost;
      }
      buttons[1].setClicked(false);
    }

    if (buttons[2].getIsClicked()) {
      if (numCoins >= assaultCost && player.gun != "assault") {
        player.gun = "assault";
        numCoins -= assaultCost;
      }
      buttons[2].setClicked(false);
    }

    if (buttons[3].getIsClicked()) {
      if (numCoins >= healthCost) {
        if (player.lives < 18) {
          player.lives++;
          numCoins -= healthCost;
        }
      }
      buttons[3].setClicked(false);
    }

    if (buttons[4].getIsClicked()) {
      if (numCoins >= ammoCost) {
        if (player.bulletsLeft < 99) {
          player.bulletsLeft += 2;
          numCoins -= ammoCost;
        }
      }
      buttons[4].setClicked(false);
    }

    for (int i = 0; i<buttons.length; i++) {
      if ((i == 1 || i == 3) && numCoins < shotgunCost)
        buttons[i].setHighlight(color(255, 0, 0));
      else if (i == 2 && numCoins < assaultCost)
        buttons[i].setHighlight(color(255, 0, 0));
      else if ( i == 4 && numCoins < ammoCost)
        buttons[i].setHighlight(color(255, 0, 0));
      else buttons[i].setHighlight(color(255, 255, 255));
    }
  }
}
public class Sprite {
  PImage img;
  float centreX, centreY;
  float changeX, changeY;
  float w, h;
  String fn;

  public Sprite(String filename, float scale, float x, float y) {
    fn = filename;
    img = loadImage(filename);
    w = img.width * scale;
    h = img.height * scale;
    this.centreX = x;
    this.centreY = y;
    this.changeX = 0;
    this.changeY = 0;
  }

  public Sprite(String filename, float scale) {
    this(filename, scale, 0, 0);
  }

  public void display() {
    image(img, centreX, centreY, w, h);
  }

  public void update() {
    centreX += changeX;
    centreY += changeY;
  }

  public void setRight(float right) {
    centreX = right - w/2;
  }

  public float getRight() {
    return centreX + w/2;
  }

  public void setLeft(float left) {
    centreX = left + w/2;
  }

  public float getLeft() {
    return centreX - w/2;
  }

  public void setTop(float top) {
    centreY = top+h/2;
  }

  public float getTop() {
    return centreY - h/2;
  }

  public void setBottom(float bottom) {
    centreY = bottom - h/2;
  }
  public float getBottom() {
    return centreY + h/2;
  }

  public PImage mirrorX(PImage src) {
    PImage dst = createImage(src.width, src.height, ARGB);
    src.loadPixels();
    dst.loadPixels();
    for (int y = 0; y < src.height; y++ ) {
      for (int x = 0; x < src.width; x++ ) {
        dst.pixels[y*src.width+x] = src.pixels[y*src.width+src.width-x-1];
      }
    }
    dst.updatePixels();
    return(dst);
  }
}
  public void settings() {  size(800, 600); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "JumpQuest" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
