# flowable-app
Document Workflow Service implemented using Flowable CMMN. 

Workflow Creation: Users can create a workflow (CMMN Case).

Task Management: Each workflow contains multiple tasks.
  Tasks can support:
  Upload (files/documents)
  Download (retrieve business files)
  Consolidate (aggregate or process files as a final step)

Business Files:
Each task can have multiple business files attached.
All data (workflows, tasks, files) must be persisted using Flowable’s tables.
All retrieval and updates via Flowable REST APIs.
