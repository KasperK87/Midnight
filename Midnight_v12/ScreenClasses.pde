class Screen extends Thread {
  public Screen() {
  }
  public void render() {
  }
  public void update(float dt) {
  }

  public void run() {
    while (true) {
      render();
    }
  }
}

class TitleMenu extends Screen {
  char previousKey;
  int previousKeyCode;
  byte selectedOp;

  String player2;
  // 0 = start
  // 1 = options
  // 2 = quit

  public TitleMenu() {
    selectedOp = 0;

    player2 = "NO";
  }
  public void render() {
    background(0);

    textSize(64);
    textAlign(CENTER);
    fill(255);
    text("Kill All Zombies!", width/2, height/2-150);

    textSize(32);
    textAlign(CENTER);
    fill(255);
    if (selectedOp == 0) {
      fill(255, 0, 0);
    }
    text("START", width/2, height/2-25);
    fill(255);
    if (selectedOp == 1) {
      fill(255, 0, 0);
    }
//    text("CONFIGURE GAMEPAD", width/2, height/2+25);
//    fill(255);
//    if (selectedOp == 2) {
//      fill(255, 0, 0);
//    }
    text("PLAYER 2: " + player2, width/2, height/2+25);
    fill(255);
    if (selectedOp == 2) {
      fill(255, 0, 0);
    }
    text("QUIT", width/2, height/2+125);
  }
  public void update(float dt) {
    String move = controller.menuControl(); 
    if (move == "down") {
      selectedOp = (byte)constrain(selectedOp+1, 0, 3);
    } else if (move == "up") {
      selectedOp = (byte)constrain(selectedOp-1, 0, 3);
    } else if (move == "select") {
      if (selectedOp == 0) {
        if (player2 == "YES")
          player2Active = true;
        else 
          player2Active = false;
        currentScreen = goToWorld();
        println("current screen changed");
      } 
//      else if (selectedOp == 1) {
//        String[] lines = loadStrings("player_org");
//        saveStrings("./data/player", lines);        
//        if (gpad != null) {
//          gpad.close();
//          gpad.available = true;
//        }
//        gpad = control.getMatchedDevice("player");
//      } 
      else if (selectedOp == 1) {
        if (player2 == "NO") {
          player2 = "YES";
        } else 
          player2 = "NO";
      } else if (selectedOp == 2) {
        exit();
      }
    }

    if (mouseX < width/2 + textWidth("START")/2 && mouseX > width/2 - textWidth("START")/2 
      && mouseY < height/2-25 && mouseY > height/2-25 - textAscent()) {
      selectedOp = 0;
      if (mousePressed) {
        if (player2 == "YES") {
          player2Active = true;
          if (gpad2 == null && gpad1 != null) {
            controller1 = new Controller(null, true); 
            controller2 = new Controller(gpad1);
          }
        } else 
          player2Active = false;

        currentScreen = goToWorld();
      }
    } 
//    else if (mouseX < width/2 + textWidth("CONFIGURE GAMEPAD")/2 && mouseX > width/2 - textWidth("CONFIGURE GAMEPAD")/2 
//      && mouseY < height/2+25 && mouseY > height/2+25 - textAscent()) {
//      selectedOp = 1;
//      if (mousePressed) {
//        String[] lines = loadStrings("player_org");
//        saveStrings("./data/player", lines);     
//        if (gpad != null) {
//          gpad.close();
//          gpad.available = true;
//        }
//        gpad = control.getMatchedDevice("player");
//      }
//    } 
    else if (mouseX < width/2 + textWidth("PLAYER 2:" + player2)/2 && mouseX > width/2 - textWidth("PLAYER 2:" + player2)/2 
      && mouseY < height/2+25 && mouseY > height/2+25 - textAscent()) {
      selectedOp = 1;
      if (mousePressed && mouseStateReleased) {
        if (player2 == "NO") {
          player2 = "YES";
        } else 
          player2 = "NO";
        mouseStateReleased = false;
      }
    } else if (mouseX < width/2 + textWidth("QUIT")/2 && mouseX > width/2 - textWidth("QUIT")/2 
      && mouseY < height/2+125 && mouseY > height/2+125 - textAscent()) {
      selectedOp = 2;
      if (mousePressed) {
        exit();
      }
    } 
    previousKey = key;
    previousKeyCode = keyCode;
  }
}

class PauseMenu extends Screen {
  char previousKey;
  int previousKeyCode;
  byte selectedOp;
  // 0 = start
  // 1 = options
  // 2 = quit

