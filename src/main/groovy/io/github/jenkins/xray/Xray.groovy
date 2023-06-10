package io.github.jenkins.xray;

/**
 * Utility to manage XRAY items through a Jenkins pipeline (scripted or declarative)
 *
 * <p>Example:
 * <pre>
 * {@code
 *   @Library('xray') import io.github.jenkins.jenkins.xray.Xray
 *   def xray = new Xray(this, [url: 'https://company.jira.com/', credentialsId: 'jira_credentials')
 *   pipeline {
 *     agent any
 *     stages {
 *       stage('Example') {
 *         steps {
 *           script {
 *             def data = xray.getTestExecution('ASSPI-427')
 *             echo "test execution ${data}"
 *
 *             def json = readJSON(text: data)
 *             echo "status: ${json[0].status}"
 *           }
 *         }
 *       }
 *     }
 *   }}</pre>
 */
class Xray {

    private final XrayInvoker invoker;

    Xray(steps, Map<String, String> configs) {
        this.invoker = new XrayUrlInvoker(steps, configs)
    }

    // TODO create an interface and implementations for cURL and httpRequest plugin
    // NOTE: httpRequest would allow http calls on any operating system
    // steps.httpRequest(authenticate: "${credentialId}", quiet: true,
    //         contentType: 'APPLICATION_JSON', responseHandle: 'STRING',
    //         url: "${url}${XRAY_ENDPOINT}/testexec/${testPlanId}/test?detailed=${detailed}")

    /**
     * Return a list of tests associated with a test execution
     * @param testExecKey test execution key
     * @param detailed detailed information
     * @return Test execution data
     */
    def getTestExecution(String testExecKey, boolean detailed = false) {
        invoker.invoke('GET', "testexec/${testExecKey}/test?detailed=${detailed}")
    }

    /**
     * Get a test run information
     * @param testRunId test run id
     * @return Test run data
     */
    def getTestRunDetail(String testRunId) {
        invoker.invoke('GET', "testrun/${testRunId}")
    }

    /**
     * Get a test run information
     * @param testExecIssueKey test execution key
     * @param testIssueKey test issue key
     * @return Test run data
     */
    def getTestRunDetail(String testExecIssueKey, String testIssueKey) {
        invoker.invoke('GET', "testrun?testExecIssueKey=${testExecIssueKey}&testIssueKey=${testIssueKey}")
    }

    /**
     * Update the status of a test run
     * @param testRunId test run id
     * @param status status
     */
    def updateTestRunStatus(String testRunId, String status) {
        invoker.invoke('PUT', "testrun/${testRunId}/status?status=${status}")
    }

    /**
     * Update the status of a test step
     * @param testRunId test run id
     * @param stepId test step id
     * @param status status
     */
    def updateTestStepStatus(String testRunId, String stepId, String status) {
        invoker.invoke('PUT', "testrun/${testRunId}/step/${stepId}/status?status=${status}")
    }

}
