package com.xinzy.microapp.lib.util

import java.security.MessageDigest


fun String.sha1() = hash("SHA-1")
fun String.sha256() = hash("SHA-256")
fun String.sha512() = hash("SHA-512")
fun String.md5() = hash("MD5")

internal fun String.hash(type: String) = try {
    MessageDigest.getInstance(type).digest(toByteArray()).joinToString(separator = "") { String.format("%02x", it) }
} catch (e: Exception) {
    ""
}