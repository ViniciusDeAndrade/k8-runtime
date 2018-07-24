package br.ufpe.cin.gfads.kuber;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import br.ufpe.cin.gfads.scheduler.Schedule;
import io.kubernetes.client.ApiException;
import io.kubernetes.client.apis.AppsV1beta1Api;
import io.kubernetes.client.apis.CoreV1Api;
import io.kubernetes.client.models.AppsV1beta1Deployment;
import io.kubernetes.client.models.AppsV1beta1DeploymentList;
import io.kubernetes.client.models.AppsV1beta1DeploymentSpec;
import io.kubernetes.client.models.V1Pod;
import io.kubernetes.client.models.V1PodList;
import io.kubernetes.client.models.V1PodSpec;
import io.kubernetes.client.models.V1PodTemplateSpec;

/**
 * 
 * @author Vinicius
 *
 */
public class KubernetesController {

	public static Map<String, String> getNodeSelector(AppsV1beta1Deployment deployment) {
		AppsV1beta1DeploymentSpec spec = deployment.getSpec();
		V1PodTemplateSpec temp = spec.getTemplate();
		V1PodSpec podSpec = temp.getSpec();
		Map<String, String> map = podSpec.getNodeSelector();
		return map;
	}

	/**
	 * This method looks for a kubernetes deployment object and returns it
	 * @param v1betaApi 
	 * @param name is the name of the deployment object you want to look for
	 * @return the deployment object
	 * @throws Exception 
	 */
	public static AppsV1beta1Deployment getDeployment(AppsV1beta1Api v1betaApi, String name) throws Exception {
		AppsV1beta1DeploymentList list = v1betaApi.listNamespacedDeployment("teste", null, null, null, null, null, null, null, null, null);
		//AppsV1beta1DeploymentList list = v1betaApi.listDeploymentForAllNamespaces(null, null, null, null, null, null);
		for(AppsV1beta1Deployment deployment : list.getItems()) {
			String deploymentName = deployment.getMetadata().getName();
			//System.out.println(deployment.getMetadata().getName());
			if(name.equals(deploymentName)) {
				return deployment;
			}
		}
		return null;
	}
	
	public static List<AppsV1beta1Deployment> getDeploymentsByNamespace(AppsV1beta1Api v1betaApi, String name,String namespace) throws Exception {
		AppsV1beta1DeploymentList list = v1betaApi.listNamespacedDeployment(namespace, null, null, null, null, null, null, null, null, null);
		List<AppsV1beta1Deployment> deploys = new LinkedList<AppsV1beta1Deployment>();
		for(AppsV1beta1Deployment deployment : list.getItems()) {
			String deploymentName = deployment.getMetadata().getName();
			if(name.contains(deploymentName)) {
				deploys.add(deployment);
			}
		}
		return deploys;
	}

	public static List<V1Pod> getScheduledPods(CoreV1Api core, String namespace, String schedulerName) throws Exception {
		V1PodList list = core.listNamespacedPod(namespace, null, null, null, null, null, null, null, null, null);
		List<V1Pod> podsScheduleds = new LinkedList<V1Pod>();
		for (V1Pod pod : list.getItems()) {
			if(!pod.getSpec().getSchedulerName().isEmpty()) 
				podsScheduleds.add(pod);			
		}

		return podsScheduleds;
	}
	
	public static String getNodeOfNotPendingPod(CoreV1Api core, String namespace,String serviceName) throws Exception {
		V1PodList podList = core.listNamespacedPod(namespace, null, null, null, null, null, null, null, null, null);
		for (V1Pod pod: podList.getItems()) {
			if(pod.getMetadata().getName().contains(serviceName))
				if(!pod.getStatus().getPhase().equalsIgnoreCase("pending"))
					return pod.getSpec().getNodeName();
			
		}
		return null;
	}
	
	public static String verifyScheduler(CoreV1Api core, String serviceName) throws Exception {
		V1PodList list = core.listPodForAllNamespaces(null, null, null, null, null, null, null, null, null);
		for (V1Pod pod: list.getItems()) {
			if(pod.getMetadata().getName().contains(serviceName)) 
				if(pod.getSpec().getSchedulerName().equals("gfads"))
				return pod.getSpec().getSchedulerName();
		}
		return "default-scheduler";
	}
	
}