  public PauseMenu() {
    selectedOp = 0;
  }
  public void render() {
    //fill(0, 127);
    //rect(0, 0, height, width);

    textSize(64);
    textAlign(CENTER);
    fill(255);
    text("Kill All Zombies!", width/2, height/2-150);

    textSize(32);
    textAlign(CENTER);
    fill(255);
    if (selectedOp == 0) {
      fill(255, 0, 0);
    }
    text("RESUME", width/2, height/2-25);
    fill(255);
    if (selectedOp == 1) {
      fill(255, 0, 0);
    }
    text("RESTART", width/2, height/2+25);
//    fill(255);
//    if (selectedOp == 2) {
//      fill(255, 0, 0);
//    }
//    text("CONFIGURE GAMEPAD", width/2, height/2+75);
    fill(255);
    if (selectedOp == 2) {
      fill(255, 0, 0);
    }
    text("QUIT", width/2, height/2+125);
  }
  public void update(float dt) {
    String move = controller.menuControl(); 
    if (move == "down") {
      selectedOp = (byte)constrain(selectedOp+1, 0, 2);
    } else if (move == "up") {
      selectedOp = (byte)constrain(selectedOp-1, 0, 2);
    } else if (move == "select") {
      if (selectedOp == 0) {
        currentScreen = goToWorld();
      } else if (selectedOp == 1) {
        createWorldMap();
        worldInstance = mapInstance[1][1];
        currentScreen = worldInstance;
      } 
//      else if (selectedOp == 2) {
//        String[] lines = loadStrings("player_org");
//        saveStrings("./data/player", lines);        
//        if (gpad != null) {
//          gpad.close();
//          gpad.available = true;
//        }
//        gpad = control.getMatchedDevice("player");
//      } 
      else if (selectedOp == 2) {
        exit();
      }
    }

    if (mouseX < width/2 + textWidth("START")/2 && mouseX > width/2 - textWidth("START")/2 
      && mouseY < height/2-25 && mouseY > height/2-25 - textAscent()) {
      selectedOp = 0;
      if (mousePressed) {
        currentScreen = goToWorld();
      }
    } else if (mouseX < width/2 + textWidth("RESTART")/2 && mouseX > width/2 - textWidth("RESTART")/2 
      && mouseY < height/2+25 && mouseY > height/2+25 - textAscent()) {
      selectedOp = 1;
      if (mousePressed) {
        createWorldMap();
        worldInstance = mapInstance[1][1];
        currentScreen = worldInstance;
      }
    } 
//    else if (mouseX < width/2 + textWidth("CONFIGURE GAMEPAD")/2 && mouseX > width/2 - textWidth("CONFIGURE GAMEPAD")/2 
//      && mouseY < height/2+75 && mouseY > height/2+75 - textAscent()) {
//      selectedOp = 2;
//      if (mousePressed) {
//        String[] lines = loadStrings("player_org");
//        saveStrings("./data/player", lines);    
//        if (gpad != null) {
//          gpad.close();
//          gpad.available = true;
//        }
//        gpad = control.getMatchedDevice("player");
//      }
//    } 
    else if (mouseX < width/2 + textWidth("QUIT")/2 && mouseX > width/2 - textWidth("QUIT")/2 
      && mouseY < height/2+125 && mouseY > height/2+125 - textAscent()) {
      selectedOp = 2;
      if (mousePressed) {
        exit();
      }
    }
    previousKey = key;
    previousKeyCode = keyCode;
  }
}

class CustomScreen extends Screen {
  char previousKey;
  int previousKeyCode;
  byte selectedOp;
  // 0 = start
  // 1 = options
  // 2 = quit

  String customTitle;

