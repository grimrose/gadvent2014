import com.zaxxer.hikari.HikariConfig
import org.grimrose.gadvent2014.Message
import org.grimrose.gadvent2014.MessageModule
import org.grimrose.gadvent2014.MessageService
import ratpack.form.Form
import ratpack.groovy.sql.SqlModule
import ratpack.groovy.templating.TemplatingModule
import ratpack.hikari.HikariModule
import ratpack.jackson.JacksonModule
import ratpack.remote.RemoteControlModule
import ratpack.rx.RxRatpack
import ratpack.jackson.Jackson

import static ratpack.groovy.Groovy.groovyTemplate
import static ratpack.groovy.Groovy.ratpack


ratpack {
    bindings {
        add TemplatingModule

        add(HikariModule) { HikariConfig config ->
            config.driverClassName = 'org.postgresql.Driver'

            def uri = new URI(System.env.DATABASE_URL ?: "postgres://test:test@localhost/gadvent2014")

            def url = "jdbc:postgresql://${uri.host}${uri.path}"
            def username = uri.userInfo.split(":")[0]
            def password = ''
            if (!uri.userInfo.endsWith(':')) {
                password = uri.userInfo.split(":")[1]
            }

            config.jdbcUrl = url
            config.username = username
            config.password = password ?: ''

        }
        add new SqlModule()
        add new JacksonModule()
        add new RemoteControlModule()

        add new MessageModule()

        init { MessageService messageService ->
            RxRatpack.initialize()
            messageService.init()
        }
    }

    handlers { MessageService messageService ->
        get {
            render groovyTemplate("index.html",
                    title: "Message App"
            )
        }

        handler('messages') {
            byMethod {
                get {
                    redirect '/'
                }
                post {
                    // メッセージを登録する。
                    Form form = parse(Form)
                    def contents = form.get('contents', '')
                    if (!contents) {
                        render groovyTemplate('index.html',
                                title: "Message App",
                                errorMessage: 'メッセージを入力してください'
                        )
                    } else {
                        context.blocking {
                            messageService.create(contents)
                        }.then {
                            redirect '/'
                        }
                    }
                }
            }
        }

        delete('messages/:id') {
            // メッセージを削除する。
            def id = pathTokens["id"].toLong()
            context.blocking {
                messageService.delete(id)
            }.then {
                render Jackson.json('success')
            }
        }

        handler('api') {
            messageService.all().subscribe { List<Message> messages ->
                render Jackson.json(messages)
            }
        }

        assets "public"
    }
}
