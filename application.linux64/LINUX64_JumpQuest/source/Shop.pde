class Shop {
  MenuButton buttons[] = new MenuButton[5];
  MenuButton mb;
  PImage pistol, shotgun, assault, health, ammo;
  PImage shop;
  int pistolX, pistolY, pistolCost;
  int shotgunX, shotgunY, shotgunCost;
  int assaultX, assaultY, assaultCost;
  int healthX, healthY, healthCost;
  int ammoX, ammoY, ammoCost;

  public Shop() {
    //variables for the shop items
    pistol = loadImage("gfx/PISTOL.png");
    shotgun = loadImage("gfx/SHOTGUN.png");
    assault = loadImage("gfx/ASSAULT.png");
    health = loadImage("gfx/HEALTH.png");
    ammo = loadImage("gfx/AMMO.png");
    pistolX =  + width*2/7;
    pistolY = + height * 2/5;
    pistolCost = 0;

    shotgunX =  + width/2;
    shotgunY =  + height * 2/5;
    shotgunCost = 10;

    assaultX =  + width*5/7;
    assaultY =  + height * 2/5;
    assaultCost = 20;

    healthX =  + width*9/23;
    healthY = (int)( + height * 3.5/5);
    healthCost = 5;

    ammoX =  + width*14/23;
    ammoY =  (int)( + height * 3.5/5);
    ammoCost = 1;

    buttons[0] = new MenuButton(pistolX, pistolY, pistol.width, pistol.height, pistol);
    buttons[1] = new MenuButton(shotgunX, shotgunY, shotgun.width, shotgun.height, shotgun);
    buttons[2] = new MenuButton(assaultX, assaultY, assault.width, assault.height, assault);
    buttons[3] = new MenuButton(healthX, healthY, health.width, health.height, health);
    buttons[4] = new MenuButton(ammoX, ammoY, ammo.width, ammo.height, ammo);
  }

  void display() {    //displays each shop item, and the corresponding price
    for (int i = 0; i < buttons.length; i++) {
      buttons[i].draws();
    }
    textMode(CORNER);
    textSize(25);
    fill(255);
    text("$" + pistolCost, viewX +pistolX, viewY +pistolY);
    text("$" + shotgunCost, viewX +shotgunX, viewY +shotgunY);
    text("$" + assaultCost, viewX+ assaultX, viewY +assaultY);
    text("$" + healthCost, viewX +healthX, viewY +healthY + 5);
    text("$" + ammoCost, viewX +ammoX, viewY + ammoY + 5);
    //image(shop, viewX + width/2, viewY + height/2);
  }

  void update() {
    //updates each button on the shop
    buttons[0].posX = viewX + width*2/7;
    buttons[0].posY = viewY + height * 2/5;

    buttons[1].posX = viewX + width/2;
    buttons[1].posY = viewY + height * 2/5;

    buttons[2].posX =  viewX + width*5/7;
    buttons[2].posY = viewY + height * 2/5;

    buttons[3].posX = viewX + width*9/23;
    buttons[3].posY = (viewY + height * 3.5/5);

    buttons[4].posX = viewX + width*14/23;
    buttons[4].posY = (viewY + height * 3.5/5);

    //checks if the player can afford or already purchased items in shop
    if (buttons[0].getIsClicked() ) {
      player.gun = "pistol";
      buttons[0].setClicked(false);
    }

    if (buttons[1].getIsClicked()) {
      if (numCoins >= shotgunCost && player.gun != "shotgun") {
        player.gun = "shotgun";
        numCoins -= shotgunCost;
      }
      buttons[1].setClicked(false);
    }

    if (buttons[2].getIsClicked()) {
      if (numCoins >= assaultCost && player.gun != "assault") {
        player.gun = "assault";
        numCoins -= assaultCost;
      }
      buttons[2].setClicked(false);
    }

    if (buttons[3].getIsClicked()) {
      if (numCoins >= healthCost) {
        if (player.lives < 18) {
          player.lives++;
          numCoins -= healthCost;
        }
      }
      buttons[3].setClicked(false);
    }

    if (buttons[4].getIsClicked()) {
      if (numCoins >= ammoCost) {
        if (player.bulletsLeft < 99) {
          player.bulletsLeft += 2;
          numCoins -= ammoCost;
        }
      }
      buttons[4].setClicked(false);
    }

    for (int i = 0; i<buttons.length; i++) {
      if ((i == 1 || i == 3) && numCoins < shotgunCost)
        buttons[i].setHighlight(color(255, 0, 0));
      else if (i == 2 && numCoins < assaultCost)
        buttons[i].setHighlight(color(255, 0, 0));
      else if ( i == 4 && numCoins < ammoCost)
        buttons[i].setHighlight(color(255, 0, 0));
      else buttons[i].setHighlight(color(255, 255, 255));
    }
  }
}
