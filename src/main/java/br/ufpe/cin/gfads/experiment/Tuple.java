package br.ufpe.cin.gfads.experiment;

public class Tuple {
	
	private final String tech = "Kubernetes";
	//node.js or nginx
	private String app;
	// 1, 2 or 3
	private int replicas;
	//its to decide yet. Now, lets start with 30 
	private long callIndex;
	//time to move 
	private long timeToMove;
	
	public Tuple() {}
	
	public Tuple(String app, int replicas, long callIndex, long timeToMove) {
		this.app = app;
		this.replicas = replicas;
		this.callIndex = callIndex;
		this.timeToMove = timeToMove;
	}
	public String getApp() {
		return app;
	}
	public void setApp(String app) {
		this.app = app;
	}
	public int getReplicas() {
		return replicas;
	}
	public void setReplicas(int replicas) {
		this.replicas = replicas;
	}
	public long getcallIndex() {
		return callIndex;
	}
	public void setcallIndex(long callIndex) {
		this.callIndex = callIndex;
	}
	public long gettimeToMove() {
		return timeToMove;
	}
	public void settimeToMove(int timeToMove) {
		this.timeToMove = timeToMove;
	}
	
	public String getTech() {
		return this.tech;
	}
	@Override
	public String toString() {
		return "tech = " + tech + ", app = " + app + ", replicas = " + replicas + ", callIndex = " + callIndex
				+ ", timeToMove = " + timeToMove ;
	}	
	
	public String toStringCSV() {
		return tech +" , " + app +" , " + replicas +" , " + callIndex +" , " + timeToMove; 
	}
	
}
