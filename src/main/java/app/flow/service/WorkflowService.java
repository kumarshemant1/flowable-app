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

import com.fasterxml.jackson.databind.ObjectMapper;

import app.flow.model.BusinessFile;
import app.flow.model.Workflow;

@Service
public class WorkflowService {

	private CmmnRuntimeService cmmnRuntimeService;
	private CmmnTaskService cmmnTaskService;
	private ObjectMapper objectMapper;

	public WorkflowService(CmmnRuntimeService cmmnRuntimeService, CmmnTaskService cmmnTaskService, ObjectMapper objectMapper) {
		this.cmmnRuntimeService = cmmnRuntimeService;
		this.cmmnTaskService = cmmnTaskService;
		this.objectMapper = objectMapper;
	}

	public String startWorkflow(Workflow workflowDTO) {
        Map<String, Object> variables = new HashMap<>();
        // Store as JSON-compatible Map
        variables.put("workflow", objectMapper.convertValue(workflowDTO, Map.class));
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
        // Store as List<Map> for Flowable serialization
        List<Map<String, Object>> mappedFiles = new ArrayList<>();
        for (BusinessFile dto : businessFiles) {
            mappedFiles.add(objectMapper.convertValue(dto, Map.class));
        }
        cmmnTaskService.setVariable(taskId, "businessFile", mappedFiles);
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
                result.add(objectMapper.convertValue(o, BusinessFile.class));
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
        if (workflow != null) {
            return objectMapper.convertValue(workflow, Workflow.class);
        }
        return null;
    }

    private Task findTask(String caseInstanceId, String taskId) {
        return cmmnTaskService.createTaskQuery().caseInstanceId(caseInstanceId).taskId(taskId).singleResult();
    }
}
