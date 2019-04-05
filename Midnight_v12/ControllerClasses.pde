import org.gamecontrolplus.gui.*;
import org.gamecontrolplus.*;
import net.java.games.input.*;

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

  String menuControl() {
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

  PVector getOffsetScreen() {
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

  boolean shoot() {
    if (gpad != null && gpad.getButton("shoot").pressed() && !p_gpadShoot) {
      p_gpadShoot = true;
      p_keyboadShoot = true;
      return true;
    }

    if (gpad != null)
      p_gpadShoot = gpad.getButton("shoot").pressed();

    return false;
  }


  boolean sprint() {
    if (takeKeyboard && mousePressed && mouseButton == RIGHT || (gpad != null && gpad.getButton("sprint").pressed()))
      return true;
    else
      return false;
  }


  boolean reload() {
    if (takeKeyboard && keyPressed == true && key == 'r' || (gpad != null && gpad.getButton("reload").pressed())) {
      return true;
    }
    return false;
  }

  boolean drop() {
    if (takeKeyboard &&keyPressed == true && key == 'f' || ( gpad != null && gpad.getButton("drop").pressed())) {
      return true;
    }
    return false;
  }

  boolean map() {
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

  boolean stim() {
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

  String itemselect() {
    String pressed = "nothing";
    if (gpad != null && gpad.getHat("itemselect").getX() > 0.0) {
      if (p_gpadItemSelectKey) {
        pressed = "right";
        p_gpadItemSelectKey = false;
      }
    } 
    if (gpad != null && gpad.getHat("itemselect").getX() < 0.0) {
      if (p_gpadItemSelectKey) {
        pressed = "left";
        p_gpadItemSelectKey = false;
      }
    }
    if (gpad != null && gpad.getHat("itemselect").getX() == 0.0) {
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

  boolean restart() {
    if (gpad != null && gpad.getButton("restart").pressed() && p_gpadRestartKey == false) {
      p_gpadRestartKey = gpad.getButton("restart").pressed();
      return true;
    }
    if (gpad != null)
      p_gpadRestartKey = gpad.getButton("restart").pressed();
    return false;
  }

  boolean pause() {
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

void mouseWheel(MouseEvent event) {
  float e = event.getCount();
  player1.getInventory().setSelected((player1.getInventory().selected + 0.5*e));
}
