package fr.xxgoldenbluexx.paper_server_updater.papermc.model;

public record PaperMCProjectVersion(
		String project_id,
		String project_name,
		String version,
		String[] builds
		){
	
	public static String path(String project, String version) {
		return "projects/"+project+"/versions/"+version;
	}
	
}
