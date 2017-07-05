package simpleHashMap;

public class MyHashMap {

	private StringList[] array;
	private int arraySize;
	public int collisions = 0;
	
	public MyHashMap(int size) {
		array = new StringList[size];
		arraySize = size;
	}
	
	public void insert(String s){
		long preHash = stringHash(s);
		int hash = (int)(preHash % arraySize);
		//System.out.println(s + ": hash: " + hash);
		if(array[hash] == null){
			array[hash] = new StringList();
		}else{
			collisions++;
		}
		array[hash].add(s);
	}
	
	public boolean contains(String s){
		long preHash = stringHash(s);
		int hash = (int)(preHash % arraySize);
		if(array[hash] == null){//if there is no list yet, it isn't in the map
			return false;
		}
		return array[hash].contains(s); //returns true or false, depending on whether or not it finds the string
	}

	private long stringHash(String s) {//turns string into hash value
	     int intLength = s.length() / 4;
	     long sum = 0;
	     for (int j = 0; j < intLength; j++) {
	       char c[] = s.substring(j * 4, (j * 4) + 4).toCharArray();
	       long mult = 1;
	       for (int k = 0; k < c.length; k++) {
			 sum += c[k] * mult;
			 mult *= 256;
	       }
	     }

	     char c[] = s.substring(intLength * 4).toCharArray();
	     long mult = 1;
	     for (int k = 0; k < c.length; k++) {
	       sum += c[k] * mult;
	       mult *= 256;
	     }

	     return(Math.abs(sum));
	   }
}
