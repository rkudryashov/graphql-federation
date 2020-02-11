package io.graphqlfederation.planetservice.web.dto

class GraphQLRequest(
    val query: String,
    val variables: Map<String, Any?>?,
    val operationName: String?
)

class GraphQLResponse(
    val data: Map<String, Any?>?,
    val errors: List<GraphQLError>?
)

class GraphQLError(
    val message: String?
)
