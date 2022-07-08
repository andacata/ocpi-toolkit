package com.izivia.ocpi.toolkit.modules.cdr.domain

import com.izivia.ocpi.toolkit.annotations.Partial

/**
 * This class contains the signed and the plain/unsigned data. By decoding the data, the receiver can check if the content has not
been altered
 * @property nature Nature of the value, in other words, the event this value belongs to.
Possible values at moment of writing:
- Start (value at the start of the Session)
- End (signed value at the end of the Session)
- Intermediate (signed values take during the Session, after Start, before End)
Others might be added later.
 * @property plain_data The unencoded string of data. The format of the content depends on the
EncodingMethod field.
 * @property signed_data Blob of signed data, base64 encoded. The format of the content depends on the
EncodingMethod field.
 */
@Partial
data class SignedValue(
    val nature: String,
    val plain_data: String,
    val signed_data: String
)
