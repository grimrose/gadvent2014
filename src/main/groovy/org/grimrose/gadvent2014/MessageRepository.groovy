package org.grimrose.gadvent2014

import com.google.inject.Inject
import groovy.sql.Sql
import groovy.util.logging.Slf4j
import rx.Observable

import java.sql.Timestamp

@Slf4j
class MessageRepository {

    @Inject
    private Sql sql

    void initialize() {
        log.info "Creating tables"
        sql.executeUpdate("DROP TABLE IF EXISTS messages")
        sql.executeUpdate("CREATE TABLE IF NOT EXISTS messages (id SERIAL PRIMARY KEY, createAt TIMESTAMP, contents TEXT)")
    }

    Observable<List<Message>> findAll() {
        Observable.from(sql.rows("SELECT id, createAt, contents FROM messages ORDER BY createAt")).map {
            new Message(id: it.id, createAt: it.createAt, contents: it.contents)
        }.toList()
    }

    def insert(Message message) {
        sql.executeInsert("INSERT INTO messages (createAt, contents) VALUES (?, ?)", new Timestamp(message.createAt.getTime()), message.contents)
    }

    def delete(long id) {
        sql.executeUpdate("DELETE FROM messages WHERE id = ?", id)
    }

}
