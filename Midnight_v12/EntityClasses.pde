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

  PVector getPos() {
    return pos;
  }

  int getH() {
    return h;
  }

  int getW() {
    return w;
  }

  void setPos(PVector v) {
    pos = new PVector(v.x, v.y);
  }

  void posAdd(PVector v) {
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
  color skin;

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

  void setReadyForTransfer(boolean set) {
    readyForTransfer = set;
  }

  int getCurrentSectorX() {
    return currentSectorX;
  }
  int getCurrentSectorY() {
    return currentSectorY;
  }
  void setCurrentSectorX(int x) {
    currentSectorX = x;
  }
  void setCurrentSectorY(int y) {
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

  void transferSector() {
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

  void remember(String type, int x, int y) {
    if (!checkInMemory(type, x, y)) {
      if (currentMemoryPoint > memory.length-1) {
        currentMemoryPoint = 0;
      }
      memory[currentMemoryPoint] = new Percept(type, new PVector(x, y));
      currentMemoryPoint++;
    }
  }

  Inventory getInventory() {
    return inventory;
  }

  float getAccuracy() {
    accuracy = 90.0*((float)getStamina()/getMaxStamina())-90.0;
    if (getHeldObject() != null) {
      Item gun =  getHeldObject();
      accuracy -= gun.maxAccuracy;
    }
    return accuracy;
  }

  void openDoor() {
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

  void shoot() {
  }

  Timer getTimer() {
    return timer;
  }

  void setTimer(Timer set) {
    timer = set;
  }

  void setPos(PVector v) {
    super.setPos(v);
  }

  int getHP() {
    return hp;
  }

  void setHP(int set) {
    hp = set;
  }

  boolean checkInMemory(String type, int x, int y) {
    for (int i = 0; i < memory.length; i++) {
      if (memory[i].same(new Percept(type, new PVector(x, y)))) {
        return true;
      }
    }
    return false;
  }

  void drop () {
    if (getHeldObject() != null) {
      getHeldObject().setPos(new PVector(getPos().x-16, getPos().y-16));
      getHeldObject().held = false;
      worldInstance.items.add(getHeldObject());
      setHeldObject(null);
    }
  }

  void render() {
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

  void kill() {
    drop ();
    isAlive = false;
  }

  float getStamina() {
    return currStamina;
  }

  boolean staminaIsFull() {
    if (currStamina == maxStamina) {
      return true;
    }
    return false;
  }

  String getType() {
    return type;
  }

  void setType(String set) {
    type = set;
  }

  int getMaxStamina() {
    return maxStamina;
  }

  boolean useStamina(int value) {
    if (currStamina > 0) {
      currStamina -= value;
      return true;
    }
    return false;
  }

  void setStamina(int set) {
    currStamina = set;
  }

  void regenStamina(float dt) {
    if (currStamina < maxStamina) {
      currStamina += 1*100*dt;
    } else 
      setStamina(maxStamina);
  }  

  boolean isAlive() {
    return isAlive;
  }

  Item getHeldObject() {
    return getInventory().getSelected();
  }

  void setHeldObject(Item set) {
    getInventory().setItem(getInventory().selected, set);
  }

  void update(float dt) {
    Human obj = worldInstance.getNearest("human", this);

    if (obj != null && dist(getPos().x, getPos().y, obj.getPos().x, obj.getPos().y) < 8)
    {
      float push = atan2(obj.getPos().y-(getPos().y), obj.getPos().x-(getPos().x));

      velocity.x = -cos(push)*1.5;
      velocity.y = -sin(push)*1.5;
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

  void kill() {
    humanRef.kill();
  }

  int getCurrentSectorX() {
    return humanRef.getCurrentSectorX();
  }
  int getCurrentSectorY() {
    return humanRef.getCurrentSectorY();
  }
  void setCurrentSectorX(int x) {
    humanRef.setCurrentSectorX(x);
  }
  void setCurrentSectorY(int y) {
    humanRef.setCurrentSectorY(y);
  }

  void setStamina(int set) {
    humanRef.setStamina(set);
  }

  PVector getPos() {
    return humanRef.getPos();
  }

  void setReadyForTransfer(boolean set) {
    humanRef.setReadyForTransfer(set);
  }

  Timer getTimer() {
    return humanRef.getTimer();
  }

  void setTimer(Timer set) {
    humanRef.setTimer(set);
  }

  Inventory getInventory() {
    return humanRef.getInventory();
  }

  void posAdd(PVector v) {
    humanRef.posAdd(v);
  }

  void posSet(PVector v) {
    humanRef.setPos(v);
  }

  int getH() {
    return humanRef.getH();
  }

  String getType() {
    return humanRef.getType();
  }

  int getW() {
    return humanRef.getW();
  }

  float getAccuracy() {
    return humanRef.getAccuracy();
  }

  boolean isAlive() {
    return humanRef.isAlive();
  }

  void setType(String set) {
    humanRef.setType(set);
  }

  Item getHeldObject() {
    return humanRef.getHeldObject();
  }

  void dropItem () {
    humanRef.drop();
  }

  void setHeldObject(Item r) {
    humanRef.setHeldObject(r);
  }

  float getStamina() {
    return humanRef.getStamina();
  }

  boolean staminaIsFull() {
    return humanRef.staminaIsFull();
  }

  int getMaxStamina() {
    return humanRef.getMaxStamina();
  }

  int getHP() {
    return humanRef.getHP();
  }

  void setHP(int set) {
    humanRef.setHP(set);
  }

  void regenStamina(float dt) {
    humanRef.regenStamina(dt);
  }

  boolean useStamina(int value) {
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

        velocity.x = -cos(push)*1.5;
        velocity.y = -sin(push)*1.5;
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



  void transferSector() {
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
