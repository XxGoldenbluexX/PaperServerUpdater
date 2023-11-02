package fr.xxgoldenbluexx.paper_server_updater;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import fr.xxgoldenbluexx.paper_server_updater.papermc.PaperMCDownloadApi;
import fr.xxgoldenbluexx.paper_server_updater.papermc.model.PaperMCProjectVersionBuild;

public class PaperServerUpdater {

	public static void main(String[] args) {
		// Fetch build
		
		Logger.getGlobal().info("Get paper latest version info\n");
		
		var task = PaperMCDownloadApi.getLatestPaperBuildAsync();
		PaperMCProjectVersionBuild build;
		try {
			build = task.get();
		}catch(Exception e) {
			Logger.getGlobal().log(Level.SEVERE, "Error occurred while fetching paper latest build", e);
			return;
		}
		
		Logger.getGlobal().info(String.format("Paper latest verions: %s build: %s fileName: %s\n",
				build.version(),
				build.build(),
				build.downloads().application().name()));
		
		// Fetch current paper server file
		File currentDir = new File(".");
		
		File currentPaper;
		try {
			currentPaper = findPaperJar(currentDir);
		}catch(Exception e) {
			Logger.getGlobal().log(Level.SEVERE, "Error occurred while fetching current paper jar", e);
			return;
		}
		
		// Compare latest and used
		var appName = build.downloads().application().name();
		if (appName.contentEquals(currentPaper.getName())) {
			Logger.getGlobal().log(Level.INFO, "Paper is already up to date\n");
			return;
		}
		
		// Delete current paper
		if (!currentPaper.delete()) {
			Logger.getGlobal().log(Level.WARNING, "Pnable to delete current paper");
		}
		
		// Download latest paper
		
		Logger.getGlobal().info("Download latest paper jar...\n");
		
		var targetFile = new File(currentDir, build.downloads().application().name());
		try {
			PaperMCDownloadApi.downloadTo(build, targetFile).get();
		}catch(Exception e) {
			Logger.getGlobal().log(Level.SEVERE, "Error while downloading paper latest build", e);
			return;
		}
		
		Logger.getGlobal().info("Latest paper jar downloaded\n");
	}
	
	private static File findPaperJar(File directory) throws Exception{
		if (!directory.isDirectory()) {
			throw new Exception("Unable to fetch current directory");
		}
		var paperFiles = directory.listFiles((f,n) -> {
			var lowerName = n.toLowerCase();
			return lowerName.startsWith("paper-") && lowerName.endsWith(".jar");
		});
		if (paperFiles.length != 1) {
			throw new Exception(String.format("Found %s potential files as current paper server, need 1", paperFiles.length));
		}
		return paperFiles[0];
	}
	
}
