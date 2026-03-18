
package com.example.fruitcrate
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.View
import kotlin.math.abs
import kotlin.random.Random
class GameView(context: Context) : View(context) {

    private val paint = Paint()
    private val qLearning = QLearning()
    private var initialized = false

    private var birdX = 400f
    private var birdY = 1000f
    private val birdRadius = 60f
    private val normalStep = 30f
    private val slowStep = 10f

    private var fruitX = 0f
    private var fruitY = 0f
    private val fruitRadius = 40f
    private val fruitSpeed = 12f

    private var brickX = 0f
    private var brickY = 0f
    private val brickWidth = 100f
    private val brickHeight = 100f
    private val brickSpeed = 10f

    private var poisonX = 0f
    private var poisonY = 0f
    private val poisonRadius = 40f
    private val poisonSpeed = 14f

    private var slowTimer = 0
    private val slowDuration = 60
    private var totalReward = 0.0
    private var fruitsCollected = 0
    private var bricksHit = 0
    private var poisonsHit = 0


    // Durum belirleme
    private fun getFruitPos(): FruitPos =
        when {
            fruitX < birdX - 30f -> FruitPos.LEFT
            fruitX > birdX + 30f -> FruitPos.RIGHT
            else -> FruitPos.CENTER
        }

    private fun getBrickDist(): BrickDist =
        if (abs(brickY - birdY) < 600f && abs(brickX - birdX) < 250f) BrickDist.NEAR else BrickDist.FAR

    private fun getPoisonDist(): PoisonDist =
        if (abs(poisonY - birdY) < 350f && abs(poisonX - birdX) < 200f) PoisonDist.NEAR else PoisonDist.FAR

    private fun getSpeedState(): SpeedState =
        if (slowTimer > 0) SpeedState.SLOW else SpeedState.NORMAL

    private fun getEnvState(): EnvState =
        EnvState(getFruitPos(), getBrickDist(), getPoisonDist(), getSpeedState())

    // Resetler
    private fun resetFruit() {
        fruitX = Random.nextFloat() * (width - 100f) + 50f; fruitY = -50f
    }

    private fun resetBrick() {
        brickX = Random.nextFloat() * (width - brickWidth); brickY = -100f
    }

    private fun resetPoison() {
        poisonX = Random.nextFloat() * (width - 100f) + 50f; poisonY = -100f
    }

    // Çarpışma
    private fun checkCollision(): Boolean = abs(birdX - fruitX) < 80f && abs(birdY - fruitY) < 80f
    private fun checkPoisonCollision(): Boolean = abs(birdX - poisonX) < 80f && abs(birdY - poisonY) < 80f
    private fun checkBrickCollision(): Boolean {
        val closestX = birdX.coerceIn(brickX, brickX + brickWidth)
        val closestY = birdY.coerceIn(brickY, brickY + brickHeight)
        return (birdX - closestX) * (birdX - closestX) + (birdY - closestY) * (birdY - closestY) < birdRadius * birdRadius
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (!initialized && width > 0) {
            resetFruit(); resetBrick(); resetPoison()
            birdY = height * 0.8f
            initialized = true
        }

        canvas.drawColor(Color.WHITE)

        // State ve Aksiyon
        val prevState = getEnvState()
        val stateIndex = encode(prevState)
        val action = qLearning.chooseAction(stateIndex)

        // Aksiyon uygula
        val step = if (slowTimer > 0) slowStep else normalStep
        when (action) {
            Action.LEFT -> { birdX -= step; if (birdX < birdRadius) birdX = birdRadius }
            Action.RIGHT -> { birdX += step; if (birdX > width - birdRadius) birdX = width - birdRadius }
            Action.STAY -> {}
        }

        // Çevreyi ilerlet
        fruitY += fruitSpeed
        brickY += brickSpeed
        poisonY += poisonSpeed
        if (slowTimer > 0) slowTimer--


        var baseReward = 0.0
        var didEventHappen = false

        //  Çarpışmalar
        if (checkBrickCollision()) {
            baseReward = -35.0; bricksHit++; resetBrick(); didEventHappen = true
        }
        else if (checkCollision()) {
            baseReward = 15.0; fruitsCollected++; resetFruit(); didEventHappen = true
        }
        else if (checkPoisonCollision()) {
            baseReward = -10.0; poisonsHit++; slowTimer = slowDuration; resetPoison(); didEventHappen = true
        }

        else {
            if (fruitY > height) {
                baseReward = -5.0; resetFruit(); didEventHappen = true
            }

            // TUĞLA KONTROLÜ
            if (brickY > height) {
                if (Random.nextFloat() < 0.02f) {
                    resetBrick()
                } else {
                    brickY = height + 100f
                }
            }

            // ZEHİR KONTROLÜ
            if (poisonY > height) {

                if (Random.nextFloat() < 0.01f) {
                    resetPoison()
                } else {
                    poisonY = height + 100f
                }
            }
        }

        val currState = getEnvState()
        val shapingReward = computeShapingReward(prevState, currState, action)

        qLearning.update(stateIndex, action, baseReward + shapingReward, encode(currState))
        if (didEventHappen) {
            qLearning.decayEpsilon(0.998)
        } else {
            qLearning.decayEpsilon(0.99995)
        }

        totalReward += baseReward + shapingReward
        drawGameElements(canvas)
        invalidate()
    }

