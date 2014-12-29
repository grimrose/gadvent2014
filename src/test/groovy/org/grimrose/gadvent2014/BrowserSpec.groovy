package org.grimrose.gadvent2014

import geb.spock.GebSpec
import groovy.sql.Sql
import ratpack.groovy.test.LocalScriptApplicationUnderTest
import ratpack.test.remote.RemoteControl
import spock.lang.Shared

class BrowserSpec extends GebSpec {

    @Shared
    LocalScriptApplicationUnderTest aut = new LocalScriptApplicationUnderTest(development: 'true')

    def setupSpec() {
        browser.baseUrl = aut.address.toString()
    }

    def setup() {
        RemoteControl remote = new RemoteControl(aut)
        remote.exec {
            get(Sql).execute("delete from messages")
        }
    }

    def cleanupSpec() {
        aut.close()
    }

    def "should entry and delete message"() {
        given:
        go('/')

        when:
        $('#messageContents').value '1234'
        $('button[type=submit]').click()

        and:
        waitFor { $('#messagesBody').children() }

        then:
        $('#messagesBody').children().size() == 1

        and:
        $('#messagesBody').find('button').click()

        then:
        waitFor(2) {
            $('#messagesBody').children().isEmpty()
        }
    }

}
