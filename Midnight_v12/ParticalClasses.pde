class Partical extends Entity {
  color c;
  int lifetime;
  int size;

  PImage sprite;

  Partical(PVector setPos, color setC, int setLife, int setSize) {
    setPos(setPos);
    type = "partical";
    c = setC;
    lifetime = setLife;
    size = setSize;
  }

  void update(float dt) {
    lifetime--;
    
    getPos().add(velocity);
    
    if (lifetime < 0){
     remove = true; 
    }
  }

  void render() {
    if (sprite == null)
    {
      stroke(c);
      strokeWeight(size);
      point(getPos().x, getPos().y);
      }
    }
  }
