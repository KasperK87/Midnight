import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import ddf.minim.spi.*; 
import ddf.minim.signals.*; 
import ddf.minim.*; 
import ddf.minim.analysis.*; 
import ddf.minim.ugens.*; 
import ddf.minim.effects.*; 
import org.gamecontrolplus.gui.*; 
import org.gamecontrolplus.*; 
import net.java.games.input.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class Midnight_v12 extends PApplet {

boolean debug  = false;

Minim minim;
AudioPlayer player;

Screen currentScreen;
World worldInstance;
SpriteHolder spriteHolderInstance;

World mapInstance[][];

Human player1;
Human player2;

boolean player2Active = false;

PVector keyDirection;
boolean mouseStatePressed;
boolean mouseStateReleased = true;

Controller controller;
Controller controller1;
Controller controller2;

boolean wholeMap;

int lastTime;
int thisTime;
float dt;

PFont font;

//soundfiles
  
public void setup() {
  
  //nessesary for running under processing 3.0
  //Multipel grafich bugs intruduced by the new engine
  //use processing 2.X if you want to compile the game
  surface.setSize(1216, 832);
  
  font = createFont("Arial Bold", 18);
  textFont(font);

  println("loading textures \n...");
  spriteHolderInstance = new SpriteHolder();
  println("textures loaded");
  
  println("loading sounds \n...");
  minim = new Minim(this);
  player = minim.loadFile("soundtrack.aiff");
  player.loop();
  println("sounds loaded \n...");

  mapInstance = new World[3][3];

  currentScreen = new TitleMenu();

  println("init controller \n...");
  // Initialise the ControlIO
  control = ControlIO.getInstance(this);
  // Find a device that matches the configuration file

  //gpad = control.getMatchedDevice("player");
  try {
    gpad1 = control.getDevice("Controller (XBOX 360 For Windows)");
  } 
  catch (Exception e)
  {
    try {
      gpad1 = control.getDevice("Controller (Xbox 360 Wireless Receiver for Windows)");
    } 
    catch (Exception x)
    {
      println("no xbox controller plugged in");
    }
  }

  if (gpad1 != null) {
    gpad1 = control.getMatchedDevice("player");
  }

  //gpad = control.getMatchedDevice("player");
  try {
    gpad2 = control.getDevice("Controller (Xbox 360 Wireless Receiver for Windows)");
  } 
  catch (Exception e)
  {
    println("no xbox controller plugged in");
  }

  if (gpad2 != null) {
    gpad2 = control.getMatchedDevice("player");
  }

  controller = new Controller(gpad1);
  
  controller1 = new Controller(gpad1, true);
  controller2 = new Controller(gpad2);


  keyDirection = new PVector(0, 0);
  println("controller created");

}


public void draw() {
  
  println("starting gameloop \n...");

  thisTime = millis() - lastTime;
  lastTime = lastTime+thisTime;
  dt = thisTime/1000.0f/2;

  noSmooth();
  currentScreen.update(dt);
  background(0);
  currentScreen.render();

  if (gpad != null && controller.map()) {
    if (!wholeMap) {
      wholeMap = true;
    } else {
      wholeMap= false;
    }
  }

  if (gpad != null && controller.restart()) {
    currentScreen = new TitleMenu();
    currentScreen = new TitleMenu();
    createWorldMap();
    worldInstance = mapInstance[1][1];
    currentScreen = worldInstance;
  }

  println("finished gameloop");
}

public void createWorldMap() {
  for (int x = 0; x < mapInstance.length; x++)
    for (int y = 0; y < mapInstance[x].length; y++)
      mapInstance[x][y] = new World(52, 52);

  //outer world boundry
  mapInstance[0][0].setTiles("end", 0, 0, 52, 1);
  mapInstance[0][0].setTiles("end", 0, 0, 1, 52);

  mapInstance[1][0].setTiles("end", 0, 0, 52, 1);

  mapInstance[2][0].setTiles("end", 0, 0, 52, 1);
  mapInstance[2][0].setTiles("end", 51, 0, 1, 52);

  mapInstance[2][1].setTiles("end", 51, 0, 1, 52);

  mapInstance[2][2].setTiles("end", 51, 0, 1, 52);
  mapInstance[2][2].setTiles("end", 0, 51, 52, 1);

  mapInstance[1][2].setTiles("end", 0, 51, 52, 1);

  mapInstance[0][2].setTiles("end", 0, 0, 1, 52);
  mapInstance[0][2].setTiles("end", 0, 51, 52, 1);

  mapInstance[0][1].setTiles("end", 0, 0, 1, 52);

  mapInstance[1][1].agents[0] = new Player(mapInstance[1][1].agents[0], controller1);
  mapInstance[1][1].agents[0] = new HUD(mapInstance[1][1].agents[0], controller1);
  player1 = mapInstance[1][1].agents[0];

  //mapInstance[1][1].agents[1].setPos(new PVector(player1.getPos().x, player1.getPos().y+16));
  if (player2Active) {
    mapInstance[1][1].agents[1] = new Player(mapInstance[1][1].agents[1], controller2);
    HUD player2HUD = new HUD(mapInstance[1][1].agents[1], controller2);
    player2HUD.drawLocationY = + 500;
    mapInstance[1][1].agents[1] = player2HUD;
    player2 = mapInstance[1][1].agents[1];
  }
}

public World goToWorld() {
  println("goToWorld() started");
  if (worldInstance == null) {
    createWorldMap();
    worldInstance = mapInstance[1][1];
    currentScreen = worldInstance;
    println("world instance created");
  }
  println("goToWorld ended");
  return worldInstance;
} 


public void keyPressed() {
  println("keyPressed() started \n...");
  if (keyPressed == true && (key == 's' || key == 'S' || keyCode == DOWN)) {
    keyDirection.y = 1;
  } else if (keyPressed == true  && (key == 'w' || key == 'W' || keyCode == UP)) {
    keyDirection.y = -1;
  } else if (keyPressed == true && (key == 'a' || key == 'A' || keyCode == LEFT)) {
    keyDirection.x = -1;
  } else if (keyPressed == true  && (key == 'd' || key == 'D' || keyCode == RIGHT)) {
    keyDirection.x = 1;
  }

  if (key == 'R') {
    currentScreen = new TitleMenu();
    createWorldMap();
    worldInstance = mapInstance[1][1];
    currentScreen = worldInstance;
  }
  if (key == 'm' || key == 'M') {
    if (!wholeMap) {
      wholeMap = true;
    } else {
      wholeMap= false;
    }
  }
  if (key == '1') {
    worldInstance.agents[0].getInventory().setSelected(0);
  }
  if (key == '2') {
    worldInstance.agents[0].getInventory().setSelected(1);
  }
  if (key == '3') {
    worldInstance.agents[0].getInventory().setSelected(2);
  }
  if (key == '4') {
    worldInstance.agents[0].getInventory().setSelected(3);
  }
  if (key == '5') {
    worldInstance.agents[0].getInventory().setSelected(4);
  }
  if (key == ESC) {
    key=0;
  }
  println("keyPressed() ended");
}

public void keyReleased() {
  println("keyReleased() started \n...");
  if ((key == 's' || key == 'S' || keyCode == DOWN)) {
    keyDirection.y = 0;
  } else if ((key == 'w' || key == 'W' || keyCode == UP)) {
    keyDirection.y = 0;
  } else if ((key == 'a' || key == 'A' || keyCode == LEFT)) {
    keyDirection.x = 0;
  } else if ((key == 'd' || key == 'D' || keyCode == RIGHT)) {
    keyDirection.x = 0;
  }
  if (key == ' ')
    controller.p_keyboardStimKey = false;
  key = 0;
  println("keyReleased() ended \n...");
}

public void mousePressed() {
  println("mousePressed() started");
  mouseStatePressed = true;
  println("mousepressed() ended");
}

public void mouseReleased() {
  println("mousePressed() started");
  mouseStateReleased = true;
  println("mousepressed() ended");
}
class Percept {
  PVector location;
  String type;

  Percept(String isType, PVector isHere) {
    location = isHere;
    type = isType;
  }

  public boolean same(Percept p) {
    if (location.equals(p.location) && type.equals(p.type))
    {
      return true;
    } else {
      return false;
    }
  }
}





ControlIO control;
Configuration config;
ControlDevice gpad;
ControlDevice gpad1;
ControlDevice gpad2;

class Controller {
  ControlDevice gpad;
  boolean takeKeyboard = false;

  char previousKey;
  int previousKeyCode;

  boolean padReady;

  boolean p_gpadMapKey; 
  boolean p_gpadRestartKey;
  boolean p_gpadPauseKey;
  boolean p_gpadPunchKey;
  boolean p_gpadItemSelectKey;
  boolean p_gpadStimKey;

  boolean p_gpadSelectRight;
  boolean p_gpadSelectLeft;

  boolean p_gpadSprint;
  boolean p_gpadShoot;
  float p_sprint_shoot;

  boolean p_keyboardStimKey;
  boolean p_keyboadShoot; 

  PVector p_offset = new PVector();

  Controller() {
  }

  Controller(ControlDevice set_gpad) {
    gpad = set_gpad;
  }

  Controller(ControlDevice set_gpad, boolean setTakeKeyboard) {
    gpad = set_gpad;
    takeKeyboard = setTakeKeyboard;
  }

  public String menuControl() {
    String pressed = "nothing";
    if (keyPressed == true && (key != previousKey || keyCode != previousKeyCode)  && (key == 'w' || key == 'W' || keyCode == UP || keyCode == LEFT)) {
      pressed = "up";
    } else if (gpad != null && gpad.getSlider("verticalMove").getValue() < 0 && padReady) {
      pressed = "up";
      padReady = false;
    } else if (keyPressed == true && (key != previousKey || keyCode != previousKeyCode)  && (key == 's' || key == 'S' || keyCode == DOWN || keyCode == RIGHT)) {
      pressed = "down";
    } else if (gpad != null && gpad.getSlider("verticalMove").getValue() > 0 && padReady) {
      pressed = "down";
      padReady = false;
    } else if (gpad != null && controller.shoot()) {
      pressed = "select";
    } else if (keyPressed == true && (key != previousKey || keyCode != previousKeyCode)  && (key == ENTER || key == ' ')) {
      pressed = "select";
    }

    if (gpad != null && gpad.getSlider("verticalMove").getValue() == 0) {
      padReady = true;
    }

    previousKey = key;
    previousKeyCode = keyCode;

    return pressed;
  }

  public PVector getOffsetScreen() {
    if (gpad != null) {
      PVector offset = p_offset;
      float speed = 1;

      if (offset.x > gpad.getSlider("horizontalShoot").getValue()*width/8) {
        offset.x -= speed;
      } else if (offset.x < gpad.getSlider("horizontalShoot").getValue()*width/8) {
        offset.x += speed;
      }

      if (offset.y > gpad.getSlider("verticalShoot").getValue()*width/8) {
        offset.y -= speed;
      } else if (offset.y < gpad.getSlider("verticalShoot").getValue()*width/8) {
        offset.y += speed;
      } 

      if (gpad.getSlider("horizontalShoot").getValue() == 0 && (offset.x > -speed && offset.x < speed)) {
        offset.x = 0;
      }

      if (gpad.getSlider("verticalShoot").getValue() == 0 && (offset.y > -speed && offset.y < speed)) {
        offset.y = 0;
      }


      if (gpad.getSlider("horizontalShoot").getValue() > 0)
        offset.x = constrain(offset.x+speed, -width/8, width/8);
      else if (gpad.getSlider("horizontalShoot").getValue() < 0)
        offset.x = constrain(offset.x-speed, -width/8, width/8);

      if (gpad.getSlider("verticalShoot").getValue() > 0)
        offset.y = constrain(offset.y+speed, -height/8, height/8);
      else if (gpad.getSlider("verticalShoot").getValue() < 0)
        offset.y = constrain(offset.y-speed, -height/8, height/8);

      return offset;
    } else 
      return new PVector();
  }

  public boolean shoot() {
    if (gpad != null && gpad.getButton("shoot").pressed() && !p_gpadShoot) {
      p_gpadShoot = true;
      p_keyboadShoot = true;
      return true;
    }

    if (gpad != null)
      p_gpadShoot = gpad.getButton("shoot").pressed();

    return false;
  }


  public boolean sprint() {
    if (takeKeyboard && mousePressed && mouseButton == RIGHT || (gpad != null && gpad.getButton("sprint").pressed()))
      return true;
    else
      return false;
  }


  public boolean reload() {
    if (takeKeyboard && keyPressed == true && key == 'r' || (gpad != null && gpad.getButton("reload").pressed())) {
      return true;
    }
    return false;
  }

  public boolean drop() {
    if (takeKeyboard &&keyPressed == true && key == 'f' || ( gpad != null && gpad.getButton("drop").pressed())) {
      return true;
    }
    return false;
  }

  public boolean map() {
    if (gpad != null && gpad.getButton("map").pressed() && !p_gpadMapKey) {
      p_gpadMapKey = gpad.getButton("map").pressed();
      return true;
    }
    if (gpad != null)
      p_gpadMapKey = gpad.getButton("map").pressed();

    //    if (key == ' ')
    //      p_keyboardStimKey = true;
    return false;
  }

  public boolean stim() {
    if (gpad != null && gpad.getButton("stim").pressed() && !p_gpadStimKey) {
      p_gpadStimKey = gpad.getButton("stim").pressed();
      return true;
    } else if (key == ' ' && !p_keyboardStimKey && takeKeyboard) {
      p_keyboardStimKey = true;
      return true;
    }
    if (gpad != null)
      p_gpadStimKey = gpad.getButton("stim").pressed();
      
      p_keyboardStimKey = keyPressed;
    return false;
  }

  public String itemselect() {
    String pressed = "nothing";
    if (gpad != null && gpad.getHat("itemselect").getX() > 0.0f) {
      if (p_gpadItemSelectKey) {
        pressed = "right";
        p_gpadItemSelectKey = false;
      }
    } 
    if (gpad != null && gpad.getHat("itemselect").getX() < 0.0f) {
      if (p_gpadItemSelectKey) {
        pressed = "left";
        p_gpadItemSelectKey = false;
      }
    }
    if (gpad != null && gpad.getHat("itemselect").getX() == 0.0f) {
      p_gpadItemSelectKey = true;
    }

    if (gpad != null) {
      if (gpad.getSlider("select").getValue() > 0 && !p_gpadSelectLeft) {
        pressed = "left";
      } else if (gpad.getSlider("select").getValue() < 0 && !p_gpadSelectRight) {
        pressed = "right";
      }

      if (gpad.getSlider("select").getValue() > 0) {
        p_gpadSelectLeft = true;
      } else
        p_gpadSelectLeft = false;

      if (gpad.getSlider("select").getValue() < 0) {
        p_gpadSelectRight = true;
      } else
        p_gpadSelectRight = false;
    }

    return pressed;
  }

  public boolean restart() {
    if (gpad != null && gpad.getButton("restart").pressed() && p_gpadRestartKey == false) {
      p_gpadRestartKey = gpad.getButton("restart").pressed();
      return true;
    }
    if (gpad != null)
      p_gpadRestartKey = gpad.getButton("restart").pressed();
    return false;
  }

  public boolean pause() {
    if (gpad != null && gpad.getButton("pause").pressed() && p_gpadPauseKey == false) {
      p_gpadPauseKey = gpad.getButton("pause").pressed();
      return true;
    } else if (keyPressed && key == ESC) {
      return true;
    }
    if (gpad != null)
      p_gpadPauseKey = gpad.getButton("pause").pressed();
    return false;
  }
}

public void mouseWheel(MouseEvent event) {
  float e = event.getCount();
  player1.getInventory().setSelected((player1.getInventory().selected + 0.5f*e));
}

class Entity {
  String type;
  private PVector pos = new PVector();
  protected int w, h;
  PVector velocity = new PVector();

  boolean transfered;
  boolean readyForTransfer;

  boolean remove = false;

  public Entity() {
  }

  public PVector getPos() {
    return pos;
  }

  public int getH() {
    return h;
  }

  public int getW() {
    return w;
  }

  public void setPos(PVector v) {
    pos = new PVector(v.x, v.y);
  }

  public void posAdd(PVector v) {
    pos.add(v);
  }

  public void render() {
  }
  public void pre() {
  }

  public void update(float dt) {
  }
}

class Human extends Entity {
  private Boolean isAlive;
  int hp = 2;
  Timer timer;
  //private Rifle heldObject;

  int damaged;

  int tilePos[][];
  int skin;

  int maxStamina;
  float currStamina;

  float dir;

  int frames;
  int spriteFrame;

  int currentMemoryPoint;
  Percept memory[];

  int delay;

  int currentSectorX = 1;
  int currentSectorY = 1;

  float accuracy = 1;

  Inventory inventory;

  public Human() {
  }

  public void setReadyForTransfer(boolean set) {
    readyForTransfer = set;
  }

  public int getCurrentSectorX() {
    return currentSectorX;
  }
  public int getCurrentSectorY() {
    return currentSectorY;
  }
  public void setCurrentSectorX(int x) {
    currentSectorX = x;
  }
  public void setCurrentSectorY(int y) {
    currentSectorY = y;
  }


  public Human(float x, float y) {
    type = "human";
    isAlive = true;

    inventory = new Inventory(5); 



    memory = new Percept[10];
    for (int i = 0; i < memory.length; i++) {
      memory[i] = new Percept(" ", new PVector(0, 0));
    }





    setPos(new PVector(x, y));

    velocity = new PVector(0, 0);
    w = 8;
    h = 12;

    tilePos = new int[2][5];
    setTilePos();

    skin = color(255, 0, 0);

    maxStamina = 300;
    currStamina = maxStamina;

    delay = (int)random(0, 120);
  }

  public void transferSector() {
  }

  protected void setTilePos() {

    //    tilePos[0][0] = (int)(pos.x)/16;
    //    tilePos[1][0] = (int)(pos.y)/16;
    //
    //    tilePos[0][1] = (int)(pos.x+w)/16;
    //    tilePos[1][1] = (int)(pos.y)/16;
    //
    //    tilePos[0][2] = (int)(pos.x+w)/16;
    //    tilePos[1][2] = (int)(pos.y+h)/16;
    //
    //    tilePos[0][3] = (int)(pos.x)/16;
    //    tilePos[1][3] = (int)(pos.y+h)/16;
    //
    //    tilePos[0][4] = (int)(pos.x-w/2)/16;
    //    tilePos[1][4] = (int)(pos.y-h/2)/16;
  }

  public void remember(String type, int x, int y) {
    if (!checkInMemory(type, x, y)) {
      if (currentMemoryPoint > memory.length-1) {
        currentMemoryPoint = 0;
      }
      memory[currentMemoryPoint] = new Percept(type, new PVector(x, y));
      currentMemoryPoint++;
    }
  }

  public Inventory getInventory() {
    return inventory;
  }

  public float getAccuracy() {
    accuracy = 90.0f*((float)getStamina()/getMaxStamina())-90.0f;
    if (getHeldObject() != null) {
      Item gun =  getHeldObject();
      accuracy -= gun.maxAccuracy;
    }
    return accuracy;
  }

  public void openDoor() {
    int cornerX = (int)((getPos().x-w/2)/16);
    int cornerY = (int)((getPos().y-h/2)/16);
    //    
    //     float dist = dist(getPos().x, getPos().y, player1.getPos().x, player1.getPos().y);
    //        if (dist < 50) {
    //          open_door.amp(map(50-dist, 0, 50, 0.0001, 1.0));
    //          open_door.play();
    //        }

    for (int x = cornerX-1; x <= cornerX + 1; x++)
      for (int y = cornerY-1; y <= cornerY + 1; y++) {
        if (x >= 0 && x <= 51 && y >= 0 && y <= 51)
          if (worldInstance.tiles[x][y].type == "door") {
            worldInstance.tiles[x][y].sprite = spriteHolderInstance.doorOpen;
            worldInstance.collisionMask[x][y] = false;
          }
      }
  }

  public void shoot() {
  }

  public Timer getTimer() {
    return timer;
  }

  public void setTimer(Timer set) {
    timer = set;
  }

  public void setPos(PVector v) {
    super.setPos(v);
  }

  public int getHP() {
    return hp;
  }

  public void setHP(int set) {
    hp = set;
  }

  public boolean checkInMemory(String type, int x, int y) {
    for (int i = 0; i < memory.length; i++) {
      if (memory[i].same(new Percept(type, new PVector(x, y)))) {
        return true;
      }
    }
    return false;
  }

  public void drop () {
    if (getHeldObject() != null) {
      getHeldObject().setPos(new PVector(getPos().x-16, getPos().y-16));
      getHeldObject().held = false;
      worldInstance.items.add(getHeldObject());
      setHeldObject(null);
    }
  }

  public void render() {
    if (isAlive()) {
      //fill(skin);
      //rect(getPos().x-w/2, getPos().y-h/2, w, h);

      frames++;
      if (frames > 10) {
        spriteFrame++;

        if (spriteFrame > 7) {
          spriteFrame = 0;
        }
        frames = 0;
      }


      image(spriteHolderInstance.buffManIdle[spriteFrame], getPos().x-w/2, getPos().y-h/2, 16, 16);

      if (getInventory().getSelected() != null) {
        image(getHeldObject().sprite, getPos().x-16, getPos().y-16, 32, 32);
      }
    }

    Timer timer = getTimer();
    if (timer != null) {
      timer.render(getPos());
    }
  }

  public void kill() {
    drop ();
    isAlive = false;
  }

  public float getStamina() {
    return currStamina;
  }

  public boolean staminaIsFull() {
    if (currStamina == maxStamina) {
      return true;
    }
    return false;
  }

  public String getType() {
    return type;
  }

  public void setType(String set) {
    type = set;
  }

  public int getMaxStamina() {
    return maxStamina;
  }

  public boolean useStamina(int value) {
    if (currStamina > 0) {
      currStamina -= value;
      return true;
    }
    return false;
  }

  public void setStamina(int set) {
    currStamina = set;
  }

  public void regenStamina(float dt) {
    if (currStamina < maxStamina) {
      currStamina += 1*100*dt;
    } else 
      setStamina(maxStamina);
  }  

  public boolean isAlive() {
    return isAlive;
  }

  public Item getHeldObject() {
    return getInventory().getSelected();
  }

  public void setHeldObject(Item set) {
    getInventory().setItem(getInventory().selected, set);
  }

  public void update(float dt) {
    Human obj = worldInstance.getNearest("human", this);

    if (obj != null && dist(getPos().x, getPos().y, obj.getPos().x, obj.getPos().y) < 8)
    {
      float push = atan2(obj.getPos().y-(getPos().y), obj.getPos().x-(getPos().x));

      velocity.x = -cos(push)*1.5f;
      velocity.y = -sin(push)*1.5f;
    } else {
      Human target = worldInstance.getNearest("zombie", this);

      if (target != null) {
        dir = atan2(target.getPos().y-(getPos().y), target.getPos().x-(getPos().x));
        PVector shootDir = new PVector(-(getPos().x - target.getPos().x), -(target.getPos().y-getPos().y));

        if (getInventory().getSelected() != null && frameCount%60 == 0) {




          Item gun;
          if (getHeldObject() != null)
            gun =  getHeldObject();
          else 
            gun =  new Item(getPos().x, getPos().y, 0, 100, 1, spriteHolderInstance.rifle);
          float aim = getAccuracy();


          Bullet instance;

          for (int x = 0; x < gun.volley; x++) {
            instance = new Bullet(getPos(), shootDir, aim);
            worldInstance.worldObjs.add(instance);
          }
        }
        velocity.x = -cos(dir)*16*8*dt;
        velocity.y = -sin(dir)*16*8*dt;
      } else {
        ArrayList<Percept> seenDoors = worldInstance.LOSAI(this);

        //Percept p = getNearestP(seenDoors, this);

        int rndStart = (int)random(0, seenDoors.size ());


        if (frameCount%(180 + delay) == 0) {
          for (int i = 0; i < seenDoors.size (); i++) {

            if (rndStart >= seenDoors.size ()) {
              rndStart = 0;
            }

            Percept p = seenDoors.get(rndStart);
            if (!checkInMemory(p.type, (int)p.location.x, (int)p.location.y)) {

              dir = -atan2(p.location.y*16+8-(getPos().y), p.location.x*16+8-(getPos().x));

              velocity.x = cos(dir)*16*5*dt;
              velocity.y = -sin(dir)*16*5*dt;
            }
            rndStart++;
          }
        }
      }
    }


    posAdd(worldInstance.checkCol(this));

    //check Current location;
    int currX = (int)((getPos().x)/16);
    int currY = (int)((getPos().y)/16);
    if (worldInstance.tiles[currX][currY].type == "door") {
      remember("door", currX, currY);
    }

    setTilePos();

    Timer timer = getTimer();
    if (timer != null) {
      timer.update(this, dt); 
      if (timer.remove) {
        setTimer(null);
      }
    }
  }


  public Percept getNearestP(ArrayList<Percept> listP, Entity fromMe) {
    PVector fromHere = fromMe.getPos();
    float min = 12*16;
    Percept p = null;
    Percept obj = null;

    for (int i = 0; i < listP.size (); i++) {
      obj = listP.get(i);
      if (!checkInMemory(obj.type, (int)obj.location.x, (int)obj.location.y)) {
        float curr = dist(obj.location.x, obj.location.y, fromHere.x, fromHere.y);

        if (curr != 0 && (curr < min || min == 0)) {
          //if (LOS(fromMe, agents[i])) {
          min = curr;
          p = obj;
          //}
        }
      }
    }
    return p;
  }
}

class HumanDecorater extends Human {
  protected Human humanRef;

  HumanDecorater() {
  }

  public void kill() {
    humanRef.kill();
  }

  public int getCurrentSectorX() {
    return humanRef.getCurrentSectorX();
  }
  public int getCurrentSectorY() {
    return humanRef.getCurrentSectorY();
  }
  public void setCurrentSectorX(int x) {
    humanRef.setCurrentSectorX(x);
  }
  public void setCurrentSectorY(int y) {
    humanRef.setCurrentSectorY(y);
  }

  public void setStamina(int set) {
    humanRef.setStamina(set);
  }

  public PVector getPos() {
    return humanRef.getPos();
  }

  public void setReadyForTransfer(boolean set) {
    humanRef.setReadyForTransfer(set);
  }

  public Timer getTimer() {
    return humanRef.getTimer();
  }

  public void setTimer(Timer set) {
    humanRef.setTimer(set);
  }

  public Inventory getInventory() {
    return humanRef.getInventory();
  }

  public void posAdd(PVector v) {
    humanRef.posAdd(v);
  }

  public void posSet(PVector v) {
    humanRef.setPos(v);
  }

  public int getH() {
    return humanRef.getH();
  }

  public String getType() {
    return humanRef.getType();
  }

  public int getW() {
    return humanRef.getW();
  }

  public float getAccuracy() {
    return humanRef.getAccuracy();
  }

  public boolean isAlive() {
    return humanRef.isAlive();
  }

  public void setType(String set) {
    humanRef.setType(set);
  }

  public Item getHeldObject() {
    return humanRef.getHeldObject();
  }

  public void dropItem () {
    humanRef.drop();
  }

  public void setHeldObject(Item r) {
    humanRef.setHeldObject(r);
  }

  public float getStamina() {
    return humanRef.getStamina();
  }

  public boolean staminaIsFull() {
    return humanRef.staminaIsFull();
  }

  public int getMaxStamina() {
    return humanRef.getMaxStamina();
  }

  public int getHP() {
    return humanRef.getHP();
  }

  public void setHP(int set) {
    humanRef.setHP(set);
  }

  public void regenStamina(float dt) {
    humanRef.regenStamina(dt);
  }

  public boolean useStamina(int value) {
    return humanRef.useStamina(value);
  }
}


class Zombie extends HumanDecorater {
  float minDist;
  float dir;

  public Zombie(Human host) {
    type = "zombie";

    minDist = 8;

    humanRef = host;

    //heldObject = humanRef.heldObject;
    w = humanRef.w;
    h = humanRef.h;
    tilePos = humanRef.tilePos;
    humanRef.skin = color(0, 255, 0);
    velocity = new PVector(0, 0);

    memory = new Percept[10];
    for (int i = 0; i < memory.length; i++) {
      memory[i] = new Percept(" ", new PVector(0, 0));
    }
  }



  public void render() {
    if (isAlive()) {
      image(spriteHolderInstance.hz0, getPos().x-w/2, getPos().y-h/2, 16, 16);
    }
    //worldInstance.LOS(this, worldInstance.agents[0]);
  }
  public void update(float dt) {
    if (isAlive()) {

      //ckeck distance to other zombies if to close move away
      Human obj = worldInstance.getNearest("zombie", this);

      if (obj != null && dist(getPos().x, getPos().y, obj.getPos().x, obj.getPos().y) < minDist)
      {
        float push = atan2(obj.getPos().y-(getPos().y), obj.getPos().x-(getPos().x));

        velocity.x = -cos(push)*1.5f;
        velocity.y = -sin(push)*1.5f;
      } else {

        Human target = worldInstance.getNearest("human", this);
        if (target != null) {
          dir = -atan2(target.getPos().y-(humanRef.getPos().y), target.getPos().x-(humanRef.getPos().x));
        } else {
          //here
          ArrayList<Percept> seenDoors = worldInstance.LOSAI(this);

          //Percept p = getNearestP(seenDoors, this);

          int rndStart = (int)random(0, seenDoors.size ());

          if (frameCount%(180 + delay) == 0) {
            for (int i = 0; i < seenDoors.size (); i++) {

              if (rndStart >= seenDoors.size ()) {
                rndStart = 0;
              }

              Percept p = seenDoors.get(rndStart);
              if (!checkInMemory(p.type, (int)p.location.x, (int)p.location.y)) {

                dir = -atan2(p.location.y*16+8-(getPos().y), p.location.x*16+8-(getPos().x));

                velocity.x = cos(dir)*16*5*dt;
                velocity.y = -sin(dir)*16*5*dt;
              }
              rndStart++;
            }
          }

          //here
        }
        velocity.x = cos(dir)*16*6*dt;
        velocity.y = -sin(dir)*16*6*dt;
      }
    }

    if (getHeldObject() != null) {
      dropItem ();
    }

    posAdd(worldInstance.checkCol(this));

    setTilePos();
  }
}


class Player extends HumanDecorater {
  int frames;
  int spriteFrame;

  Controller controller;

  float speed;

  boolean p_gpadShootKey;

  PVector p_shootDir;

  boolean transfered;

  int currentSectorX = 1;
  int currentSectorY = 1;

  public Player(Human host) {
    type = "human";
    setHP(6);
    controller = new Controller(gpad);

    humanRef = host;
    //isAlive = humanRef.isAlive;

    transfered = false;

    tilePos = humanRef.tilePos;
    skin = humanRef.skin; 
    humanRef.skin = color(0, 0, 255);
    velocity = new PVector(0, 0);
    speed = 4;

    frames = 0;
    spriteFrame = 0;

    p_gpadShootKey = false;
    p_shootDir = new PVector();
  }

  public Player(Human host, Controller setController) {
    type = "human";
    humanRef = host;
    setHP(6);

    controller = setController;

    //isAlive = humanRef.isAlive;

    transfered = false;

    tilePos = humanRef.tilePos;
    skin = humanRef.skin; 
    humanRef.skin = color(0, 0, 255);
    velocity = new PVector(0, 0);
    speed = 4;

    frames = 0;
    spriteFrame = 0;

    p_gpadShootKey = false;
    p_shootDir = new PVector();
  }

  public void kill() {
    humanRef.kill();
    currentScreen = new CustomScreen("You got shot!");
  }



  public void transferSector() {
    println("transfer player from sector " + currentSectorX + currentSectorY);
    int cornerX = (int)((getPos().x-w/2)/16);
    int cornerY = (int)((getPos().y-h/2)/16);

    if (player2Active) {

      if (cornerX-1 <= 0 && getCurrentSectorX() != 0 && !transfered)
      {      
        println("transfer -1x, current x: " + getCurrentSectorX());
        player1.transfered = true;
        player2.transfered = true;

        player1.posAdd(new PVector(49*16, 0));
        player2.posAdd(new PVector(49*16, 0));

        mapInstance[currentSectorX-1][currentSectorY].agents[0] = mapInstance[currentSectorX][currentSectorY].agents[0];
        mapInstance[currentSectorX-1][currentSectorY].agents[1] = mapInstance[currentSectorX][currentSectorY].agents[1];
        worldInstance = mapInstance[currentSectorX-1][currentSectorY];
        currentScreen = mapInstance[currentSectorX-1][currentSectorY];
        player1.setCurrentSectorX(getCurrentSectorX()-1);
        player2.setCurrentSectorX(getCurrentSectorX()-1);
        currentSectorX--;
      } else if (cornerX+1 >= 51 && getCurrentSectorX() < 2 && !transfered)
      {
        println("transfer +1x, current x: " + getCurrentSectorX());
        player1.transfered = true;
        player2.transfered = true;

        player1.posAdd(new PVector(-49*16, 0));
        player2.posAdd(new PVector(-49*16, 0));

        mapInstance[currentSectorX+1][currentSectorY].agents[0] = mapInstance[currentSectorX][currentSectorY].agents[0];
        mapInstance[currentSectorX+1][currentSectorY].agents[1] = mapInstance[currentSectorX][currentSectorY].agents[1];
        worldInstance = mapInstance[currentSectorX+1][currentSectorY];
        currentScreen = mapInstance[currentSectorX+1][currentSectorY];
        player1.setCurrentSectorX(getCurrentSectorX()+1);
        player2.setCurrentSectorX(getCurrentSectorX()+1);
        currentSectorX++;
      } else if (cornerY+1 >= 51 && getCurrentSectorY() != 2 && !transfered)
      {
        println("transfer +1y, current y: " + getCurrentSectorY());
        player1.transfered = true;
        player2.transfered = true;

        player1.posAdd(new PVector(0, -49*16));
        player2.posAdd(new PVector(0, -49*16));

        mapInstance[currentSectorX][currentSectorY+1].agents[0] = mapInstance[currentSectorX][currentSectorY].agents[0];
        mapInstance[currentSectorX][currentSectorY+1].agents[1] = mapInstance[currentSectorX][currentSectorY].agents[1];
        worldInstance = mapInstance[currentSectorX][currentSectorY+1];
        currentScreen = mapInstance[currentSectorX][currentSectorY+1];
        player1.setCurrentSectorY(getCurrentSectorY()+1);
        player2.setCurrentSectorY(getCurrentSectorY()+1);
        currentSectorY++;
      } else if (cornerY-1 <= 0 && getCurrentSectorY() != 0 && !transfered)
      {
        println("transfer -1y, current y: " + getCurrentSectorY() + "and cornerY is: " + cornerY);
        player1.transfered = true;
        player2.transfered = true;

        player1.posAdd(new PVector(0, 49*16));
        player2.posAdd(new PVector(0, 49*16));

        mapInstance[currentSectorX][currentSectorY-1].agents[0] = mapInstance[currentSectorX][currentSectorY].agents[0];
        mapInstance[currentSectorX][currentSectorY-1].agents[1] = mapInstance[currentSectorX][currentSectorY].agents[1];
        worldInstance = mapInstance[currentSectorX][currentSectorY-1];
        currentScreen = mapInstance[currentSectorX][currentSectorY-1];
        player1.setCurrentSectorY(getCurrentSectorY()-1);
        player2.setCurrentSectorY(getCurrentSectorY()-1);
        currentSectorY--;
      } else {
        println("transfer denied");
      }
    } else {
      if (cornerX-1 <= 0 && getCurrentSectorX() != 0 && !transfered)
      {      
        println("transfer -1x, current x: " + getCurrentSectorX());
        //transfered = true;
        mapInstance[currentSectorX-1][currentSectorY].agents[0] = mapInstance[currentSectorX][currentSectorY].agents[0]; 
        worldInstance = mapInstance[currentSectorX-1][currentSectorY];
        currentScreen = mapInstance[currentSectorX-1][currentSectorY];
        setCurrentSectorX(getCurrentSectorX()-1);
        currentSectorX--;
        player1.posAdd(new PVector(49*16+8, 0));
      } else if (cornerX+1 >= 51 && getCurrentSectorX() < 2 && !transfered)
      {
        println("transfer +1x, current x: " + getCurrentSectorX());
        //transfered = true;
        mapInstance[currentSectorX+1][currentSectorY].agents[0] = mapInstance[currentSectorX][currentSectorY].agents[0];
        worldInstance = mapInstance[currentSectorX+1][currentSectorY];
        currentScreen = mapInstance[currentSectorX+1][currentSectorY];
        setCurrentSectorX(getCurrentSectorX()+1);
        currentSectorX++;
        player1.posAdd(new PVector(-49*16-8, 0));
      } else if (cornerY+1 >= 51 && getCurrentSectorY() != 2 && !transfered)
      {
        println("transfer +1y, current y: " + getCurrentSectorY());
        //transfered = true;
        player1.posAdd(new PVector(0, -49*16-8));

        mapInstance[currentSectorX][currentSectorY+1].agents[0] = mapInstance[currentSectorX][currentSectorY].agents[0];
        worldInstance = mapInstance[currentSectorX][currentSectorY+1];
        currentScreen = mapInstance[currentSectorX][currentSectorY+1];
        setCurrentSectorY(getCurrentSectorY()+1);
        currentSectorY++;
      } else if (cornerY-1 <= 0 && getCurrentSectorY() != 0 && !transfered)
      {
        println("transfer -1y, current y: " + getCurrentSectorY() + "and cornerY is: " + cornerY);
        //transfered = true;
        player1.posAdd(new PVector(0, 49*16+8));
        mapInstance[currentSectorX][currentSectorY-1].agents[0] = mapInstance[currentSectorX][currentSectorY].agents[0];
        worldInstance = mapInstance[currentSectorX][currentSectorY-1];
        currentScreen = mapInstance[currentSectorX][currentSectorY-1];
        setCurrentSectorY(getCurrentSectorY()-1);
        currentSectorY--;
      } else {
        println("transfer denied");
      }
    }
  }

  public void render() {
    //humanRef.render();
    frames++;

    if (frames > 10) {
      spriteFrame++;

      if (spriteFrame > 1) {
        spriteFrame = 0;
      }
      frames = 0;
    }

    pushMatrix();
    imageMode(CENTER);
    translate(getPos().x, getPos().y);

    if (velocity.x < 0) {
      scale(-1, 1);
    }


    if (getType() == "human") {
      if (spriteFrame == 0) {
        image(spriteHolderInstance.img0, 0, 0, 16, 16);
      } else if (spriteFrame == 1) {
        image(spriteHolderInstance.img1, 0, 0, 16, 16);
      }
    } else if (getType() == "zombie") {
      if (spriteFrame == 0) {
        image(spriteHolderInstance.img2, 0, 0, 16, 16);
      } else if (spriteFrame == 1) {
        image(spriteHolderInstance.img3, 0, 0, 16, 16);
      }
    }

    if (getHeldObject() != null) {
      image(getHeldObject().sprite, 0, 0-1, 32, 32);
    }

    Timer timer = getTimer();
    if (timer != null) {
      timer.render();
    }
    popMatrix();
    imageMode(CORNER);
  }
  public void update(float dt) {

    if (controller.gpad != null && (controller.gpad.getSlider("verticalMove").getValue() != 0  || controller.gpad.getSlider("horizontalMove").getValue() != 0)) {
      velocity = new PVector(controller.gpad.getSlider("horizontalMove").getValue(), controller.gpad.getSlider("verticalMove").getValue());
    } else if (controller.takeKeyboard) {
      velocity = new PVector(keyDirection.x, keyDirection.y);
    } else
      velocity = new PVector();
    velocity.normalize();
    velocity.mult(speed*16*dt);

    posAdd(worldInstance.checkCol(this));

    if (getPos().x > 32 &&  getPos().x < 50*16 && getPos().y > 32 &&  getPos().y < 50*16) {
      player1.transfered = false;
      if (player2Active)
        player2.transfered = false;
    }

    if (controller.takeKeyboard && pmouseX != mouseX && pmouseY != mouseY) {
      if (wholeMap) {
        p_shootDir = new PVector(mouseX-getPos().x, -(mouseY-getPos().y));
        p_shootDir = new PVector(-(width/2-mouseX), (height/4-mouseY));
        //p_shootDir.rotate(180);
      } else {
        p_shootDir = new PVector(-(width/2-mouseX), height/2-mouseY);
      }
    } else if (controller.gpad != null && (controller.gpad.getSlider("verticalShoot").getValue() != 0  || controller.gpad.getSlider("horizontalShoot").getValue() != 0)) {
      PVector dir = new PVector(controller.gpad.getSlider("horizontalShoot").getValue(), -controller.gpad.getSlider("verticalShoot").getValue());
      p_shootDir = dir;
    }

    if (controller.takeKeyboard && mouseStatePressed && mouseButton == LEFT || (controller.gpad != null && (controller.shoot() && !p_gpadShootKey))) {
      Item gun;
      if (getHeldObject() != null)
        gun =  getHeldObject();
      else 
        gun =  new Item(getPos().x, getPos().y, 0, 100, 1, spriteHolderInstance.rifle);

      if (gun.type == "gun" && gun.stock > 0) {
        float aim = getAccuracy();

        if (getStamina() > 0 && useStamina(gun.staminaPrice)) {
          Bullet instance;

          for (int x = 0; x < gun.volley; x++) {
            instance = new Bullet(getPos(), p_shootDir, aim);

            if (getHeldObject() == null) {
              instance.type = "punch";
            }

            worldInstance.worldObjs.add(instance);
          }
        }
        gun.stock--;
      } else {
        gun.action(this);
      }
      mouseStatePressed = false;
    } else if (controller.reload() && getHeldObject() != null && getHeldObject().type == "gun" && getHeldObject().stock < getHeldObject().maxStock) {
      Item obj = getInventory().findStockInInventory("ammo");
      if (obj != null)
        setTimer(new Timer(30, 'r', this));

      //getHeldObject().reload(this);
    } 

    if (controller.drop() && getHeldObject() != null) {
      dropItem();
    }

    if (controller.stim()) {
      Item obj = getInventory().findStockInInventory("greenpills");
      if (obj != null)
        obj.action(this);
    }

    speed = 4;
    if (velocity.mag() == 0) {
      regenStamina(dt);
      regenStamina(dt);
    }

    if (controller.sprint()) {
      if (useStamina(1)) {
        speed += 4;
      }
    } else {
      regenStamina(dt);
    }

    getInventory().update();

    Timer timer = getTimer();
    if (timer != null) {
      timer.update(this, dt); 
      if (timer.remove) {
        setTimer(null);
      }
    }
  }
}

class SpriteHolder
{
  PImage img0;
  PImage img1;
  PImage img2;
  PImage img3;

  PImage h0;
  PImage hz0;

  PImage buffManIdle[];

  PImage grass;
  PImage lowerWall;
  PImage vRoad;
  PImage hRoad;
  PImage floor;

  PImage rifle;
  PImage shotgun;
  PImage ammo;

  PImage medkit;
  PImage gpills;

  PImage doorClosed;
  PImage doorOpen;

  PImage heart;
  PImage halfheart;

  public SpriteHolder() {
    img0 = loadImage("0.png");
    img1 = loadImage("1.png");
    img2 = loadImage("z0.png");
    img3 = loadImage("z1.png");

    buffManIdle = new PImage[8];
    buffManIdle[0] = loadImage("h0.png");
    buffManIdle[1] = loadImage("h1.png");
    buffManIdle[2] = loadImage("h2.png");
    buffManIdle[3] = loadImage("h3.png");
    buffManIdle[4] = loadImage("h4.png");
    buffManIdle[5] = loadImage("h3.png");
    buffManIdle[6] = loadImage("h2.png");
    buffManIdle[7] = loadImage("h1.png");

    h0 = loadImage("h0.png");
    hz0 = loadImage("hz0.png");

    grass = loadImage("grassTile.png");
    grass.resize(0, 16);
    lowerWall = loadImage("wall_brick.png");
    lowerWall.resize(0, 16);
    vRoad = loadImage("road_asphalt_ns.png");
    vRoad.resize(0, 16);
    hRoad = loadImage("road_asphalt_ew.png");
    hRoad.resize(0, 16);
    floor = loadImage("floor_planks.png");
    floor.resize(0, 16);

    doorClosed = loadImage("wooden_door_closed.png");
    doorOpen = loadImage("wooden_door_open.png");

    rifle = loadImage("item_hunting_rifle.png");
    shotgun = loadImage("shotgun.png");
    ammo = loadImage("ammo.png");

    medkit = loadImage("medkit.png");
    gpills = loadImage("greenPills.png");

    heart = loadImage("heart.png");
    halfheart = loadImage("halfHeart.png");
  }
}


class Item extends Entity {
  boolean held;
  float maxAccuracy;
  int volley;
  int staminaPrice;
  int stock;
  int maxStock = 20;

  PImage sprite;
  


  Item() {
  }

  Item(float x, float y, String setType) {
    type = setType;
    setPos(new PVector(x, y));
    if (type == "medkit") {
      sprite = spriteHolderInstance.medkit;
    } else if (type == "greenpills") {
      stock = 8;
      sprite = spriteHolderInstance.gpills;
      staminaPrice = 0;
    } else if (type == "ammo") {
      stock = 8;
      sprite = spriteHolderInstance.ammo;
      staminaPrice = 50;
    }
    held = false;
    w = 32;
    h = 32;
  }

  Item(float x, float y) {
    type = "gun";
    held = false;
    setPos(new PVector(x, y));
    maxStock = 8;
    stock = 8;
    maxAccuracy = 0;

    volley = 1;
    staminaPrice = 100;

    sprite = spriteHolderInstance.rifle;

    w = 32;
    h = 32;
  }

  Item(float x, float y, float maxAcc, int priceSTM, int setVolley, PImage setSprite) {
    type = "gun";
    maxStock = 8;
    stock = 8;
    held = false;
    setPos(new PVector(x, y));

    maxAccuracy = maxAcc;

    volley = setVolley;
    staminaPrice = priceSTM;

    sprite = setSprite;

    w = 32;
    h = 32;
  }

  public void action(Human obj) {
    if (type == "medkit") {
      obj.setTimer(new Timer(30, 'm', obj));
    } else if (type == "greenpills") {
      obj.setStamina(obj.getMaxStamina());
      stock--;
      if (stock <= 0)
        this.remove = true;
    } else if (type == "ammo")
    {
      Item gun = obj.getInventory().findItemInInventory("gun");
      if (gun != null && gun.stock < gun.maxStock)
        obj.setTimer(new Timer(30, 'r', obj));
      //      //TO DO -->
      //      Item item = obj.getInventory().findItemInInventory("gun");
      //
      //      if (item != null) {
      //        if (stock + item.stock < item.maxStock) {
      //          item.stock += stock;
      //          stock = 0;
      //        } else {
      //          stock = stock + item.stock - item.maxStock;
      //          item.stock = item.maxStock;
      //        }
      //      }
      //      if (stock <= 0)
      //        this.remove = true;
    }
  }

  public void useMedkit(Human actor) {
    actor.setHP(actor.getHP()+1);
    this.remove = true;
  }

  public void reload(Human obj) {
    if (type == "gun") { 
      Item item = obj.getInventory().findStockInInventory("ammo");

      if (item != null) {
        if (item.stock + stock < maxStock) {
          stock += item.stock;
          item.stock = 0;
        } else {
          item.stock = stock + item.stock - maxStock;
          stock = maxStock;
        }
        if (item.stock <= 0)
          item.remove = true;
      }
    }
  }

  public void update(float dt) {
    Human obj = worldInstance.getNearestNoLOS("any", this);

    if (obj != player1 && obj != player2) {
      if (obj != null && !held && worldInstance.tiles[(int)(this.getPos().x+this.getW()/2)/16][(int)(this.getPos().y+this.getH()/2)/16] == worldInstance.tiles[(int)(obj.getPos().x)/16][(int)(obj.getPos().y)/16]) {
        if (obj.getInventory().putItemIn(this))
          held = true;
      }
    } else if (player1 != null && obj == player1) {      
      if (obj != null && !held && worldInstance.tiles[(int)(this.getPos().x+this.getW()/2)/16][(int)(this.getPos().y+this.getH()/2)/16] == worldInstance.tiles[(int)(obj.getPos().x)/16][(int)(obj.getPos().y)/16] && controller1.drop() == false) {
        if (obj.getInventory().putItemIn(this))
          held = true;
      }
    } else if (player2 != null && obj == player2) {
      if (obj != null && !held && worldInstance.tiles[(int)(this.getPos().x+this.getW()/2)/16][(int)(this.getPos().y+this.getH()/2)/16] == worldInstance.tiles[(int)(obj.getPos().x)/16][(int)(obj.getPos().y)/16] && controller2.drop() == false) {
        if (obj.getInventory().putItemIn(this))
          held = true;
      }
    }
  }

  public void render() {
    if (held == false) {
      image(sprite, getPos().x, getPos().y, w, h);
    }
  }
}
class Bullet extends Entity {
  PVector speed;

  public void isPunch() {
    this.type = "bullet";
  }

  Bullet(PVector setPos, int targetX, int targetY, float accuracy) {
    this.type = "bullet";
    setPos(new PVector(setPos.x, setPos.y));
    float dir = -atan2(targetY-(setPos.y), targetX-(setPos.x));

    velocity = new PVector(cos(dir), -sin(dir));
    velocity.rotate(radians(random(accuracy)-accuracy/2));
    speed = new PVector(velocity.x, velocity.y);
    speed.mult(3);
    velocity.mult(10);

  
  }

  Bullet(PVector playerPos, PVector setPos, int targetX, int targetY, float accuracy) {
    this.type = "bullet";
    setPos(new PVector(playerPos.x, playerPos.y));
    float dir = -atan2(targetY-(setPos.y), targetX-(setPos.x));

    velocity = new PVector(cos(dir), -sin(dir));
    velocity.rotate(radians(random(accuracy)-accuracy/2));
    speed = new PVector(velocity.x, velocity.y);
    speed.mult(3);
    velocity.mult(10);

 
  }

  Bullet(PVector setPos, PVector vDir, float accuracy) {
    this.type = "bullet";
    setPos(new PVector(setPos.x, setPos.y));
    float dir = vDir.heading();

    velocity = new PVector(cos(dir), -sin(dir));
    velocity.rotate(radians(random(accuracy)-accuracy/2));
    speed = new PVector(velocity.x, velocity.y);
    speed.mult(3);
    velocity.mult(10);

  }

  public void update(float dt) {
    if (!worldInstance.getIfCol(this)) { 
      PVector change = velocity.get();
      //change.mult(dt*100);
      this.posAdd(change);

      velocity = speed;
    } else {
      velocity = new PVector(0, 0);
    }
  }

  public void render() {
    strokeWeight(5);
    point(getPos().x, getPos().y);
    strokeWeight(1);
  }
}

class Inventory {
  Item inventoryItems[];
  float selected;

  Inventory(int size) {
    inventoryItems = new Item[size];

    for (int i = 0; i < inventoryItems.length; i++) {
      inventoryItems[i] = null;
    }
  }

  public Item getItem(int index) {
    return inventoryItems[index];
  }

  public void setSelected(float set) {
    selected = constrain(set, -1, inventoryItems.length);
    if (selected == -1)
      selected = inventoryItems.length-1;
    else if (selected == inventoryItems.length)
      selected = 0;
  }

  public Item getSelected() {
    return inventoryItems[(int)selected];
  }

  public void removeSelectedItem() {
    inventoryItems[(int)selected].held = false;
    inventoryItems[(int)selected] = null;
  }

  public Item findItemInInventory(String type) {
    for (int i = 0; i < inventoryItems.length; i++) {
      if (inventoryItems[i] != null && inventoryItems[i].type == type && inventoryItems[i].stock < inventoryItems[i].maxStock) {
        return inventoryItems[i];
      }
    }
    return null;
  }

  public Item findStockInInventory(String type) {
    for (int i = 0; i < inventoryItems.length; i++) {
      if (inventoryItems[i] != null && inventoryItems[i].type == type) {
        return inventoryItems[i];
      }
    }
    return null;
  }

  public boolean putItemIn(Item item) {
    if (item.stock > 0 && item.type != "gun") {
      Item obj = findItemInInventory(item.type);
      if (obj != null) {
        if (obj.stock + item.stock <= item.maxStock) {
          obj.stock += item.stock;
          item.stock = 0;
          item.remove = true;
          return false;
        } else {
          item.stock = obj.stock + item.stock - item.maxStock;
          obj.stock = item.maxStock;
        }
      }
    }
    for (int i = 0; i < inventoryItems.length; i++) {
      if (inventoryItems[i] == null) {
        item.held = true;
        inventoryItems[i] = item;
        return true;
      }
    }
    return false;
  }

  public void setItem(float index, Item set) {
    inventoryItems[(int)index] = set;
  }

  public void update() {
    for (int i = 0; i < inventoryItems.length; i++) {
      if (inventoryItems[i] != null && inventoryItems[i].remove == true)
        inventoryItems[i] = null;
    }
  }

  public void render(int x, int y) {
    for (int i = 0; i < inventoryItems.length; i++) {
      strokeWeight(5);
      stroke(255);
      fill(150); 
      imageMode(CORNER);
      if (i == selected) {
        fill(0, 255, 0);
      }      
      rect(x+i*(72), y, 64, 64);
      if (inventoryItems[i] != null) {
        image(inventoryItems[i].sprite, x+i*(72), y, 64, 64);
        if (inventoryItems[i].stock > 0) {
          textSize(16);
          textAlign(LEFT);
          fill(0);
          text(inventoryItems[i].stock, x+i*(72)+4, y+16);
        }
      }
    }
  }
}

class Partical extends Entity {
  int c;
  int lifetime;
  int size;

  PImage sprite;

  Partical(PVector setPos, int setC, int setLife, int setSize) {
    setPos(setPos);
    type = "partical";
    c = setC;
    lifetime = setLife;
    size = setSize;
  }

  public void update(float dt) {
    lifetime--;
    
    getPos().add(velocity);
    
    if (lifetime < 0){
     remove = true; 
    }
  }

  public void render() {
    if (sprite == null)
    {
      stroke(c);
      strokeWeight(size);
      point(getPos().x, getPos().y);
      }
    }
  }

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

    strokeWeight(0.5f);
    beginShape(TRIANGLES);
    vertex(0, 0); //A
    strokeWeight(0.05f);
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
    strokeWeight(0.5f);
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

  public void pre() {
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

  public void render(int x, int y) {
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

class World extends Screen implements Runnable {
  protected boolean collisionMask[][];
  protected Tile tiles[][];
  boolean sectorClear = false;
  Human agents[];
  PVector screenOffset;
  //protected Rifle items[];

  PImage worldMap;
  PImage greyMap;

  //for counting
  int civilians, zombies;

  ArrayList<Entity> worldObjs;
  ArrayList<Entity> particals = new ArrayList<Entity>();
  ArrayList<Item> items = new ArrayList<Item>();

  PGraphics player1View;
  PGraphics player2View;


  public World(int x, int y) {
    player1View = createGraphics(width, height/2);
    player2View = createGraphics(width, height/2);

    println("init world textuter \n...");
    worldMap = new PImage(x*16, y*16);
    greyMap = new PImage(x*16, y*16);
    println("World textuter created");

    println("init world misc \n...");
    screenOffset = new PVector(); 
    collisionMask = new boolean[x][y];
    tiles = new Tile[x][y];

    agents = new Human[100];
    //items = new Rifle[5];

    worldObjs = new ArrayList<Entity>();
    println("world misc created");

    println("creating tiles \n...");
    setCol(true, 0, 0, x, y);
    setCol(false, 1, 1, x-2, y-2);


    //    makeBuilding("sector", 3, 3, 13, 13);
    //    makeBuilding("sector", 3, 3+13+4, 13, 13);
    //    makeBuilding("sector", 3, 3+13+4+13+4, 13, 12);
    //    
    //    makeBuilding("sector", 3+13+4, 3, 13, 13);
    //    makeBuilding("sector", 3+13+4, 3+13+4, 13, 13);
    //    makeBuilding("sector", 3+13+4, 3+13+4+13+4, 13, 12);
    //    
    //    makeBuilding("sector", 3+13+4+12+4, 3, 13, 13);
    //    makeBuilding("sector", 3+13+4+12+4, 3+13+4, 13, 13);
    //    makeBuilding("sector", 3+13+4+12+4, 3+13+4+13+4, 13, 12);

    for (x = 0; x < collisionMask.length; x++) {
      for (y = 0; y < collisionMask[x].length; y++) {
        if (collisionMask[x][y] == true) {
          tiles[x][y] = new Tile("wall");
        } else {
          tiles[x][y] = new Tile("grass");
        }
      }
    }

    setTiles("exit", 0, 0, x, y);
    setTiles("grass", 1, 1, x-2, y-2);

    makeBuilding("sector", 0, 0, x, y, 0, 0);
    println("tiles created");

    println("burning tiles to world map \n...");
    for (int i = 0; i < tiles.length; i++) {
      for (int j = 0; j < tiles[i].length; j++) {
        if (tiles[i][j].type == "lowerWall") {
          worldMap.set(i*16, j*16, spriteHolderInstance.lowerWall);
        } else if (tiles[i][j].type == "grass") {

          worldMap.set(i*16, j*16, spriteHolderInstance.grass);
        } else if (tiles[i][j].type == "vRoad") {

          worldMap.set(i*16, j*16, spriteHolderInstance.vRoad);
        } else if (tiles[i][j].type == "hRoad") {

          worldMap.set(i*16, j*16, spriteHolderInstance.hRoad);
        } else if (tiles[i][j].type == "floor" || tiles[i][j].type == "door") {

          worldMap.set(i*16, j*16, spriteHolderInstance.floor);
        }
      }
    }

    println("tiles burned");

    //populate
    println("populating world \n...");
    for (int i = 0; i < agents.length; i++) {

      agents[i] = new Human(random(1, 640+160+32), random(1, 480+320+32));

      while (checkInWall (agents[i]) == true)
      {
        agents[i] = new Human(random(1, 640+160+32), random(1, 480+320+32));
        //agents[i].isAlive = false;
      }
    }

    //add items
    for (int i = 0; i < 20; i++) {
      int posX = (int)(random(1, 640-16)/16);
      int posY = (int)(random(1, 480-16)/16);

      Item item;
      int rnd = (int)random(5); 

      if (rnd == 0)
        item = new Item(random(32, 640+160+16), random(32, 480+320+16));
      else if (rnd == 1)
        item = new Item(random(32, 640+160+16), random(32, 480+320+16), 45, 200, 5, spriteHolderInstance.shotgun);
      else if (rnd == 2) 
        item = new Item(random(32, 640+160+16), random(32, 480+320+16), "greenpills");
      else if (rnd == 3) 
        item = new Item(random(32, 640+160+16), random(32, 480+320+16), "ammo");
      else {
        item = new Item(random(32, 640+160+16), random(32, 480+320+16), "medkit");
      }

      while (collisionMask[ (int)(item.getPos().x+item.getW()/2)/16][(int)(item.getPos().y+item.getH()/2)/16] == true)
      { 
        rnd = (int)random(4); 

        if (rnd == 0)
          item = new Item(random(32, 640+160+16), random(32, 480+320+16));
        else if (rnd == 1)
          item = new Item(random(32, 640+160+16), random(32, 480+320+16), 45, 200, 9, spriteHolderInstance.shotgun);
        else if (rnd == 2)
          item = new Item(random(32, 640+160+16), random(32, 480+320+16), "greenpills");
        else if (rnd == 3) 
          item = new Item(random(32, 640+160+16), random(32, 480+320+16), "ammo");
        else {
          item = new Item(random(32, 640+160+16), random(32, 480+320+16), "medkit");
        }
      }
      items.add(item);
    }

    //make one the player
    agents[2] = new Zombie(agents[2]);
    agents[3] = new Zombie(agents[3]);
    agents[4] = new Zombie(agents[4]);

    println("world populated");
  }

  public boolean LOS(Entity agent, Entity target) {
    float dir = -atan2(target.getPos().y-(agent.getPos().y), target.getPos().x-(agent.getPos().x));

    PVector unitVector = new PVector(cos(dir), -sin(dir));

    float sightRadius = dist(agent.getPos().x, agent.getPos().y, target.getPos().x, target.getPos().y);

    try {
      for (float i = 0; i < 1; i += 0.05f) {
        int currX = (int)((agent.getPos().x+unitVector.x*sightRadius*i)/16);
        int currY = (int)((agent.getPos().y+unitVector.y*sightRadius*i)/16);


        if (collisionMask[currX][currY])
        {
          return false;
        }
      }
    }
    catch (IndexOutOfBoundsException e)
    {
      System.out.println("Can't do that!");
    }

    //line(agent.getPos().x, agent.getPos().y, agent.getPos().x+unitVector.x*sightRadius*1, agent.getPos().y+unitVector.y*sightRadius*1);
    return true;
  }

  private void cleanLos() {
    for (int x = 0; x < collisionMask.length; x++) {
      for (int y = 0; y < collisionMask[x].length; y++) {

        tiles[x][y].visible = false;
      }
    }
  }

  private void LOS(Human agent, float sightRadius) {
    //int originX = (int)((agent.getPos().x)/16);
    //int originY = (int)((agent.getPos().y)/16);

    for (int x = 0; x < collisionMask.length; x++) {
      for (int y = 0; y < collisionMask[x].length; y++) {

        //tiles[x][y].visible = false;

        if (dist(agent.getPos().x, agent.getPos().y, x*16+8, y*16+8) <= sightRadius) {

          float dir = -atan2(y*16+8-(agent.getPos().y), x*16+8-(agent.getPos().x));
          //float dir = atan2(y*16-(agent.getPos().y), -(agent.getPos().x));
          //float dir = -atan2(y-originY, x-originX);

          PVector unitVector = new PVector(cos(dir), -sin(dir));

          boolean blocked = false;

          try {
            for (float i = 0; i < 1; i += 0.05f) {
              int currX = (int)((agent.getPos().x+unitVector.x*sightRadius*i)/16);
              int currY = (int)((agent.getPos().y+unitVector.y*sightRadius*i)/16);

              if (collisionMask[currX][currY] == false && !blocked)
              {
                tiles[currX][currY].visible = true;
                tiles[currX][currY].seen = true;
              } else {
                if (tiles[currX][currY].type == "lowerWall") {
                  tiles[currX][currY].visible = true;
                  tiles[currX][currY].seen = true;
                } else if (tiles[currX][currY].type == "door") {
                  tiles[currX][currY].visible = true;
                  tiles[currX][currY].seen = true;
                } else {
                  //tiles[currX][currY].visible = false;
                }
                blocked = true;
              }
            }
          }
          catch (IndexOutOfBoundsException e)
          {
            System.out.println("Can't do that!");
          }
        }
      }
    }
  }

  public ArrayList<Percept> LOSAI(Entity agent) {
    ArrayList<Percept> percepts = new ArrayList<Percept>();

    int originX = constrain((int)((agent.getPos().x)/16), 9, 52-9);
    int originY = constrain((int)((agent.getPos().y)/16), 9, 52-9);
    try {
      for (int x = originX-8; x < originX+8; x++) {
        for (int y = originY-8; y < originY+8; y++) {
          if (tiles[x][y].type == "door") {
            //println("found a door");
            percepts.add(new Percept("door", new PVector(x, y)));
          }
        }
      }
    }
    catch (IndexOutOfBoundsException e)
    {
      println("outside world map");
    }
    return percepts;
  }

  private void makeBuilding(String type, int x, int y, int w, int h, int cellX, int cellY) {
    if (type == "sector") {
      int minSplit = 10;

      int vSplit = (int)random(x+minSplit, w-minSplit);
      int hSplit = (int)random(x+minSplit, h-minSplit);

      setTiles("vRoad", vSplit, 1, 2, h-2);
      setTiles("hRoad", 1, hSplit, w-2, 2);

      int vSplit2;
      int hSplit2;

      if (w-vSplit+1 < w/2) {
        vSplit2 = (int)random(x+minSplit, vSplit-minSplit);
        setTiles("vRoad", vSplit2, 1, 2, h-2);
      } else {
        vSplit2 = (int)random(vSplit+minSplit, w-minSplit);
        setTiles("vRoad", vSplit2, 1, 2, h-2);
      }

      if (h-hSplit+1 < h/2) {
        hSplit2 = (int)random(y+minSplit, hSplit-minSplit);
        setTiles("hRoad", 1, hSplit2, w-2, 2);
      } else {
        hSplit2 = (int)random(hSplit+minSplit, h-minSplit);
        setTiles("hRoad", 1, hSplit2, w-2, 2);
      }

      setTiles("hRoad", 1, 1, w-2, 1);
      setTiles("hRoad", 1, h-2, w-2, 1);

      setTiles("vRoad", 1, 1, 1, h-2);
      setTiles("vRoad", w-2, 1, 1, h-2);

      PVector[] intersections = new PVector[4];

      if (vSplit < vSplit2) {
        intersections[0] = new PVector(vSplit, 0);
        intersections[3] = new PVector(vSplit, 0);

        intersections[1] = new PVector(vSplit2, 0);
        intersections[2] = new PVector(vSplit2, 0);
      } else {
        intersections[0] = new PVector(vSplit2, 0);
        intersections[3] = new PVector(vSplit2, 0);

        intersections[1] = new PVector(vSplit, 0);
        intersections[2] = new PVector(vSplit, 0);
      } 

      if (hSplit < hSplit2) {
        intersections[0].y = hSplit;
        intersections[1].y = hSplit;

        intersections[2].y = hSplit2;
        intersections[3].y = hSplit2;
      } else {
        intersections[0].y = hSplit2;
        intersections[1].y = hSplit2;

        intersections[2].y = hSplit;
        intersections[3].y = hSplit;
      } 

      makeBuilding("house", 3, 3, (int)intersections[0].x-4, (int)intersections[0].y-4, 0, 0);
      makeBuilding("house", 3, (int)intersections[0].y+3, (int)intersections[3].x-4, (int)intersections[3].y-4-(int)intersections[0].y, 0, 0);
      makeBuilding("house", 3, (int)intersections[3].y+3, (int)intersections[0].x-4, h-6 - (int)intersections[3].y, 0, 0);

      makeBuilding("house", (int)intersections[0].x+3, 3, (int)intersections[1].x - 7 - (int)intersections[0].x+3, (int)intersections[0].y-4, 0, 0);
      makeBuilding("house", (int)intersections[0].x+3, (int)intersections[0].y+3, (int)intersections[1].x - 7 - (int)intersections[0].x+3, (int)intersections[3].y-4-(int)intersections[0].y, 0, 0);
      makeBuilding("house", (int)intersections[0].x+3, (int)intersections[3].y+3, (int)intersections[1].x - 7 - (int)intersections[0].x+3, h-6 - (int)intersections[3].y, 0, 0);

      makeBuilding("house", (int)intersections[1].x+3, 3, w - (int)intersections[1].x -6, (int)intersections[0].y-4, 0, 0);
      makeBuilding("house", (int)intersections[1].x+3, (int)intersections[0].y+3, w - (int)intersections[1].x -6, (int)intersections[3].y-4-(int)intersections[0].y, 0, 0);
      makeBuilding("house", (int)intersections[1].x+3, (int)intersections[3].y+3, w - (int)intersections[1].x -6, h-6 - (int)intersections[3].y, 0, 0);
    }
    if (type == "house") {
      if (cellX == 0 && cellY == 0) {
        setCol(true, x, y, w, h);
        setCol(false, x+1, y+1, w-2, h-2);

        setTiles("lowerWall", x, y, w, h);
        setTiles("floor", x+1, y+1, w-2, h-2);
      } else {
        setCol(true, x+1, y+1, w-1, h-1);
        setCol(false, x+1, y+1, w-2, h-2);
        setTiles("lowerWall", x+1, y+1, w-1, h-1);
        setTiles("floor", x+1, y+1, w-2, h-2);
      }



      int minSplit = 5;
      int vSplit = w;
      int hSplit = h;

      println("making house: " + w + " " + h);

      if (w >= 10 && h >= 10) {
        if (hSplit >= 10) {

          hSplit = (int)random(minSplit, h-minSplit);
          println("height is now: " + h);
        }
        if (w >= 10)
          vSplit = (int)random(minSplit, w-minSplit);

        if (vSplit < w) {
          makeBuilding("house", x, y, vSplit, hSplit, cellX, cellY);
          makeBuilding("house", x+vSplit-1, y, w-vSplit+1, hSplit, cellX++, cellY);
        }
        if (hSplit < h) {
          makeBuilding("house", x, y+hSplit-1, vSplit, h-hSplit+1, cellX, cellY++);
          makeBuilding("house", x+vSplit-1, y+hSplit-1, w-vSplit+1, h-hSplit+1, cellX++, cellY++);
        }
      } else {
        if (cellX == 0 && cellY == 0) {
          collisionMask[(int)(x+w/2)][y] = true;
          tiles[(int)(x+w/2)][y].type = "door";
          tiles[(int)(x+w/2)][y].sprite = spriteHolderInstance.doorClosed;

          collisionMask[x][(int)(y+h/2)] = true;
          tiles[x][(int)(y+h/2)].type = "door";
          tiles[x][(int)(y+h/2)].sprite = spriteHolderInstance.doorClosed;
        } else if (cellY == 0) {
          collisionMask[x][(int)(y+h/2)] = true;
          tiles[x][(int)(y+h/2)].type = "door";
          tiles[x][(int)(y+h/2)].sprite = spriteHolderInstance.doorClosed;
        } else if (cellX == 1) {
          collisionMask[(int)(x+w/2)][y] = true;
          tiles[(int)(x+w/2)][y].type = "door";
          tiles[(int)(x+w/2)][y].sprite = spriteHolderInstance.doorClosed;
        }
        collisionMask[(int)(x+w/2)][y+h-1] = true;
        tiles[(int)(x+w/2)][y+h-1].type = "door";
        tiles[(int)(x+w/2)][y+h-1].sprite = spriteHolderInstance.doorClosed;



        collisionMask[x+w-1] [(int)(y+h/2)]= true;
        tiles[x+w-1][(int)(y+h/2)].type = "door";
        tiles[x+w-1][(int)(y+h/2)].sprite = spriteHolderInstance.doorClosed;
      }
    }
  }

  public Human getNearest(String kind, Entity fromMe) {
    PVector fromHere = fromMe.getPos();
    float min = 12*16;
    Human nearest = null;

    if (kind != "any") {
      for (int i = 0; i < agents.length; i++) {
        if (agents[i].type == kind && agents[i].isAlive()) {

          float curr = dist(agents[i].getPos().x, agents[i].getPos().y, fromHere.x, fromHere.y);

          if (curr != 0 && (curr < min || min == 0)) {
            if (LOS(fromMe, agents[i])) {
              min = curr;
              nearest = agents[i];
            }
          }
        }
      }
    } else {
      for (int i = 0; i < agents.length; i++) {
        if (agents[i].isAlive()) {

          float curr = dist(agents[i].getPos().x, agents[i].getPos().y, fromHere.x, fromHere.y);

          if (curr != 0 && (curr < min || min == 0)) {
            if (LOS(fromMe, agents[i])) {
              min = curr;
              nearest = agents[i];
            }
          }
        }
      }
    }
    return nearest;
  }



  public Human getNearestNoLOS(String kind, Entity fromMe) {
    PVector fromHere = fromMe.getPos();
    float min = 0;
    Human nearest = null;

    if (kind != "any") {
      for (int i = 0; i < agents.length; i++) {
        if (agents[i].type == kind && agents[i].isAlive()) {

          float curr = dist(agents[i].getPos().x, agents[i].getPos().y, fromHere.x, fromHere.y);

          if (curr != 0 && (curr < min || min == 0)) {
            {
              min = curr;
              nearest = agents[i];
            }
          }
        }
      }
    } else {
      for (int i = 0; i < agents.length; i++) {
        if (agents[i].isAlive()) {

          float curr = dist(agents[i].getPos().x, agents[i].getPos().y, fromHere.x, fromHere.y);

          if (curr != 0 && (curr < min || min == 0)) {
            {
              min = curr;
              nearest = agents[i];
            }
          }
        }
      }
    }
    return nearest;
  }

  private void setCol(boolean set, int x, int y, int w, int h) {
    for (int i = x; i < x+w; i++) {
      for (int j = y; j < y+h; j++) {
        if (i >= 0 && i < collisionMask.length && j >= 0 && j < collisionMask[i].length)
        {
          collisionMask[i][j] = set;
        }
      }
    }
  }

  private void setTiles(String set, int x, int y, int w, int h) {
    for (int i = x; i < x+w; i++) {
      for (int j = y; j < y+h; j++) {
        if (i >= 0 && i < collisionMask.length && j >= 0 && j < collisionMask[i].length)
        {
          tiles[i][j].type = set;
        }
      }
    }
  }

  public boolean checkInWall(Entity agent) {
    int cornerX = (int)((agent.getPos().x-agent.getW()/2)/16);
    int cornerY = (int)((agent.getPos().y-agent.getH()/2)/16);

    if (collisionMask[cornerX][cornerY] == true)
    {
      return true;
    }

    cornerX = (int)((agent.getPos().x+agent.getW()/2)/16);
    cornerY = (int)((agent.getPos().y-agent.getH()/2)/16);

    if (collisionMask[cornerX][cornerY] == true)
    {
      return true;
    }

    cornerX = (int)((agent.getPos().x+agent.getW()/2)/16);
    cornerY = (int)((agent.getPos().y+agent.getH()/2)/16);

    if (collisionMask[cornerX][cornerY] == true)
    {
      return true;
    }

    cornerX = (int)((agent.getPos().x-agent.getW()/2)/16);
    cornerY = (int)((agent.getPos().y+agent.getH()/2)/16);

    if (collisionMask[cornerX][cornerY] == true)
    {
      return true;
    }

    return false;
  }

  public boolean getIfCol(Entity agent) {

    PVector velocity = new PVector(agent.velocity.x, agent.velocity.y);

    int cornerX = (int)((agent.getPos().x-agent.getW()/2+agent.velocity.x)/16);
    int cornerY = (int)((agent.getPos().y-agent.getH()/2+agent.velocity.y)/16);

    try {

      if (collisionMask[cornerX][cornerY] == true)
      {
        return true;
      }

      cornerX = (int)((agent.getPos().x+agent.getW()/2+agent.velocity.x)/16);
      cornerY = (int)((agent.getPos().y-agent.getH()/2+agent.velocity.y)/16);

      if (collisionMask[cornerX][cornerY] == true)
      {
        return true;
      }

      cornerX = (int)((agent.getPos().x+agent.getW()/2+agent.velocity.x)/16);
      cornerY = (int)((agent.getPos().y+agent.getH()/2+agent.velocity.y)/16);

      if (collisionMask[cornerX][cornerY] == true)
      {
        return true;
      }

      cornerX = (int)((agent.getPos().x-agent.getW()/2+agent.velocity.x)/16);
      cornerY = (int)((agent.getPos().y+agent.getH()/2+agent.velocity.y)/16);

      if (collisionMask[cornerX][cornerY] == true)
      {
        return true;
      }
    } 
    catch (IndexOutOfBoundsException e)
    {
      println("collision outside world map");
    }

    return false;
  }

  public PVector checkCol(Human agent) {

    PVector velocity = new PVector(agent.velocity.x, agent.velocity.y);

    try {

      int cornerX = (int)((agent.getPos().x-agent.w/2+agent.velocity.x)/16);
      int cornerY = (int)((agent.getPos().y-agent.h/2)/16);

      if (collisionMask[cornerX][cornerY] == true)
      {
        velocity.x = 0;
        checkInteraction(cornerX, cornerY, agent);
      }

      cornerX = (int)((agent.getPos().x+agent.w/2+agent.velocity.x)/16);
      cornerY = (int)((agent.getPos().y-agent.h/2)/16);

      if (collisionMask[cornerX][cornerY] == true)
      {
        velocity.x = 0;
        checkInteraction(cornerX, cornerY, agent);
      }

      cornerX = (int)((agent.getPos().x+agent.w/2+agent.velocity.x)/16);
      cornerY = (int)((agent.getPos().y+agent.h/2)/16);

      if (collisionMask[cornerX][cornerY] == true)
      {
        velocity.x = 0;
        checkInteraction(cornerX, cornerY, agent);
      }

      cornerX = (int)((agent.getPos().x-agent.w/2+agent.velocity.x)/16);
      cornerY = (int)((agent.getPos().y+agent.h/2)/16);

      if (collisionMask[cornerX][cornerY] == true)
      {
        velocity.x = 0;
        checkInteraction(cornerX, cornerY, agent);
      }

      //y-axis

      cornerX = (int)((agent.getPos().x-agent.w/2)/16);
      cornerY = (int)((agent.getPos().y-agent.h/2+agent.velocity.y)/16);

      if (collisionMask[cornerX][cornerY] == true)
      {
        velocity.y = 0;
        checkInteraction(cornerX, cornerY, agent);
      }

      cornerX = (int)((agent.getPos().x+agent.w/2)/16);
      cornerY = (int)((agent.getPos().y-agent.h/2+agent.velocity.y)/16);

      if (collisionMask[cornerX][cornerY] == true)
      {
        velocity.y = 0;
        checkInteraction(cornerX, cornerY, agent);
      }

      cornerX = (int)((agent.getPos().x+agent.w/2)/16);
      cornerY = (int)((agent.getPos().y+agent.h/2+agent.velocity.y)/16);

      if (collisionMask[cornerX][cornerY] == true)
      {
        velocity.y = 0;
        checkInteraction(cornerX, cornerY, agent);
      }

      cornerX = (int)((agent.getPos().x-agent.w/2)/16);
      cornerY = (int)((agent.getPos().y+agent.h/2+agent.velocity.y)/16);

      if (collisionMask[cornerX][cornerY] == true)
      {
        velocity.y = 0;
        checkInteraction(cornerX, cornerY, agent);
      }
    } 
    catch (IndexOutOfBoundsException e)
    {
      velocity.x = 0;
      velocity.y = 0;

      println("no Stay!");
    }

    return velocity;
  }

  private void checkInteraction(int cornerX, int cornerY, Human agent) {
    if (tiles[cornerX][cornerY].type == "door") {
      agent.velocity.x = 0;
      agent.velocity.y = 0;
      if (agent.getTimer() == null)
        agent.setTimer(new Timer(30, 'd', agent));
    } else if (tiles[cornerX][cornerY].type == "exit") {
      agent.velocity.x = 0;
      agent.velocity.y = 0;
      if (agent.getTimer() == null)
        agent.setTimer(new Timer(30, 'x', agent));
    }
  }

  public void closeUp() {
    if (!wholeMap) {
      translate(width/2, height/2);
      translate(-player1.getPos().x*4-screenOffset.x, -player1.getPos().y*4-screenOffset.y);
      scale(4, 4);
    }
  }

  public void popCloseUp() {
    if (!wholeMap) {
      translate(width/2, height/2);
      translate(-player1.getPos().x*4, -player1.getPos().y*4);
      scale(4, 4);
    }
  }

  public void render() {
    background(0);

    //pushMatrix();
    closeUp();

    int minx = 0;
    int maxx = collisionMask.length;
    int miny = 0; 
    int maxy = collisionMask[0].length;

    if (player2 != null && (abs(player1.getPos().x - player2.getPos().x) > 152 || abs(player1.getPos().y - player2.getPos().y) > 104)) {
      wholeMap = true;
    } else if (player2 != null) {
      wholeMap = false;
    }

    //
    //    if (!wholeMap)
    //    {
    //      int currX = (int)((player1.getPos().x)/16);
    //      int currY = (int)((player1.getPos().y)/16);
    //
    //      minx = constrain(currX-12, 0, collisionMask.length);
    //      miny = constrain(currY-10, 0, collisionMask[0].length);
    //      maxx = constrain(currX+14, 0, collisionMask.length);
    //      maxy = constrain(currY+10, 0, collisionMask[0].length);

    //      image(
    //      worldMap, 0.0+(int)player1.getPos().x-12*16, 
    //      0.0+(int)player1.getPos().y-10*16, 
    //      24*16, 
    //      20*16, 
    //
    //      (int)player1.getPos().x-12*16, 
    //      (int)player1.getPos().y-10*16, 
    //      24*16+(int)player1.getPos().x-12*16, 
    //      20*16+(int)player1.getPos().y-10*16);
    //    } else
    //      image(worldMap, 0.0, 0.0);
    //    noStroke();
    //
    //
    for (int x = minx; x < maxx; x++) {
      for (int y = miny; y < maxy; y++) {
        if (tiles[x][y].update) {
          if (tiles [x][y].seen) {
            if (tiles[x][y].type == "floor")
            {
              image(spriteHolderInstance.floor, x*16, y*16);
            } else if (tiles[x][y].type == "door")
            {
              image(spriteHolderInstance.floor, x*16, y*16);
              image(tiles[x][y].sprite, x*16, y*16);
            } else if (tiles[x][y].type == "lowerWall")
            {
              image(spriteHolderInstance.lowerWall, x*16, y*16);
            } else if (tiles[x][y].type == "vRoad")
            {
              image(spriteHolderInstance.vRoad, x*16, y*16);
            } else if (tiles[x][y].type == "hRoad")
            {
              image(spriteHolderInstance.hRoad, x*16, y*16);
            } else if (tiles[x][y].type == "grass")
            {
              image(spriteHolderInstance.grass, x*16, y*16);
            } 

            if (!tiles[x][y].visible) { 
              fill(0, 127);
              rect(x*16, y*16, 16, 16);
            }
          } else if (tiles[x][y].type == "end") {
            fill(255, 0, 0);
            rect(x*16, y*16, 16, 16);
          } else {
            fill(0);
            rect(x*16, y*16, 16, 16);
          }
        }
        //optimizing, only show FOV near player
        if ( player2 != null && (dist(player1.getPos().x, player1.getPos().y, x*16+8, y*16+8) > 168 && dist(player2.getPos().x, player2.getPos().y, x*16+8, y*16+8) > 168)) {
          tiles[x][y].update = false;
        } else if (player2 == null && dist(player1.getPos().x, player1.getPos().y, x*16+8, y*16+8) > 168) {
          tiles[x][y].update = false;
        } else {
          tiles[x][y].update = true;
        }
      }
    }


    for (int i = 0; i < agents.length; i++) {
      if (tiles[(int)(agents[i].getPos().x)/16][(int)(agents[i].getPos().y)/16].visible) {
        agents[i].render();
      }
    }

    for (int i = 0; i < items.size (); i++) {
      Item item = items.get(i);
      if (tiles[(int)(item.getPos().x+item .getW()/2)/16][(int)(item .getPos().y+item .getH()/2)/16].visible) {
        item.render();
      }
    }

    for (int x = 0; x < worldObjs.size (); x++) {
      stroke(0);
      Entity instance = worldObjs.get(x); 
      instance.render();
    }

    for (int i = particals.size ()-1; i >= 0; i--) {
      Entity instance = particals.get(i); 
      instance.render();
    }

    if (player2 != null && (abs(player1.getPos().x - player2.getPos().x) > 152 || abs(player1.getPos().y - player2.getPos().y) > 104)) {
      resetMatrix();
      drawPlayer1View();
      drawPlayer2View();
      image(player1View, 0, 0);
      image(player2View, 0, height/2);
    }

    //calls draw function which should be on top of everything else
    //mostly just the HUD 
    for (int i = 0; i < agents.length; i++) {
      if (tiles[(int)(agents[i].getPos().x)/16][(int)(agents[i].getPos().y)/16].visible) {
        agents[i].pre();
      }
    }
  }

  public void update(float dt) {
    println("calculate screen of set \b...");
    screenOffset = controller.getOffsetScreen();
    println("screen of set calculated\b...");

    println("calculate FOV for player \b...");
    cleanLos();
    LOS(player1, 8*16);
    if (player2Active)
      LOS(player2, 8*16);
    println("FOV calculated\b");

    civilians = 0;
    zombies = 0;

    println("updating agants \n...");
    for (int i = 0; i < agents.length; i++) {
      if (agents[i].isAlive()) {
        agents[i].update(dt);

        if (agents[i].type == "human") {
          civilians++;
          Human nearest = getNearest("zombie", agents[i]); 
          if (nearest != null && dist(agents[i].getPos().x, agents[i].getPos().y, nearest.getPos().x, nearest.getPos().y) < 12)
          {
            if (agents[i] == player1)
            {
              if (frameCount%30 == 0) {
                player1.setHP(player1.getHP()-1);
                createParticals(5, nearest.getPos().get(), color(255, 0, 0), 15, 2);
                if (player1.getHP() <= 0) {
                  player1.setType("zombie");
                  player1.type = "zombie";
                  player1.setHP(2);
                }
              }
            } else if (agents[i] == player2)
            {
              if (frameCount%30 == 0) {
                player2.setHP(player2.getHP()-1);
                createParticals(5, nearest.getPos().get(), color(255, 0, 0), 15, 2);
                if (player2.getHP() <= 0) {
                  player2.setType("zombie");
                  player2.type = "zombie";
                  player2.setHP(2);
                }
              }
            } else {
              if (frameCount%10 == 0) {
                agents[i].setHP(agents[i].getHP()-1);
                createParticals(5, nearest.getPos().get(), color(255, 0, 0), 15, 2);
                if (agents[i].getHP() <= 0) {
                  agents[i] = new Zombie(agents[i]);
                  agents[i].setHP(2);
                }
              }
            }
          }
        } else {
          zombies++;
        }
      }
    }
    println("agents updated");

    println("updating misc \n...");
    for (int i = 0; i < items.size (); i++) {
      Item item = items.get(i);
      item.update(dt);

      if (item.remove == true) {
        items.remove(i);
      }
    }

    for (int i = worldObjs.size ()-1; i >= 0; i--) {
      Entity instance = worldObjs.get(i); 

      instance.update(dt);

      Human nearest = getNearest("any", instance);
      if (nearest != null && dist(instance.getPos().x, instance.getPos().y, nearest.getPos().x, nearest.getPos().y) < 8) {
        nearest.setHP(nearest.getHP()-1);
        createParticals(5, nearest.getPos().get(), color(255, 0, 0), 15, 2);
        if (nearest.getHP() <= 0)
          nearest.kill();
        instance.velocity = new PVector(0, 0);
      }

      if (instance.velocity.equals( new PVector(0, 0)) || instance.remove == true) {
        worldObjs.remove(i);
      }

      if (instance.type == "punch") {
        instance.remove = true;
      }
    }

    for (int i = particals.size ()-1; i >= 0; i--) {
      Entity instance = particals.get(i); 

      instance.update(dt);

      if (instance.remove == true) {
        particals.remove(i);
      }
    }

    if (controller.pause()) {
      currentScreen = new PauseMenu();
    }

    if (zombies == 1) {
      sectorClear = true;
      //currentScreen = new CustomScreen("Victory!");
    } else if (civilians  == 0) {
      currentScreen = new CustomScreen("Zombies eat All!");
    }
    println("misc updated");
  }

  private void splitScreen() {
  }

  private PGraphics drawPlayer1View() {
    player1View.beginDraw();
    player1View.noSmooth();
    player1View.background(0);
    player1View.image(get((int)player1.getPos().x-608/4, (int)player1.getPos().y-208/4, 608/2, 208/2), 0, 0, 1216, 416);
    player1View.endDraw();
    return player1View;
  }

  private PGraphics drawPlayer2View() {
    player2View.beginDraw();
    player2View.noSmooth();
    player2View.background(0);
    player2View.image(get((int)player2.getPos().x-608/4, (int)player2.getPos().y-208/4, 608/2, 208/2), 0, 0, 1216, 416);
    player2View.endDraw();
    return player2View;
  }

  private void createParticals(int amount, PVector setPos, int setC, int setLife, int setSize) {
    for (int x = 0; x < amount; x++) {
      Partical blood = new Partical(setPos, setC, setLife, setSize);
      blood.velocity = PVector.random3D();   
      particals.add(blood);
    }
  }
}

class Tile {
  String type;
  boolean visible;
  boolean seen;
  boolean update;
  PImage sprite;

  String[] tags;
  String[] content;

  public Tile(String setType) {

    update = true;
    visible = false;
    seen = false;
    type = setType;
    tags = new String[2];

    if (type == "wall") {
      tags[0] = "solid";
    } else {
      tags[0] = "free";
    }

    if (type == "door") {
      sprite = spriteHolderInstance.doorClosed;
    }

    content = new String[4];
  }
}

class Timer {
  float clock;
  float targetClock;
  char type;
  boolean remove;

  Entity p_selected;

  Timer(int setClock, char setType, Human actor) {
    clock = 0;
    targetClock = setClock;
    type = setType;
    p_selected = actor.getInventory().getSelected();
  }

  public void render() {
    fill(127);
    ellipse(0, -8, 8, 8);
    fill(200);
    arc(0, -8, 7, 7, radians(-90), radians((clock/targetClock)*360-90), PIE);
  }

  public void render(PVector pos) {
    fill(127);
    ellipse(pos.x, pos.y-8, 8, 8);
    fill(200);
    arc(pos.x, pos.y-8, 7, 7, radians(-90), radians((clock/targetClock)*360-90), PIE);
  }

  public void update(Human actor, float dt) {
    if (actor.velocity.x == 0 && actor.velocity.y == 0 && actor.getInventory().getSelected() == p_selected) {
      clock += 36*dt;
      p_selected = actor.getInventory().getSelected();
    } else {
      remove = true;
    }
    if (clock >= targetClock) {
      Item obj;
      switch(type) { 
      case 'r':
        obj = actor.getInventory().getSelected();

        if (obj != null && obj.type == "gun") {
          obj.reload(actor);
        } else {
          actor.getInventory().findItemInInventory("gun").reload(actor);
        }
        remove = true;
        break;
      case 'm':
        obj = actor.getInventory().getSelected();
        if (obj != null && obj.type == "medkit") {
          obj.useMedkit(actor);
        }
        remove = true;
        break;
      case 'd':
        actor.openDoor();

        remove = true;
        break;
      case 'x':
        println("player1 pos " + player1.getPos());
        if (player2Active) {
          if (!player1.transfered && !player2.transfered && (abs((int)player1.getPos().x - (int)player2.getPos().x) < 8 ||  abs((int)player1.getPos().y - (int)player2.getPos().y) < 8))
            actor.transferSector();
        } else
          actor.transferSector();

        remove = true;
        break;
      }
    }
  }
}

  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "Midnight_v12" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
