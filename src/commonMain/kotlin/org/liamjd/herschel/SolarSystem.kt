package org.liamjd.herschel

import com.soywiz.klock.seconds
import com.soywiz.korge.annotations.KorgeExperimental
import com.soywiz.korge.input.onClick
import com.soywiz.korge.input.onOut
import com.soywiz.korge.input.onOver
import com.soywiz.korge.scene.AlphaTransition
import com.soywiz.korge.scene.Scene
import com.soywiz.korge.view.*
import com.soywiz.korim.color.Colors
import org.liamjd.herschel.science.technology.Technology
import org.liamjd.herschel.state.GameModel
import org.liamjd.herschel.state.GameState
import org.liamjd.herschel.ui.*

class SolarSystem(private val gameState: GameState) : Scene() {

	private val topBarPadding = 5.0
	private var gm: GameModel = gameState.gm
	var availableTechs: List<Technology>? = null

	override suspend fun Container.sceneInit() {
		val tooltipContainer = Container().apply { name = "tooltips" }
		val centerY = views.virtualHeightDouble / 2
		views.gameWindow.title = "Herschel: Solar System"

		val menuButton = text("Main Menu") {
			onClick {
				sceneContainer.changeTo<MainMenu>(time = 1.seconds, transition = AlphaTransition)
			}
		}
		val year = text("Year: ${gm.year}") {
			position(views.virtualWidthDouble - topBarPadding - this.width, topBarPadding)

			addUpdater {
				text = "Year: ${gm.year}"
			}
		}

		val topBar = solidRect(width = views.virtualWidthDouble, height = 40.0, Colors.DARKBLUE) {
			addChild(menuButton)
			addChild(year)
		}

		val nextTurn = text("Next Turn") {
			position(views.virtualWidthDouble - 100,views.virtualHeightDouble - 32.0)
			onClick {
				gameState.nextTurn()
				availableTechs = gameState.getAvailableTechnologies(gm.era)
			}
		}

		val wrap = wrappableText("Fly me to the moon and let me sing among the starts,... jupiter and mars. SuperCALLIfragilisticexpaladocious.", wrapWidth = 180.0,textSize = 14.0, color = Colors.PINK, wrapAlignment = WrapAlignment.LEFT) {
			position(100,100)
			addTooltip("From Mary Poppins", backgroundColor = Colors.LIGHTGRAY, container = tooltipContainer)
		}


		val techContainer = container() {
			solidRect(200.0, 300.0, Colors.SLATEBLUE) {
				position(680, 180)
			}

				availableTechs = gameState.getAvailableTechnologies(gm.era)
				var techX = 700
				var techY = 200
				availableTechs?.apply {
					for (tech in this) {
						wrappableText(tech.name, wrapWidth = 180.0, wrapAlignment = WrapAlignment.LEFT) {
							position(techX, techY)
							addTooltip(tech.description, color = Colors.YELLOW, container = tooltipContainer)
						/*	val desc = Tooltip(text = tech.description, textSize = 14.0, wrapWidth = 160.0 , color = Colors.YELLOW, backgroundColor = Colors.DARKGRAY , wrapAlignment = WrapAlignment.LEFT).apply {
								visible = false
								position(this@wrappableText.x + 10.0, this@wrappableText.y + 10)
							}
							onOver {
								desc.visible = true
							}
							onOut {
								desc.visible = false
							}
							addChild(desc)*/
						techY = (techY + (this.lineCount * textSize)).toInt() + 20
						}
					}

			}
		}
		addChild(tooltipContainer)
	}
}

