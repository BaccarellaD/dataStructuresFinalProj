package genericLinkPack1;

public class GenericLink <T extends Comparable>{
	private T item;
	private GenericLink next;
	public GenericLink(T item){
		this.item = item;
	}
	public void setNext(GenericLink next){
		this.next = next;
	}
	public GenericLink getNext(){
		return next;
	}
	public T getItem(){
		return item;
	}
	public void setItem(T item){
		this.item = item;
	}
	@Override
	public String toString(){
		return item + " ";
	}
}
