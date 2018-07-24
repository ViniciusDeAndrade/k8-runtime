package br.ufpe.cin.gfads.scheduler;

import com.google.common.reflect.TypeToken;

import br.ufpe.cin.gfads.kuber.KubernetesController;
import io.kubernetes.client.ApiClient;
import io.kubernetes.client.ApiException;
import io.kubernetes.client.Configuration;
import io.kubernetes.client.apis.AppsV1beta1Api;
import io.kubernetes.client.apis.CoreV1Api;
import io.kubernetes.client.models.V1Binding;
import io.kubernetes.client.models.V1ObjectMeta;
import io.kubernetes.client.models.V1ObjectReference;
import io.kubernetes.client.models.V1Pod;
import io.kubernetes.client.util.Watch;

/**
 * just to store the old scheduler
 * @author vinicius
 *
 */
@Deprecated
public class Scheduler2 {
	private ApiClient client;
	private CoreV1Api core;
	private AppsV1beta1Api beta;

	public Scheduler2(ApiClient client) {
		this.client = client;
		this.core = new CoreV1Api(client);
		this.beta = new AppsV1beta1Api(client);
	}

	public boolean watchAndSchedule(String namespace, String serviceName, String chosenNode) throws Exception   {
		client.setConnectTimeout(3600);
		Configuration.setDefaultApiClient(client);
		String schedulerName = KubernetesController.verifyScheduler(core, serviceName);

		// Retrieving Pods events
		Watch<V1Pod> w = Watch.createWatch(client, 
				core.listNamespacedPodCall(namespace, null, null, null, null, null, 
						null, 
						null, null, true, null, null), 
				new TypeToken<Watch.Response<V1Pod>>() {}.getType());



		// For all pods events
		for( Watch.Response<V1Pod> event : w ) {
			// check if the pod is "pending" and its scheduller name is the same of ours
			// the follwing code is inspired in this example:
			// https://github.com/kubernetes-client/java/blob/997c088f88fffba5d9195ce91fad89dddb57cff4/examples/src/main/java/io/kubernetes/client/examples/WatchExample.java
			if("Pending".equals(event.object.getStatus().getPhase()) && 
					schedulerName.contains(event.object.getSpec().getSchedulerName())) {

				System.out.println(KubernetesController.getDeployment(beta, serviceName).toString());;

				System.out.println("Scheduling " + event.object.getMetadata().getName());
				// apply the binding and attach the pod to a node
				V1Binding res = schedule(core, serviceName, chosenNode, namespace);
				if(res != null)
					return true;


			}
		}
		return false;
	}

	/**
	 * this method attach one microservice to one node
	 * @param core
	 * @param serviceName
	 * @param chosenNode
	 * @return
	 * @throws ApiException 
	 */
	private static V1Binding schedule(CoreV1Api core, String serviceName, String chosenNode, String namespace)
			throws ApiException {
		// instantiate a binding object
		V1Binding body = new V1Binding();

		// setting the binding target
		V1ObjectReference target = new V1ObjectReference();
		target.kind("Node");

		target.apiVersion("apps1beta1");
		target.name(chosenNode);

		// setting the pod to be attached to selected node
		V1ObjectMeta meta = new V1ObjectMeta();
		meta.name(serviceName);

		// fill the binding object
		body.target(target);
		body.metadata(meta);

		// apply the binding in the cluster
		return core.createNamespacedBinding(namespace, body, null);

	}

}
