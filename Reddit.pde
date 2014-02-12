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
	
	int getInitialLength() {
		// set stimulus display timing for the first article
    // set the length for which this should be displayed
    return currentArticle.title.length();
	}
	
	
  
  int advance() { 
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
  
  void markCurrentAsCool() {
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

