
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

  void action(Human obj) {
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

  void useMedkit(Human actor) {
    actor.setHP(actor.getHP()+1);
    this.remove = true;
  }

  void reload(Human obj) {
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

  void update(float dt) {
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

  void render() {
    if (held == false) {
      image(sprite, getPos().x, getPos().y, w, h);
    }
  }
}
class Bullet extends Entity {
  PVector speed;

  void isPunch() {
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

  void update(float dt) {
    if (!worldInstance.getIfCol(this)) { 
      PVector change = velocity.get();
      //change.mult(dt*100);
      this.posAdd(change);

      velocity = speed;
    } else {
      velocity = new PVector(0, 0);
    }
  }

  void render() {
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

  Item getItem(int index) {
    return inventoryItems[index];
  }

  void setSelected(float set) {
    selected = constrain(set, -1, inventoryItems.length);
    if (selected == -1)
      selected = inventoryItems.length-1;
    else if (selected == inventoryItems.length)
      selected = 0;
  }

  Item getSelected() {
    return inventoryItems[(int)selected];
  }

  void removeSelectedItem() {
    inventoryItems[(int)selected].held = false;
    inventoryItems[(int)selected] = null;
  }

  Item findItemInInventory(String type) {
    for (int i = 0; i < inventoryItems.length; i++) {
      if (inventoryItems[i] != null && inventoryItems[i].type == type && inventoryItems[i].stock < inventoryItems[i].maxStock) {
        return inventoryItems[i];
      }
    }
    return null;
  }

  Item findStockInInventory(String type) {
    for (int i = 0; i < inventoryItems.length; i++) {
      if (inventoryItems[i] != null && inventoryItems[i].type == type) {
        return inventoryItems[i];
      }
    }
    return null;
  }

  boolean putItemIn(Item item) {
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

  void setItem(float index, Item set) {
    inventoryItems[(int)index] = set;
  }

  void update() {
    for (int i = 0; i < inventoryItems.length; i++) {
      if (inventoryItems[i] != null && inventoryItems[i].remove == true)
        inventoryItems[i] = null;
    }
  }

  void render(int x, int y) {
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
