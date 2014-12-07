package org.grimrose.gadvent2014

import com.google.inject.AbstractModule
import com.google.inject.Scopes

class MessageModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(MessageRepository).in(Scopes.SINGLETON)
        bind(MessageService).in(Scopes.SINGLETON)
    }

}
