package app.flow.controller;

import java.util.List;

import org.flowable.task.api.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import app.flow.entity.Document;
import app.flow.service.DocumentWorkflowService;

@RestController
@RequestMapping(path = "/cases")
public class DocumentWorkflowController {

	@Autowired
    private DocumentWorkflowService documentWorkflowService;

    // Start a case
    @PostMapping("/start")
    public String startWorkflow(@RequestParam String initiator) {
        return documentWorkflowService.startCase(initiator);
    }

    // Upload a document
    @PostMapping("/upload/{caseInstanceId}/{taskId}")
    public void uploadDocument(@PathVariable String caseInstanceId,
                              @PathVariable String taskId,
                              @RequestParam("file") MultipartFile file) throws Exception {
        documentWorkflowService.uploadDocument(caseInstanceId, taskId, file);
    }

    // Review a document
    @PostMapping("/review/{taskId}")
    public void reviewDocument(@PathVariable String taskId,
                               @RequestParam String outcome) {
        documentWorkflowService.reviewDocument(taskId, outcome);
    }

    // Download a document
    @GetMapping("/download/{documentId}")
    public ResponseEntity<Resource> downloadDocument(@PathVariable Long documentId) {
        Document document = documentWorkflowService.downloadDocument(documentId);
        ByteArrayResource resource = new ByteArrayResource(document.getContent());
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + document.getFileName() + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

    // Get tasks for a user
    @GetMapping("/tasks/{userId}")
    public List<Task> getTasks(@PathVariable String userId) {
        return documentWorkflowService.getTasksForUser(userId);
    }
}
