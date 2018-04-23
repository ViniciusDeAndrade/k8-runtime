package com.example;

/**
 * this class helps the task 2 to deal with the list
 * @author Vinicius
 *
 */
public class Node {

	private int data;
	private Node next = null;
	private Node before = null;
	
	public Node(int data) {
		this.data = data;
	}

	public int getData() {
		return data;
	}

	public void setData(int data) {
		this.data = data;
	}

	public Node getNext() {
		return next;
	}

	public void setNext(Node next) {
		this.next = next;
	}

	public Node getBefore() {
		return before;
	}

	public void setBefore(Node before) {
		this.before = before;
	}
	
	
	
	

}
