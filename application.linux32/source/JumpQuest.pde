final static float SPEED = 5;
final static float TILE_SCALE = 50.0/128;
final static float TILE_SIZE = 50;
final static float SPRITE_SIZE = 50;
final static float GRAVITY = 0.6;
final static float JUMPSPEED = 14;

float TOP_MARGIN = height * 0.2;
float BOT_MARGIN = height * 0.8;
float RIGHT_MARGIN = width * 0.6;
float LEFT_MARGIN = width * 0.4;

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
void setup() {
  size(800, 600);
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

  TOP_MARGIN = height * 0.3;
  BOT_MARGIN = height * 0.2;
  RIGHT_MARGIN = width * 0.4;
  LEFT_MARGIN = width * 0.2;

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
void draw() {

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

void updateAll() {
  score =(round((isMax(viewX)/10)/10.0)*10) + enemiesKilled*50;
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

int isMax(int num) {
  if (num > maxNum) {
    maxNum = num;
  }
  return maxNum;
}
//----------------------------------------------------------\\











//------------------------ DISPLAY METHODS --------------------\\

void displayAll() {
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

void dogDisplay() {
  dog.update();
  dog.updateAni();
  dog.display();
}

void gameOverDisplay() {
  fill(255);
  textSize(30);
  text("GAME OVER", viewX + width/2, viewY + height/2);
  if (player.lives <= 0)
    text("Press SPACE to restart", viewX + width/2, viewY + height/2 + 100);
  else
    text("You win!", viewX + width/2, viewY + height/2 + 100);
}

void lowAmmoDisplay() {
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


void playDeath() {
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

void displayHUD() {
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
void resetAll() {
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

void resetKeys() {
  keyUp = false;
  keyDown = false;
  keyRight = false;
  keyLeft = false;
}

//-----------------------------------------------------\\













//--------------------CHECK METHODS--------------------------\\

void checkAll() {
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
void checkBullets() {
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
void checkEnemy() {
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
void collectCoins() {
  ArrayList<Sprite> coinList = checkCollisionList(player, coins);
  if (coinList.size() > 0) {
    for (Sprite coin : coinList) {
      numCoins++;
      coins.remove(coin);
    }
  }
}

//checks whether the scrolls should be displayed based on player colliding with them
void checkScrolls() {
  ArrayList<Sprite> scrollList = checkCollisionList(player, scrolls);
  if (scrollList.size() > 0) {
    for (Sprite scroll : scrollList) {
      ((Scrolls)scroll).displayText();
    }
  }
}

//method increases the level number each time the player collides with the extract car
void checkNextLevel() {
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


void checkEnding() {
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
void checkDeath() {
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
void checkGuns() {
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
void hidePlayer() {
  playerDraw = false;
}

//creates the map based on the level number.
void loadLevel() {
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
void loadingScreen() {
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
void scroll() {
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
void drawHitbox(Sprite s) {
  rect(s.getLeft(), s.getTop(), s.getRight() - s.getLeft(), s.getBottom() - s.getTop());
}

//moves the screen when the player is damaged, displays blood
void shakeScreen() {
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
boolean isCollide(Sprite s1, Sprite s2) {
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
void mouseMoved() {
  mouseReleased = false;
}

void mouseReleased() {
  if (menu.display && !menu.creditsScreen && !menu.levelScreen)
    menu.buttons[1].setClicked(true);
  if (menu.display && !menu.creditsScreen && !menu.levelScreen)
    menu.buttons[3].setClicked(true);

  mouseReleased = true;
}

void mouseClicked() {
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
void keyPressed() {
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
void keyReleased() {
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
void checkMovements() {
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
void createPlatforms(String filename) {
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
        Enemy enemy = new Enemy("spritesheets/heavy.png", 1.5, bLeft, bRight, "heavy"); 
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
