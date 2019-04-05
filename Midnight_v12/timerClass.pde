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

  void render() {
    fill(127);
    ellipse(0, -8, 8, 8);
    fill(200);
    arc(0, -8, 7, 7, radians(-90), radians((clock/targetClock)*360-90), PIE);
  }

  void render(PVector pos) {
    fill(127);
    ellipse(pos.x, pos.y-8, 8, 8);
    fill(200);
    arc(pos.x, pos.y-8, 7, 7, radians(-90), radians((clock/targetClock)*360-90), PIE);
  }

  void update(Human actor, float dt) {
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
