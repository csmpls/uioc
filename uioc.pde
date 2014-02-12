import processing.serial.*;
import mindset.*;
import controlP5.*;
import java.util.Date;

/*
        ////////////////////////////////////////////////
        //////////////// A   R   E /////////////////////
        //////////////// Y   O   U /////////////////////
        /////////////////INTERESTED/////////////////////
        !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        !!!!!!!!! HAND-ROLLED IN !!!!!!!!!!!!!!!!!!!!!!!
        !!!!!!!!!!!!!!!!!!!!!!!!!THE XANA-KITCHEN!!!!!!!
        !!!!!!! EVANSTON, IL 2013 !!!! !!!!!!!!!!!!!!!!!
        !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        ////////////////////////////////////////////////
        ////////////////////////////////////////////////
        ////////////////////////////////////////////////
* * */
Neurosky neurosky = new Neurosky();
String com_port = "/dev/tty.MindWave";
Display display;
ControlP5 cp5;
FeedbackManager feedback; // for now, just one global feedback manager
                          // later - specific feedback managers depending
                          // and we extend the general FeedbackManager class

String articles_file = "feed2.json";

String session_id;

void setup() {
  size (displayWidth, displayHeight);
  frameRate(24);
  smooth(); 
  stroke(255);
  textLeading(-5);
  frameRate(24);


  cp5 = new ControlP5(this);
   
	display = new Display();
  neurosky.initialize(this, com_port, false);

  // 5 articles between each review session
  feedback = new FeedbackManager(5);

  smooth();
  noStroke();
}
 
void draw() {
    fill(display.background_color,122);
    rect(-2,-2,width+2, height+2);
    stroke(display.text_color);

    neurosky.update();
    
    display.update_stimulus();
    //log.updateLog(display.getStimulusIndex(), display.getStimulusName());
   
}

void keyPressed() {
  
  if (key == 'j') {
    display.advance(); 
  }
  
  if (key =='c') {
    display.change_colors();
  }

  if (key == 'q') {
    quit();
  }

  if (key == 'k') {
    display.show_splashscreen = false;
  }
  
}

boolean sketchFullScreen() {
  return true;
}

void set_session_id() {
     // get a unix timestamp
  Date d = new Date();
  session_id = String.valueOf(d.getTime()/1000);
}

public void bang() {

  display.show_review = false;
  display.show_stimulus = true;
  cp5.hide();
 
}


void quit() {
  exit();
}

