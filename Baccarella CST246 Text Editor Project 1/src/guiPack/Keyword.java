package guiPack;

import genericLinkPack1.GenericLinkedList;

public class Keyword implements Comparable<Keyword>{ //this item stores the keyword and a list of words that follow it
	private String word;
	private GenericLinkedList<String> followingWords;
	public Keyword(String word) {
		this.word = word;
		followingWords = new GenericLinkedList<String>();
	}
	public void addFollowingWord(String fWord){
		followingWords.add(fWord);
	}
	public String getRandomFollowing(){
		return followingWords.getRandom(); //calls the get random function from the list of following words
	}
	public int numOfFollowingWords(){
		return followingWords.getNumItems();
	}
	public String getWord(){
		return word;
	}
	@Override
	public String toString(){
		return "KeyWord: " + word + " || FollowingWords: [" + followingWords + "]";
	}
	@Override
	public int compareTo(Keyword k) {
		return word.compareTo(k.getWord());
	}
}
