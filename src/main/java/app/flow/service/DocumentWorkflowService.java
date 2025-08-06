package app.flow.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.flowable.cmmn.api.CmmnRuntimeService;
import org.flowable.cmmn.api.CmmnTaskService;
import org.flowable.cmmn.api.runtime.CaseInstance;
import org.flowable.task.api.Task;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import app.flow.dao.DocumentRepository;
import app.flow.entity.Document;

@Service
public class DocumentWorkflowService {

    private CmmnRuntimeService cmmnRuntimeService;
    private CmmnTaskService cmmnTaskService;
    private DocumentRepository documentRepository;
    
    public DocumentWorkflowService(CmmnRuntimeService cmmnRuntimeService, CmmnTaskService cmmnTaskService, DocumentRepository documentRepository) {
    	this.cmmnRuntimeService = cmmnRuntimeService;
    	this.cmmnTaskService = cmmnTaskService;
    	this.documentRepository = documentRepository;
    }

    // Start a new case instance
    public String startCase(String initiator) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("initiator", initiator);
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder()
        	    .caseDefinitionKey("documentCase")
        	    .variables(variables)
        	    .start();
        return caseInstance.getId();
    }

    // Upload a document and complete the upload task
    public void uploadDocument(String caseInstanceId, String taskId, MultipartFile file) throws IOException {
        Document document = new Document();
        document.setFileName(file.getOriginalFilename());
        document.setContent(file.getBytes());
        document.setCaseInstanceId(caseInstanceId);
        documentRepository.save(document);

        Map<String, Object> variables = new HashMap<>();
        variables.put("documentId", document.getId());
        cmmnTaskService.complete(taskId, variables);
    }

    // Review the document and complete the review task
    public void reviewDocument(String taskId, String outcome) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("reviewOutcome", outcome);
        cmmnTaskService.complete(taskId, variables);
    }

    // Download the document
    public Document downloadDocument(Long documentId) {
        return documentRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Document not found"));
    }

    // Query tasks for a user
    public List<Task> getTasksForUser(String userId) {
        return cmmnTaskService.createTaskQuery()
                .taskAssignee(userId)
                .list();
    }
}
