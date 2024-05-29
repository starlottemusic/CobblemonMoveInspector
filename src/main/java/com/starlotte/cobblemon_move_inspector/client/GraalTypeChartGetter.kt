package com.starlotte.cobblemon_move_inspector.client

import com.cobblemon.mod.common.battles.runner.ShowdownService
import com.cobblemon.mod.common.battles.runner.graal.GraalShowdownService
import java.io.File
import java.util.HashMap

class GraalTypeChartGetter {

    // Creates separate context, switch to this instead if reusing Cobblemon's context ends up causing issues
    /*
        @Transient
    lateinit var context: Context
    @Transient
    val unbundler = GraalShowdownUnbundler()

    fun openConnection() {
        unbundler.attemptUnbundle()
        createContext()
        boot()
    }

    private fun createContext() {
        val wd = Paths.get("./showdown")
        val access = HostAccess.newBuilder(HostAccess.EXPLICIT)
                .allowIterableAccess(true)
                .allowArrayAccess(true)
                .allowListAccess(true)
                .allowMapAccess(true)
                .build()
        context = Context.newBuilder("js")
                .allowIO(true)
                .fileSystem(FileSystem.newDefaultFileSystem())
                .allowExperimentalOptions(true)
                .allowPolyglotAccess(PolyglotAccess.ALL)
                .allowHostAccess(access)
                .allowCreateThread(true)
                .logHandler(GraalLogger)
                .option("engine.WarnInterpreterOnly", "false")
                .option("js.commonjs-require", "true")
                .option("js.commonjs-require-cwd", "showdown")
                .option(
                        "js.commonjs-core-modules-replacements",
                        "buffer:buffer/,crypto:crypto-browserify,path:path-browserify"
                )
                .allowHostClassLoading(true)
                .allowNativeAccess(true)
                .allowCreateProcess(true)
                .build()

        context.eval("js", """
            globalThis.process = {
                cwd: function() {
                    return '';
                }
            }
        """.trimIndent())
    }

    private fun boot() {
        context.eval("js", File("showdown/data/mods/cobblemon/typechart.js").readText())
    }*/

    fun getTypeChart(typeMap : HashMap<String, HashMap<String, Integer>>) {
        val service = ShowdownService.service as GraalShowdownService

        //Maybe figure out a way to just copy this context as I don't feel comfy changing values inside of it - cyvack
        val typeChart = service.context.eval("js", File("showdown/data/mods/cobblemon/typechart.js").readText())
        val getCobbledTypeChart = typeChart.getMember("TypeChart")

        val keys = getCobbledTypeChart.memberKeys
        for (elementalKey in keys) {
            val matchupMap = HashMap<String, Integer>()
            val elementalIntMatchup = getCobbledTypeChart.getMember(elementalKey).getMember("damageTaken")

            for (elementalMatchup in elementalIntMatchup.memberKeys) {
                val damage = elementalIntMatchup.getMember(elementalMatchup).asInt()
                matchupMap[elementalMatchup.lowercase()] = damage as Integer
            }

            typeMap[elementalKey.lowercase()] = matchupMap
//            println("Computing matchups for: $elementalKey : $matchupMap")
        }
    }
}
