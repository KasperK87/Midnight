class Percept {
  PVector location;
  String type;

  Percept(String isType, PVector isHere) {
    location = isHere;
    type = isType;
  }

  boolean same(Percept p) {
    if (location.equals(p.location) && type.equals(p.type))
    {
      return true;
    } else {
      return false;
    }
  }
}
