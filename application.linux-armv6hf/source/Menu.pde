class Menu {
  Controls control;
  LevelSelect levelSelect;
  Credits credit;
  MenuButton buttons[] = new MenuButton[4];

  PImage play, level, credits, avatar, select, jump, quest, bg, buildings, enter, back, car, car2, controls;

  int pos = 1;
  int bgScale;
  int bgX  = 0, bgX2 = width;
  int carX, carX2;

  float rand1, rand2;
  float r1, g1, b1;
  float r2, g2, b2;
  float selectY;

  boolean display = true;
  boolean select1, select2, select3, select4;
  boolean controlsScreen = false;
  boolean levelScreen = false;
  boolean creditsScreen = false;

  Menu() {
    control = new Controls();
    levelSelect = new LevelSelect();
    credit = new Credits();
    play = loadImage("gfx/PLAY.png");
    level = loadImage("gfx/LEVEL.png");
    credits = loadImage("gfx/CREDITS.png");
    controls = loadImage("gfx/CONTROLS.png");
    avatar = loadImage("menu/avatar.png");
    select = loadImage("gfx/select.png");
    jump = loadImage("menu/jumpquest.png");
    bg = loadImage("backgrounds/BG.png");
    buildings = loadImage("backgrounds/buildings.png");
    enter  = loadImage("gfx/enter.png");
    back = loadImage("gfx/back.png");
    car = loadImage("tiles/car.png");
    selectY = height *2/5;
    car2 = mirrorX(car);
    imageScale(play, 0.3);
    imageScale(level, 0.3);
    imageScale(controls, 0.3);
    imageScale(credits, 0.3);
    imageScale(select, 0.8);

    r1 = random(255);
    g1 = random(255);
    b1 = random(255);

    r2 = random(255);
    g2 = random(255);
    b2 = random(255);
    
    buttons[0] = new MenuButton(width/2, height * 2/5, play.width, play.height, play);
    buttons[1] = new MenuButton(width/2, height * 2.5/5, level.width, level.height, level);
    buttons[2] = new MenuButton(width/2, height * 3/5, controls.width, controls.height, controls);
    buttons[3] = new MenuButton(width/2, height * 3.5/5, credits.width, credits.height, credits);
  }

  void imageScale(PImage toScale, float scale) {
    toScale.resize(int(toScale.width*scale), int(toScale.height*scale));
  }

  void display() {
    if (levelScreen) {
      levelSelect.update();
    } else if (controlsScreen) {
      control.update();
    } else if (creditsScreen) {
      credit.update();
    } else {
      background(0, 0, 60);
      wallpaper();

      for (int i = 0; i < buttons.length; i++) {
        if (i+1 == pos && frameCount % 20 < 8) {
        } else
          buttons[i].draws();
      }

      image(avatar, width * 4/5, height - avatar.height/2, avatar.width, avatar.height);
      image(select, width * 2/7, selectY, select.width, select.height);
      image(jump, width/2, height/ 6, jump.width*2/3, jump.height*2/3);

      if ((frameCount % 40) < 20)
        image(enter, width/2, height * 9/10, enter.width*2, enter.height*2);    //flashes "press enter"
    }
  }


  void wallpaper() {
    image(bg, width/2, height/2, bg.width, bg.height);
    image(buildings, bgX, height/2 + height/5, width, buildings.height);
    image(buildings, bgX2, height/2 + height/5, width, buildings.height);
    noStroke();
    fill(0, 0, 60);
    rect(0, height*15/16, width, height/18);
    stroke(1);
    fill(25);
    rect(0, height*8/9, width, height/18);
    bgX--;
    bgX2--;

    if (bgX+width/2 <= 0)
      bgX = width + width/2;
    if (bgX2+width/2<= 0)
      bgX2 = width+width/2;

    drawCars();
  }


//draws random cars at random intervals with random colours in the menu screen
  void drawCars() {
    carX+= 5 + rand1;
    carX2-= 7 + rand2;

    if (random(1000) < 10 && carX > width) {    
      carX = 0-car.width;
      rand1 = random(10);

      r1 = random(255);
      g1 = random(255);
      b1 = random(255);
    }
    tint(r1, g1, b1);
    image(car, carX, height*8/9, car.width/3, car.height/3);

    if (random(1000) < 10 && carX2 < 0) {
      carX2 = width+car.width;
      rand2 = random(12);

      r2 = random(255);
      g2 = random(255);
      b2 = random(255);
    }
    tint(r2, g2, b2);
    image(car2, carX2, height*9/10, car.width/3, car.height/3);

    tint(255);
  }

//mirrors the sprite image
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

  void setSelect(int position) {
    selectY = height* (2 + ((position -1) * 0.5))/5;    //equation for getting the selection position.
  }

  void update() {


    select1 = false;
    select2 = false;
    select3 = false;
    if (pressed) {
      pressed = false;
      
      if (keyUp)    //sets the select icon to the buttons heights
        selectY -= height* .5/5;
      if (selectY < height * 2/5)
        selectY = height * 3.5/5;
      if (keyDown)
        selectY += height*.5/5;
      if (selectY > height * 3.5/5)
        selectY = height * 2/5;
    }

    if (selectY == height*2/5)     //sets the selection number to the corresponding position (eg. if hovering over play, position = 1, if level select, pos =2)
      pos = 1;
    if (selectY == height*2.5/5)
      pos = 2;
    if (selectY == height*3/5)
      pos = 3;
    if (selectY == height*3.5/5)
      pos = 4;

    if (keyEnter) {    //if enter is clicked, set the icon position to that button
      if (pos == 1)
        select1 = true;
      if (pos == 2)
        select2 = true;
      if (pos == 3)
        select3 = true;
      if (pos == 4)
        select4 = true;
    }
    if (keyCode == ESC)
      key = 0;

    if (buttons[0].getIsReleased() || select1 ) {    //if play is pressed, reset all, and create a new player, load game
      resetAll();
      levelNum = 1;
      player = new Player("spritesheets/SpriteSheet.png", 1);
      numCoins = 0;
      player.bulletsLeft = 14;
      loadLevel();
      display = false;
      mouseReleased =  false;
      buttons[0].setPressed(false);
    }

    if ((buttons[1].getIsReleased() || select2 ) && !levelScreen && !creditsScreen) {    //if level select is pressed, reset all, display levels.
      buttons[1].mouseIsPressed = false;
      levelScreen = true;
      levelSelect.display();
      player.bulletsLeft = 14;

      buttons[1].getIsPressed();
      mouseReleased =  false;
      println("0");
    }

    if ((buttons[2].getIsReleased() || select3) && !controlsScreen) {    //if controls is pressed, set the screen to controls.
      controlsScreen = true;
      control.display();
      player.bulletsLeft = 14;

      mouseReleased =  false;
      buttons[2].setPressed(false);
    }

    if ((buttons[3].getIsReleased() || select4) && !creditsScreen) {    //if credits is pressed, create a "minigame" with a new character and randomly spawning enemies.
      buttons[3].setClicked(false);
      resetAll();
      player = new Player("spritesheets/SpriteSheet.png", 1);
      credit.creditY = height + 20;
      createPlatforms("maps/map_credits.csv");
      player.setBottom(GROUND_LEVEL);
      player.centreX = width/2;
      viewX = 0;
      viewY = 0;
      select4 = false;
      creditsScreen = true;
      credit.display();
      mouseReleased =  false;
    }
  }
}
