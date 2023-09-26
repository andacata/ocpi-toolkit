package com.izivia.ocpi.toolkit.modules.locations.http.cpo

import com.izivia.ocpi.toolkit.common.OcpiResponseBody
import com.izivia.ocpi.toolkit.modules.buildHttpRequest
import com.izivia.ocpi.toolkit.modules.isJsonEqualTo
import com.izivia.ocpi.toolkit.modules.locations.LocationsCpoServer
import com.izivia.ocpi.toolkit.modules.locations.domain.*
import com.izivia.ocpi.toolkit.modules.locations.repositories.LocationsCpoRepository
import com.izivia.ocpi.toolkit.modules.locations.services.LocationsCpoService
import com.izivia.ocpi.toolkit.samples.common.Http4kTransportServer
import com.izivia.ocpi.toolkit.transport.TransportClient
import com.izivia.ocpi.toolkit.transport.domain.HttpMethod
import com.izivia.ocpi.toolkit.transport.domain.HttpResponse
import com.izivia.ocpi.toolkit.transport.domain.HttpStatus
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import java.time.Instant

class LocationsCpoHttpGetEvseTest {
    @Test
    fun `should be evse`() {
        val slots = object {
            var locationId = slot<String>()
            var evseUid = slot<String>()
        }
        val srv = mockk<LocationsCpoRepository>() {
            every { getEvse(capture(slots.locationId), capture(slots.evseUid)) } answers {
                Evse(
                    uid = "3256",
                    evse_id = "BE*BEC*E041503001",
                    status = Status.AVAILABLE,
                    capabilities = listOf(Capability.RESERVABLE),
                    connectors = listOf(
                        Connector(
                            id = "1",
                            standard = ConnectorType.IEC_62196_T2,
                            format = ConnectorFormat.CABLE,
                            power_type = PowerType.AC_3_PHASE,
                            max_voltage = 220,
                            max_amperage = 16,
                            tariff_ids = listOf("11"),
                            last_updated = Instant.parse("2015-03-16T10:10:02Z")
                        ),
                        Connector(
                            id = "2",
                            standard = ConnectorType.IEC_62196_T2,
                            format = ConnectorFormat.SOCKET,
                            power_type = PowerType.AC_3_PHASE,
                            max_voltage = 220,
                            max_amperage = 16,
                            tariff_ids = listOf("13"),
                            last_updated = Instant.parse("2015-03-18T08:12:01Z")
                        ),
                    ),
                    floor_level = "-1",
                    physical_reference = "1",
                    last_updated = Instant.parse("2015-06-28T08:12:01Z")
                )
            }
        }.buildServer()
        OcpiResponseBody.now = { Instant.parse("2015-06-30T21:59:59Z") }

        // when
        val resp: HttpResponse = srv.send(
            buildHttpRequest(HttpMethod.GET, "/locations/LOC1/3256")
        )

        // then
        expectThat(slots) {
            get { locationId.captured }.isEqualTo("LOC1")
            get { evseUid.captured }.isEqualTo("3256")
        }
        expectThat(resp) {
            get { status }.isEqualTo(HttpStatus.OK)
            get { body }.isJsonEqualTo(
                """
                {
                  "data": {
                        "uid": "3256",
                        "evse_id": "BE*BEC*E041503001",
                        "status": "AVAILABLE",
                        "capabilities": [
                            "RESERVABLE"
                        ],
                        "connectors": [
                              {
                                "id": "1",
                                "standard": "IEC_62196_T2",
                                "format": "CABLE",
                                "power_type": "AC_3_PHASE",
                                "max_voltage": 220,
                                "max_amperage": 16,
                                "tariff_ids": ["11"],
                                "last_updated": "2015-03-16T10:10:02Z"
                              },
                              {
                                "id": "2",
                                "standard": "IEC_62196_T2",
                                "format": "SOCKET",
                                "power_type": "AC_3_PHASE",
                                "max_voltage": 220,
                                "max_amperage": 16,
                                "tariff_ids": ["13"],
                                "last_updated": "2015-03-18T08:12:01Z"
                              }
                        ],
                        "physical_reference": "1",
                        "floor_level": "-1",
                        "last_updated": "2015-06-28T08:12:01Z"
                  },
                  "status_code": 1000,
                  "status_message": "Success",
                  "timestamp": "2015-06-30T21:59:59Z"
                }
                 """.trimIndent()
            )
        }
    }
}

private fun LocationsCpoRepository.buildServer(): TransportClient {
    val transportServer = Http4kTransportServer("http://localhost:1234", 1234)
    LocationsCpoServer(
        service = LocationsCpoService(this),
        basePath = "/locations"
    ).registerOn(transportServer)

    return transportServer.initRouterAndBuildClient()
}
