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
    centreY = entity.centreY  - entity.h/3.2;

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

  void update() {
    move();
  }

  void move() {
    centreX += vel*dir;
    centreY += yvel;
  }

  void setCollided(boolean b) {
  }
}
