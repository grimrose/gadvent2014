package org.grimrose.gadvent2014

import com.google.inject.Inject
import groovy.util.logging.Slf4j

@Slf4j
class MessageService {

    @Inject
    MessageRepository repository

    void init() {
        repository.initialize()
    }

    def all() {
        repository.findAll()
    }

    def create(String contents) {
        Message message = new Message(createAt: new Date(), contents: contents)
        repository.insert(message)
    }

    def delete(long id) {
        repository.delete(id)
    }

}
