package com.odobo.grails.plugin.springsecurity.rest

import grails.plugins.rest.client.RestResponse
import grails.util.Holders
import spock.lang.IgnoreIf
import spock.lang.Unroll

@IgnoreIf({ Holders.config.grails.plugin.springsecurity.rest.token.validation.useBearerToken })
class RestLogoutFilterSpec extends AbstractRestSpec {

    void "logout filter can remove a token"() {
        given:
        RestResponse authResponse = sendCorrectCredentials()
        String token = authResponse.json.access_token

        when:
        def response = restBuilder.post("${baseUrl}/api/logout") {
            header 'X-Auth-Token', token
        }

        then:
        response.status == 200

        when:
        response = restBuilder.get("${baseUrl}/api/validate") {
            header 'X-Auth-Token', token
        }

        then:
        response.status == 401
    }

    void "logout filter returns 404 if token is not found"() {
        when:
        def response = restBuilder.post("${baseUrl}/api/logout") {
            header 'X-Auth-Token', 'whatever'
        }

        then:
        response.status == 404

    }

    void "calling /api/logout without token returns 400"() {
        when:
        def response = restBuilder.post("${baseUrl}/api/logout")

        then:
        response.status == 400
    }

    @Unroll
    void "#httpMethod requests generate #statusCode responses"() {

        given:
        RestResponse authResponse = sendCorrectCredentials()
        String token = authResponse.json.access_token

        when:
        def response = restBuilder."${httpMethod}"("${baseUrl}/api/logout") {
            header 'X-Auth-Token', token
        }

        then:
        response.status == statusCode

        where:
        httpMethod  | statusCode
        'get'       | 405
        'post'      | 200
        'put'       | 405
        'delete'    | 405
    }


}