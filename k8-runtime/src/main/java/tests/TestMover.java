package tests;


import java.io.FileReader;
import java.util.Map;

import environment.SetEnvironment;
import implMoviment.Mover;
import io.kubernetes.client.ApiClient;
import io.kubernetes.client.Configuration;
import io.kubernetes.client.apis.AppsV1beta1Api;
import io.kubernetes.client.apis.AutoscalingV2alpha1Api;
import io.kubernetes.client.util.Config;
import io.kubernetes.client.util.KubeConfig;
import moviment.Moviment;
import io.kubernetes.client.ApiResponse;
import io.kubernetes.client.models.AppsV1beta1Deployment;

import objects.K8OBjects;
import simulatedHost.CheckResources;

public class TestMover {
	public static void main(String[] args) throws Exception {

		//seta e configura tudo. Devolve o client
		ApiClient client = SetEnvironment.SetEnv();
		AppsV1beta1Api beta = new AppsV1beta1Api(client);

		String service = "carts"; // use "redis-cache" or "web-server"
		String hostNode = "swarm1" ;// use swarm1 or swarm5
		String destinationNode = "swarm5" ;// use swarm1 or swarm5
		final String namespace = "sock-shop";


		int swarmMemory = 32;
		int swarmProcessor = 5;
		//setting a host
		CheckResources checkResources = new CheckResources(swarmMemory, swarmProcessor);

		int demandedMemory = 8;
		double demandedProcessorPower = 4.9;

		boolean teste = checkResources.check(demandedMemory, demandedProcessorPower);
		if(!teste) {
			System.out.println("não vai mover");
		}else {
			System.out.println("vai mover");
			

			Mover mover = new Mover(beta,service);

			//sock-shop é o namespace da aplicação
			//service é o nome da aplicação. Tudo nesse contexto especificado aqui!

			Moviment m = Moviment.create(namespace,service,hostNode,destinationNode);

			mover.move(m);
		}

	}	

}
