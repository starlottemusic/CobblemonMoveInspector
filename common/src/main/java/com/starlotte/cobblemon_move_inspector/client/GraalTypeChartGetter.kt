package com.starlotte.cobblemon_move_inspector.client

import com.cobblemon.mod.common.Cobblemon
import com.cobblemon.mod.common.battles.runner.graal.GraalLogger
import com.cobblemon.mod.common.battles.runner.graal.GraalShowdownUnbundler
import com.cobblemon.mod.relocations.graalvm.polyglot.Context
import com.cobblemon.mod.relocations.graalvm.polyglot.HostAccess
import com.cobblemon.mod.relocations.graalvm.polyglot.PolyglotAccess
import com.cobblemon.mod.relocations.graalvm.polyglot.io.FileSystem
import java.io.File
import java.nio.file.Paths
import kotlin.collections.HashMap

class GraalTypeChartGetter {

    // Creates separate context, switch to this instead if reusing Cobblemon's context ends up causing issues

    @kotlin.jvm.Transient
    lateinit var context: Context
    @kotlin.jvm.Transient
    val unbundler = GraalShowdownUnbundler()

    fun openConnection() {
        unbundler.attemptUnbundle()
        createContext()
        if (File("showdown/data/typechart.js").isFile) context.eval("js", File("showdown/data/typechart.js").readText())
        else if (File("showdown/data/mods/cobblemon/typechart.js").isFile) context.eval("js", File("showdown/data/mods/cobblemon/typechart.js").readText())
        else Cobblemon.LOGGER.error("Hacked JS files in datapacks or some weird file system setup that Star failed to anticipate.")
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

    fun getTypeChart(typeMap : HashMap<String, HashMap<String, Float>>) {
        /*
        //Maybe figure out a way to just copy this context as I don't feel comfy changing values inside of it - cyvack
        val service = ShowdownService.service as GraalShowdownService
        val typeChart = service.context.eval("js", File("showdown/data/mods/cobblemon/typechart.js").readText())
        val getCobbledTypeChart = typeChart.getMember("TypeChart")
        */

        val getCobbledTypeChart = context.getBindings("js").getMember("TypeChart")

        val keys = getCobbledTypeChart.memberKeys
        for (elementalKey in keys) {
            val matchupMap = HashMap<String, Float>()
            val elementalIntMatchup = getCobbledTypeChart.getMember(elementalKey).getMember("damageTaken")

            for (elementalMatchup in elementalIntMatchup.memberKeys) {
                val damageID = elementalIntMatchup.getMember(elementalMatchup).asInt()
                var damageMult = 1f;
                when (damageID) {
                    1 -> damageMult = 2f
                    2 -> damageMult = 0.5f;
                    3 -> damageMult = 0f;
                }
                matchupMap[elementalMatchup.lowercase()] = damageMult;
            }

            typeMap[elementalKey.lowercase()] = matchupMap
            println("Computing matchups for: $elementalKey : $matchupMap")
        }
    }
}
