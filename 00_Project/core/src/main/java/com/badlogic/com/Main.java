package com.badlogic.com;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class  Main implements ApplicationListener {
    // Textures
    Texture backgroundTexture;
    Texture playerTexture;
    Texture platformTexture;
    Texture blueLaserTexture;
    Texture laserDropTexture;

    // Movement
    float jumpEnd;
    float fallingSpeed;
    float jumpSpeed;
    boolean playerOnGround;
    boolean jumpActive;
    int additionalJumps;
    float currentGround;

    // Viewport
    SpriteBatch spriteBatch;
    FitViewport viewport;

    // Platforms
    float platformHeight;
    int platformLeftDirection;
    int platformRightDirection;
    int middlePlatformDirection;

    float random;

    // Sprite
    Sprite playerSprite;
    Sprite platformLeftSprite;
    Sprite platformRightSprite;
    Sprite middlePlatformSprite;
    Sprite bigLaserSprite;

    // Rectangles
    Rectangle playerRectangle;
    Rectangle middlePlatformRectangle;
    Rectangle bigLaserRectangle;
    Rectangle laserDropRectangle;

    /** Laser */
    // Big Laser
    int maxAmountOfAttacks;
    int currentAmountOfAttacks;
    int bigLaserDirection;
    boolean bigLaserActive;

    // Laser Drops
    Array<Sprite> laserDropSprite;
    boolean laserDropActive;
    float dropTimer;

    /** Declaring variables */
    @Override
    public void create() {
        backgroundTexture = new Texture("whiteBackground.jpg");
        playerTexture = new Texture("player1.png");
        platformTexture = new Texture("Platform.png");
        blueLaserTexture = new Texture("Laser4.png");
        laserDropTexture = new Texture("Laser_Drop2.png");

        // Viewport size
        spriteBatch = new SpriteBatch();
        viewport = new FitViewport(24, 14.11764705882353f);

        // Player
        playerSprite = new Sprite(playerTexture);
        playerSprite.setSize(1, 1);

        // Movement and Gravitation
        jumpSpeed = 0;
        fallingSpeed = 0;
        jumpActive = false;
        additionalJumps = 2;
        currentGround = 0;

        // Platforms
        platformHeight = 3;

        platformLeftSprite = new Sprite(platformTexture);
        platformRightSprite = new Sprite(platformTexture);
        middlePlatformSprite = new Sprite(platformTexture);
        platformRightSprite.setSize(3, 0.2f);
        platformLeftSprite.setSize(3, 0.2f);
        middlePlatformSprite.setSize(0.2f, 3);

        platformLeftDirection = 0;
        platformRightDirection = 0;
        middlePlatformDirection = 0;
        middlePlatformSprite.setX(viewport.getWorldWidth()/2);
        platformLeftSprite.setY(platformHeight);
        platformRightSprite.setY(platformHeight);

        random = (float) (Math.random() * 0) + (viewport.getWorldWidth()/2);
        platformLeftSprite.setX(random);
        random = (float) (Math.random() * (viewport.getWorldWidth()/2)) + viewport.getWorldWidth();
        platformRightSprite.setX(random);
        random = (float) (Math.random() * 0) + viewport.getWorldHeight() - playerSprite.getHeight() - middlePlatformSprite.getHeight();
        middlePlatformSprite.setY(random);

        /** Laser */
        currentAmountOfAttacks = 0;
        maxAmountOfAttacks = 1;

        // Big Laser
        bigLaserSprite = new Sprite(blueLaserTexture);
        bigLaserSprite.setSize(1.5f, viewport.getWorldHeight());
        bigLaserSprite.setY(0);
        bigLaserSprite.setX(0);
        bigLaserActive = false;

        // Laser Drops
        laserDropSprite = new Array<>();

        // Rectangle
        playerRectangle = new Rectangle();
        middlePlatformRectangle = new Rectangle();
        bigLaserRectangle = new Rectangle();
        laserDropRectangle = new Rectangle();
    }

    @Override
    // Scales the window
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void render() {
        input();
        logic();
        draw();
    }

    /** Processes the inputs */
    public void input() {
        float playerSpeed = 10;
        float playerJumpHeight = 2;
        float jumpSpeedReduction = 0.5f;
        float minJumpSpeed = 5f;
        float delta = Gdx.graphics.getDeltaTime();

        // Going Left and Right
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            playerSprite.translateX(playerSpeed * delta);
        } else if(Gdx.input.isKeyPressed(Input.Keys.A)) {
            playerSprite.translateX(-playerSpeed * delta);
        }

        // Checks if the player is on the ground
        if (playerSprite.getY() <= currentGround) {
            playerOnGround = true;
            fallingSpeed = 0;
            jumpSpeed = 15;
            additionalJumps = 1;
        } else {
            playerOnGround = false;
        }

        // Checks if he is allowed to jump
        if (playerOnGround == true) {
            if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
                jumpActive = true;
                jumpEnd = playerSprite.getY() + playerJumpHeight;
            }
        }

        if (jumpActive == true) {
            if (playerSprite.getY() < jumpEnd) {
                playerSprite.translateY(jumpSpeed * delta);

                if (jumpSpeed - jumpSpeedReduction < 0) {
                    jumpSpeed = minJumpSpeed;
                }

            } else {
                if (Gdx.input.isKeyPressed(Input.Keys.SPACE) && additionalJumps > 0) {
                    jumpActive = true;
                    jumpEnd = playerSprite.getY() + playerJumpHeight;
                    additionalJumps--;
                } else {
                    jumpActive = false;
                }
            }
        }

    }


    public void logic() {
        // 0 ist nach unten oder nach links

        // Window
        float delta = Gdx.graphics.getDeltaTime();
        float worldWidth = viewport.getWorldWidth();
        float worldHeight = viewport.getWorldHeight();

        // Player and Platforms
        float playerWidth = playerSprite.getWidth();
        float playerHeight = playerSprite.getHeight();
        float platformLeftWidth = platformLeftSprite.getWidth();
        float platformLeftSpeed = 0.1f;
        float platformRightWidth = platformRightSprite.getWidth();
        float platformRightSpeed = 0.11f;
        float middlePlatformSpeed = 0.05f;
        float middlePlatformMinHeight = 3;
        float middlePlatformMaxHeight = worldHeight - middlePlatformSprite.getHeight();

        float fallingSpeedAcceleration = 0.02f;

        // Laser variables
        float bigLaserSpeed = 5;
        int randomInt;
        float laserDropSpeed = 8;

        // Player can't go out of map
        playerSprite.setX(MathUtils.clamp(playerSprite.getX(), 0, worldWidth - playerWidth));

        if (playerSprite.getY() < 0) {
            playerSprite.setY(0);
        }

        // Checks if a player is jumping through the middle platform
        if (playerSprite.getY() + playerHeight >= middlePlatformSprite.getY() && playerSprite.getY() < middlePlatformSprite.getY() + (middlePlatformSprite.getHeight()/2) && playerSprite.getX() < middlePlatformSprite.getX() && playerSprite.getX() + playerWidth > middlePlatformSprite.getX() + middlePlatformSprite.getWidth()) {
            jumpActive = false;
        }

        // Checks if a player is jumping through the left platform
        if (jumpActive == true && playerSprite.getY() + playerHeight >= platformLeftSprite.getY() && currentGround == 0) {
            if (playerSprite.getX() + playerWidth >= platformLeftSprite.getX() && playerSprite.getX() <= platformLeftSprite.getX() + platformLeftWidth) {
                jumpActive = false;
                currentGround = platformLeftSprite.getY();
            }
        }

        // Checks if a player is jumping through the right platform
        if (jumpActive == true && playerSprite.getY() + playerHeight >= platformRightSprite.getY() && currentGround == 0) {
            if (playerSprite.getX() + playerWidth >= platformRightSprite.getX() && playerSprite.getX() <= platformRightSprite.getX() + platformRightWidth) {
                jumpActive = false;
                currentGround = platformRightSprite.getY();
            }
        }

        // Checks if the player is on the left or the right platform
        if (playerSprite.getY() >= platformLeftSprite.getY() && playerSprite.getX() + playerWidth >= platformLeftSprite.getX() && playerSprite.getX() <= platformLeftSprite.getX() + platformLeftWidth) {
            playerOnGround = true;
            currentGround = platformLeftSprite.getY() + platformRightSprite.getHeight();
        } else if (playerSprite.getY() >= platformRightSprite.getY() && playerSprite.getX() + playerWidth >= platformRightSprite.getX() && playerSprite.getX() <= platformRightSprite.getX() + platformRightWidth) {
            playerOnGround = true;
            currentGround = platformRightSprite.getY() + platformRightSprite.getHeight();
        } else {
            playerOnGround = false;
            currentGround = 0;
        }

        // Platform behavior
        // Platform left
        if (platformLeftSprite.getX() == (worldWidth/2) - platformLeftWidth) {
            platformLeftDirection = 1;
        } else if (platformLeftSprite.getX() == 0) {
            platformLeftDirection = 0;
        }

        // Platform left goes left
        if (platformLeftDirection == 0) {
            platformLeftSprite.setX((platformLeftSprite.getX() + platformLeftSpeed));
            if (currentGround != 0 && playerSprite.getX() + playerWidth >= platformLeftSprite.getX() && playerSprite.getX() <= platformLeftSprite.getX() + platformLeftWidth) {
                playerSprite.setX(playerSprite.getX() + platformLeftSpeed);
            }

        } else {
            // Platform left goes right
            platformLeftSprite.setX((platformLeftSprite.getX() - platformLeftSpeed));
            if (currentGround != 0 && playerSprite.getX() + playerWidth >= platformLeftSprite.getX() && playerSprite.getX() <= platformLeftSprite.getX() + platformLeftWidth) {
                playerSprite.setX(playerSprite.getX() - platformLeftSpeed);
            }
        }

        platformLeftSprite.setX(MathUtils.clamp(platformLeftSprite.getX(), 0, (worldWidth/2) - platformLeftWidth));

        // Platform Right
        if (platformRightSprite.getX() == worldWidth - platformRightWidth) {
            platformRightDirection = 1;
        } else if (platformRightSprite.getX() == (worldWidth/2)) {
            platformRightDirection = 0;
        }

        // Platform right goes left
        if (platformRightDirection == 0) {
            platformRightSprite.setX((platformRightSprite.getX() + platformRightSpeed));
            if (currentGround != 0 && playerSprite.getX() + playerWidth >= platformRightSprite.getX() && playerSprite.getX() <= platformRightSprite.getX() + platformRightWidth) {
                playerSprite.setX(playerSprite.getX() + platformRightSpeed);
            }
        } else {
            // Platform right goes right
            platformRightSprite.setX((platformRightSprite.getX() - platformRightSpeed));
            if (currentGround != 0 && playerSprite.getX() + playerWidth >= platformRightSprite.getX() && playerSprite.getX() <= platformRightSprite.getX() + platformRightWidth) {
                playerSprite.setX(playerSprite.getX() - platformRightSpeed);
            }
        }

        platformRightSprite.setX(MathUtils.clamp(platformRightSprite.getX(), (worldWidth/2), worldWidth - platformRightWidth));

        // Middle platform
        if (middlePlatformSprite.getY() == middlePlatformMinHeight) {
            middlePlatformDirection = 1;
        } else if (middlePlatformSprite.getY() == middlePlatformMaxHeight) {
            middlePlatformDirection = 0;
        }

        // Middle platform going down
        if (middlePlatformDirection == 0) {
            middlePlatformSprite.setY(middlePlatformSprite.getY() - middlePlatformSpeed);
        } else {
            // Middle platform going up
            middlePlatformSprite.setY(middlePlatformSprite.getY() + middlePlatformSpeed);
        }

        middlePlatformSprite.setY(MathUtils.clamp(middlePlatformSprite.getY(), middlePlatformMinHeight, middlePlatformMaxHeight));

        // Declaring rectangles
        playerRectangle.set(playerSprite.getX(), playerSprite.getY(), playerWidth, playerHeight);
        middlePlatformRectangle.set(middlePlatformSprite.getX(), middlePlatformSprite.getY(), middlePlatformSprite.getWidth(), middlePlatformSprite.getHeight());

        // Hit-box of the middle platform
        if (playerSprite.getY() >= middlePlatformSprite.getY() && playerSprite.getY() <= middlePlatformSprite.getY() + middlePlatformSprite.getHeight()) {
            if (playerRectangle.overlaps(middlePlatformRectangle)) {
                if (playerSprite.getX() + playerWidth > middlePlatformSprite.getX() && playerSprite.getX() < middlePlatformSprite.getX() && currentGround != platformLeftSprite.getY()) {
                    playerSprite.setX(middlePlatformSprite.getX() - playerWidth);
                } else {
                    playerSprite.setX(middlePlatformSprite.getX() + middlePlatformSprite.getWidth());
                }
            }
        }

        // Attacks
        // Initializing Attacks
        if (currentAmountOfAttacks < maxAmountOfAttacks) {
            randomInt = (int) (Math.random() * 2) + 1;


            switch (randomInt) {
                // Big Laser
                case 1:
                    if (!bigLaserActive) {
                        bigLaserActive = true;
                        currentAmountOfAttacks++;
                        randomInt = (int) (Math.random() * 2) + 1;
                        if (randomInt == 1) {
                            bigLaserSprite.setX(0 - bigLaserSprite.getWidth());
                        } else {
                            bigLaserSprite.setX(worldWidth);
                        }
                        bigLaserDirection = randomInt;
                    }
                    break;

                case 2:
                    if (!laserDropActive) {
                        laserDropSprite.clear();
                        laserDropActive = true;
                        currentAmountOfAttacks++;
                    }
                    break;
            }
        }

        // Execute Attacks
        // Execute big laser
        if (bigLaserActive) {
            // Bestimme die Bewegung basierend auf der Richtung
            if (bigLaserDirection == 1) {
                bigLaserSprite.setX(bigLaserSprite.getX() + (bigLaserSpeed * delta));
            } else {
                bigLaserSprite.setX(bigLaserSprite.getX() - (bigLaserSpeed * delta));
            }



            // Überprüfe, ob der Laser innerhalb der Weltbreite ist
            if ((bigLaserDirection == 1 && bigLaserSprite.getX() <= worldWidth) ||
                (bigLaserDirection == 2 && bigLaserSprite.getX() > 0)) {

                // Kollision mit der linken Plattform
                if (bigLaserSprite.getX() + bigLaserSprite.getWidth() > platformLeftSprite.getX() &&
                    bigLaserSprite.getX() < platformLeftSprite.getX() + platformLeftSprite.getWidth()) {
                    bigLaserSprite.setY(platformLeftSprite.getY() + platformLeftSprite.getHeight());
                }
                // Kollision mit der rechten Plattform
                else if (bigLaserSprite.getX() + bigLaserSprite.getWidth() > platformRightSprite.getX() &&
                    bigLaserSprite.getX() < platformRightSprite.getX() + platformRightSprite.getWidth()) {
                    bigLaserSprite.setY(platformRightSprite.getY() + platformRightSprite.getHeight());
                }
                // Keine Kollision mit Plattformen
                else {
                    bigLaserSprite.setY(0);
                }

                // Hit box
                bigLaserRectangle.set(bigLaserSprite.getX(), bigLaserSprite.getY(), bigLaserSprite.getWidth(), bigLaserSprite.getHeight());

                if (playerRectangle.overlaps(bigLaserRectangle)) {
                    reloadGame();
                }

            } else {
                // Wenn der Laser den Bildschirm verlässt
                bigLaserActive = false;
                currentAmountOfAttacks--;
            }
        }

        // Execute the laser Drops
        if (laserDropActive) {
            for (int i = laserDropSprite.size - 1; i >= 0; i--) {
                Sprite dropSprite = laserDropSprite.get(i);
                float dropWidth = dropSprite.getWidth();
                float dropHeight = dropSprite.getHeight();

                dropSprite.translateY(-laserDropSpeed * delta);
                // Apply the drop position and size to the dropRectangle
                laserDropRectangle.set(dropSprite.getX(), dropSprite.getY(), dropWidth, dropHeight);

                 if (playerRectangle.overlaps(laserDropRectangle)) {
                     reloadGame();
                }
            }

            dropTimer += delta;
            if (dropTimer > 0.1f) {
                dropTimer = 0;
                createDrop();
            }
        }

        // Gravitation
        if (playerSprite.getY() > currentGround && !jumpActive) {
            playerSprite.translateY(-fallingSpeed);
            fallingSpeed += fallingSpeedAcceleration;
            if (playerSprite.getY() <= currentGround) {
                playerSprite.setY(currentGround);
            }
        }
    }

    // Drawing the textures
    public void draw() {
        ScreenUtils.clear(Color.BLACK); // Bild auf Schwarz resetten
        viewport.apply();
        spriteBatch.setProjectionMatrix(viewport.getCamera().combined);

        // Nur hier reinzeichnen
        spriteBatch.begin();

        // Background
        float worldWidth = viewport.getWorldWidth();
        float worldHeight = viewport.getWorldHeight();
        spriteBatch.draw(backgroundTexture, 0, 0, worldWidth, worldHeight);

        // Platforms
        platformLeftSprite.draw(spriteBatch);
        platformRightSprite.draw(spriteBatch);
        middlePlatformSprite.draw(spriteBatch);

        /** Laser */
        // Big Laser
        if (bigLaserActive) {
            bigLaserSprite.draw(spriteBatch);
        }

        // Laser Drops
        if (laserDropActive) {
            for (Sprite dropSprite : laserDropSprite) {
                dropSprite.draw(spriteBatch);
            }
        }

        // Player
        playerSprite.draw(spriteBatch);

        spriteBatch.end();
    }

    // Create Laser Drop
    private void createDrop() {
        float dropWidth = 0.7f;
        float dropHeight = 1.5f;
        int maxDrops = 30;

        Sprite dropSprite = new Sprite(laserDropTexture);
        dropSprite.setSize(dropWidth, dropHeight);
        dropSprite.setX(MathUtils.random(0f, viewport.getWorldWidth() - dropWidth));
        dropSprite.setY(viewport.getWorldHeight());

        if (maxDrops > laserDropSprite.size) {
            laserDropSprite.add(dropSprite);
        } else {
            if (laserDropSprite.peek().getY() + dropHeight < 0) {
                laserDropActive = false;
                currentAmountOfAttacks--;
                laserDropSprite.clear();
            }
        }
    }

    // Reload the game
    public void reloadGame() {
        // Player cords
        playerSprite.setX(viewport.getWorldWidth() / 2);
        playerSprite.setY(0);

        // Platform cords
        platformLeftSprite.setX(0);
        platformRightSprite.setX(viewport.getWorldWidth() - platformRightSprite.getWidth());

        // Laser
        currentAmountOfAttacks = 0; 
        bigLaserActive = false;
        laserDropActive = false;
    }

    @Override
    public void pause() {
        // Invoked when your application is paused.
    }

    @Override
    public void resume() {
        // Invoked when your application is resumed after pause.
    }

    @Override
    public void dispose() {
        // Destroy application's resources here.
    }
}
