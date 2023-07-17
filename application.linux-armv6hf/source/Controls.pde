class Controls {
  PImage text, back, title, titleLight, trademark, buildings, bg, arrow;
  MenuButton exit;
  Controls() {
    back = loadImage("menu/controlsScreen.png");
    buildings = loadImage("backgrounds/buildings.png");
    bg = loadImage("backgrounds/BG.png");
    arrow = loadImage("gfx/arrow.png");
    arrow = mirrorX(arrow);
    exit = new MenuButton(50, 50, arrow.width, arrow.height, arrow);
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

  void imageScale(PImage toScale, float scale) {
    toScale.resize(int(toScale.width*scale), int(toScale.height*scale));
  }

  void update() {
    display();

    if (key == ESC || exit.getIsReleased()) {
      menu.setSelect(3);
      menu.controlsScreen = false;
      mouseReleased =  false;

      key = 0;
    }
  }

  void display() {
    background(0, 0, 60);
    image(bg, width/2, height/2, bg.width, bg.height);
    image(buildings, width/2, height/2 + height/5, width, buildings.height);
    image(back, width/2, height/2, back.width, back.height);
    exit.draws();
  }
}
