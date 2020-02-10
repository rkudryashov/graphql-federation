package io.graphqlfederation.authservice.web.dto

class GraphQLRequest(
    val variables: Map<String, Any>?,
    val query: String
)

class GraphQLResponse(
    val data: Map<String, Any?>?,
    val errors: List<GraphQLError>?
)

class GraphQLError(
    val message: String?
)
