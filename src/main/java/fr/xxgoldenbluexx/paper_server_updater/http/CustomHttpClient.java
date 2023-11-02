package fr.xxgoldenbluexx.paper_server_updater.http;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CustomHttpClient {

	private URL baseAddress;
	
	public CustomHttpClient(String baseAddress) {
		try {
			this.baseAddress = new URL(baseAddress);
			var protocol = this.baseAddress.getProtocol();
			if (!protocol.contentEquals("http") && !protocol.contentEquals("https")) {
				throw new MalformedURLException("Protocol used in CustomHttpClient should be http or https (found "+protocol+')');
			}
		}catch(Exception e) {
			Logger.getGlobal().log(Level.SEVERE, "Error while parsing base address ("+baseAddress+')', e);
		}
	}
	
	private URL urlFromBase(String path) {
		try {
			return new URL(baseAddress, path);
		}catch(Exception e) {
			Logger.getGlobal().log(Level.SEVERE, String.format("Error while merging base URL (%s) with path (%s)", baseAddress.toExternalForm(), path), e);
		}
		return null;
	}
	
	public CompletableFuture<String> getAsync(String path, String acceptedMimeTypes){
		var url = urlFromBase(path);
		if (url == null) {
			return null;
		}
		return CompletableFuture.supplyAsync(() -> {
			try {
				var conn = (HttpURLConnection)url.openConnection();
				conn.setRequestMethod("GET");
				conn.setRequestProperty("Accept", acceptedMimeTypes);
				var code = conn.getResponseCode();
				if (code < 200 && code >= 300) {
					throw new Exception("Http response code is not in 200 range ("+code+')');
				}
				try (var isReader = new InputStreamReader(conn.getInputStream());
						var bufferedReader = new BufferedReader(isReader);){
					var sb = new StringBuilder();
					String out;
					while ((out = bufferedReader.readLine()) != null) {
						sb.append(out);
					}
					return sb.toString();
				}
			}catch(Exception e) {
				throw new RuntimeException("Erreur while get async "+url,e);
			}
		});
	}
	
	public <T> CompletableFuture<T> getFromJsonAsync(String path, Class<T> type){
		var task = getAsync(path, "application/json");
		return task.thenApply(json -> {
			try {
				var mapper = new ObjectMapper();
				mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
				return mapper.readValue(json, type);
			} catch (Exception e) {
				throw new RuntimeException("Erreur parson json "+json,e);
			}
		});
	}
	
}
