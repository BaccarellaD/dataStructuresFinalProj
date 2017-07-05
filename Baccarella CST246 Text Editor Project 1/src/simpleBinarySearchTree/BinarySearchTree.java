package simpleBinarySearchTree;

public class BinarySearchTree <T extends Comparable>{

	private Node<T> root;
	
	public BinarySearchTree() {
		root = null;
	}
	public T find(Comparable key){//allows the BST to check any object, not type safe though
		Node<T> cur = root;
		while(cur.obj.compareTo(key) != 0){
			if(cur.obj.compareTo(key) > 0){ 
				cur = cur.left;
			}else{
				cur = cur.right;
			}
			if(cur == null){
				return null;
			}
		}
		return cur.obj;
	}
	public void insert(T newObj){
		Node<T> newNode = new Node<T>(newObj);
		
		if(root == null){
			root = newNode;
		}else{
			Node<T> current = root;
			Node<T> parent;
			//int layer = 1;
			while(true){
				//layer++;
				//System.out.println("Layer: " + layer++);
				parent = current;
				if(newObj.compareTo(current.obj) < 0){ //go left
					current = current.left;
					if(current == null){
						parent.left = newNode;
						break;
					}
				}else{ //go right
					current = current.right;
					if(current == null){
						parent.right =  newNode;
						break;
					}
				}
			}
			//System.out.println("Deepest Layer: " + layer);
		}
	}

}
