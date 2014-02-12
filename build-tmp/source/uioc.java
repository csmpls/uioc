import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import processing.serial.*; 
import mindset.*; 
import controlP5.*; 
import java.util.Date; 
import java.util.ArrayDeque; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class uioc extends PApplet {






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

public void setup() {
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
 
public void draw() {
    fill(display.background_color,122);
    rect(-2,-2,width+2, height+2);
    stroke(display.text_color);

    neurosky.update();
    
    display.update_stimulus();
    //log.updateLog(display.getStimulusIndex(), display.getStimulusName());
   
}

public void keyPressed() {
  
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

public boolean sketchFullScreen() {
  return true;
}

public void set_session_id() {
     // get a unix timestamp
  Date d = new Date();
  session_id = String.valueOf(d.getTime()/1000);
}

public void bang() {

  display.show_review = false;
  display.show_stimulus = true;
  cp5.hide();
 
}


public void quit() {
  exit();
}



class Display {

	CheckBox review_panel;
	
	public int stimulusCount = 0;

	boolean show_splashscreen = true;
	
	// float show_stimulus_constant = 50; //ms per character - how long titles will be displayed
	// float stimulus_display_minimum = 5000; //never show a stimulus for fewer than 2000 ms
	// float stimulus_display_maximum = 10000; //or more than 10s
	float reaction_time = 350; // added time after reading for reaction
	float between_stimulus_pause = 1000; //ms

	// for testing
	float show_stimulus_constant = 3; //ms per character - how long titles will be displayed
	float stimulus_display_minimum = 500; //never show a stimulus for fewer than 2000 ms
	float stimulus_display_maximum = 10000; //or more than 10s


	boolean show_stimulus = true;
	boolean show_rest = false;
	boolean show_review = false;
	
	//stimulus timing
	float stimulus_end = 0;  
	float current_display_length; //how long current slide should be shown for
	float rest_end;
	
	//colors
	int color_num = 1;
	int num_colors = 4;

	public int background_color, text_color,secondary_text_color;

	Reddit reddit;
	
	PFont font;
	PFont second_font;


	Display() {

		reddit = new Reddit();

		current_display_length = reddit.getInitialLength() * show_stimulus_constant;
	    // set this stimulus's end time
	    stimulus_end = millis() + current_display_length + reaction_time;
	    // set the end of the rest period
	    rest_end = millis() + between_stimulus_pause;
			
	    font =  loadFont("LMSans.vlw");
	    second_font = loadFont("Monoxil-Regular-68.vlw");
		change_colors();
	}
	
	public Reddit getReddit() {
  		return reddit;
	}
	
	public void advance() {
		reddit.advance();
		stimulusCount++;
	}
	
	public int getStimulusIndex() {
		if(show_stimulus) {
			return stimulusCount;
		}
		else {
			return -1;
		}
	}

    public String getStimulusName() {
		if(show_stimulus) {
		  return reddit.currentArticle.title;
		}
		else {
		  return "";
		}
	}

	





	public void update_stimulus() {

		/// show an initial splash at first to make sure setup is working
		if (show_splashscreen) {
			drawSplashInterface();			
		}
  		

  		// draw interface in which titles are displayed
  		else {

			if (show_stimulus) {

				drawRedditInterface();

				// remve stimulus, show rest screen
				if (millis() > stimulus_end) {

					// the article we just saw,
					// store it in the review queue
					feedback.add_to_memory(reddit.currentArticle.title);

					reddit.advance();
					stimulusCount++;

					// set the length for which this should be displayed
					current_display_length = show_stimulus_constant*reddit.currentArticle.title.length();
					current_display_length = constrain(current_display_length, stimulus_display_minimum, stimulus_display_maximum);

					// set this stimulus's end time
					stimulus_end = millis() + current_display_length;

					// set the end of the rest period
					rest_end = millis() + between_stimulus_pause;



					// here is where we would see if the queue is full
					// if it is, takes us to some mode where we're reviewing
					// from that mode, a button makes show_stimulus true
					if (feedback.is_memory_full()) {
						setup_review_interface();
						show_review = true;
					} else {
						show_rest = true;
					}


					// now hide the stimulus - its time for the rest period
					show_stimulus = false;
					// a boolean here to distinguish types of rest
				}
			} else if (show_rest) {

				drawRestInterface();

				if (millis() > rest_end) {
				  // show the next stimulus
				  show_rest = false;
				  show_stimulus = true;    
				}

			} else if (show_review) {


				
			}

		}
		
	}


	


	public void setup_review_interface() {

		cp5.show();

		review_panel =  cp5.addCheckBox("review_panel")
                .setPosition((width/2) - 400, 240)
                .setColorForeground(color(120))
                .setColorActive(color(255))
                .setColorLabel(color(255))
                .setSize(40, 40)
                .setItemsPerRow(2)
                .setSpacingColumn(500)
                .setSpacingRow(20)
                ;

        int i = 0;
        for (String str : feedback.stories) {
        	println(str);
        	review_panel.addItem(Integer.toString(i), i);
        	i++;
        }

        feedback.clear();

        cp5.addBang("bang")
			.setPosition((width/2)+400, 900)
			.setSize(280, 40)
			.setTriggerEvent(Bang.RELEASE)
			.setLabel("ok >")
			;
	}


	public void change_colors() {

	  color_num++;
	  if (color_num > num_colors) {
	    color_num=0; }
    
	    //dark default
	    if (color_num == 0) {
	     background_color = color(48,16,45);
	      text_color = color(244,223,241);
	      secondary_text_color = color(53,159,120);
	    }

	  if (color_num == 1) {

	     // pinky
	     background_color = color(157,68,52);
	     text_color = color(226,179,168);
	     secondary_text_color = color(82,24,24);
	  }

	  if (color_num == 2) {
	    // light blue
	    background_color = color(26, 43, 79);
	    text_color = color(216, 224, 242);
	    secondary_text_color = color(61, 184, 98);
	  }

	  if (color_num == 3) {
	    // vintage yellow/white-on-blue
	    background_color = color(54, 41, 124);
	    text_color = color(236, 228, 246);
	    secondary_text_color = color(255, 255, 0);
	  }
  
	  if (color_num == 4) {
	    // aquamarine
	    background_color = color(53, 160, 144);
	    text_color = color(22, 67, 54);
	    secondary_text_color = color(200,234,236);
	  }
  
	  if (color_num == 5) {
	    // autumnal king
	    background_color = color(29,88,44);
	    text_color = color(191,178,64);
	    secondary_text_color = color(210,198,197);
	  }
	}
	
	public void drawRedditInterface() {

	  int x = 120;
	  int y = 60;
	  int tbox_topbar_padding = 10;
	  int topbar_height = 50;
  
	    int tbox_width = width-x-x-20;
  
  
	    fill (secondary_text_color);
	    textAlign(LEFT, CENTER);
	    textFont(second_font,24);
	    text(reddit.currentArticle.subreddit,
	    x, y, tbox_width, topbar_height);
    
	    fill(text_color);
	    textAlign(LEFT, TOP);
	    textFont(font,68);
	    text(reddit.currentArticle.title, 
	    x, y+tbox_topbar_padding+topbar_height, tbox_width, height-10);
	}

	public void drawRestInterface() {
	}

	public void drawSplashInterface() {
	  int x = 120;
	  int y = 60;
	  int tbox_topbar_padding = 10;
	  int topbar_height = 50;
  
	    int tbox_width = width-x-x-20;


	    fill(text_color);
	    textAlign(LEFT, TOP);
	    textFont(font,68);
	    text(getSplashMessage(), 
	    x, y+tbox_topbar_padding+topbar_height, tbox_width, height-10);

	}

	public void drawReviewInterface() {
		int x = 120;
		int y = 60;
		int tbox_topbar_padding = 10;
		int topbar_height = 50;
	}

	public String getSplashMessage() {
		String attn = Float.toString(neurosky.attn);
		String med = Float.toString(neurosky.med);
		String message = "Hi, welcome to the testing protocol for interest miner. When you seem to be getting a live feed of plausible values (1-100), press 'k' to start viewing the stimuli. (And press 'c' to change colors.)\n\n";
		return message + attn + "    " + med;
	}
}

class FeedbackManager {
	
	int deck_size;

	ArrayDeque<String> stories;

	FeedbackManager(int n) {
		deck_size = n;
		stories = new ArrayDeque<String>();
	}

	public void add_to_memory(String article_name) {
		stories.addLast(article_name);
	}

	public boolean is_memory_full() {
		if (stories.size() == deck_size) {
			return true;
		}
		return false;
	}

	public void clear() {
		stories.clear();
	}
}
public class Reddit {
  
  
  // you need your private home feed json.
  //find this by going to to Preferences > RSS Feeds 
  //then, next to Private Listings > your front page, copy the JSON link.
  JSONObject json;

  int curr_time = 0;

  ArrayList articles = new ArrayList();
  int curr = 0; //current article index
  Article currentArticle; // current title


  Reddit() {
    
    // get the json array the holds the list of articles
    JSONObject feed = loadJSONObject(articles_file);
    feed = feed.getJSONObject("data");
    JSONArray values = feed.getJSONArray("children");
  
    for (int i = 0; i < values.size(); i++) {
      
      // get the payload
      JSONObject article = values.getJSONObject(i); 
      article = article.getJSONObject("data");
  
      //parse the payload
      String title = article.getString("title");
      String url = article.getString("url");
      String subreddit = article.getString("subreddit");
      
      //add the parsed article to our list
      Article a  = new Article(title, url, subreddit);
      articles.add(a);
      
    }
    
    // set the first article
    currentArticle = (Article)articles.get(curr);
    
  }
	
	public int getInitialLength() {
		// set stimulus display timing for the first article
    // set the length for which this should be displayed
    return currentArticle.title.length();
	}
	
	
  
  public int advance() { 
    curr++;

    if (curr > articles.size()-1) {
      quit();
      return 0;
    }
    
    //TODO: timestamped change to a log
    
    //set this as our current article
    currentArticle = (Article)articles.get(curr);
    return 1;
    
  }
  
  public void markCurrentAsCool() {
    Article a = (Article)articles.get(curr);
    a.setCool();
  }

}

public class Article {

  public String title;
  public String url;
  public String img;
  public String subreddit;
  public boolean isCool;
  
  Article (String s, String u, String sr) {
    title = s;
    url = u;
    subreddit = sr;
    isCool = false;
  }

  Article () {
    title = "error!!";
  }
  
  public void setCool() {
    isCool = true;
  }
}

public class HTML {
    
  public String getLeadingHTML() {
  	return "<!doctype HTML>\n" +
          "<head><script src='http://code.jquery.com/jquery-1.9.1.js'></script></head>\n" + 
  	"<body>\n" +
          "<h1>check the articles you were interested in at the time you saw them.</h1>" + 
          "<div class = 'squaredOne'>\n" +
  	"<form name='reviewform' action='http://cosmopol.is/interestminer/index.py/' method='POST'>\n" +
    "<input type ='text' visibility='hidden' name='session-id' value = " + session_id + ">this is your session id - don't change (this will be hidden soon)<p>\n";
  }
  
  public String articleToHTML(Article a, int index) {
  	return "<input type='checkbox' class = 'big-check' name = '" + index + "'>" 
  + (int)(index) + " - " + a.title + "</p>";
  }
  
  public String getTrailingHTML() {
  	return 
          "<input type='submit'></form>\n" +
          "</div>\n" +
          "</body>\n" + 
  	"</html>";
  }  

}
/*
NEUROSKY
yes@cosmopol.is

hand-rolled in los angeles
august 2011

* * * / 

this class stores data from a neurosky mindset. 
it uses those data to calculate some in-house metrics:

float attn,
float med
  0-100 - e-sense attention/meditation score. 
  (these scores are produced by dark magic
  (ML) inside the neurosky API.)
  
float attn_pulse, 
float med_pulse
  0-100 - eased/smoothed version of attn
  and med. ideally, these values guard
  against the spikes we sometimes see
  in the the e-sense readings.
  
*/

public class Neurosky {
  PApplet parent;
  MindSet ns;
  
  String com_port;
  boolean god;
  
  float attn;
  float med;
  
  int alpha1, alpha2, beta1, beta2, delta, gamma1, gamma2, theta;
  
  float attn_pulse;
  float med_pulse;
  
  boolean is_meditating = false;
  boolean is_attentive = false;
  
  boolean has_initialized = false;

   float pulse_easing = .1f; 

  public void initialize(PApplet parent, String com_port, boolean god) {
    this.god = god;
    this.parent = parent;
    this.com_port = com_port;
    ns = new MindSet(parent);
    ns.connect(this.com_port);
  }
  
  public int update() {
    
    try {
        med = ns.data.meditation; 
        attn = ns.data.attention; 
        
        alpha1 = ns.data.alpha1;
        alpha2 = ns.data.alpha2;
        beta1 = ns.data.beta1;
        beta2 = ns.data.beta2;
        delta = ns.data.delta;
        gamma1 = ns.data.gamma1;
        gamma2 = ns.data.gamma2;
        theta = ns.data.theta;
     
      
        set_attn_pulse();
        set_med_pulse();
      
      
      
      
    } catch( ArrayIndexOutOfBoundsException e ) {
        return 1;
      }
     
     return 0;
    }
  
  public void set_attn_pulse() {
    attn_pulse += (attn - attn_pulse) * pulse_easing;
    attn_pulse = constrain(attn_pulse, 0.0f, 100.0f);
  }
  
  public void set_med_pulse() {
    med_pulse += (med - med_pulse) * pulse_easing;
    med_pulse = constrain(med_pulse, 0.0f, 100.0f);
  }

}
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "uioc" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
