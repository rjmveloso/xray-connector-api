package io.github.jenkins.xray

import com.github.tomakehurst.wiremock.junit5.WireMockTest
import org.junit.jupiter.api.Test

import static com.github.tomakehurst.wiremock.client.WireMock.get
import static com.github.tomakehurst.wiremock.client.WireMock.ok
import static com.github.tomakehurst.wiremock.client.WireMock.put
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching

@WireMockTest(httpPort = 8080)
class XrayUrlInvokerTest {

    def steps = [
            withCredentials: (list, closure) -> { closure.call() },
            usernameColonPassword: {},
            env: [USERPASS: {}]
    ]

    private final XrayUrlInvoker victim = new XrayUrlInvoker(steps, [url: 'http://localhost:8080'])

    @Test
    void 'test_get_resource'() {
        def result =
                """
                {
                   "id":371,
                   "status":"TODO",
                   "testKey":"CALC-12",
                   "testExecKey":"CALC-13",
                   "assignee":"admin",
                   "executedBy":"admin",
                   "startedOn":"2016-10-11T17:14:03+01:00",
                   "finishedOn": "2016-10-24T14:58:35+01:00",
                   "duration": 1115072328
                }
                """
        stubFor(get(urlPathMatching('.*/testrun/.*')).willReturn(ok(result)))

        def response = victim.invoke('GET', '/testrun/1')

        assert response == result
    }

    @Test
    void 'test_put_with_body'() {
        stubFor(put(urlPathMatching('.*/testrun/.*')).willReturn(ok()))

        def data =
                """
                {
                    "id":"729",
                    "status":"PASS",
                    "comment":"comment",
                    "defects":[
                            "TEST-114",
                            "TEST-115",
                            "TEST-116"
                    ]
                }
                """
        def response = victim.invoke('PUT', '/testrun/3/step/2', data)

        assert response == ''
    }

    @Test
    void 'test_put_without_body'() {
        stubFor(put(urlPathMatching('.*/testrun/.*')).willReturn(ok()))

        def response = victim.invoke('PUT', '/testrun/3/status?status=PASS')

        assert response == ''
    }

}
