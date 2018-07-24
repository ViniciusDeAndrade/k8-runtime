package hello;

import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.diff.JsonDiff;

import io.kubernetes.client.ApiClient;
import io.kubernetes.client.ApiException;
import io.kubernetes.client.ApiResponse;
import io.kubernetes.client.Configuration;
import io.kubernetes.client.apis.AppsV1beta1Api;
import io.kubernetes.client.models.AppsV1beta1Deployment;
import io.kubernetes.client.models.AppsV1beta1DeploymentList;
import io.kubernetes.client.models.AppsV1beta1DeploymentSpec;
import io.kubernetes.client.models.V1PodSpec;
import io.kubernetes.client.models.V1PodTemplateSpec;
import io.kubernetes.client.util.Config;
import io.kubernetes.client.util.KubeConfig;


// using the kubectl: kubectl --kubeconfig=$KUBE_TEST patch deployment web-server -p '{"spec": {"template":{"spec":{"nodeSelector":{"kubernetes.io/hostname":"swarm"}}}}}'
/**
 * this class was the first one did to make the move. Now it is refactored.
 * @author vinicius
 *
 */
@Deprecated
public class MoveMain {

	static final ObjectMapper mapper = new ObjectMapper();

	public static Map[] determineJsonPatch(Object current, Object desired) {
		
		JsonNode desiredNode = mapper.convertValue(desired, JsonNode.class);
		JsonNode currentNode = mapper.convertValue(current, JsonNode.class);

		return mapper.convertValue(JsonDiff.asJson(currentNode, desiredNode), Map[].class);
	}

	//done!
	public static AppsV1beta1Deployment getDeployment(AppsV1beta1Api v1betaApi, String name) throws Exception {
		AppsV1beta1DeploymentList list = v1betaApi.listDeploymentForAllNamespaces(null, null, null, null, null, null, null, null, null);
		for(AppsV1beta1Deployment deployment : list.getItems()) {
			String deploymentName = deployment.getMetadata().getName();
			if(name.equals(deploymentName)) {
				return deployment;
			}
		}
		return null;
	}

	//done
	static Map<String, String> getNodeSelector(AppsV1beta1Deployment deployment) {
		AppsV1beta1DeploymentSpec spec = deployment.getSpec();
		
		V1PodTemplateSpec temp = spec.getTemplate();
		
		V1PodSpec podSpec = temp.getSpec();
				
		Map<String, String> map = podSpec.getNodeSelector();
		
		return map;
	}

	/*
	 * Does not implement this class using static methods!!!
	 */
	public static void main(String[] args) throws Exception {
		// solution based on this snippet:
		// https://github.com/spinnaker/clouddriver/pull/1868/files#diff-150b8912ec8e0cd49b16cb79345b11e7R98'

		String namespace = "teste";
		String service = "user4";// use "redis-cache" or "web-server" //carts-db
		String node = "swarm1"; // use "swarm1" or "swarm5"

		
		final String pathToGfadsConfigFile = "/home/vinicius/Documentos/gfads-test.config";
		FileReader gfadsConfigFile = new FileReader(pathToGfadsConfigFile);
		KubeConfig kc = KubeConfig.loadKubeConfig(gfadsConfigFile);
		// the client is using the proxy address
		ApiClient client = Config.fromConfig(kc);
		//ApiClient client = Config.fromUrl("http://127.0.0.1:8001");
		Configuration.setDefaultApiClient(client);

		AppsV1beta1Api v1betaApi = new AppsV1beta1Api(client);

		// both "current" and "desired" must have the same attributes
		// but they cannot be the same object
		AppsV1beta1Deployment current = MoveMain.getDeployment(v1betaApi, service);
		AppsV1beta1Deployment desired = MoveMain.getDeployment(v1betaApi, service);

		Map<String, String> nodeSelector = getNodeSelector(desired);
		
		Map[] result;
		if(nodeSelector == null) {
			result = new Map[]{new HashMap<String, String>()};
			result[0].put("op", "add");
			result[0].put("path","/spec/template/spec/nodeSelector");
			Map<String, String> patch = new HashMap<String, String>();
			patch.put("kubernetes.io/hostname", node);
			result[0].put("value",patch);
		}
		else {
			nodeSelector.put("kubernetes.io/hostname", node);
			result = determineJsonPatch(current, desired);
		}
		
		try {
			
			ApiResponse<AppsV1beta1Deployment> response = 
					v1betaApi.patchNamespacedDeploymentWithHttpInfo(service, namespace, result, "true");
			System.out.println(response.getData());
		}
		catch (ApiException e) {
			System.out.println(e.getResponseBody());
			e.printStackTrace();
		}

	}

}
