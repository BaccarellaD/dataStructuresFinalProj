package simpleHashMap;

public class StringList {

	private Link firstLink;
	
	public StringList() {
		firstLink = null;
	}
	
	public void add(String data){ //adds first link
		Link newLink = new Link(data);
		newLink.setNext(firstLink);
		firstLink = newLink;
	}
	public boolean contains(String item){
		Link curLink = firstLink;
		while(curLink != null){//loops until link is null
			if (curLink.getItem().compareTo(item) == 0){
				return true; //if found returns true
			}
			curLink = curLink.getNext();
		}
		return false;
	}

}
