const { ApolloServer } = require("apollo-server");
const { ApolloGateway } = require("@apollo/gateway");

const gateway = new ApolloGateway({
    serviceList: [
        { name: "planet-service", url: "http://localhost:8081/graphql" },
        { name: "satellite-service", url: "http://localhost:8082/graphql" },
    ]
});

const server = new ApolloServer({ gateway, subscriptions: false });

server.listen().then(({ url }) => {
    console.log(`🚀 Server ready at ${url}`);
});
