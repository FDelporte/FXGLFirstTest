package be.webtechie;

import static com.almasb.fxgl.dsl.FXGL.getAppHeight;
import static com.almasb.fxgl.dsl.FXGL.getAppWidth;
import static com.almasb.fxgl.dsl.FXGL.getGameController;
import static com.almasb.fxgl.dsl.FXGL.getGameWorld;
import static com.almasb.fxgl.dsl.FXGL.onBtnDown;
import static com.almasb.fxgl.dsl.FXGL.onCollisionBegin;
import static com.almasb.fxgl.dsl.FXGL.onKey;
import static com.almasb.fxgl.dsl.FXGL.run;
import static com.almasb.fxgl.dsl.FXGL.showMessage;
import static com.almasb.fxgl.dsl.FXGL.spawn;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.util.Duration;

/**
 * Main class of the application
 */
public class GeoWarsApp extends GameApplication {

    /**
     * Types of objects we are going to use in our game.
     */
    public enum EntityType {
        PLAYER, BULLET, ENEMY
    }

    /**
     * Reference to the factory which will defines how all the types must be created.
     */
    private final GeoWarsFactory geoWarsFactory = new GeoWarsFactory();

    /**
     * Player object we are going to use to provide to the factory so it can start a bullet from the player center.
     */
    private Entity player;

    /**
     * Main entry point where the application starts.
     *
     * @param args Start-up arguments
     */
    public static void main(String[] args) {
        // Launch the FXGL game application
        launch(args);
    }

    /**
     * General game settings. For now only the title is set, but a longer list of options is available.
     *
     * @param settings The settings of the game which can be further extended here.
     */
    @Override
    protected void initSettings(GameSettings settings) {
        settings.setTitle("Geometry Wars");
    }

    /**
     * Input configuration, here you configure all the input events like key presses, mouse clicks, etc.
     */
    @Override
    protected void initInput() {
        onKey(KeyCode.LEFT, () -> this.player.translateX(-5));
        onKey(KeyCode.RIGHT, () -> this.player.translateX(5));
        onKey(KeyCode.UP, () -> this.player.translateY(-5));
        onKey(KeyCode.DOWN, () -> this.player.translateY(5));
        onBtnDown(MouseButton.PRIMARY, () -> spawn("bullet", this.player.getCenter()));
    }

    /**
     * Initialization of the game by providing the {@link EntityFactory}.
     */
    @Override
    protected void initGame() {
        getGameWorld().addEntityFactory(this.geoWarsFactory);

        // Add the player
        this.player = spawn("player", getAppWidth() / 2 - 15, getAppHeight() / 2 - 15);
        this.geoWarsFactory.setPlayer(this.player);

        // Add a new enemy every second
        run(() -> spawn("enemy"), Duration.seconds(1.0));
    }

    /**
     * Initialization of the physics to detect e.g. collisions.
     */
    @Override
    protected void initPhysics() {
        onCollisionBegin(EntityType.BULLET, EntityType.ENEMY, (bullet, enemy) -> {
            bullet.removeFromWorld();
            enemy.removeFromWorld();
        });

        onCollisionBegin(EntityType.ENEMY, EntityType.PLAYER, (enemy, player) -> {
            showMessage("You Died!", () -> {
                getGameController().startNewGame();
            });
        });
    }
}