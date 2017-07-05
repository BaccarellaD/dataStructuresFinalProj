package genericLinkPack1;

public class GenericLinkedList <T extends Comparable>{
	private GenericLink<T> firstLink;
	private int numItems;
	public GenericLinkedList(){
		firstLink = null;
		numItems = 0;
	}
	public T getRandom(){
		return getItemAtIndex((int)(Math.random()*numItems));
	}
	public void add(T data){ //adds first link
		GenericLink<T> newLink = new GenericLink<T>(data);
		newLink.setNext(firstLink);
		firstLink = newLink;
		numItems++;
	}
	
	public T remove(T item){
		if(item.equals(firstLink.getItem())){ //checks first link
			return removeFirst();
		}
		T foundItem;
		GenericLink<T> prevLink = firstLink;
		GenericLink<T> curLink = firstLink.getNext();
		while(curLink != null){
			if (curLink.getItem().compareTo(item) == 0){ //if item is the same
				foundItem = (T)curLink.getItem(); //removes the item
				prevLink.setNext(curLink.getNext());
				numItems--;
				return foundItem; //returns the item it found
			}
			prevLink = curLink;
			curLink = curLink.getNext();
		}
		return null; //if it went through and didn't find the item it returns null
	}
	public T removeFirst(){ 
		T firstItem = firstLink.getItem();
		firstLink = firstLink.getNext();
		numItems--;
		return firstItem;
	}
	public T getItemAtIndex(int index){
		int count = 0;
		GenericLink<T> curLink = firstLink;
		while(curLink != null){ //breaks if it gets to a null item
			if(count == index){ //returns the item from the index
				return curLink.getItem();
			}
			count++;
			curLink = curLink.getNext();
		}
		return null;
	}
	public T searchSequential(T item){
		GenericLink<T> curLink = firstLink;
		while(curLink != null){
			if (curLink.getItem().compareTo(item) == 0){ //if item is found
				return curLink.getItem();
			}
			curLink = curLink.getNext();
		}
		return null; //it item wasnt found, returns null
	}
	public int getNumItems(){
		return numItems;
	}
	@Override
	public String toString(){
		String rString = "";
		GenericLink<T> curLink = firstLink;
		while(curLink != null){
			rString += curLink + ", ";
			curLink = curLink.getNext();
		}
		return rString;
	}
}
