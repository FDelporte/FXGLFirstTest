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
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.util.Duration;

public class GeoWarsApp extends GameApplication {

    public enum EntityType {
        PLAYER, BULLET, ENEMY
    }

    private final GeoWarsFactory geoWarsFactory = new GeoWarsFactory();
    private Entity player;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setTitle("Geometry Wars");
    }

    @Override
    protected void initInput() {
        onKey(KeyCode.LEFT, () -> player.translateX(-5));
        onKey(KeyCode.RIGHT, () -> player.translateX(5));
        onKey(KeyCode.UP, () -> player.translateY(-5));
        onKey(KeyCode.DOWN, () -> player.translateY(5));
        onBtnDown(MouseButton.PRIMARY, () -> spawn("bullet", player.getCenter()));
    }

    @Override
    protected void initGame() {
        getGameWorld().addEntityFactory(this.geoWarsFactory);

        this.player = spawn("player", getAppWidth() / 2 - 15, getAppHeight() / 2 - 15);
        this.geoWarsFactory.setPlayer(this.player);

        run(() -> spawn("enemy"), Duration.seconds(1.0));
    }

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