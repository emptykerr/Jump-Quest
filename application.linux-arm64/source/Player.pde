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
        bulletVel = 6.0;
        Bullet bullet = new Bullet("gfx/bullet.png", 1.0, (int)direction, bulletVel, 1*random(-2, 2), 1.0, this);
        bullets.add(bullet);
      }
    } else if (gun == "pistol") {
      if (bulletsLeft > 0) {
        bulletVel = 7.0;
        Bullet bullet = new Bullet("gfx/bullet.png", 1.0, (int)direction, bulletVel, 0, 1.0, this);
        bullets.add(bullet);
        bulletsLeft--;
      }
    } else if (gun == "assault") {
      if (bulletsLeft > 0) {
        bulletVel = 9.0;
        Bullet bullet = new Bullet("gfx/bullet.png", 1.0, (int)direction, bulletVel, 0, 1.0, this);
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
      rect(viewX + 118 + (i * 9.37), viewY + 78, 7, 23);
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
