package org.liamjd.herschel

import com.soywiz.klock.milliseconds
import com.soywiz.klock.seconds
import com.soywiz.korge.animate.animate
import com.soywiz.korge.input.mouse
import com.soywiz.korge.input.onClick
import com.soywiz.korge.scene.AlphaTransition
import com.soywiz.korge.scene.Scene
import com.soywiz.korge.tween.duration
import com.soywiz.korge.tween.get
import com.soywiz.korge.tween.tween
import com.soywiz.korge.ui.UIView
import com.soywiz.korge.view.*
import com.soywiz.korge.view.filter.PageFilter
import com.soywiz.korge.view.tween.moveBy
import com.soywiz.korim.color.Colors
import com.soywiz.korim.color.RGBA
import org.liamjd.herschel.state.GameState

class SolarSystem(private val gameState: GameState) : Scene() {
	private val topBarPadding = 5.0

	var zoomFocus = 1

	override suspend fun Container.sceneInit() {
		val centerY = views.virtualHeightDouble / 2
		views.gameWindow.title = "Herschel: Solar System"

		val menuButton = text("Main Menu") {
			onClick {
				sceneContainer.changeTo<MainMenu>(time = 1.seconds, transition = AlphaTransition)
			}
		}
		val year = text("Year: ${gameState.year}") {
			position(views.virtualWidthDouble - topBarPadding - this.width, topBarPadding)
		}

		val zoomText = text(zoomFocus.toString()) {
			position(50.0,50.0)
		}

		val zoomOut = text("ZOOM OUT") {
			onClick { zoomOut(); zoomText.setText(zoomFocus.toString()) }
			position((views.virtualHeightDouble / 2) - topBarPadding - (this.width / 2),topBarPadding)
		}

		val topBar = solidRect(width = views.virtualWidthDouble, height = 40.0, Colors.DARKBLUE) {
			addChild(menuButton)
			addChild(year)
			addChild(zoomOut)
		}



		val earth = circle(100.0) {
			xy(200.0,centerY)
			color = Colors.GREEN
		}


		camera {

		}


	}

	private fun zoomOut() {
		if(zoomFocus <= 5) {
			println("Zooming out")
			zoomFocus++


		}
	}
}

typealias ZoomFocus = Int
