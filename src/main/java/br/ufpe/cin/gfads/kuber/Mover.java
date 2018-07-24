package br.ufpe.cin.gfads.kuber;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.ufpe.cin.gfads.cluster.ClusterConnection;
import br.ufpe.cin.gfads.moviment.Moviment;
import br.ufpe.cin.gfads.moviment.ReificationInterface;
import br.ufpe.cin.gfads.scheduler.Schedule;
import io.kubernetes.client.ApiException;
import io.kubernetes.client.ApiResponse;
import io.kubernetes.client.apis.AppsV1beta1Api;
import io.kubernetes.client.apis.CoreV1Api;
import io.kubernetes.client.models.AppsV1beta1Deployment;

public class Mover implements ReificationInterface{

	private AppsV1beta1Deployment current,desired;
	private AppsV1beta1Api beta;
	private Map<String,String> nodeSelector;


	public Mover(AppsV1beta1Api appsV1beta1Api, String service) throws Exception {
		this.beta = appsV1beta1Api;
		this.current = KubernetesController.getDeployment(beta, service);
		this.desired = KubernetesController.getDeployment(beta, service);

	}

	public Mover() {}

	/**
	 * here we got the move from Adalberto's interface
	 */
	public boolean move(Moviment moviment) {
		try {
			return this.move(moviment.getApplication(), moviment.getService(), moviment.getHostDestination(), "kubernetes.io/hostname");
		}catch (Exception e) {
			e.printStackTrace();
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
	 * @param namespace is the virtual cluster where we want to apply this operation
	 * @param service is the name of the app we want to move
	 * @param node is the host destination
	 * @param key
	 * @return
	 * @throws Exception
	 */
	private boolean move (String namespace, String service, String node,String key) throws Exception {

		//nodeSelector = {kubernetes.io/hostname=swarm1}
		Map<String, String> nodeSelector = KubernetesController.getNodeSelector(desired);

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
			//System.out.println(result.toString());

		}

		try {
			ApiResponse<AppsV1beta1Deployment> response = 
					beta.patchNamespacedDeploymentWithHttpInfo(service, namespace, result, "true");
			//scheduler aqui!!
			/*CoreV1Api core = new CoreV1Api(ClusterConnection.SetEnv());
			String scheduler = KubernetesController.verifyScheduler(core, service);
			if(!scheduler.equals("default-scheduler")) {
				Schedule schedule = new Schedule(ClusterConnection.SetEnv());
				schedule.watchAndSchedule(namespace, service, node);
			}
			*/
			response.getData();
			return true;
		}
		catch (ApiException e) {
			System.out.println("erro no mover");
			System.out.println(e.getResponseBody());
			e.printStackTrace();
			return false;
		}

	}
}