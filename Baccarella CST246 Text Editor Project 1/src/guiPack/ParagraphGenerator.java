package guiPack;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import genericLinkPack1.GenericLinkedList;
public class ParagraphGenerator {
	private GenericLinkedList<Keyword> keywords;
	private File importFile;
	
	public ParagraphGenerator(File f){
		importFile = f;
		keywords = new GenericLinkedList<Keyword>();
		importWords();
	}
	public String generateParagraph(int numWords, String s) throws WordNotFoundException{
		
		//double startTime = System.currentTimeMillis();
		StringBuilder paragraph = new StringBuilder();
		Keyword word = keywords.searchSequential(new Keyword(s));//search keywords for word
		if(word == null){
			throw new WordNotFoundException(); //this is thrown if the seed word is not found
		}
		for(int i = 0; i < numWords; i++){ //loops until it gets up to num words to generate
			paragraph.append(word.getWord() + " "); //will add the word to the paragraph
			if(i % 10 == 0 && i != 0){ //if the tenth word, then indent
				paragraph.append("\n");
			}
			word = keywords.searchSequential(new Keyword(word.getRandomFollowing())); //finds the keyword, of a random following word of the previous word
			if(word == null){//checks to see if word has a word following it, only needed if the last word imported is unique, but its good to have
				word = keywords.getRandom();
			}
		}
		//double endTime = System.currentTimeMillis();
		//System.out.println("Milliseconds to generate: " + (endTime - startTime));
		return paragraph.toString();
	}
	private void importWords(){
		
		try {
			StringBuilder fullString = new StringBuilder();//file built into this
			BufferedReader br = new BufferedReader(new FileReader(importFile));
			String line;
			while((line = br.readLine()) != null){//builds sb
				fullString.append(line + "\n");
			}
			br.close();
			
			Pattern wordPattern = Pattern.compile("(\\w*['’-]*\\w)+"); //pattern to pull out words
			Matcher wordMatch = wordPattern.matcher(fullString.toString()); //sends the imported file to the matcher
			if(!wordMatch.find()){ //if the file is blank it returns
				return; //didn't find any matches
			}
			String prevWord = wordMatch.group().toLowerCase(); //sets prev word to match
			Keyword firstKeyword = new Keyword(prevWord); //makes it a keyword
			Keyword prevKeyWord = firstKeyword; //sets it to prev keyword
			keywords.add(firstKeyword);//adds first keyword to list
			wordMatch.find(); //finds another word
			String curWord = wordMatch.group().toLowerCase(); //sets that word to the cur word
			
			while(wordMatch.find()){//while there's still words
				Keyword curKeyWord = keywords.searchSequential(new Keyword(curWord));
				if(curKeyWord == null){ //if keywords does not contain curWord
					curKeyWord = new Keyword(curWord);
					keywords.add(curKeyWord);//adds curWord to keywords
				}
				
				prevKeyWord.addFollowingWord(curWord); //adds curword to previous words following list
				prevKeyWord = curKeyWord;
				prevWord = curWord;
				curWord = wordMatch.group().toLowerCase();
			}
			keywords.searchSequential(new Keyword(prevWord)).addFollowingWord(curWord); //adds last word to following words
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void displayDataStructure(){//used for testing
		System.out.println("--------------------");
		int count = 0;
		Keyword cur  = keywords.getItemAtIndex(count);
		while(cur != null){
			System.out.println(cur);
			cur  = keywords.getItemAtIndex(++count);
		}
		System.out.println("--------------------");
	}
	
	public String getRandomKeyWord(){
		return keywords.getRandom().getWord();//gets random keyword to start
	}
}
