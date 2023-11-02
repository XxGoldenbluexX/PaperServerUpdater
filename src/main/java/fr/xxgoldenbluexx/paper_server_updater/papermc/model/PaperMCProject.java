package fr.xxgoldenbluexx.paper_server_updater.papermc.model;

public record PaperMCProject(
		String project_id,
		String project_name,
		String[] version_groups,
		String[] versions
		){
	
	public static String path(String project) {
		return "projects/"+project;
	}
	
}
