package io.micronautgraphqlfederation.satelliteservice.web.dto

class GraphQLRequest(
    val variables: Map<String, Any>?,
    val query: String
)

class GraphQLResponse<T>(
    val data: T?,
    val errors: List<GraphQLError>?
)

class GraphQLError(
    val message: String?
)
