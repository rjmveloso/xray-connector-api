package io.github.jenkins.xray

class XrayUrlInvoker implements XrayInvoker {

    private static final XRAY_ENDPOINT = 'rest/raven/1.0/api'
    private static final CONTENT_TYPE = 'application/json'

    private final def steps;
    private final String baseURL;
    private final String credentialsId;

    // TODO use jenkins-ci:credentials library to avoid stethe need for ps
    XrayUrlInvoker(steps, Map<String, String> configs) {
        this.steps = steps
        this.baseURL = configs['url']
        this.credentialsId = configs['credentialsId']
    }

    @Override
    String invoke(String verb, String path) {
        steps.withCredentials([steps.usernameColonPassword(credentialsId: "${credentialsId}", variable: 'USERPASS')]) {
            def connection = "${baseURL}/${XRAY_ENDPOINT}/${path}".toURL().openConnection()
            connection.setRequestProperty('Authorization', "Basic ${steps.env.USERPASS}")
            connection.setRequestProperty('Content-Type', CONTENT_TYPE)
            connection.with {
                doOutput = true
                requestMethod = verb
                inputStream.text
            }
        }
    }

    @Override
    String invoke(String verb, String path, String body) {
        steps.withCredentials([steps.usernameColonPassword(credentialsId: "${credentialsId}", variable: 'USERPASS')]) {
            def connection = "${baseURL}/${XRAY_ENDPOINT}/${path}".toURL().openConnection()
            connection.setRequestProperty('Authorization', "Basic ${steps.env.USERPASS}")
            connection.setRequestProperty('Content-Type', CONTENT_TYPE)
            connection.with {
                doOutput = true
                requestMethod = verb
                outputStream.withWriter { it << body }
                inputStream.text
            }
        }
    }

}
