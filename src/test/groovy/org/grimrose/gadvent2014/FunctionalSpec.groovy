package org.grimrose.gadvent2014

import groovy.json.JsonSlurper
import groovy.sql.Sql
import ratpack.groovy.test.LocalScriptApplicationUnderTest
import ratpack.http.MediaType
import ratpack.http.client.RequestSpec
import ratpack.test.http.TestHttpClient
import ratpack.test.http.TestHttpClients
import ratpack.test.remote.RemoteControl
import spock.lang.Shared
import spock.lang.Specification

class FunctionalSpec extends Specification {

    @Shared
    LocalScriptApplicationUnderTest aut = new LocalScriptApplicationUnderTest(development: 'true')

    @Delegate
    TestHttpClient client = TestHttpClients.testHttpClient(aut)

    def setup() {
        RemoteControl remote = new RemoteControl(aut)
        remote.exec {
            get(Sql).execute("delete from messages")
        }
    }

    def cleanupSpec() {
        aut.stop()
    }

    def "should access index page"() {
        when:
        get('/')

        then:
        response.statusCode == 200
        response.body.text.contains '<title>Ratpack: Message App</title>'

        and:
        get('messages')

        then:
        response.statusCode == 200
    }

    def "should find empty messages"() {
        when:
        def json = new JsonSlurper()
        def messages = json.parseText(getText('api'))

        then:
        messages == []
    }

    def "should find any messages"() {
        given:
        def message = 'test message!'
        RemoteControl remote = new RemoteControl(aut)
        remote.exec {
            get(MessageService).create(message)
        }

        when:
        def json = new JsonSlurper()
        def messages = json.parseText(getText('api'))

        then:
        messages.size() == 1
        messages.collect { it.contents }.first() == message
    }

    def "should delete message"() {
        given:
        RemoteControl remote = new RemoteControl(aut)
        def id = remote.exec {
            get(MessageService).create('delete target message!')
            get(Sql).firstRow('SELECT m.id from messages m limit 1').id
        }

        when:
        delete("messages/${id}")

        then:
        response.statusCode == 200
        def result = remote.exec {
            get(Sql).firstRow('SELECT count(*) as result FROM messages').result
        }
        result == 0
    }

    def "should return status code 500"() {
        when:
        post('messages')

        then:
        response.statusCode == 500
    }

    def "should post message"() {
        given:
        def message = "posting message!"

        when:
        requestSpec { RequestSpec requestSpec ->
            requestSpec.headers.add("Content-Type", MediaType.APPLICATION_FORM)
            requestSpec.body.stream { stream ->
                stream << "contents=$message"
            }
        }
        then:
        post('messages')
        response.statusCode == 200

        and:
        def json = new JsonSlurper()
        def messages = json.parseText(getText('api'))

        then:
        messages.size() == 1
        messages.collect { it.contents }.first() == message
    }

}