  public CustomScreen(String input) {
    customTitle = input;
    selectedOp = 0;
  }
  public void render() {
    background(0);

    textSize(64);
    textAlign(CENTER);
    fill(255);
    text(customTitle, width/2, height/2-150);

    textSize(32);

    fill(255);
    if (selectedOp == 0) {
      fill(255, 0, 0);
    }
    text("RESTART", width/2, height/2-25);
//    fill(255);
//    if (selectedOp == 1) {
//      fill(255, 0, 0);
//    }
//    text("CONFIGURE GAMEPAD", width/2, height/2+25);
    fill(255);
    if (selectedOp == 1) {
      fill(255, 0, 0);
    }
    text("QUIT", width/2, height/2+75);
  }
  public void update(float dt) {
    String move = controller.menuControl(); 
    if (move == "down") {
      selectedOp = (byte)constrain(selectedOp+1, 0, 1);
    } else if (move == "up") {
      selectedOp = (byte)constrain(selectedOp-1, 0, 1);
    } else if (move == "select") {
      if (selectedOp == 0) {
        currentScreen = new TitleMenu();
        createWorldMap();
        worldInstance = mapInstance[1][1];
        currentScreen = worldInstance;
      } 
//      else if (selectedOp == 1) {
//        String[] lines = loadStrings("player_org");
//        saveStrings("./data/player", lines);        
//        if (gpad != null) {
//          gpad.close();
//          gpad.available = true;
//        }
//        gpad = control.getMatchedDevice("player");
//      } 
      else if (selectedOp == 1) {
        exit();
      }
    }

    if (mouseX < width/2 + textWidth("START")/2 && mouseX > width/2 - textWidth("START")/2 
      && mouseY < height/2-25 && mouseY > height/2-25 - textAscent()) {
      selectedOp = 0;
      if (mousePressed) {
        currentScreen = new TitleMenu();
        createWorldMap();
        worldInstance = mapInstance[1][1];
        currentScreen = worldInstance;
      }
    } 
//    else if (mouseX < width/2 + textWidth("CONFIGURE GAMEPAD")/2 && mouseX > width/2 - textWidth("CONFIGURE GAMEPAD")/2 
//      && mouseY < height/2+25 && mouseY > height/2+25 - textAscent()) {
//      selectedOp = 1;
//      if (mousePressed) {
//        String[] lines = loadStrings("player_org");
//        saveStrings("./data/player", lines);        
//        if (gpad != null) {
//          gpad.close();
//          gpad.available = true;
//        }
//        gpad = control.getMatchedDevice("player");
//      }
//    } 
    else if (mouseX < width/2 + textWidth("QUIT")/2 && mouseX > width/2 - textWidth("QUIT")/2 
      && mouseY < height/2+75 && mouseY > height/2+75 - textAscent()) {
      selectedOp = 1;
      if (mousePressed) {
        exit();
      }
    }
    previousKey = key;
    previousKeyCode = keyCode;
  }
}

class HUD extends HumanDecorater {
  PVector p_shootDir = new PVector();
  Controller controller;

  Minimap minimap;

  float drawLocationY = -150;

  HUD(Human instance, Controller setController) {
    type = "human";
    humanRef = instance;

    controller = setController;

    velocity = new PVector(0, 0);
    p_shootDir = new PVector();

    minimap = new Minimap();
  }

  public void update(float dt) {
    humanRef.update(dt);

    String pressed = controller.itemselect(); 

    if (pressed == "right") {
      getInventory().setSelected(getInventory().selected+1);
    } else if (pressed == "left") {
      getInventory().setSelected(getInventory().selected-1);
    }
  }

  public void render() {
    pushMatrix();
    if (controller.takeKeyboard && pmouseX != mouseX && pmouseY != mouseY) {
      if (wholeMap) {
        //p_shootDir = new PVector(mouseX-getPos().x, (mouseY-getPos().y));
        p_shootDir = new PVector(-(width/2-mouseX), -(height/4-mouseY));
      } else {
        p_shootDir = new PVector(-(width/2-mouseX), -(height/2-mouseY));
      }
    } else if (controller.gpad != null && (controller.gpad.getSlider("verticalShoot").getValue() != 0  || controller.gpad.getSlider("horizontalShoot").getValue() != 0)) {
      PVector dir = new PVector(controller.gpad.getSlider("horizontalShoot").getValue(), controller.gpad.getSlider("verticalShoot").getValue());
      p_shootDir = dir;
    }



    //println("I have a gun");
    translate(getPos().x, getPos().y);

    p_shootDir.normalize();

    PVector unit = p_shootDir.get();
    float LengthOfTriangle = 12;

    if (getHeldObject() != null && getHeldObject().type == "gun") {
      LengthOfTriangle = 12*16;
    }

    unit.mult(LengthOfTriangle);
    unit.rotate(radians(getAccuracy()/2));

    float scalar = unit.dot(p_shootDir);    
    unit.mult(LengthOfTriangle/scalar);

    if (getStamina() < 0) {
      fill(255, 0, 0, 25);
      stroke(255, 0, 25);
    } else {
      fill(255, 255, 0, 127);
      stroke(255, 255, 127);
    }

    strokeWeight(0.5);
    beginShape(TRIANGLES);
    vertex(0, 0); //A
    strokeWeight(0.05);
    if (getStamina() < 0) {
      fill(255, 0, 0, 0);
      stroke(255, 0, 25, 0);
    } else {
      fill(255, 255, 0, 0);
      stroke(255, 255, 127, 0);
    }
    vertex(unit.x, unit.y); //B

    unit.rotate(radians(-getAccuracy()));

    vertex(unit.x, unit.y);
    strokeWeight(0.5);
    if (getStamina() < 0) {
      fill(255, 0, 0, 25);
      stroke(255, 0, 25);
    } else {
      fill(255, 255, 0, 127);
      stroke(255, 255, 127);
    }
    endShape();
    translate(-getPos().x, -getPos().y);
    noStroke();


    humanRef.render();


    worldInstance.closeUp();
    popMatrix();
  }

