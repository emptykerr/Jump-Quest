class MenuButton {
  //_________________________________________________ variables

  float posX, posY;
  float x, y;
  int wScale, hScale;
  String text;
  boolean mouseInside;
  boolean mouseClicked, mouseIsPressed;
  PImage buttonImg, buttonPressedImg;
  color colour;
  boolean mouseHasReleased;


  //_________________________________________________ constructor

  MenuButton (float x, float y, float w, float h, String t) {
    wScale = round(w);
    hScale = round(h);
    posX = x;
    posY = y;
    text = t;

    buttonImg = loadImage("menu/Button.png");
    buttonImg.resize(wScale, hScale);
    buttonPressedImg = loadImage("menu/ButtonPressed.png");
    buttonPressedImg.resize(wScale, hScale);
  }

  MenuButton (float x, float y, float w, float h, PImage img) {
    posX = x;
    posY = y;
    this.x = x;
    this.y = y;

    wScale = round(w);
    hScale = round(h);
    text = " ";
    buttonImg = img;
    buttonImg.resize(wScale, hScale);
    buttonPressedImg = img;
    buttonPressedImg.resize(wScale, hScale);
    colour = color(255, 255, 255);
  }

  //_________________________________________________ functionality

  void draws() {
    mouseHasReleased = mouseReleased;
    //colour = color(255, 255, 255);
    if (mouseX >= x - wScale/2 && mouseX <= x - wScale/2 + wScale && mouseY >= y - hScale/2 && mouseY <= y + hScale/2) 
      mouseInside = true;
    else
      mouseInside = false;

    if (mouseInside) {
      noStroke();
      fill(colour, 100);
      image(buttonPressedImg, posX, posY);
      rectMode(CENTER);
      rect(posX, posY, wScale, hScale);
      rectMode(CORNER);
      fill(255);
      textSize(50);
      textAlign(CENTER, CENTER);
      text(text, posX + wScale/2, posY + hScale/2);
    } else {
      image(buttonImg, posX, posY);
      fill(0);
      textSize(50);
      textAlign(CENTER, CENTER);
      text(text, posX + wScale/2, posY + hScale/2);
    }
  }


  //_________________________________________________ get functions
  void setHighlight(color colour) {
    this.colour = colour;
  }


  boolean getIsPressed() {
    if (mouseInside && mousePressed && mouseReleased) {
      this.mouseIsPressed = true;
      return mouseIsPressed;
    } else {
      this.mouseIsPressed = false;
      return mouseIsPressed;
    }
  }

  boolean getIsReleased() {
    if (mouseInside && mouseReleased) {
      return true;
    } else {
      return false;
    }
  }

  void setPressed(boolean b) {
    this.mouseIsPressed = b;
  }

  void setClicked(boolean b) {
    if (mouseInside)
      this.mouseClicked = b;
  }

  boolean getIsClicked() {
    if (mouseInside && mouseClicked)
      return true;

    return false;
  }
}
