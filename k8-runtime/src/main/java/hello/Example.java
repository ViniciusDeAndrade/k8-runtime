package hello;

import java.io.FileReader;
import java.io.IOException;

import io.kubernetes.client.ApiClient;
import io.kubernetes.client.ApiException;
import io.kubernetes.client.Configuration;
import io.kubernetes.client.apis.CoreV1Api;
import io.kubernetes.client.models.V1Pod;
import io.kubernetes.client.models.V1PodList;
import io.kubernetes.client.util.Config;
import io.kubernetes.client.util.KubeConfig;

public class Example {
	public static void main(String[] args) throws IOException, ApiException{
		
		final String pathToGfadsConfigFile = "/home/vinicius/Documentos/gfads-test.config";
		FileReader gfadsConfigFile = new FileReader(pathToGfadsConfigFile);
		KubeConfig kc = KubeConfig.loadKubeConfig(gfadsConfigFile);
		ApiClient client = Config.fromConfig(kc);
		Configuration.setDefaultApiClient(client);
		final String namespace = "sock-shop";

		CoreV1Api api = new CoreV1Api();
		V1PodList list = api.listNamespacedPod(namespace, null, null, null, null, null, null);
			
		for (V1Pod item : list.getItems()) {
			System.out.println(item.getMetadata().getName());
		}
	}
}