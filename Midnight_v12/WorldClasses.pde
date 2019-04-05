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

  ArrayList<Percept> LOSAI(Entity agent) {
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

  private void createParticals(int amount, PVector setPos, color setC, int setLife, int setSize) {
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
