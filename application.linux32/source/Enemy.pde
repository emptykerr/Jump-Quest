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



  void update() {
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
            Bullet bullet = new Bullet("gfx/bullet.png", 1.0, (int)direction, 7.0, 0, 1.0, this);
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
              Bullet bullet = new Bullet("gfx/bullet.png", 1.0, (int)direction, 7.0, 1*random(-2, 2), 1.0, this);
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
          changeX = 0.001;
        }
      }
    }
  }



  protected void turnTowardsPlayer(Player player) {
    if (centreX < player.getRight() && centreX > player.getLeft()) {
      changeX = 0.0001;
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
