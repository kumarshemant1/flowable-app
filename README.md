# flowable-app
Document Workflow Service implemented using Flowable CMMN. 

Each workflow → a CMMN Case, 
Each task → a CMMN Task (HumanTask or ServiceTask), 
Each task can upload/download/consolidate files, 
Each task may have multiple business files, 
Files may be re-uploaded → versioning

POST Start a workflow (with JSON body).

curl -X POST http://localhost:8080/api/workflows/start \
  -H "Content-Type: application/json" \
  -d '{
    "workflowId": "wf-1",
    "workflowName": "Test Workflow",
    "tasks": [
      {
        "taskId": "t1",
        "workflowId": "wf-1",
        "taskName": "Upload Task",
        "taskType": "UPLOAD",
        "businessFiles": [],
        "businessRoles": ["uploader"],
        "completionDay": "2025-08-09T16:43:00"
      }
    ]
  }'

POST Upload Files to a Task

curl -X POST "http://localhost:8080/api/workflows/{caseInstanceId}/tasks/{taskId}/upload" \
  -F "files=@/path/to/your/file1.txt" \
  -F "files=@/path/to/your/file2.pdf"

GET  Download (List) Files for a Task

curl -X GET "http://localhost:8080/api/workflows/{caseInstanceId}/tasks/{taskId}/files"

POST Reupload Files for a Task

curl -X POST "http://localhost:8080/api/workflows/{caseInstanceId}/tasks/{taskId}/reupload" \
  -F "files=@/path/to/your/updatedfile1.txt" \
  -F "files=@/path/to/your/updatedfile2.pdf"

POST Consilidate all files in a workflow

curl -X POST "http://localhost:8080/api/workflows/{caseInstanceId}/consolidate"

GET Get Workflow Info

curl -X GET "http://localhost:8080/api/workflows/{caseInstanceId}"

POST /api/workflows/{caseInstanceId}/consolidate for consolidation.
