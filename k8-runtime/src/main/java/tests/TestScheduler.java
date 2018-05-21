package tests;

import java.util.List;

import environment.SetEnvironment;
import io.kubernetes.client.ApiClient;
import io.kubernetes.client.apis.CoreV1Api;
import io.kubernetes.client.models.V1Pod;
import scheduler.Schedule;

public class TestScheduler {
	public static void main(String[] args) throws Exception {

		System.out.println("teste");
		/*
		ApiClient client = SetEnvironment.SetEnv();
		Schedule schedule = new Schedule();
		String namespace = "teste";
		String scheduleName = "gfads";
		CoreV1Api core = new CoreV1Api(client);

		List<V1Pod> pods = schedule.getScheduledPods(core, namespace, scheduleName);

		for (V1Pod pod : pods) {
			System.out.println(pod.getMetadata().getName());
		}
		 */

	}
}