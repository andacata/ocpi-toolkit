package samples.locations

import ocpi.locations.LocationsEmspClient
import samples.common.Http4kTransportClientBuilder

/**
 * Example on how to use the eMSP client
 */
fun main() {
    // We instantiate the clients that we want to use
    val locationsEmspClient = LocationsEmspClient(
        transportClientBuilder = Http4kTransportClientBuilder(),
        serverVersionsEndpointUrl = cpoServerVersionsUrl,
        platformRepository = DUMMY_PLATFORM_REPOSITORY
    )

    // We can use it
    println(
        locationsEmspClient.getConnector(locationId = "location1", evseUid = "evse1", connectorId = "connector1")
    )
}