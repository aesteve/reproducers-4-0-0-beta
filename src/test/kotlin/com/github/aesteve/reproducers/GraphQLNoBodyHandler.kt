package com.github.aesteve.reproducers

import graphql.GraphQL
import graphql.schema.idl.RuntimeWiring
import graphql.schema.idl.SchemaGenerator
import graphql.schema.idl.SchemaParser
import graphql.schema.idl.TypeRuntimeWiring
import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.Router
import io.vertx.ext.web.client.WebClientOptions
import io.vertx.ext.web.handler.BodyHandler
import io.vertx.ext.web.handler.graphql.GraphQLHandler
import io.vertx.junit5.Timeout
import io.vertx.junit5.VertxExtension
import io.vertx.junit5.VertxTestContext
import io.vertx.reactivex.ext.web.client.WebClient
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.util.concurrent.TimeUnit


@ExtendWith(VertxExtension::class)
class GraphQLNoBodyHandler {
    data class Book(val id: String, val name: String)

    @BeforeEach
    fun graphQlSetup(vertx: Vertx, ctx: VertxTestContext) {
        val router = Router.router(vertx)
        val sdl: String = """
            type Query {
                bookById(id: String): Book
            }
            type Book {
                id: String
                name: String
            }
        """.trimIndent()
        val types = SchemaParser().parse(sdl)
        val runtime = RuntimeWiring.newRuntimeWiring().type(
            TypeRuntimeWiring.newTypeWiring("Query").dataFetcher("bookById") { Book("Vert.x in action", "Vert.x in action") }).build()
        val graphql = GraphQL.newGraphQL(SchemaGenerator().makeExecutableSchema(types, runtime)).build()
        // router.post("/graphql").handler(BodyHandler.create()) // <- makes the test pass
        router.post("/graphql").handler(GraphQLHandler.create(graphql))
        vertx.createHttpServer()
            .requestHandler(router)
            .listen(1234, ctx.succeedingThenComplete())
    }

    @Test
    @Timeout(value = 5, timeUnit = TimeUnit.SECONDS)
    fun checkGraphQL(vertx: Vertx, ctx: VertxTestContext) {
        WebClient.create(io.vertx.reactivex.core.Vertx(vertx), WebClientOptions().setDefaultPort(1234))
            .post("/graphql")
            .rxSendJsonObject(JsonObject().put("query", "query { bookById(id: \"anything\") { name ] }"))
            .map {
                assertEquals(200, it.statusCode())
            }
            .subscribe({ ctx.completeNow() }, ctx::failNow)
    }

}
