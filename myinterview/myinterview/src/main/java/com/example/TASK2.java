package com.example;

import java.util.LinkedList;
import java.util.List;

/**
 * Task here is to write a list. Each element must know the element before and
 * after it. Print out your list and them remove the element in the middle of
 * the list. Print out again.
 *
 * 
 */
public class TASK2 {

	private Node tail = null, head = null;
	private int len = 0;

	/*
	 * consider the first position as 1
	 */
	public boolean removeFromList(int pos) {
		if(this.len == 0 || pos < 1 || pos > this.len)
			return false;
		Node aux = this.head;
		System.out.println("head = " + this.head.getData());
		System.out.println("tail = " + this.tail.getData());
		while(pos != 1) {
			pos--;
			aux = aux.getBefore();
			System.out.println("aux pointer = " + aux.getData());
		}
		aux.getBefore().setNext(aux.getNext());
		aux.getNext().setBefore(aux.getBefore());
		this.len--;
		return true;
	}
	public void addToList(int data) {
		Node newNode = new Node(data);
		if(this.len == 0) {
			this.tail = newNode;
			this.head = newNode;
		}else {
			this.tail.setBefore(newNode);
			newNode.setNext(this.tail);
			this.tail = newNode;
		}
		this.len++;
	}
	public List<Node> getNodes() {
		List<Node> nodes = new LinkedList<Node>();
		Node aux = head;
		while(aux != null) {
			nodes.add(aux);
			aux= aux.getBefore();
		}
		return nodes;
	}

	public static void main(String[] args) {

		TASK2 task = new TASK2();
		int data1 = 5;
		int data2 = 2;
		int data3 = 9;
		int data4 = 4;
		int data5 = 10;
		int data6 = 3;
		task.addToList(data1);
		task.addToList(data2);
		task.addToList(data3);
		task.addToList(data4);
		task.addToList(data5);
		task.addToList(data6);

		for (Node node : task.getNodes()) {
			System.out.println("dado de entrada = " + node.getData());
		}
		System.out.println("---------------");
		int posicaoParaRemover = 4;
		task.removeFromList(posicaoParaRemover);
		
		System.out.println("--------------------------");
		for (Node node : task.getNodes()) {
			System.out.println("dados pos remoção = " + node.getData());
		}
	}

}
