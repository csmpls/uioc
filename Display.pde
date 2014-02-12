

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

	public color background_color, text_color,secondary_text_color;

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
	
	Reddit getReddit() {
  		return reddit;
	}
	
	void advance() {
		reddit.advance();
		stimulusCount++;
	}
	
	int getStimulusIndex() {
		if(show_stimulus) {
			return stimulusCount;
		}
		else {
			return -1;
		}
	}

    String getStimulusName() {
		if(show_stimulus) {
		  return reddit.currentArticle.title;
		}
		else {
		  return "";
		}
	}

	





	void update_stimulus() {

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


	


	void setup_review_interface() {

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


	void change_colors() {

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
	
	void drawRedditInterface() {

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

	void drawRestInterface() {
	}

	void drawSplashInterface() {
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

	void drawReviewInterface() {
		int x = 120;
		int y = 60;
		int tbox_topbar_padding = 10;
		int topbar_height = 50;
	}

	String getSplashMessage() {
		String attn = Float.toString(neurosky.attn);
		String med = Float.toString(neurosky.med);
		String message = "Hi, welcome to the testing protocol for interest miner. When you seem to be getting a live feed of plausible values (1-100), press 'k' to start viewing the stimuli. (And press 'c' to change colors.)\n\n";
		return message + attn + "    " + med;
	}
}
