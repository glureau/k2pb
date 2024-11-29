package com.glureau.k2pb.compiler

import java.util.Locale

fun String.capitalizeUS() =
    replaceFirstChar { it.titlecase(Locale.US) }

fun String.decapitalizeUS() =
    replaceFirstChar { it.lowercase(Locale.US) }