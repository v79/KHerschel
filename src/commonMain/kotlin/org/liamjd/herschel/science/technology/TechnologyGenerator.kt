package org.liamjd.herschel.science.technology

import com.soywiz.korio.async.runBlockingNoSuspensions
import com.soywiz.korio.file.VfsFile
import com.soywiz.korio.file.std.resourcesVfs
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.jvm.JvmStatic

class TechnologyGenerator {



	companion object {
		@JvmStatic
		fun main(args:Array<String>) {

			val format = Json { prettyPrint = true; encodeDefaults = true }

			println("Generating technology tree")

			val techTree = setOf<Technology>(Techs.quantumComputer,Techs.roomTempSuperConductor, Techs.telomereRejuvenation, Techs.suspendedAnimation)
			val qc = format.encodeToString(techTree)
			println(qc)

			println("-------------------------------")
			runBlockingNoSuspensions {
				val listing = readResources()
				listing.collect {
					println(it.absolutePath)
				}
			}

			runBlockingNoSuspensions {
				resourcesVfs["technologies/techs.json"].writeString(qc)
			}


			val readTechs: String = runBlockingNoSuspensions {
				resourcesVfs["technologies/techs.json"].readString()
			}

			val readTechTree = Json.decodeFromString<List<Technology>>(readTechs)
			println("Read tech tree")
			for(t in readTechTree) {
				println(t)
			}

		}

		suspend fun readResources(): Flow<VfsFile> {
			return resourcesVfs[""].list()
		}

	}

	object Techs {

		val quantumComputer = Technology("1_quantumComputer").apply {
			name = "Quantum Computing"
			description = "Quantum computers won't replace your laptop or your phone, but for certain computational problems, they will turn the intractable into the trivial."

		}

		val roomTempSuperConductor = Technology("1_roomTempSuperConductor").apply {
			name = "Room Temperature Superconductors"
			description =
				"Superconductors have always required freezing temperatures - a few degrees above absolute zero. These new metamaterials will revolutionise power transmission, computing and who knows what else"
			parents.add(quantumComputer.key)
		}

		val telomereRejuvenation = Technology("2_telemereRejuv").apply {
			name = "Telemere Rejuvenation"
			description = "With every cell division, our telemeres get shorter. Eventually, they are too small to cut again, and we die. Can we trick a little extra length to the telemeres, and extend our lives a little?"
		}

		val suspendedAnimation = Technology("2_suspendedAnimation").apply {
			name = "Suspended Animation"
			description = "Some animals do it, or something similar. But can we put a person to sleep for 100 years?"
			parents.add(telomereRejuvenation.key)
		}

	}
}