  void pre() {
    resetMatrix();
    translate(0, drawLocationY);

    if (getStamina() < 0) {
      fill(255, 0, 0, 25);
      stroke(255, 0, 0);
    } else {
      fill(255, 255, 0, 25);
      stroke(255, 255, 0, 125);
    }
    textSize(32);
    textAlign(LEFT);
    fill(255);

    //text("HP: 10/10", 640+160+32, 16*3);

    //    text("YOU: ", 640+160+32, 16*3);
    //    if (type == "human") {
    //      fill (255, 0, 0);
    //      text("HUMAN", 640+160+32+textWidth("YOU: "), 16*3);
    //    } else {
    //      fill (0, 255, 0);
    //      text("ZOMBIE", 640+160+32+textWidth("HP: " + getHP() + "  YOU: "), 16*3);
    //    }
    //
    //    fill (255, 0, 0);
    //    text("Human: "+ worldInstance.civilians, 640+160+32, 16*3+50);
    //    fill (0, 255, 0);
    //    text("Zombies: "+ worldInstance.zombies, 640+160+32+textWidth("Human: "+ worldInstance.civilians)+10, 16*3+50);

    //white part of stamina bar
    noStroke();
    fill(255);
    rect(640+160+32, 16*3+50+50+64, 300, 16);


    for (int x = 0; x < getHP ()/2+1; x++) {
      if ((x+1)*2 <= getHP())
        image(spriteHolderInstance.heart, 640+160+32+x*64, 16*3+50+50, 64, 64);
      else if (getHP() >= x*2+1)
        image(spriteHolderInstance.halfheart, 640+160+32+x*64, 16*3+50+50, 64, 64);
    }
    if (getStamina() > 0)
      fill(0, 255, 0);
    else
      fill(255, 0, 0);
    rect(640+160+32, 16*3+50+50+64, getStamina(), 16);

    getInventory().render(640+160+32, 16*3+50+50+64+32);

    //    //text("Stamina: "+ getStamina() +"/"+ getMaxStamina(), 640+160+32, 16*3+50+50);
    //    //text("Food: 4000/4000", 640+160+32, 16*3+100+50);
    //    //text("Sleep: 1000/1000", 640+160+32, 16*3+150+50);
    //
    //    fill(255);
    //    text("SECTOR: " + getCurrentSectorX() + ":" + getCurrentSectorY(), 640+160+32, 16*3+100+150+300-50);
    minimap.render(640+160+32+200+6, 16*3+50+50+64+32+32+64);

  if (debug)
    text("FPS: " + frameRate, 640+160+32, 16*3+100+150+300);
    
    //    text("press 'M' for zoom", 640+160+32, 16*3+100+150+300+50);
    //    text("press 'R' for restart", 640+160+32, 16*3+150+150+300+50); 
    //    worldInstance.closeUp();
  }
}

class Minimap {
  int sectors[][][][];

  Minimap() {
    sectors = new int[mapInstance.length][mapInstance[0].length][3][3];
  }

  void render(int x, int y) {
    int cornerX = (int)((player1.getPos().x-player1.getW()/2)/(16*18));
    int cornerY = (int)((player1.getPos().y-player1.getH()/2)/(16*18));

    int cornerX2 = 0;
    int cornerY2 = 0;
    if (player2Active) {
      cornerX2 = (int)((player2.getPos().x-player2.getW()/2)/(16*18));
      cornerY2 = (int)((player2.getPos().y-player2.getH()/2)/(16*18));
    }

    if (worldInstance.sectorClear == true) {
      sectors[player1.getCurrentSectorX()][player1.getCurrentSectorY()][0][0] = 3;
    }

    noStroke();
    for (int a = 0; a < sectors.length; a++)
      for (int b = 0; b < sectors[a].length; b++) {
        if (sectors[a][b][0][0] == 3) 
          fill(255, 255, 0);
        for (int i = 0; i < sectors[a][b].length; i++)
          for (int j = 0; j < sectors[a][b][i].length; j++)
          {
            if (sectors[a][b][i][j] == 0)
              fill(127);
            else
              fill(255);

            if (player1.getCurrentSectorX() == a && player1.getCurrentSectorY() == b)
              if (cornerX == i && cornerY == j) {
                fill(127, 127, 255);
                sectors[a][b][i][j] = 2;
              }

            if (player2Active) {
              if (player2.getCurrentSectorX() == a && player2.getCurrentSectorY() == b)
                if (cornerX2 == i && cornerY2 == j) {
                  fill(127, 255, 127);
                  sectors[a][b][i][j] = 2;
                }
            }

            rect(x+16*i+52*a, y+16*j+52*b, 12, 12);
          }
      }
  }
}
