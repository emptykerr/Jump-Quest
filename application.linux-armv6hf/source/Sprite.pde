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

  void setRight(float right) {
    centreX = right - w/2;
  }

  float getRight() {
    return centreX + w/2;
  }

  void setLeft(float left) {
    centreX = left + w/2;
  }

  float getLeft() {
    return centreX - w/2;
  }

  void setTop(float top) {
    centreY = top+h/2;
  }

  float getTop() {
    return centreY - h/2;
  }

  void setBottom(float bottom) {
    centreY = bottom - h/2;
  }
  float getBottom() {
    return centreY + h/2;
  }

  PImage mirrorX(PImage src) {
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
