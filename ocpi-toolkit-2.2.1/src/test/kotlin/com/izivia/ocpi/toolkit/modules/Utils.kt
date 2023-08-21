package com.izivia.ocpi.toolkit.modules

import com.izivia.ocpi.toolkit.common.SearchResult
import com.izivia.ocpi.toolkit.transport.domain.HttpMethod
import com.izivia.ocpi.toolkit.transport.domain.HttpRequest
import io.json.compare.CompareMode
import io.json.compare.JSONCompare
import org.http4k.core.Uri
import org.http4k.core.queries
import strikt.api.DescribeableBuilder
import strikt.assertions.isNotNull

fun GET(path: String): HttpRequest = Uri.of(path).let {
    HttpRequest(HttpMethod.GET, it.path, queryParams = it.queries().toMap())
}

fun DescribeableBuilder<String?>.isJsonEqualTo(str: String) {
    isNotNull()
    JSONCompare.assertMatches(str, this.subject, setOf(CompareMode.REGEX_DISABLED))
}

fun <E> List<E>.toSearchResult(limit: Int = 50, offset: Int = 0) =
    SearchResult(this.subList(offset, Math.min(offset + limit, size)), size, limit, offset, null)
