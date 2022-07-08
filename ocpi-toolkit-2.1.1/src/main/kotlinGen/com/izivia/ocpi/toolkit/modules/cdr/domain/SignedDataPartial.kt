//  ----------
//  - WARNING -
//  ----------
//  This code is generated AND MUST NOT BE EDITED
//  ----------
package com.izivia.ocpi.toolkit.modules.cdr.domain

import kotlin.Int
import kotlin.String
import kotlin.collections.List

/**
 * Partial representation of [com.izivia.ocpi.toolkit.modules.cdr.domain.SignedData]
 */
public data class SignedDataPartial(
  public val encoding_method: String?,
  public val encoding_method_version: Int?,
  public val public_key: String?,
  public val signed_values: List<SignedValuePartial>?,
  public val url: String?,
)

public fun SignedData.toPartial(): SignedDataPartial {
   return SignedDataPartial(
     encoding_method = encoding_method,
    encoding_method_version = encoding_method_version,
    public_key = public_key,
    signed_values = signed_values.toPartial(),
    url = url
   )
}

public fun List<SignedData>.toPartial(): List<SignedDataPartial> {
   return mapNotNull { it.toPartial() }
}
