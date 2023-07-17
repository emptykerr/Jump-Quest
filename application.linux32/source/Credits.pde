class Credits {
  Enemy enemy1, enemy2;
  MenuButton exit;
  PImage text, back, title, titleLight, trademark, buildings, bg, arrow, creditsBox;
  ArrayList<Sprite> creditPlatforms;
  ArrayList<Sprite> creditEnemies;
  boolean enemiesSet = false;
  float creditY = height + 20;

  public Credits() {
    buildings = loadImage("backgrounds/buildings.png");
    creditsBox = loadImage("menu/creditsBox.png");
    bg = loadImage("backgrounds/BG.png");
    arrow = loadImage("gfx/arrow.png");
    arrow = mirrorX(arrow);
    exit = new MenuButton(65, 65, arrow.width, arrow.height, arrow);
    creditPlatforms = new ArrayList<Sprite>();
    creditEnemies = new ArrayList<Sprite>();
    enemy1 = new Enemy("spritesheets/melee.png", 1, 0, width, "melee");
    enemy2 = new Enemy("spritesheets/melee.png", 1, 0, width, "melee");
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
      menu.setSelect(4);
      menu.creditsScreen = false;
      mouseReleased =  false;
      key = 0;
    }

    if (player.centreX + player.changeX < 0 || player.centreX  + player.changeX> width) {
      player.changeX = 0;
    }
    this.checkEnemies();

    if (!enemiesSet) {
      setEnemy1();
      setEnemy2();
      enemiesSet = true;
    }
  }

  void setEnemy1() {
    enemy1 = new Enemy("spritesheets/melee.png", 1, 0, width, "melee");
    enemy1.changeX = 3;
    enemy1.centreY = 505;
    enemy1.centreX = 0 - 30;
    creditEnemies.add(enemy1);
  }

  void setEnemy2() {
    enemy2 = new Enemy("spritesheets/melee.png", 1, 0, width, "melee");
    enemy2.changeX = -3;
    enemy2.centreY = 505;
    enemy2.centreX = width + 30;
    creditEnemies.add(enemy2);
  }

  void drawCredits() {
    fill(0, 0, 0, 50);
    rect(0, 0, width, height);
    String[] lines = loadStrings("menu/credits.txt");
    fill(255);
    textSize(20);
    creditY -= 0.5;
    for (int i = 0; i < lines.length; i++) {

      text(lines[i], width/2, creditY + 30 * i);
    }
    fill(0, 0, 60);
    rectMode(CENTER);
    rect(width/2, 0, 480, 70);
    rectMode(CORNER);
    image(creditsBox, width/2, creditsBox.height/2 + 20);
  }


  void display() {

    player.bulletsLeft = 50;
    background(0, 0, 60);
    image(bg, width/2, height/2, bg.width, bg.height);
    image(buildings, width/2, height/2 + height/5, width, buildings.height);

    player.updateAni();
    resolvePlatformCollisions(player, platforms);
    checkMovements();
    player.display();
    checkGuns();

    for (Sprite bullet : bullets) {
      bullet.display();
      bullet.update();
    }

    for (Sprite platform : creditPlatforms) {
      platform.display();
    }

    if (!creditEnemies.isEmpty())
      for (Sprite enemy : creditEnemies) {
        enemy.update();
        ((Enemy)enemy).updateAni();
        enemy.display();
        if ((enemy.centreX > width + 50 || enemy.centreX < 0 - 50) && creditEnemies.get(0) != enemy)
          //creditEnemies.remove(enemy);
          println();
      }
    if (random(1000) < 5) {
      setEnemy1();
    }
    if (random(1000) < 5) {
      setEnemy2();
    }
    drawCredits();
    exit.draws();
  }

  void checkEnemies() {
    ArrayList<Sprite> bulletsToRemove = new ArrayList<Sprite>();
    for (Sprite bullet : bullets) {
      ArrayList<Sprite> enemyList = checkCollisionList(bullet, creditEnemies);
      for (Sprite enemy : enemyList) {
        creditEnemies.remove(enemy);
        bulletsToRemove.add(bullet);
      }
    }
    bullets.removeAll(bulletsToRemove);

    for (int i = 0; i < creditEnemies.size(); i++)
      if (creditEnemies.get(i).centreX < 0 - 30 || creditEnemies.get(i).centreX > width + 30)
        creditEnemies.remove(creditEnemies.get(i));
  }
}
