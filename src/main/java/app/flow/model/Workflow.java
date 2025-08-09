package app.flow.model;

import java.util.List;

import lombok.Data;

@Data
public class Workflow {
	private String workflowId;
	private String workflowName;
	private List<Task> tasks;
}
