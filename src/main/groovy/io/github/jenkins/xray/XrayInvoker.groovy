package io.github.jenkins.xray

interface XrayInvoker {

    String invoke(String verb, String path)

    String invoke(String verb, String path, String body)

}
