class LevelSelect {
  MenuButton buttons[] = new MenuButton[4];
  PImage l1, l2, l3, exitButton, bg;

  public LevelSelect() {
    l1 = loadImage("menu/l1.png");
    l2 = loadImage("menu/l2.png");
    l3 = loadImage("menu/l3.png");
    exitButton = loadImage("menu/exitButton.png");
    bg = loadImage("backgrounds/levelSelect.png");

    buttons[0] = new MenuButton(width*2/7, height/2, l1.width, l1.height, l1);
    buttons[1] = new MenuButton(width/2, height/2, l2.width, l2.height, l2);
    buttons[2] = new MenuButton(width*5/7, height/2, l3.width, l3.height, l3);
    buttons[3] = new MenuButton(width/2, height*3/4, exitButton.width, exitButton.height, exitButton);
  }

  void display() {
    for (int i = 0; i < 4; i++) {
      menu.buttons[i].setClicked(false);
    }

    image(bg, width/2, height/2);
    for (int i = 0; i < buttons.length; i++) {
      buttons[i].draws();
    }
  }

  void update() {
    display();
    
    if (buttons[0].getIsReleased()) {    //play
      levelNum = 1;
      loadLevel();

      menu.display = false;
      menu.levelScreen = false;
      buttons[0].setPressed(false);
      mouseReleased =  false;
    }

    if (buttons[1].getIsReleased()) {    //level select
      levelNum = 2;
      loadLevel();

      menu.display = false;
      menu.levelScreen = false;
      buttons[1].setPressed(false);
      mouseReleased =  false;
    }

    if (buttons[2].getIsReleased()) {    //controls
      levelNum = 3;
      loadLevel();
      mouseReleased =  false;

      menu.display = false;
      menu.levelScreen = false;
      buttons[2].setPressed(false);
      mouseReleased =  false;
    }

    if (key == ESC || this.buttons[3].getIsReleased()) {    //credits
      menu.setSelect(2);
      menu.levelScreen = false;
      menu.display = true;
      buttons[3].setPressed(false);
      mouseReleased = false;
      key = 0;
    }
  }
}
