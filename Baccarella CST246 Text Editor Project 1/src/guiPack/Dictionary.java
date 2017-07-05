package guiPack;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.File;
import simpleHashMap.MyHashMap;

public class Dictionary {
	
	private MyHashMap dictHM = new MyHashMap(133334);//dictionary hashset, should fill to .75 capacity @ 133334
	
	/*
	 * 	Measured Collisions
	 * 
	 * Array Size		Collisions		% Improvement over 100k		% Expected if linear	% Difference
	 * 100,000			37,145			0							0						0
	 * 125,000			31,858			14.23%						25%						-10.77%
	 * 133,334			30,067			19.06%						33%						-13.94%
	 * 150,000			28,117			24.30%						50%						-25.70%
	 * 200,000			24,271			34.65%						100%					-65.35%
	 * 
	 * Quickly diminishing returns for added array size, 33% larger seems to be a good compromise
	 */
	
	public Dictionary(){
		buildDictionary();
	}
	
	public boolean checkWord(String word){
		return dictHM.contains(word);
	}
	
	private void buildDictionary(){
		try {
			//long before = System.currentTimeMillis();
			BufferedReader br = new BufferedReader(new FileReader(new File("dictionaryFolder/dictionary.txt")));
			String line = null;
			
			while((line = br.readLine()) != null){
				dictHM.insert(line.toLowerCase());//inserts line after its been put as lower case
			}
			//System.out.println("Collisions: " + dictHM.collisions);
			br.close();
			//System.out.println("Time to make dict: " + (System.currentTimeMillis()-before));
		} catch (IOException e) {
			//dictionary not found
			e.printStackTrace();
		}
	}
}
