package app.flow.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.flowable.cmmn.api.CmmnRuntimeService;
import org.flowable.cmmn.api.CmmnTaskService;
import org.flowable.cmmn.api.runtime.CaseInstance;
import org.flowable.task.api.Task;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import app.flow.model.BusinessFile;
import app.flow.model.Workflow;

@Service
public class WorkflowService {

	private CmmnRuntimeService cmmnRuntimeService;
	private CmmnTaskService cmmnTaskService;

	public WorkflowService(CmmnRuntimeService cmmnRuntimeService, CmmnTaskService cmmnTaskService) {
		this.cmmnRuntimeService = cmmnRuntimeService;
		this.cmmnTaskService = cmmnTaskService;
	}

	public String startWorkflow(Workflow workflow) {
		Map<String, Object> variables = new HashMap<>();
		variables.put("workflow", workflow);
		CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder()
				.caseDefinitionKey("workflowManagement")
				.variables(variables)
				.start();
		return caseInstance.getId();
	}

	public List<BusinessFile> uploadFiles(String caseInstanceId, String taskId, List<MultipartFile> files) {
		Task task = findTask(caseInstanceId, taskId);
		if (task == null) return null;

		List<BusinessFile> businessFiles = new ArrayList<>();
		for (MultipartFile file : files) {
			BusinessFile fileMeta = new BusinessFile();
			fileMeta.setId(UUID.randomUUID().toString());
			fileMeta.setTaskId(taskId);
			fileMeta.setBusinessFileName(file.getOriginalFilename());
			fileMeta.setRetainFile(true);
			// File content storage can be added here
			businessFiles.add(fileMeta);
		}
		cmmnTaskService.setVariable(taskId, "businessFile", businessFiles);
		return businessFiles;
	}

	public List<BusinessFile> getTaskFiles(String caseInstanceId, String taskId) {
		Task task = findTask(caseInstanceId, taskId);
		if (task == null) return null;
		Object files = cmmnTaskService.getVariable(taskId, "businessFile");
		if (files instanceof List<?>) {
			List<?> list = (List<?>) files;
			List<BusinessFile> result = new ArrayList<>();
			for (Object o : list) {
				if (o instanceof BusinessFile) {
					result.add((BusinessFile) o);
				} else if (o instanceof Map) {
					// for deserialized maps (if stored as JSON)
					Map map = (Map) o;
					BusinessFile dto = new BusinessFile();
					dto.setId((String) map.get("id"));
					dto.setTaskId((String) map.get("taskId"));
					dto.setBusinessFileName((String) map.get("businessFileName"));
					Object retainObj = map.get("retainFile");
					dto.setRetainFile(retainObj != null && Boolean.parseBoolean(retainObj.toString()));
					result.add(dto);
				}
			}
			return result;
		}
		return Collections.emptyList();
	}

	public List<BusinessFile> reuploadFiles(String caseInstanceId, String taskId, List<MultipartFile> files) {
		// Same as uploadFiles
		return uploadFiles(caseInstanceId, taskId, files);
	}

	public List<BusinessFile> consolidateFiles(String caseInstanceId) {
		List<Task> tasks = cmmnTaskService.createTaskQuery().caseInstanceId(caseInstanceId).list();
		List<BusinessFile> allFiles = new ArrayList<>();
		for (Task task : tasks) {
			List<BusinessFile> files = getTaskFiles(caseInstanceId, task.getId());
			if (files != null) allFiles.addAll(files);
		}
		// Place for actual consolidation logic
		return allFiles;
	}

	public Workflow getWorkflow(String caseInstanceId) {
		Object workflow = cmmnRuntimeService.getVariable(caseInstanceId, "workflow");
		if (workflow instanceof Workflow) return (Workflow) workflow;
		if (workflow instanceof Map) {
			// If Flowable deserializes as Map, map fields manually
			Map map = (Map) workflow;
			Workflow dto = new Workflow();
			dto.setWorkflowId((String) map.get("workflowId"));
			dto.setWorkflowName((String) map.get("workflowName"));
			// Tasks mapping can be added if needed
			return dto;
		}
		return null;
	}

	private Task findTask(String caseInstanceId, String taskId) {
		return cmmnTaskService.createTaskQuery().caseInstanceId(caseInstanceId).taskId(taskId).singleResult();
	}
}
