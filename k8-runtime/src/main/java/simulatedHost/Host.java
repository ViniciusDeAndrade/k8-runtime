package simulatedHost;

public class Host {

	//memory in GB
	private int memory;
	//processor in GHz
	private int processor;
	
	public Host(int memory, int processor) {
		this.memory = memory;
		this.processor = processor;
	}

	public int getMemory() {
		return memory;
	}

	public int getProcessor() {
		return processor;
	}
	
	
}
