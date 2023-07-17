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
