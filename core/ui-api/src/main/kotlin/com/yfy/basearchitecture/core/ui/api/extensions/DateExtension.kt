package com.yfy.basearchitecture.core.ui.api.extensions

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun Long.formatDate(): String {
    val sdf = SimpleDateFormat("dd MMM yyyy", Locale("tr"))
    return sdf.format(Date(this))
}