package app.flow.controller;

import java.util.Collections;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import app.flow.model.BusinessFile;
import app.flow.model.Workflow;
import app.flow.service.WorkflowService;

@RestController
@RequestMapping("/api/workflows")
public class WorkflowController {

	private WorkflowService workflowService;

	public WorkflowController(WorkflowService workflowService) {
		this.workflowService = workflowService;
	}

	@PostMapping("/start")
	public ResponseEntity<?> startWorkflow(@RequestBody Workflow workflowDTO) {
		String caseInstanceId = workflowService.startWorkflow(workflowDTO);
		return ResponseEntity.ok(Collections.singletonMap("caseInstanceId", caseInstanceId));
	}

	@PostMapping("/{caseInstanceId}/tasks/{taskId}/upload")
	public ResponseEntity<?> uploadFiles(@PathVariable String caseInstanceId, @PathVariable String taskId,
			@RequestParam("files") List<MultipartFile> files) {
		List<BusinessFile> result = workflowService.uploadFiles(caseInstanceId, taskId, files);
		return ResponseEntity.ok(Collections.singletonMap("files", result));
	}

	@GetMapping("/{caseInstanceId}/tasks/{taskId}/files")
	public ResponseEntity<?> getTaskFiles(@PathVariable String caseInstanceId, @PathVariable String taskId) {
		List<BusinessFile> result = workflowService.getTaskFiles(caseInstanceId, taskId);
		return ResponseEntity.ok(Collections.singletonMap("files", result));
	}

	@PostMapping("/{caseInstanceId}/tasks/{taskId}/reupload")
	public ResponseEntity<?> reuploadFiles(@PathVariable String caseInstanceId, @PathVariable String taskId,
			@RequestParam("files") List<MultipartFile> files) {
		List<BusinessFile> result = workflowService.reuploadFiles(caseInstanceId, taskId, files);
		return ResponseEntity.ok(Collections.singletonMap("files", result));
	}

	@PostMapping("/{caseInstanceId}/consolidate")
	public ResponseEntity<?> consolidateFiles(@PathVariable String caseInstanceId) {
		List<BusinessFile> result = workflowService.consolidateFiles(caseInstanceId);
		return ResponseEntity.ok(Collections.singletonMap("allFiles", result));
	}

	@GetMapping("/{caseInstanceId}")
	public ResponseEntity<?> getWorkflow(@PathVariable String caseInstanceId) {
		Workflow workflow = workflowService.getWorkflow(caseInstanceId);
		return ResponseEntity.ok(Collections.singletonMap("workflow", workflow));
	}
}