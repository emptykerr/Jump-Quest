public class Exit extends Animated {
  public Exit(String filename, float scale) {
    super(filename, scale);
    standIdle = new PImage[1];
    PImage img = loadImage(filename);
    standIdle[0]  = img;
    currentImages = standIdle;
  }
}
