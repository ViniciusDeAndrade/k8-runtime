package simulatedHost;

public class CheckResources {
	private Host host;
	
	public CheckResources (int memory, int processor) {
		this.host = new Host(memory, processor);
	}
	
	public boolean check (int memory , int processor) {
		if(memory < this.host.getMemory() && processor < this.host.getProcessor())
			return true;
		else
			return false;
	}
	
	public boolean check (int memory , double processor) {
		if(memory < this.host.getMemory() && processor < this.host.getProcessor())
			return true;
		else
			return false;
	}
}
