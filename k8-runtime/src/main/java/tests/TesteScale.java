package tests;

import java.util.List;

import environment.SetEnvironment;
import io.kubernetes.client.ApiClient;
import io.kubernetes.client.ApiException;
import io.kubernetes.client.apis.AppsV1beta1Api;
import io.kubernetes.client.apis.CoreV1Api;
import io.kubernetes.client.models.AppsV1beta1Deployment;
import io.kubernetes.client.models.V1Pod;
import scheduler.Schedule;

public class TesteScale {
	//tentei interceptar aqui.

	public static void main(String[] args) throws Exception {
		defaultScale();
	}

	private static void defaultScale() throws ApiException {
		final String namespace = "sock-shop"; 
		final String service = "carts";
		ApiClient client  = SetEnvironment.SetEnv();
		AppsV1beta1Api appsV1beta1Api = new AppsV1beta1Api(client);
		List<AppsV1beta1Deployment> deploys = 
				appsV1beta1Api.listNamespacedDeployment(namespace, null, null, null, null, null, null).getItems();

		for (AppsV1beta1Deployment deployment : deploys) 
			if(deployment.getMetadata().getName().equals(service)) {
				System.out.println("deployment replica = " + deployment.getSpec().getReplicas());
				deployment.getSpec().setReplicas(3);
				System.out.println("deployment replicas = " + deployment.getSpec().getReplicas());
				//falta ajeitar essa linha aqui
				appsV1beta1Api.patchNamespacedDeployment(service, namespace, null, "true");
			}
	}
}
