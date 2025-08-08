# flowable-app
Document Workflow Service implemented using Flowable CMMN. 

Each workflow → a CMMN Case, 
Each task → a CMMN Task (HumanTask or ServiceTask), 
Each task can upload/download/consolidate files, 
Each task may have multiple business files, 
Files may be re-uploaded → versioning

POST /workflows/start – Start new workflow instance

POST /workflows/{caseId}/tasks/{taskId}/upload – Upload business files

GET /workflows/{caseId}/tasks/{taskId}/files – Download files

POST /workflows/{caseId}/tasks/{taskId}/reupload – Reupload a file

POST /workflows/{caseId}/consolidate – Consolida
