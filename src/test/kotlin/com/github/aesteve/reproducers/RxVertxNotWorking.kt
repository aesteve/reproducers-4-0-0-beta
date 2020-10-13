package com.github.aesteve.reproducers

import io.vertx.junit5.VertxExtension
import io.vertx.junit5.VertxTestContext
import io.vertx.reactivex.core.Vertx
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(VertxExtension::class)
class RxVertxNotWorking {

    @Test
    fun usingRxVertxAsParameter(vertx: Vertx, ctx: VertxTestContext) {
        ctx.completeNow()
    }

}
