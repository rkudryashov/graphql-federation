package io.graphqlfederation.satelliteservice.web.graphql

import graphql.ErrorType
import graphql.GraphQLError
import graphql.execution.DataFetcherExceptionHandlerParameters
import graphql.execution.DataFetcherExceptionHandlerResult
import graphql.execution.SimpleDataFetcherExceptionHandler
import graphql.language.SourceLocation
import org.slf4j.LoggerFactory
import javax.inject.Singleton

@Singleton
class CustomDataFetcherExceptionHandler : SimpleDataFetcherExceptionHandler() {

    private val log = LoggerFactory.getLogger(this.javaClass)

    override fun onException(handlerParameters: DataFetcherExceptionHandlerParameters): DataFetcherExceptionHandlerResult {
        val exception = handlerParameters.exception
        log.error("Exception while GraphQL data fetching", exception)

        val error = object : GraphQLError {
            override fun getMessage(): String = "There was an error: ${exception.message}"

            override fun getErrorType(): ErrorType? = null

            override fun getLocations(): MutableList<SourceLocation>? = null
        }

        return DataFetcherExceptionHandlerResult.newResult().error(error).build()
    }
}