package br.ufpe.cin.gfads.tests;

import java.util.Random;

import br.ufpe.cin.gfads.cluster.ClusterConnection;
import br.ufpe.cin.gfads.experiment.SettleTuple;
import br.ufpe.cin.gfads.experiment.Tuple;
import br.ufpe.cin.gfads.kuber.KubernetesController;
import br.ufpe.cin.gfads.kuber.Mover;
import br.ufpe.cin.gfads.moviment.Moviment;
import br.ufpe.cin.gfads.scheduler.Schedule;
import io.kubernetes.client.ApiClient;
import io.kubernetes.client.apis.AppsV1beta1Api;
import io.kubernetes.client.apis.CoreV1Api;

public class MoveAndSchedule {
	public static void main(String[] args) throws Exception {
		//seta e configura tudo. Devolve o client
		ApiClient client = ClusterConnection.SetEnv();
		AppsV1beta1Api beta = new AppsV1beta1Api(client);
		CoreV1Api core = new CoreV1Api(client);
		Schedule schedule = new Schedule(client);
		Mover mover;
		Moviment m;
		
		//filename
		String fileName = "two-replicas-nginx.csv";
		int replicas = 2;
		long start, finish;//, timeAmongCalls;
		SettleTuple settleTuple = new SettleTuple(fileName);

		//String service = "carts"; // use "redis-cache" or "web-server"
		String services [] = new String[5];
		services[0] = "nginx1";
		services[1] = "nginx2";
		services[2] = "nginx3";
		services[3] = "nginx4";
		services[4] = "nginx5";

		String hostNode = "swarm1" ;// use swarm1 or swarm5
		String destinationNode = "swarm5" ;// use swarm1 or swarm5
		final String namespace = "teste";


		System.out.println("start measurement");
		/*final int mean = 10000;
		final int stdDesviation = 2000;	
		*/
		//tamanho da amostra
		int n = 50;
		//começa a medição
		for(int index = 0 ; index < n ; index++) {
			for(int j = 0; j < services.length; j++) {
				String serv = services[j];
				start = System.nanoTime();
				//create a moviment and do it for one pod
				mover = new Mover(beta,serv);
				m = Moviment.create(namespace,serv,hostNode,destinationNode);
				mover.move(m);
				//schedule a pod
				String schedulerName = KubernetesController.verifyScheduler(core, serv);
				if(!schedulerName.equals("default-scheduler")) {
					schedule.watchAndSchedule(namespace, serv, destinationNode, core);
				}

				finish = System.nanoTime() - start;
				System.out.println("TTM = "+finish/1000000 + " index = " + index + " service = " + serv );
				//timeAmongCalls = (long) Math.abs(new Random().nextGaussian() * mean + stdDesviation);
				settleTuple.addTuple(new Tuple(serv, replicas ,index , finish/1000000));
				//ate aqui
				Thread.sleep(25000);
			}
			if(destinationNode.equals("swarm1"))
				destinationNode = "swarm5";
			else
				destinationNode = "swarm1";
		}
		System.out.println("done");
		settleTuple.flushCSV();
	}


}
