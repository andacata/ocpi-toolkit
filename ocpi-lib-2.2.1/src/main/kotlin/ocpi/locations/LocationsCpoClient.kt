package ocpi.locations

import common.*
import ocpi.credentials.repositories.PlatformRepository
import ocpi.locations.domain.*
import transport.TransportClient
import transport.domain.HttpMethod
import transport.domain.HttpRequest

/**
 * Sends calls to an eMSP server
 * @property transportClient
 */
class LocationsCpoClient(
    private val transportClient: TransportClient,
    private val platformRepository: PlatformRepository
) : LocationsEmspInterface {

    override fun getLocation(
        countryCode: CiString,
        partyId: CiString,
        locationId: CiString
    ): OcpiResponseBody<Location?> =
        transportClient
            .send(
                HttpRequest(
                    method = HttpMethod.GET,
                    path = "/locations/$countryCode/$partyId/$locationId",
                ).authenticate(platformRepository = platformRepository, baseUrl = transportClient.baseUrl)
            )
            .parseBody()

    override fun getEvse(
        countryCode: CiString,
        partyId: CiString,
        locationId: CiString,
        evseUid: CiString
    ): OcpiResponseBody<Evse?> =
        transportClient
            .send(
                HttpRequest(
                    method = HttpMethod.GET,
                    path = "/locations/$countryCode/$partyId/$locationId/$evseUid"
                ).authenticate(platformRepository = platformRepository, baseUrl = transportClient.baseUrl)
            )
            .parseBody()

    override fun getConnector(
        countryCode: CiString,
        partyId: CiString,
        locationId: CiString,
        evseUid: CiString,
        connectorId: CiString
    ): OcpiResponseBody<Connector?> =
        transportClient
            .send(
                HttpRequest(
                    method = HttpMethod.GET,
                    path = "/locations/$countryCode/$partyId/$locationId/$evseUid/$connectorId"
                ).authenticate(platformRepository = platformRepository, baseUrl = transportClient.baseUrl)
            )
            .parseBody()

    override fun putLocation(
        countryCode: CiString,
        partyId: CiString,
        locationId: CiString,
        location: Location
    ): OcpiResponseBody<Location> =
        transportClient
            .send(
                HttpRequest(
                    method = HttpMethod.PUT,
                    path = "/locations/$countryCode/$partyId/$locationId",
                    body = mapper.writeValueAsString(location)
                ).authenticate(platformRepository = platformRepository, baseUrl = transportClient.baseUrl)
            )
            .parseBody()

    override fun putEvse(
        countryCode: CiString,
        partyId: CiString,
        locationId: CiString,
        evseUid: CiString,
        evse: Evse
    ): OcpiResponseBody<Evse> =
        transportClient
            .send(
                HttpRequest(
                    method = HttpMethod.PUT,
                    path = "/locations/$countryCode/$partyId/$locationId/$evseUid",
                    body = mapper.writeValueAsString(evse)
                ).authenticate(platformRepository = platformRepository, baseUrl = transportClient.baseUrl)
            )
            .parseBody()

    override fun putConnector(
        countryCode: CiString,
        partyId: CiString,
        locationId: CiString,
        evseUid: CiString,
        connectorId: CiString,
        connector: Connector
    ): OcpiResponseBody<Connector> =
        transportClient
            .send(
                HttpRequest(
                    method = HttpMethod.PUT,
                    path = "/locations/$countryCode/$partyId/$locationId/$evseUid/$connectorId",
                    body = mapper.writeValueAsString(connector)
                ).authenticate(platformRepository = platformRepository, baseUrl = transportClient.baseUrl)
            )
            .parseBody()

    override fun patchLocation(
        countryCode: CiString,
        partyId: CiString,
        locationId: CiString,
        location: LocationPartial
    ): OcpiResponseBody<Location?> = transportClient
        .send(
            HttpRequest(
                method = HttpMethod.PATCH,
                path = "/locations/$countryCode/$partyId/$locationId",
                body = mapper.writeValueAsString(location)
            ).authenticate(platformRepository = platformRepository, baseUrl = transportClient.baseUrl)
        )
        .parseBody()

    override fun patchEvse(
        countryCode: CiString,
        partyId: CiString,
        locationId: CiString,
        evseUid: CiString,
        evse: EvsePartial
    ): OcpiResponseBody<Evse?> = transportClient
        .send(
            HttpRequest(
                method = HttpMethod.PATCH,
                path = "/locations/$countryCode/$partyId/$locationId",
                body = mapper.writeValueAsString(evse)
            ).authenticate(platformRepository = platformRepository, baseUrl = transportClient.baseUrl)
        )
        .parseBody()

    override fun patchConnector(
        countryCode: CiString,
        partyId: CiString,
        locationId: CiString,
        evseUid: CiString,
        connectorId: CiString,
        connector: ConnectorPartial
    ): OcpiResponseBody<Connector?> = transportClient
        .send(
            HttpRequest(
                method = HttpMethod.PATCH,
                path = "/locations/$countryCode/$partyId/$locationId",
                body = mapper.writeValueAsString(connector)
            ).authenticate(platformRepository = platformRepository, baseUrl = transportClient.baseUrl)
        )
        .parseBody()
}