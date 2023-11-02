package fr.xxgoldenbluexx.paper_server_updater.papermc.model;

import java.util.Date;

public record PaperMCProjectVersionBuild(
		String project_id,
		String project_name,
		String version,
		String build,
		Date time,
		String channel,
		boolean promoted,
		PaperMCPaperBuildDownloads downloads
		){
	
	public static String path(String project, String version, String build) {
		return "projects/"+project+"/versions/"+version+"/builds/"+build;
	}
	
}
