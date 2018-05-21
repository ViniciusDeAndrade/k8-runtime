package implMoviment;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import hello.Hello;
import io.kubernetes.client.ApiClient;
import io.kubernetes.client.ApiException;
import io.kubernetes.client.ApiResponse;
import io.kubernetes.client.Configuration;
import io.kubernetes.client.apis.AppsV1beta1Api;
import io.kubernetes.client.models.AppsV1beta1Deployment;
import io.kubernetes.client.util.Config;
import moviment.Moviment;
import moviment.ReificationInterface;
import objects.K8OBjects;
import objects.Mapper;

public class Mover implements ReificationInterface{

	private AppsV1beta1Deployment current,desired;
	private AppsV1beta1Api beta;
	private K8OBjects k8 ;
	private Map<String,String> nodeSelector;
	
	public Mover(AppsV1beta1Api appsV1beta1Api, String service) throws Exception {
		k8 = new K8OBjects();
		this.beta = appsV1beta1Api;
		this.current = k8.getDeployment(beta, service);
		this.desired = k8.getDeployment(beta, service);	
	}
	
	public Mover() {}
	
	/**
	 * here we got the move from Adalberto's interface
	 */
	public boolean move(Moviment moviment) {
		try {
			return this.move(moviment.getApplication(), moviment.getService(), moviment.getHostDestination(), "kubernetes.io/hostname");
		}catch (Exception e) {
			return false;
		}
	}
	
	/**
	 * here we got the move from Adalberto's interface
	 */
	public boolean move(List<Moviment> script){

		for(Moviment mov : script) {
			boolean test = this.move(mov);
			if(test == false)
				return false;
		}
		return true;

	}


	/**The trully moviment implementation
	 * this is the implementation of the real move!
	 * @param namespace
	 * @param service
	 * @param node
	 * @param key
	 * @return
	 * @throws Exception
	 */
	private boolean move (String namespace, String service, String node,String key) throws Exception {
		

		Map<String, String> nodeSelector = K8OBjects.getNodeSelector(desired);
		Map[] result;
		if(nodeSelector == null) {
			result = new Map[]{new HashMap<String, String>()};
			result[0].put("op", "add");
			result[0].put("path","/spec/template/spec/nodeSelector");
			Map<String, String> patch = new HashMap<String, String>();
			patch.put(key, node);
			result[0].put("value",patch);
		}
		else {
			nodeSelector.put(key, node);
			result = Mapper.determineJsonPatch(current, desired);
		}

		try {

			ApiResponse<AppsV1beta1Deployment> response = beta.patchNamespacedDeploymentWithHttpInfo(service, namespace, result, "true");
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