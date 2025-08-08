# flowable-app
Document Workflow Service implemented using Flowable CMMN. 

Each workflow → a CMMN Case, 
Each task → a CMMN Task (HumanTask or ServiceTask), 
Each task can upload/download/consolidate files, 
Each task may have multiple business files, 
Files may be re-uploaded → versioning

POST /api/workflows/start to start a workflow (with JSON body).
POST /api/workflows/{caseInstanceId}/tasks/{taskId}/upload for uploading.
GET  /api/workflows/{caseInstanceId}/tasks/{taskId}/files to get files.
POST /api/workflows/{caseInstanceId}/tasks/{taskId}/reupload for re-uploading.
POST /api/workflows/{caseInstanceId}/consolidate for consolidation.
