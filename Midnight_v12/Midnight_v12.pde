import ddf.minim.spi.*;
import ddf.minim.signals.*;
import ddf.minim.*;
import ddf.minim.analysis.*;
import ddf.minim.ugens.*;
import ddf.minim.effects.*;

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

void setup() {
  //code starts in MIDNIGHT_v12.java NOT HERE
  //size(1216, 832, P2D);

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


void draw() {
  println("starting gameloop \n...");

  thisTime = millis() - lastTime;
  lastTime = lastTime+thisTime;
  dt = thisTime/1000.0/2;

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

void createWorldMap() {
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

World goToWorld() {
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


void keyPressed() {
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

void keyReleased() {
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

void mousePressed() {
  println("mousePressed() started");
  mouseStatePressed = true;
  println("mousepressed() ended");
}

void mouseReleased() {
  println("mousePressed() started");
  mouseStateReleased = true;
  println("mousepressed() ended");
}
