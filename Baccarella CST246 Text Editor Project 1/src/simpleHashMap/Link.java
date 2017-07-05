package simpleHashMap;

public class Link {

	private String item;
	private Link next;
	
	public Link(String item){
		this.item = item;
	}
	public void setNext(Link next){
		this.next = next;
	}
	public Link getNext(){
		return next;
	}
	public String getItem(){
		return item;
	}
	public void setItem(String item){
		this.item = item;
	}
	@Override
	public String toString(){
		return item + " ";
	}
	
}
