# flowable-app
Document Workflow Service implemented using Flowable CMMN. 

<?xml version="1.0" encoding="UTF-8"?>
<definitions xmlns="http://www.omg.org/spec/CMMN/20151109/MODEL"
             xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
             xsi:schemaLocation="http://www.omg.org/spec/CMMN/20151109/MODEL http://www.omg.org/spec/CMMN/20151109/CMMN11.xsd">

    <case id="documentCase" name="Document WorkFlow">
        <casePlanModel id="documentCasePlanModel" name="Document WorkFlow">

            <planItem id="upload" definitionRef="uploadTask">
                <itemControl>
                    <requiredRule>
                        <condition>${true}</condition>
                    </requiredRule>
                </itemControl>
            </planItem>

            <planItem id="download" definitionRef="downloadTask">
                <entryCriterion id="uploadCompleteSentry" sentryRef="uploadCompleteSentry" />
            </planItem>

            <planItem id="review" definitionRef="reviewTask">
                <entryCriterion id="downloadCompleteSentry" sentryRef="downloadCompleteSentry" />
            </planItem>

            <planItem id="reupload" definitionRef="uploadTask">
                <entryCriterion id="reviewRejectSentry" sentryRef="reviewRejectSentry" />
            </planItem>

            <sentry id="uploadCompleteSentry">
                <planItemOnPart sourceRef="upload">
                    <standardEvent>complete</standardEvent>
                </planItemOnPart>
            </sentry>

            <sentry id="downloadCompleteSentry">
                <planItemOnPart sourceRef="download">
                    <standardEvent>complete</standardEvent>
                </planItemOnPart>
            </sentry>

            <sentry id="reviewApprovedSentry">
                <planItemOnPart sourceRef="review">
                    <standardEvent>complete</standardEvent>
                </planItemOnPart>
                <ifPart>
                    <condition><![CDATA[${reviewOutcome == 'approved'}]]></condition>
                </ifPart>
            </sentry>

            <sentry id="reviewRejectSentry">
                <planItemOnPart sourceRef="review">
                    <standardEvent>complete</standardEvent>
                </planItemOnPart>
                <ifPart>
                    <condition><![CDATA[${reviewOutcome == 'rejected'}]]></condition>
                </ifPart>
            </sentry>

        </casePlanModel>

        <humanTask id="uploadTask" name="Upload Document">
            <flowable:assignee>initiator</flowable:assignee>
        </humanTask>

        <humanTask id="reviewTask" name="Review Document">
            <flowable:assignee>reviewer</flowable:assignee>
        </humanTask>

        <humanTask id="downloadTask" name="Download Document">
            <flowable:assignee>initiator</flowable:assignee>
        </humanTask>
        
    </case>
</definitions>
