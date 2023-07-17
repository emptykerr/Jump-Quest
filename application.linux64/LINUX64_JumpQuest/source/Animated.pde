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
