package samples.common

import ocpi.credentials.repositories.PlatformRepository
import ocpi.versions.domain.Endpoint
import ocpi.versions.domain.Version

class PlatformCacheRepository: PlatformRepository {
    val platforms = mutableMapOf<String, Platform>()

    override fun saveVersion(platformUrl: String, version: Version): Version = platforms
        .getOrDefault(platformUrl, Platform(platformUrl))
        .copy(version = version)
        .also { platforms[it.url] = it }
        .let { it.version!! }

    override fun saveEndpoints(platformUrl: String, endpoints: List<Endpoint>): List<Endpoint> = platforms
        .getOrDefault(platformUrl, Platform(platformUrl))
        .copy(endpoints = endpoints)
        .also { platforms[it.url] = it }
        .let { it.endpoints!! }

    override fun saveCredentialsTokenB(platformUrl: String, credentialsTokenB: String): String = platforms
        .getOrDefault(platformUrl, Platform(platformUrl))
        .copy(tokenB = credentialsTokenB)
        .also { platforms[it.url] = it }
        .let { it.tokenB!! }

    override fun saveCredentialsTokenC(platformUrl: String, credentialsTokenC: String): String = platforms
        .getOrDefault(platformUrl, Platform(platformUrl))
        .copy(tokenC = credentialsTokenC)
        .also { platforms[it.url] = it }
        .let { it.tokenC!! }

    override fun getCredentialsTokenC(platformUrl: String): String? = platforms[platformUrl]?.tokenC

    override fun getCredentialsTokenA(platformUrl: String): String? = platforms[platformUrl]?.tokenA

    override fun removeCredentialsTokenA(platformUrl: String) {
        platforms
            .getOrDefault(platformUrl, Platform(platformUrl))
            .copy(tokenA = null)
            .also { platforms[it.url] = it }
            .let { it.tokenC!! }
    }

    override fun getPlatformByTokenA(token: String): String? = platforms
        .values.firstOrNull { it.tokenA == token }?.url

    override fun getPlatformByTokenB(token: String): String? = platforms
        .values.firstOrNull { it.tokenB == token }?.url

    override fun getPlatformByTokenC(token: String): String? = platforms
        .values.firstOrNull { it.tokenC == token }?.url
}