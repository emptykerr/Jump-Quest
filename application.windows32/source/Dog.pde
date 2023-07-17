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
