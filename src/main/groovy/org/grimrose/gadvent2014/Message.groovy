package org.grimrose.gadvent2014

import groovy.transform.Canonical


@Canonical
class Message {
    Long id
    Date createAt
    String contents
}
