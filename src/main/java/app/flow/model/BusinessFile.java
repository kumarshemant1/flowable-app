package app.flow.model;

import lombok.Data;

@Data
public class BusinessFile {
	private String id;
    private String taskId;
    private String businessFileName;
    private boolean retainFile;
}
