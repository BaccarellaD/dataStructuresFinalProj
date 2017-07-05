package guiPack;

import java.util.regex.Matcher;

public class Readability1 extends Helper{
	
	private double timeToComplete;
	private double timeBefore;
	private double timeAfter;

	public Readability1(String text) {
		super(text);
		timeToComplete = -1; //genericTime if not yet found 
	}

	@Override
	public int getNumWords() {
		if(super.wordCount == -1){//checks if code already ran
			Matcher matchWords = super.wordPattern.matcher(super.getText());
			super.wordCount = 0;
			while(matchWords.find()){
				super.wordCount++;
			}
		}
		return super.wordCount;
	}

	@Override
	public int getNumSentences() {
		if(super.sentenceCount == -1){//checks if code already ran
			Matcher matchSentences = super.sentencePattern.matcher(super.getText());
			super.sentenceCount = 0;
			while(matchSentences.find()){
				super.sentenceCount++;
			}
		}
		return super.sentenceCount;
	}
	
	@Override
	public int getNumSyllables() {
		if(super.syllableCount == -1){//checks if code already ran
			Matcher matchsyllables = super.syllablePattern.matcher(super.getText());
			super.syllableCount = 0;
			while(matchsyllables.find()){
				super.syllableCount++;
			}
		}
		return super.syllableCount;
	}

	@Override
	public double getFleschScore() {
		
		timeBefore = System.currentTimeMillis();//run flesch score first for accurate time calc
		
		double avgSentLength = (double)getNumWords() / (double)getNumSentences(); 
		double avgSylPerWord = (double)getNumSyllables() / (double)getNumWords();
		
		timeAfter =  System.currentTimeMillis();
		if(timeToComplete == -1){ //makes sure time to complete is only calculated on first call
		timeToComplete = timeAfter - timeBefore;
		}

		return (206.835 - (1.015 * avgSentLength)) - (84.6 * avgSylPerWord);
	}

	@Override
	public double getTimeToComplete() {
		return timeToComplete;
	}
	
	
	
}
