package guiPack;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayDeque;
import java.util.Locale;

import simpleBinarySearchTree.BinarySearchTree;

public class StockDataBase {

	private BinarySearchTree<StockDateCouple> stockBST = new BinarySearchTree<StockDateCouple>();
	private long accessTime;
	
	public static final String TIME_PATTERN = "yyyy-MM-dd"; //final string, called from gui as well for consistent formating
	
	
	public StockDataBase() {
		accessTime = -1; //default value
		buildDataBase();
	}
	public long getAccessTime() {
		return accessTime;
	}
	public Double getStockPrice(LocalDate date){ //double allows null to be returned
		long timeBefore = System.currentTimeMillis();
		StockDateCouple found = stockBST.find(date);
		long timeAfter = System.currentTimeMillis();
		accessTime = timeAfter - timeBefore;
		if(found == null){ //returns null if date wasn't found
			return null;
		}else{
			return found.getStockPrice();
		}
	}
	private void buildDataBase(){
		try {

			BufferedReader br = new BufferedReader(new FileReader(new File("stockData/stockPrices.txt")));
			ArrayDeque<String> queue = new ArrayDeque<String>();
			String line = null;
			int lineCount = 0;
			while((line = br.readLine()) != null){//will pull lines from stock file and place them in a queue
				queue.add(line);
				lineCount++;
			}
			
			br.close();
			
			StockDateCouple[] couples = new StockDateCouple[lineCount]; //an array of couples for sequential order
			
			/*
			 */
			String curLine = queue.removeFirst(); //current line pulled from queue
			String[] coupleString = curLine.split("\\s+");//splits lines on spaces
			String dateString = coupleString[0].substring(3, coupleString[0].length());
			LocalDate curDate = stringToLocalDate(dateString); //pulls first string and uses it as date, !crops out strange start of text chars!
			double curDouble = Double.parseDouble(coupleString[1]); //pulls second string and uses it as the double
			couples[0] = new StockDateCouple(curDate, curDouble);
			/*
			 */
			
			for(int i = 1; i < lineCount; i++){ //iterates through queue
				curLine = queue.removeFirst(); //current line pulled from queue
				coupleString = curLine.split("\\s+");//splits lines on spaces
				curDate = stringToLocalDate(coupleString[0]); //pulls first string and uses it as date
				try{
					curDouble = Double.parseDouble(coupleString[1]); //pulls second string and uses it as the double
				}catch(NumberFormatException e){
					curDouble = -1;//if value is blank or listed as a '.' the value is set to -1
				}
				
				couples[i] = new StockDateCouple(curDate, curDouble);
			}
			
			addArrayRecursively(couples, 0, couples.length-1); //adds array recursively
			
			
		} catch (IOException e) {
			//if dictionary file isn't found
			//add error msg.
			e.printStackTrace();
		}
	}
	private LocalDate stringToLocalDate(String sDate){
		DateTimeFormatter dFormat = DateTimeFormatter.ofPattern(TIME_PATTERN); //specifies pattern
		dFormat = dFormat.withLocale(Locale.US); //tbh not 100% sure what this does, just a check I think, not really needed
		return LocalDate.parse(sDate, dFormat);//parses string with sDate
	}
	
	private void addArrayRecursively(StockDateCouple[] couples, int leftPtr, int rightPtr){//should balance the tree perfectly if ordered (hopefully)
		if(leftPtr > rightPtr){ //returns when pointers cross
			return;
		}
		int mid = (int)((rightPtr + leftPtr)/2); //mid is the middle of pointers
		stockBST.insert(couples[mid]); //adds mid couple
		addArrayRecursively(couples, leftPtr, mid-1); //add left side
		addArrayRecursively(couples, mid+1, rightPtr);//add right side
	}
}
