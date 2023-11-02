package fr.xxgoldenbluexx.paper_server_updater.papermc;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.nio.channels.Channels;
import java.util.concurrent.CompletableFuture;

import fr.xxgoldenbluexx.paper_server_updater.http.CustomHttpClient;
import fr.xxgoldenbluexx.paper_server_updater.papermc.model.PaperMCProject;
import fr.xxgoldenbluexx.paper_server_updater.papermc.model.PaperMCProjectVersion;
import fr.xxgoldenbluexx.paper_server_updater.papermc.model.PaperMCProjectVersionBuild;

public class PaperMCDownloadApi {
	
	public static CompletableFuture<PaperMCProjectVersionBuild> getLatestPaperBuildAsync() {
		return CompletableFuture.supplyAsync(() ->{
			try {
				var cli = new CustomHttpClient("https://api.papermc.io/v2/");
				// Fetch project
				var paperInfo = cli.getFromJsonAsync(PaperMCProject.path("paper"), PaperMCProject.class).get();
				var versions = paperInfo.versions();
				var latestVersion = versions[versions.length-1];
				// Fetch version
				var versionInfo = cli.getFromJsonAsync(PaperMCProjectVersion.path("paper", latestVersion), PaperMCProjectVersion.class).get();
				var builds = versionInfo.builds();
				var latestBuild = builds[builds.length-1];
				// Fetch build
				var buildInfo = cli.getFromJsonAsync(PaperMCProjectVersionBuild.path("paper", latestVersion, latestBuild), PaperMCProjectVersionBuild.class).get();
				return buildInfo;
			}catch(Exception e) {
				throw new RuntimeException(e);
			}
		});
	}
	
	public static CompletableFuture<Void> downloadTo(PaperMCProjectVersionBuild build, File file) {
		return CompletableFuture.runAsync(() -> {
			try {
				var url = new URL(
						String.format("https://api.papermc.io/v2/projects/paper/versions/%s/builds/%s/downloads/%s",
								build.version(),
								build.build(),
								build.downloads().application().name())
						);
				try (var inChannel = Channels.newChannel(url.openStream());
						var fos = new FileOutputStream(file);
						var outChannel = fos.getChannel()){
					outChannel.transferFrom(inChannel, 0, Long.MAX_VALUE);
				}
			}catch(Exception e) {
				throw new RuntimeException(e);
			}
		});
	}
	
}
