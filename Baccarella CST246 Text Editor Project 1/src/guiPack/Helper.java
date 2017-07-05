package guiPack;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class Helper {

	private String text;
	private String syllableFilter = "(?=\\s|d)(ere|are)|((?!e(\\s|[.?!\\:\",])|(?<=\\w([blscfhnk]))((ed)|(es))\\s|(?<![Pp])eo|"
			+ "(?<![st])io|(?=[u])eo)[AEIOUYaeiouy]+)|"
			+ "(?<![AEIOUYaeiouyd])(?<!')[BCDFGHJKLMNPQRSTVXZbdfghjklmnpqrstvwxz]+e(\\s|[.?!\\:\",])"
			+ "|(?<=[kczdbfg])le[AEIOUYaeiouy]*|'[v]|"
			+ "((?<=wre)(tc)|[Nn]s)hed|[EeIi](?=[Oo])[AEIUYaeiuy]*|\\s[Cc]hed\\s";
	
	//"((?!e(\\s|[.?!\",]))[AEIOUYaeiouy]+)|\\s[BCDFGHJKLMNPQRSTVXZbcdfghjklmnpqrstvxz]+e\\s";   : just e
	
	/*full: "(?=\\s|d)(ere|are)|((?!e(\\s|[.?!\\:\",])|(?<=\\w([blscfhnk]))((ed)|(es))\\s|(?<![Pp])eo|"
	+ "(?<![st])io|(?=[u])eo)[AEIOUYaeiouy]+)|"
	+ "(?<![AEIOUYaeiouyd])(?<!')[BCDFGHJKLMNPQRSTVXZbdfghjklmnpqrstvwxz]+e(\\s|[.?!\\:\",])"
	+ "|(?<=[kczdbfg])le[AEIOUYaeiouy]*|'[v]|"
	+ "((?<=wre)(tc)|[Nn]s)hed|[EeIi](?=[Oo])[AEIUYaeiuy]*|\\s[Cc]hed\\s";*/
	
	private String wordFilter = "(\\w*['’-]*\\w)+";
	
	//"[A-Za-z]+[-.:\\/]*[A-Za-z]*"
	
	private String sentenceFilter = "([BCDFGHJKLMNPQRSTVWXZbcdfghjklmnpqrstvwxz]+[AEIOUYaeiouy]+|[AEIOUYaeiouy]+[BCDFGHJKLMNPQRSTVWXZbcdfghjklmnpqrstvwxz]+)+[?.!]+\\s*('|\"|’|‘)*\\s*('|\"|’|‘)*"
									+ "([BCDFGHJKLMNPQRSTVWXZ]+[bcdfghjklmnpqrstvwxz]*[aeiouy]*|[AEIOUY]+[aeiouy]?[bcdfghjklmnpqrstvwxz]?)|.$";

	//
	
	//patterns and counts initialized here
	protected Pattern syllablePattern;
	protected Pattern wordPattern;
	protected Pattern sentencePattern;
	
	protected int wordCount;
	protected int sentenceCount;
	protected int syllableCount;
	
	public Helper(String text){
		this.text = text;
		syllablePattern = Pattern.compile(syllableFilter);//compiles patterns
		wordPattern = Pattern.compile(wordFilter);
		sentencePattern = Pattern.compile(sentenceFilter);
		wordCount = -1;//sets to -1 to allow for readability to only run once with extended classes
		sentenceCount = -1;
		syllableCount = -1;
	}
	
	public List<String> getTokens(String pattern){
		ArrayList<String> tokens = new ArrayList<>();
		Pattern tokenPattern = Pattern.compile(pattern);
		Matcher match = tokenPattern.matcher(text);
		while(match.find()){
			tokens.add(match.group());
		}
		return tokens;
	}
	public int getNumTokens(String pattern){
		int count = 0;
		Pattern tokenPattern = Pattern.compile(pattern);
		Matcher match = tokenPattern.matcher(text);
		while(match.find()){
			count++;
		}
		return count;
	}
	public abstract int getNumSyllables();
	
	public abstract int getNumWords();
	
	public abstract int getNumSentences();
	
	public abstract double getFleschScore();
	
	public abstract double getTimeToComplete();
	
	public String getText(){
		return text;
	}
}
