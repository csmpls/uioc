import java.util.ArrayDeque;
class FeedbackManager {
	
	int deck_size;

	ArrayDeque<String> stories;

	FeedbackManager(int n) {
		deck_size = n;
		stories = new ArrayDeque<String>();
	}

	void add_to_memory(String article_name) {
		stories.addLast(article_name);
	}

	boolean is_memory_full() {
		if (stories.size() == deck_size) {
			return true;
		}
		return false;
	}

	void clear() {
		stories.clear();
	}
}