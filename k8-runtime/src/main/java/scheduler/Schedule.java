package scheduler;

import java.util.LinkedList;
import java.util.List;

import io.kubernetes.client.ApiException;
import io.kubernetes.client.apis.CoreV1Api;
import io.kubernetes.client.models.V1Pod;
import io.kubernetes.client.models.V1PodList;

public class Schedule {

	public List<V1Pod> getScheduledPods(CoreV1Api core, String namespace, String schedulerName) throws Exception {
		V1PodList list = core.listNamespacedPod(namespace, null, null, null, null, null, null);
		List<V1Pod> podsScheduleds = new LinkedList<V1Pod>();
		for (V1Pod item : list.getItems()) 
			if(!item.getSpec().getSchedulerName().isEmpty() && 
					item.getSpec().getSchedulerName().equals(schedulerName)) {
					podsScheduleds.add(item);			
		}
		
		return podsScheduleds;
	}
	
	public static void main(String[] args) {
		System.out.println("teste");
	}
}
