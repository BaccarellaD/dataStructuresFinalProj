package guiPack;

import java.util.regex.Matcher;

public class Readability2 extends Helper{
	
	private double timeToComplete;
	private double timeBefore;
	private double timeAfter;
	
	public Readability2(String text) {
		super(text);
		analyse();
	}
	
	private void analyse(){
		timeBefore = System.currentTimeMillis();
		Matcher matchSentences = super.sentencePattern.matcher(super.getText());
		Matcher matchWords = super.wordPattern.matcher(super.getText());
		Matcher matchSyllables = super.syllablePattern.matcher(super.getText());
		super.wordCount = 0;
		super.syllableCount = 0;
		super.sentenceCount = 0;
		
		boolean workingSen = true;
		boolean workingWord = true;
		boolean workingSyl = true;
		
		while(workingSyl || workingWord || workingSen){
			
			if(workingSen){ //<- this structure minimizes setting variables, results are more optimized
				if(matchSentences.find()){ //if another sentence is found
					sentenceCount++; //sent count is incremented 
				}else{
					workingSen = false; //else boolean is set to false and block is never entered again
				}
			}
			if(workingWord){
				if(matchWords.find()){
					wordCount++;
				}else{
					workingWord = false;
				}
			}
			if(workingSyl){
				if(matchSyllables.find()){
					syllableCount++;
				}else{
					workingSyl = false;
				}
			}
		}
		timeAfter = System.currentTimeMillis();
		timeToComplete = timeAfter - timeBefore;
	}
	/*
	private void analyse(){ //this algorithm was slower
		timeBefore = System.currentTimeMillis();
		Matcher matchSentences = super.sentencePattern.matcher(super.getText());
		Matcher matchWords = super.wordPattern.matcher(super.getText());
		Matcher matchSyllables = super.syllablePattern.matcher(super.getText());
		super.wordCount = 0;
		super.syllableCount = 0;
		super.sentenceCount = 0;
		
		boolean working = true;
		
		while(working){
			working = false;
			if(matchSentences.find()){
				sentenceCount++;
				if(!working){
					working = true;
				}
			}
			if(matchWords.find()){
				wordCount++;
				if(!working){
					working = true;
				}
			}
			if(matchSyllables.find()){
				syllableCount++;
				//working = true;
				if(!working){
					working = true;
				}
			}
		}
		timeAfter = System.currentTimeMillis();
		timeToComplete = timeAfter - timeBefore;
	}*/

	@Override
	public double getFleschScore() {
		double avgSentLength = (double)getNumWords() / (double)getNumSentences(); 
		double avgSylPerWord = (double)getNumSyllables() / (double)getNumWords();

		return (206.835 - (1.015 * avgSentLength)) - (84.6 * avgSylPerWord);
	}

	@Override
	public int getNumSyllables() {
		return super.syllableCount;
	}

	@Override
	public int getNumWords() {
		return super.wordCount;
	}

	@Override
	public int getNumSentences() {
		return super.sentenceCount;
	}

	@Override
	public double getTimeToComplete() {
		return timeToComplete;
	}
}
