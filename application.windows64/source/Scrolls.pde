public class Scrolls extends Animated {
  float boundaryTop, boundaryBot;
  String text;
  public Scrolls(String filename, float scale, float bTop, float bBot, String text) {
    super(filename, scale);
    boundaryTop = bTop;
    boundaryBot = bBot;
    standIdle = new PImage[1];
    changeY = 0.2;
    this.text = text;
    PImage scrollImg = loadImage("gfx/scroll.png");
    super.w = 25;
    super.h = 25;
    for (int i = 0; i < standIdle.length; i++) {
      standIdle[i] = scrollImg;
      currentImages = standIdle;
    }
  }

  void update() {
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
  void  displayText() {
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
