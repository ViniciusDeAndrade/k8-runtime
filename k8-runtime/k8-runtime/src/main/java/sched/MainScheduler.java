package sched;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.common.reflect.TypeToken;

import br.ufpe.cin.gfads.cluster.ClusterConnection;
import io.kubernetes.client.ApiClient;
import io.kubernetes.client.Configuration;
import io.kubernetes.client.apis.CoreV1Api;
import io.kubernetes.client.models.V1Binding;
import io.kubernetes.client.models.V1Node;
import io.kubernetes.client.models.V1NodeCondition;
import io.kubernetes.client.models.V1ObjectMeta;
import io.kubernetes.client.models.V1ObjectReference;
import io.kubernetes.client.models.V1Pod;
import io.kubernetes.client.models.V1Service;
import io.kubernetes.client.util.Config;
import io.kubernetes.client.util.Watch;

// this implementation is based on this tutorial
// https://sysdig.com/blog/kubernetes-scheduler/
//
// How to run this example:
//  1. Deploy the nginx-replication_controller.yaml:
//			kubectl apply -f nginx-replicaion_controller.yaml
//
//  2. Check if all pods are pending: 
// 			kubectl get pods -o wide
//
//  3. Run this scheduler
//
//  4. Check if all pods are attached to a node
//
//  5. Scale out the pods to 5 replicas
// 			kubectl scale rc nginx --replicas=5
//
// 	6. Check if there are two pending pods
//
//  7. Run the scheduler again
//
//  8. Check if all pods are attached to a node
/**
 * this class was the first schedule. Now it is refactored
 * @author vinicius
 *
 */
@Deprecated
public class MainScheduler {

	public static List<String> getNodesAvailable(CoreV1Api v1) throws Exception {
		// getting a list of nodes
		List<V1Node> nodes = 
				v1.listNode(null, null, null, null, null, null, null, null, null)
				.getItems();

		List<String> readyNodes = new ArrayList<>();

		// filter all nodes that are "ready"
		for( V1Node n : nodes ) {
			List<V1NodeCondition> conditions = n.getStatus().getConditions();
			for( V1NodeCondition status : conditions) {
				if("True".equals(status.getStatus()) &&
						"Ready".equals(status.getType())) {
					readyNodes.add(n.getMetadata().getName() );
				}
			}
		}

		return Collections.unmodifiableList(readyNodes);
	}

	public static V1Binding scheduler(CoreV1Api v1, String name, List<String> nodes) throws Exception {
		return scheduler(v1, name, nodes, "teste");
	}

	public static V1Binding scheduler(CoreV1Api v1, String name, List<String> nodes, String namespace) throws Exception {
		// instantiate a binding object
		V1Binding body = new V1Binding();

		// selects a node to attach the pod
		String node = nodeSelection(name, nodes);

		// setting the binding target
		V1ObjectReference target = new V1ObjectReference();
		target.kind("Node");
		target.apiVersion("v1");
		target.name(node);

		// setting the pod to be attached to selected node
		V1ObjectMeta meta = new V1ObjectMeta();
		meta.name(name);

		// fill the binding object
		body.target(target);
		body.metadata(meta);

		// apply the binding in the cluster
		return v1.createNamespacedBinding(namespace, body, null);

	}

	/*
	This method selects a node where the pod will be attached
	If the pod name ends in a number (0-9) it will be attached to "swarm1"
	If the pod name ends in a letter (a-zA-Z) it will be attached to "swarm5"
	You should change the nodes names to reflects your cluster configuration 
	 */
	public static String nodeSelection(String pod, List<String> nodes) {
		char lastPodChar = pod.charAt(pod.length()-1);

		String nodeSelected = "";
		// if the node name ends in a number
		if(Character.isDigit(lastPodChar)) {
			// selects the swarm1
			nodeSelected = nodes.stream()
					.filter(n -> n.equals("swarm1"))
					.findFirst()
					.get();

			return nodeSelected;
		}
		// if the node name ends in a letter
		else {
			// selects the swarm5
			nodeSelected = nodes.stream() // use swarm 5
					.filter(n -> n.equals("swarm1"))
					.findFirst()
					.get();

			return nodeSelected;
		}

	}

	public static void main( String[] args ) throws Exception {
		// This configuration doesn't use the Config file
		// You should change it if necessary
		ApiClient client = ClusterConnection.SetEnv();
		client.setConnectTimeout(3600);
		Configuration.setDefaultApiClient(client);

		CoreV1Api v1 = new CoreV1Api(client);

		// This name refers to the scheduler set in the deployment file
		String schedulerName = "gfads";
		String namespace = "teste";


		// Retrieving Pods events
		Watch<V1Pod> w = Watch.createWatch(client, 
				v1.listNamespacedPodCall(namespace, null, null, null, null, null, 
						null, 
						null, null, true, null, null), 
				new TypeToken<Watch.Response<V1Pod>>() {}.getType());

		// For all pods events
		for( Watch.Response<V1Pod> event : w ) {
			//System.out.println(event.object);
			// check if the pod is "pending" and its scheduller name is the same of ours
			// the follwing code is inspired in this example:
			// https://github.com/kubernetes-client/java/blob/997c088f88fffba5d9195ce91fad89dddb57cff4/examples/src/main/java/io/kubernetes/client/examples/WatchExample.java
			if( "Pending".equals(event.object.getStatus().getPhase()) &&
					schedulerName.equals(event.object.getSpec().getSchedulerName()) ) {
					//schedulerName.equals(event.object.getSpec().getSchedulerName()) ) {

				System.out.println("Scheduling " + event.object.getMetadata().getName());
				// apply the binding and attach the pod to a node
				V1Binding res = scheduler(v1, event.object.getMetadata().getName(), getNodesAvailable(v1));

			}

		}

		// This code are trowing an exception "io.kubernetes.client.ApiException: Conflict"
		// I don't know why, but the code is working fine even with this exception
	}
}
