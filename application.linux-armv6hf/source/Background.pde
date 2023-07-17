public class Background {
  PImage bg1, bg2, bg3;
  PImage firstbg1, firstbg2, firstbg3;
  PImage secondbg1, secondbg2, secondbg3;
  public Background() {
    firstbg1 = loadImage("backgrounds/bg3.png");
    firstbg2 = loadImage("backgrounds/bg2.png");
    firstbg3 = loadImage("backgrounds/bg1.png");
    secondbg1 = loadImage("backgrounds/secondBG3.png");
    secondbg2 = loadImage("backgrounds/secondBG2.png");
    secondbg3 = loadImage("backgrounds/secondBG1.png");
    bg1 = firstbg1;
    bg2 = firstbg2;
    bg3 = firstbg3;
  }


  void drawParallax() {
    if (levelNum % 2 ==  0) {
      background(8, 0, 0);
      bg1 = secondbg1;
      bg2 = secondbg2;
      bg3 = secondbg3;
    } else {
      background(12, 40, 64);
      bg1 = firstbg1;
      bg2 = firstbg2;
      bg3 = firstbg3;
    }
    //if the first backgrounds x is greater than the width of the screen, create a copy at that x + width;
    int bg1x = -(int)(screenPos((int)0, 0, 3) % bg1.width);
    int bg1y = -(int)(screenYPos(0, 1));
    copy(bg1, (int)(bg1x), bg1y, bg1.width, bg1.height, 0, 0, (int)(bg1.width), bg1.height);
    int bg1x2 = (int)(bg1.width) - bg1x;
    if (bg1x2 < width) {
      copy(bg1, 0, bg1y, bg1.width, bg1.height, (int)(bg1x2), 0, (int)(bg1.width), bg1.height);
    }


    int bg2x = -(int)(screenPos((int)0, 0, 2) % bg2.width);
    int bg2y = -(int)(screenYPos(0, 1)) ;
    copy(bg2, (int)(bg2x), bg2y, bg2.width, bg2.height, 0, 0, (int)(bg2.width), bg2.height);
    int bg2x2 = (int)(bg1.width) - bg2x;
    if (bg2x2 < width) {
      copy(bg2, 0, bg2y, bg2.width, bg2.height, (int)(bg2x2), 0, (int)(bg2.width), bg2.height);
    }


    int bg3x = -(int)(screenPos((int)0, 0, 1) % bg3.width);
    int bg3y = -(int)(screenYPos(0, 1));
    copy(bg3, (int)(bg3x), bg3y, bg3.width, bg3.height, 0, 0, (int)(bg3.width), bg3.height);
    int bg3x2 = (int)(bg3.width) - bg3x;
    if (bg3x2 < width) {
      copy(bg3, 0, bg3y, bg3.width, bg3.height, (int)(bg3x2), 0, (int)(bg3.width), bg3.height);
    }
  }

  float screenPos(int x, int y, int z) {
    return (x - viewX)/z;
  }
  float screenYPos(int y, int  z) {
    return  (y - viewY)/z;
  }

  void imageScale(PImage toScale, float scale) {
    toScale.resize(int(toScale.width*scale), int(toScale.height*scale));
  }

  void imageDouble(PImage toScale) {
    imageScale(toScale, 2.0);
  }
}
