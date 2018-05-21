package objects;

import java.util.Map;

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
public class K8OBjects {
	
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
		AppsV1beta1DeploymentList list = v1betaApi.listDeploymentForAllNamespaces(null, null, null, null, null, null);
		for(AppsV1beta1Deployment deployment : list.getItems()) {
			String deploymentName = deployment.getMetadata().getName();
			if(name.equals(deploymentName)) {
				return deployment;
			}
		}
		return null;
	}
	
	/*
	public static V1Pod getPod(CoreV1Api core, String podName) throws Exception{
		V1PodList list = core.listPodForAllNamespaces(null, null, null, null, null, null);
		for (V1Pod item : list.getItems()) 
			if(item.getMetadata().getName().equalsIgnoreCase(podName))
				return item;
		return null;
	}
	 */
}
