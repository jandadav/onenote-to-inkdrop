package com.jandadav.onenoteconverter

import java.io.File
import java.lang.IllegalArgumentException
import java.lang.IllegalStateException
import java.nio.file.Files
import java.nio.file.Paths
import java.text.DecimalFormat

val dec = DecimalFormat("00")
val headerPattern = """<p style='margin:0in;font-family:"Calibri Light";font-size:20\.0pt.*?>(.*?)<\/p>""".toRegex(
    setOf(RegexOption.DOT_MATCHES_ALL, RegexOption.MULTILINE)
)
val ghostUlPattern = """<ul style='direction:ltr;unicode-bidi:embed;margin-top:0in;margin-bottom:0in'>(.*)<\/ul>""".toRegex(
    setOf(RegexOption.DOT_MATCHES_ALL, RegexOption.MULTILINE)
)
val altPattern = """alt=".*?"""".toRegex(
    setOf(RegexOption.DOT_MATCHES_ALL, RegexOption.MULTILINE)
)

fun main(args: Array<String>) {
    if (args.size == 0 ) throw IllegalArgumentException("Provide path as first arg")
    if (args[0].isNullOrBlank()) throw IllegalArgumentException("Path is empty")
    val inputStream = File(args[0]).inputStream()
    var inputString = inputStream.bufferedReader().use { it.readText() }

    inputString = inputString.replace("&#10;", "")

    println("Cutouts:\n")

    val cutoutsExp = """<div style='direction:ltr;border-width:100%'>.*?<p style='margin:0in'>&nbsp;<\/p>""".toRegex(
        setOf(RegexOption.DOT_MATCHES_ALL, RegexOption.MULTILINE)
    )

    val cutouts = cutoutsExp.findAll(inputString)

    cutouts.forEach( action = {matchResult ->

        var cutout = matchResult.groups[0]?.value ?: "NAN"

        val sectionHeader = headerPattern.find(cutout)?.groups?.get(1)?.value?.replace("\r\n", " ")
            ?: throw IllegalStateException("Section header not found")

        cutout = cutout.replace(headerPattern) {
            val replace = it.groups[1]!!.value.replace("\r\n", " ")
            println("section name: $replace")
            "<h1>$replace</h1>"
        }

        cutout = cutout.replace(ghostUlPattern) {it.groups[1]!!.value}

        cutout = cutout.replace(altPattern, "")

        cutout = "<html><head></head><body>$cutout</body><html>"

        val path = Paths.get(args[0]).parent.resolve("${sanitizeFileName(sectionHeader)}.html")
        println(path)
        Files.write(path, cutout.toByteArray())
    })

}

fun sanitizeFileName(fileName: String): String {
    return fileName.replace(":", "-")
        //.replace(" ", "-")
        .replace("/", "-")
        .replace("\\", "-")
}



