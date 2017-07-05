package simpleBinarySearchTree;

public class Node <T extends Comparable>{

	protected Node<T> left;
	protected Node<T> right;
	
	protected T obj;
	
	protected Node(T obj) {
		this.obj = obj;
		left = null;
		right = null;
	}
	
}
