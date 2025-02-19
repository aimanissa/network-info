package com.aimanissa.base.extensions

private const val DEFAULT_LIMIT_FOR_DIVIDE = 3
private const val OFFSET_FOR_PENNY = 3

private const val DEFAULT_PHONE_ACTION = "tel:%s"

private const val HYPHEN = "-"
private const val DOT = "."
private const val COMMA = ","

fun String.toActionCallFormat() = String.format(DEFAULT_PHONE_ACTION, this)

fun Char.isHyphen() = this.toString() == HYPHEN

fun Char.isDot() = this.toString() == DOT

fun Char.isComma() = this.toString() == COMMA
