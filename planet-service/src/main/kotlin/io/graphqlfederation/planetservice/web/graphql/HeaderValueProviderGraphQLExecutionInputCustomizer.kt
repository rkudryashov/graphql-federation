package io.graphqlfederation.planetservice.web.graphql

import graphql.ExecutionInput
import io.gqljf.federation.tracing.HTTPRequestHeaders
import io.micronaut.configuration.graphql.DefaultGraphQLExecutionInputCustomizer
import io.micronaut.context.annotation.Primary
import io.micronaut.core.async.publisher.Publishers
import io.micronaut.http.HttpRequest
import org.reactivestreams.Publisher
import javax.inject.Singleton

@Singleton
// mark it as primary to override the default one
@Primary
class HeaderValueProviderGraphQLExecutionInputCustomizer : DefaultGraphQLExecutionInputCustomizer() {

    override fun customize(executionInput: ExecutionInput, httpRequest: HttpRequest<*>): Publisher<ExecutionInput> {
        val context = HTTPRequestHeaders { headerName ->
            httpRequest.headers[headerName]
        }

        return Publishers.just(executionInput.transform {
            it.context(context)
        })
    }
}
