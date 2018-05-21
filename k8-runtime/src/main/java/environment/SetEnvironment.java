package environment;

import java.io.FileNotFoundException;
import java.io.FileReader;

import io.kubernetes.client.ApiClient;
import io.kubernetes.client.Configuration;
import io.kubernetes.client.util.Config;
import io.kubernetes.client.util.KubeConfig;

public class SetEnvironment {

	public static ApiClient SetEnv() {
		final String pathToGfadsConfigFile = "/home/vinicius/Documentos/gfads-test.config";
		FileReader gfadsConfigFile;
		try {
			gfadsConfigFile = new FileReader(pathToGfadsConfigFile);
			KubeConfig kc = KubeConfig.loadKubeConfig(gfadsConfigFile);
			ApiClient client = Config.fromConfig(kc);
			Configuration.setDefaultApiClient(client);
			return client;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
		
		
	}
}
