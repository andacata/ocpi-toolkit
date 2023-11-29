package com.izivia.ocpi.toolkit.modules.credentials.repositories

import com.izivia.ocpi.toolkit.modules.credentials.domain.CredentialRole
import com.izivia.ocpi.toolkit.modules.versions.domain.Endpoint
import com.izivia.ocpi.toolkit.modules.versions.domain.Version

/**
 * **Note about tokens:**
 * The Credentials Token A is used by the sender to communicate with the receiver (only when initiating registration).
 * After registration, it is invalidated.
 *
 * Then, there are two tokens, and the token to use is specified by the registration process.
 * - When you are the receiver during registration:
 *   - OCPI Token B is the client token, the one you have to use to send requests
 *   - OCPI Token C is the server token, the one you have to use to check if requests are properly authenticated
 * - When you are the sender during registration:
 *   - OCPI Token B is the server token, the one you have to use to check if requests are properly authenticated
 *   - OCPI Token C is the client token, the one you have to use to send requests
 *
 * This is why we do not use OCPI's token B and token C naming. According to who you are in the registration process,
 * you have to use a different token. Using "Client Token" and "Server Token" simplifies that process of picking the
 * right token for the right operation.
 */
interface PlatformRepository {

    /**
     * When calling a partner, the http client has to be authenticated. This method is called to retrieve the
     * token A for a given partner identified by its /versions url.
     *
     * The token A is only used during registration process.
     *
     * @param platformUrl the partner, identified by its /versions url
     * @return the token A, if found, null otherwise
     */
    suspend fun getCredentialsTokenA(platformUrl: String): String?

    /**
     * When calling a partner, the http client has to be authenticated. This method is called to retrieve the
     * client token for a given partner identified by its /versions url.
     *
     * @param platformUrl the partner, identified by its /versions url
     * @return the client token, if found, null otherwise
     */
    suspend fun getCredentialsClientToken(platformUrl: String): String?

    /**
     * A partner has to use its server token to communicate with us. On first registration, the token used is the token
     * A. This method is called to check if the token A of a partner is valid.
     *
     * @param credentialsTokenA the token of a partner
     * @return true if the token is valid (exists), false otherwise
     */
    suspend fun isCredentialsTokenAValid(credentialsTokenA: String): Boolean

    /**
     * A partner has to use its server token to communicate with us. This method is called to check if the token of a
     * partner is valid.
     *
     * @param credentialsServerToken the token of a partner
     * @return true if the token is valid (exists), false otherwise
     */
    suspend fun isCredentialsServerTokenValid(credentialsServerToken: String): Boolean

    /**
     * Used to find a platform url by its server token. Basically used to retrieve all the partner information
     * from the token in a request.
     *
     * @param credentialsServerToken the server token, the one partners use to communicate
     * @return the platform url
     */
    suspend fun getPlatformUrlByCredentialsServerToken(credentialsServerToken: String): String?

    /**
     * Used to get available endpoints for a given partner identified by its url (platformUrl)
     *
     * @param platformUrl the partner, identified by its /versions url
     * @return the list of available endpoints for the given partner
     */
    suspend fun getEndpoints(platformUrl: String): List<Endpoint>

    /**
     * Used to get used version for a given partner identified by its url (platformUrl)
     *
     * @param platformUrl the partner, identified by its /versions url
     * @return the currently used version for the given partner or null
     */
    suspend fun getVersion(platformUrl: String): Version?

    /**
     * This is the first function to be called on registration. So that later on we can use platform url as an
     * identifier for a given partner.
     *
     * It searches for a platform with the given token A and saves the corresponding platformUrl
     *
     * @param tokenA
     * @param platformUrl the partner, identified by its /versions url
     * @return the platformUrl if a platform was found for given token A and the update was a success, null otherwise
     */
    suspend fun savePlatformUrlForTokenA(tokenA: String, platformUrl: String): String?

    /**
     * Used to save credentials roles given by a partner during registration.
     *
     * @param platformUrl the partner, identified by its /versions url
     * @param credentialsRoles
     * @return the updated credentials roles
     */
    suspend fun saveCredentialsRoles(platformUrl: String, credentialsRoles: List<CredentialRole>): List<CredentialRole>

    /**
     * Used to save available version for a given partner identified by its url (platformUrl)
     *
     * @param platformUrl the partner, identified by its /versions url
     * @param version Version
     * @return the updated version
     */
    suspend fun saveVersion(platformUrl: String, version: Version): Version

    /**
     * Used to save endpoints for a given partner identified by its url (platformUrl)
     *
     * @param platformUrl the partner, identified by its /versions url
     * @param endpoints List<Endpoint>
     * @return List<Endpoint> the updated list of endpoints
     */
    suspend fun saveEndpoints(platformUrl: String, endpoints: List<Endpoint>): List<Endpoint>

    /**
     * Called to save the client token for a given partner identified by its url (platformUrl).
     *
     * This token is the one that will be used to communicate with the partner.
     * For context in OCPI, on registration, this token is:
     * - if you are the receiver: token B
     * - if you are the sender: token C
     *
     * @param platformUrl the partner, identified by its /versions url
     * @param credentialsClientToken String
     * @return the credentialsClientToken
     */
    suspend fun saveCredentialsClientToken(platformUrl: String, credentialsClientToken: String): String

    /**
     * Called to save the server token for a given partner identified by its url (platformUrl).
     *
     * This token is the one that the partner will use to communicate. So it is this token that has to be used to check
     * if requests are properly authenticated.
     *
     * For context in OCPI, on registration, this token is:
     * - if you are the receiver: token C
     * - if you are the sender: token B
     *
     * @param platformUrl the partner, identified by its /versions url
     * @param credentialsServerToken String
     * @return the credentialsServerToken
     */
    suspend fun saveCredentialsServerToken(platformUrl: String, credentialsServerToken: String): String

    /**
     * Called once registration is done for a given partner identified by its url (platformUrl).
     *
     * The token A has to be invalidated.
     *
     * @param platformUrl the partner, identified by its /versions url
     * @return true if it was a success, false otherwise
     */
    suspend fun invalidateCredentialsTokenA(platformUrl: String): Boolean

    /**
     * Called on unregistration for a given partner identified by its url (platformUrl).
     *
     * It has to at least invalidate all the tokens. So that future requests with those token fail.
     *
     * @param platformUrl the partner, identified by its /versions url
     * @return true if it was a success, false otherwise
     */
    suspend fun unregisterPlatform(platformUrl: String): Boolean
}
