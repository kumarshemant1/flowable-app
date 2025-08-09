package app.flow.model;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class Task {
	private String taskId;
	private String workflowId;
	private String taskName;
	private String taskType; // UPLOAD, DOWNLOAD, etc.
	private List<BusinessFile> businessFiles;
	private List<String> businessRoles;
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
	private LocalDateTime completionDay;
}
