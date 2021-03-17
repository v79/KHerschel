package org.liamjd.herschel

import com.soywiz.klock.seconds
import com.soywiz.korge.input.onClick
import com.soywiz.korge.scene.AlphaTransition
import com.soywiz.korge.scene.Scene
import com.soywiz.korge.ui.uiTextButton
import com.soywiz.korge.view.Container
import com.soywiz.korge.view.alignTopToBottomOf
import com.soywiz.korge.view.centerXOn
import com.soywiz.korge.view.text
import com.soywiz.korim.color.Colors

class MainMenu() : Scene() {
	override suspend fun Container.sceneInit() {

		// set background color
		views.clearColor = Colors.BLACK

		val title = text("Herschel", textSize = 48.0) {
			centerXOn(views.root)
			y = 100.0
		}

		val startButton = uiTextButton(text = "New game") {
			centerXOn(views.root)
			y = 200.0
			onClick {

				sceneContainer.changeTo<SolarSystem>(time = 1.seconds, transition = AlphaTransition)
			}
		}

		val quitButton = uiTextButton(text = "Quit") {
			centerXOn(views.root)
			alignTopToBottomOf(startButton,20.0)
			onClick { views.gameWindow.close() }
		}
	}
}
