package ocpi.credentials

import common.httpResponse
import common.mapper
import common.parseAuthorizationHeader
import ocpi.credentials.domain.Credentials
import ocpi.credentials.services.CredentialsServerService
import transport.TransportServer
import transport.domain.FixedPathSegment
import transport.domain.HttpMethod

class CredentialsServer(
    transportServer: TransportServer,
    service: CredentialsServerService
) {
    init {
        transportServer.handle(
            method = HttpMethod.GET,
            path = listOf(
                FixedPathSegment("/credentials")
            )
        ) { req ->
            req.httpResponse {
                service.get(
                    tokenC = req.parseAuthorizationHeader()
                )
            }
        }

        transportServer.handle(
            method = HttpMethod.POST,
            path = listOf(
                FixedPathSegment("/credentials")
            )
        ) { req ->
            req.httpResponse {
                service.post(
                    tokenA = req.parseAuthorizationHeader(),
                    credentials = mapper.readValue(req.body!!, Credentials::class.java)
                )
            }
        }

        transportServer.handle(
            method = HttpMethod.PUT,
            path = listOf(
                FixedPathSegment("/credentials")
            )
        ) { req ->
            req.httpResponse {
                service.put(
                    tokenC = req.parseAuthorizationHeader(),
                    credentials = mapper.readValue(req.body!!, Credentials::class.java)
                )
            }
        }

        transportServer.handle(
            method = HttpMethod.DELETE,
            path = listOf(
                FixedPathSegment("/credentials")
            )
        ) { req ->
            req.httpResponse {
                service.delete(
                    tokenC = req.parseAuthorizationHeader()
                )
            }
        }

    }
}