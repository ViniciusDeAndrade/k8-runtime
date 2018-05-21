package br.ufpe.cin.gfads.cluster;

import java.util.HashMap;
import java.util.Map;

import br.ufpe.cin.gfads.kuber.KubernetesController;
import br.ufpe.cin.gfads.kuber.Mapper;
import io.kubernetes.client.ApiException;
import io.kubernetes.client.ApiResponse;
import io.kubernetes.client.apis.AppsV1beta1Api;
import io.kubernetes.client.models.AppsV1beta1Deployment;

public class Update {

	private AppsV1beta1Deployment current,desired;
	private AppsV1beta1Api beta;
	private KubernetesController k8 ;
	private Map<String,String> nodeSelector;

	public Update() {}
	
	public Update(AppsV1beta1Api appsV1beta1Api, String service) throws Exception {
		this.beta = appsV1beta1Api;
		this.current = k8.getDeployment(beta, service);
		this.desired = k8.getDeployment(beta, service);	
	}
	
	
	public boolean patch(String service, String namespace, String node, String key) {
		Map<String, String> nodeSelector = KubernetesController.getNodeSelector(desired);
		Map[] result;
		if(nodeSelector == null) {
			System.out.println("seletor de no é nulo");
			result = new Map[]{new HashMap<String, String>()};
			result[0].put("op", "add");
			result[0].put("path","/spec/template/spec/nodeSelector");
			Map<String, String> patch = new HashMap<String, String>();
			patch.put(key, node);
			result[0].put("value",patch);
		}
		else {
			nodeSelector.put(key, node);
			result = Mapper.determineJsonPatch(service, service);
		}

		try {

			ApiResponse<AppsV1beta1Deployment> response = 
					beta.patchNamespacedDeploymentWithHttpInfo(service, namespace, result, "true");
			System.out.println(response.getData());
			return true;
		}
		catch (ApiException e) {
			System.out.println(e.getResponseBody());
			e.printStackTrace();
			return false;
		}


	}
}