    private fun computeShapingReward(prev: EnvState, curr: EnvState, action: Action): Double {
        var reward = 0.0
        if (curr.brick == BrickDist.NEAR) {
            reward -= 0.15
        }
        if (curr.brick == BrickDist.FAR) {
            if (curr.fruit == FruitPos.LEFT && action == Action.LEFT) reward += 0.1
            if (curr.fruit == FruitPos.RIGHT && action == Action.RIGHT) reward += 0.1
            if (curr.fruit == FruitPos.CENTER && action == Action.STAY) reward += 0.1
        } else {
            val brickCenterX = brickX + brickWidth / 2f
            val horizontalDiff = birdX - brickCenterX

            if (action == Action.STAY) {
                reward -= 0.5
            }
            else if (brickCenterX > birdX && action == Action.LEFT) {
                if (abs(horizontalDiff) > 130f) reward += 0.4
                else reward += 0.25
            }
            else if (brickCenterX < birdX && action == Action.RIGHT) {
                if (abs(horizontalDiff) > 130f) reward += 0.4
                else reward += 0.25
            }
            else {
                reward -= 0.35
            }
        }

        if (curr.poison == PoisonDist.NEAR) {
            if (action != Action.STAY) reward += 0.02
            else reward -= 0.05
        }

        if (curr.speed == SpeedState.SLOW && action != Action.STAY) {
            reward -= 0.01
        }

        return reward
    }
    private fun drawGameElements(canvas: Canvas) {
        paint.color = Color.BLUE; canvas.drawCircle(birdX, birdY, birdRadius, paint)
        paint.color = Color.RED; canvas.drawCircle(fruitX, fruitY, fruitRadius, paint)
        paint.color = Color.DKGRAY; canvas.drawRect(brickX, brickY, brickX + brickWidth, brickY + brickHeight, paint)
        paint.color = Color.GREEN; canvas.drawCircle(poisonX, poisonY, poisonRadius, paint)

        paint.color = Color.BLACK; paint.textSize = 40f
        canvas.drawText("Reward: ${"%.1f".format(totalReward)}", 50f, 60f, paint)
        canvas.drawText("Fruits: $fruitsCollected | Bricks: $bricksHit | Poison: $poisonsHit", 50f, 120f, paint)
        canvas.drawText("Epsilon: ${"%.2f".format(qLearning.epsilon)}", 50f, 180f, paint)
    }
}