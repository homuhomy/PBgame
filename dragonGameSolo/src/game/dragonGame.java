package game;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Random;
import java.util.Scanner;

public class dragonGame implements Serializable {
    private static final long serialVersionUID = -1289199047421811609L;

    // seasons
    private static final String[] SEASONS = { "Spring", "Summer", "Autumn", "Winter" };

    // different season events
    private static final String[] SPRING_EVENTS = { "Reinforcement! Tower's AttackPoint +1", "Visitors! Gold +100",
            " Festival! Berserk, Diligent and Fearless +50 " };
    private static final String[] SUMMER_EVENTS = { "Oh no it's the drought! Wall's HealthPoint -50 ",
            "Outing! Berserk, Diligent and Fearless +50", "Heatstroke! Emotional, Nervous, Lazy +50" };
    private static final String[] AUTUMN_EVENTS = { "Rainy! Tower Accuracy -20%", "Flood! Wall�s HealthPoint -50",
            "Harvest! +100 Gold" };
    private static final String[] WINTER_EVENTS = { "Oh no it's blizzard! Wall's HealthPoint -50",
            "Avalanche! Emotional, Nervous, Lazy +50", "Hunger! Tower Accuracy-20%", "Tour group! +100 Gold" };

    // taxation amounts
    //private static final int[] randomgold = { 200, 250, 300, 350, 400 };

    private Dragon dragon;
    private Tower tower;
    private Wall wall;
    private Citizens citizens;

    private int year = 1;
    private int currentSeason = 0;
    private int tax = 200;
    private int gold = 200;
    public int RandomGold = 0;
    private String event;

    private transient Scanner scan = new Scanner(System.in);
    private transient Random random = new Random();

    /**
     * Constructor
     */
    public dragonGame() {
        dragon = new Dragon();
        tower = new Tower();
        wall = new Wall();
        citizens = new Citizens();

        scan = new Scanner(System.in);
        random = new Random();
    }

    /**
     * Runs the game
     *
     * @param isSavedGame is this game saved or a new game
     */
    public void run(boolean isSavedGame) {
        if (!isSavedGame)
            dragonAttack();
        else {
            System.out.println("Game successfully resumed from last saved state.");
            scan = new Scanner(System.in);
            random = new Random();
        }

        // game loop
        while (true) {
            if (!isSavedGame) {
                event = executeRandomEvent();
                ExecuteTax();
                tax = RandomGold;
                gold += tax;
                isSavedGame = false;
            }

            System.out.println("Event: " + event);
            System.out.println("Tax received from citizens this season: " + tax);
            System.out.println("Year: " + year);
            System.out.println("Season: " + SEASONS[currentSeason]);
            System.out.println("Gold: " + gold);

            // command panel
            int option = 0;
            do {
                System.out.println("1. Tower");
                System.out.println("2. Wall");
                System.out.println("3. Citizens");
                System.out.println("4. I am all ready!");
                System.out.println("5. Save Game");
                System.out.println("6. Load Game");
                System.out.println("7. Exit Game");
                System.out.print("Please enter your command: ");
                option = scan.nextInt();

                switch (option) {
                    case 1:
                        towerMenu();
                        break;
                    case 2:
                        wallMenu();
                        break;
                    case 3:
                        citizensMenu();
                        break;
                    // Move to dragon attack
                    case 4:
                        break;

                    case 5:
                        try {
                            FileOutputStream os = new FileOutputStream(new File("game.sav"));
                            ObjectOutputStream oos = new ObjectOutputStream(os);
                            oos.writeObject(this);
                            oos.close();

                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        System.out.println("Game successfully saved.");
                        break;
                    case 6:
                        try {
                            ObjectInputStream is = new ObjectInputStream(new FileInputStream(new File("game.sav")));
                            dragonGame game = (dragonGame) is.readObject();
                            is.close();

                            game.run(true);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                        System.out.println(" ");
                        System.out.println("\nGame successfully loaded from saved file");
                        break;
                    case 7:
                        System.out.println("Thank you for playing the game!");
                        System.exit(0);
                        break;
                    // Invalid option
                    default:
                        break;
                }

            } while (option != 4);

            // apply triggered emotions if any
            if (citizens.getEmotional() >= 100) {
                tower.decreaseAtkPoint();
                citizens.increaseEmotional(-100);
            }
            if (citizens.getNervous() >= 100) {
                tower.decreaseAccuracy(0.05f);
                citizens.increaseNervous(-100);
            }
            if (citizens.getLazy() >= 100) {
                wall.decreaseHp(100);
                citizens.increaseLazy(-100);
            }
            if (citizens.getBerserk() >= 100) {
                tower.upAtkPoint();
                citizens.increaseBerserk(-100);
            }
            if (citizens.getDiligent() >= 100) {
                wall.upHp();
                citizens.increaseDiligent(-100);
            }
            if (citizens.getFearless() >= 100) {
                tower.upCritChance();
                citizens.increaseFearless(-100);
            }

            // dragon attack
            dragonAttack();
            dragon.levelUp();
            dragon.recover();

            // reset event's temporary effects
            if ((SEASONS[currentSeason].equals("Autumn") && event.contains("Rainy"))
                    || (SEASONS[currentSeason].equals("Winter") && event.contains("Hunger")))
                tower.decreaseAccuracy(-0.2f);

            // next season
            currentSeason++;
            if (currentSeason >= SEASONS.length) {
                currentSeason = 0;
                year++;
            }

        }
    }

    /**
     * Shows the tower menu
     */
    private void towerMenu() {
        int option = 0;
        do {
            tower.displayStats();
            System.out.println("\n");
            System.out.println("1. Upgrade Attack(100 Gold -> 1 Attack Point)");
            System.out.println("2. Upgrade Critical Chance (100 Gold -> 5 Critical Chane %)");
            System.out.println("3. Upgrade Accuracy (100 Gold -> 4% Accuracy)");
            System.out.println("4. Back to Menu");
            System.out.print("Please Enter your command: ");
            option = scan.nextInt();

            if (option != 4 && gold < 100)
                System.out.println("You do not have enough gold to upgrade.");
            else
                switch (option) {
                    case 1:
                        gold -= 100;
                        tower.upAtkPoint();
                        System.out.println("Tower Attack Upgraded +1");
                        break;
                    case 2:
                        gold -= 100;
                        tower.upCritChance();
                        System.out.println("Tower Critical Chance Upgraded +5%");
                        break;
                    case 3:
                        gold -= 100;
                        tower.upAccuracy();
                        System.out.println("Tower Accuracy Upgraded +4%");
                        break;
                    case 4:
                        break;
                    default:
                        System.out.println("Invalid Option Selected.");
                        break;
                }

        } while (option != 4);
    }

    /**
     * Shows the wall menu
     */
    private void wallMenu() {
        int option = 0;
        do {
            wall.displayStats();
            System.out.println("\n");
            System.out.println("1. Upgrade Health(100 Gold -> 75 Health Point)");
            System.out.println("2. Upgrade Block Chance (100 Gold -> 5 Critical Chance %)");
            System.out.println("3. Back to Menu");
            System.out.print("Please Enter your command: ");
            option = scan.nextInt();

            if (option != 3 && gold < 100)
                System.out.println("You do not have enough gold to upgrade.");
            else {
                switch (option) {
                    case 1:
                        gold -= 100;
                        wall.upHp();
                        System.out.println("Wall Hp Upgraded +1");
                        break;
                    case 2:
                        gold -= 100;
                        wall.upBlockPercent();
                        System.out.println("Wall Block Percent Upgraded +5%");
                        break;
                    case 3:
                        break;
                    default:
                        System.out.println("Invalid Option Selected.");
                        break;
                }
            }

        } while (option != 3);
    }

    /**
     * Shows the citizens menu
     */
    private void citizensMenu() {
        int option = 0;
        do {
            citizens.displayStats();
            System.out.println("\n");
            System.out.println("1. Decrease Emotional (50 Gold -> 50 Emotional Point)");
            System.out.println("2. Decrease Nervous (50 Gold -> 50 Nervous Point)");
            System.out.println("3. Decrease Lazy (50 Gold -> 50 Lazy Point)");
            System.out.println("4. Increase Berserk (50 Gold -> 50 Berserk Point)");
            System.out.println("5. Increase Diligent (50 Gold -> 50 Diligent Point)");
            System.out.println("6. Increase Fearless (50 Gold -> 50 Fearless Point)");
            System.out.println("7. Back to Menu");
            System.out.print("Please Enter your command: ");
            option = scan.nextInt();

            if (option != 7 && gold < 50)
                System.out.println("You do not have enough gold to upgrade.");
            else {
                switch (option) {
                    case 1:
                        gold -= 50;
                        citizens.decreaseEmotional();
                        System.out.println("Citizens Emotional decreased -50");
                        break;
                    case 2:
                        gold -= 50;
                        citizens.decreaseNervous();
                        System.out.println("Citizens Nervous decreased -50");
                        break;
                    case 3:
                        gold -= 50;
                        citizens.decreaseLazy();
                        System.out.println("Citizens Lazy decreased -50");
                        break;
                    case 4:
                        gold -= 50;
                        citizens.increaseBerserk();
                        System.out.println("Citizens Berserk Increased +50");
                        break;
                    case 5:
                        gold -= 50;
                        citizens.increaseDiligent();
                        System.out.println("Citizens Diligent Increased +50");
                        break;
                    case 6:
                        gold -= 50;
                        citizens.increaseFearless();
                        System.out.println("Citizens Fearless Increased +50");
                        break;
                    case 7:
                        break;

                    default:
                        System.out.println("Invalid Option Selected.");
                        break;
                }
            }

        } while (option != 7);
    }

    /**
     * Executes a random event based on the current season
     *
     * @return the executed event
     */
    private String executeRandomEvent() {

        // set random event
        String event = "";
        int eventIndex = random.nextInt(3);
        switch (SEASONS[currentSeason]) {
            case "Spring":
                event = SPRING_EVENTS[eventIndex];
                break;
            case "Summer":
                event = SUMMER_EVENTS[eventIndex];
                break;
            case "Autumn":
                event = AUTUMN_EVENTS[eventIndex];
                break;
            case "Winter":
                event = WINTER_EVENTS[eventIndex];
                break;
        }

        // apply event
        switch (SEASONS[currentSeason]) {
            case "Spring":
                switch (eventIndex) {
                    case 0:
                        tower.upAtkPoint();
                        break;
                    case 1:
                        gold += 100;
                        break;
                    case 2:
                        citizens.increaseBerserk();
                        citizens.increaseDiligent();
                        citizens.increaseFearless();
                        break;
                }
                break;
            case "Summer":
                switch (eventIndex) {
                    case 0:
                        wall.decreaseHp(50);
                        break;
                    case 1:
                        citizens.increaseBerserk();
                        citizens.increaseDiligent();
                        citizens.increaseFearless();
                        break;
                    case 2:
                        citizens.increaseEmotional(50);
                        citizens.increaseNervous(50);
                        citizens.increaseLazy(50);
                        break;
                }
                break;
            case "Autumn":
                switch (eventIndex) {
                    case 0:
                        tower.decreaseAccuracy(0.2f);// temporary
                        break;
                    case 1:
                        wall.decreaseHp(50);
                        break;
                    case 2:
                        gold += 100;
                        break;
                }
                break;
            case "Winter":
                switch (eventIndex) {
                    case 0:
                        wall.decreaseHp(50);
                        break;
                    case 1:
                        citizens.increaseEmotional(50);
                        citizens.increaseNervous(50);
                        citizens.increaseLazy(50);
                        break;
                    case 2:
                        tower.decreaseAccuracy(0.2f);// temporary
                        break;
                    case 3:
                        gold += 100;
                        break;
                }
                break;
        }

        return event;
    }

    /**
     * Performs dragon attack. The dragon attacks 10 times
     */
    private void dragonAttack() {
        System.out.println("A dragon performs a sudden attack to your city!");
        dragon.displayStats();

        for (int i = 0; i < 10; i++) {

            // 1. Dragon attack on wall
            int atkPoint = dragon.getAtkPoint();
            boolean isCriticalAtk = Math.random() <= dragon.getCritChance();
            if (isCriticalAtk)
                atkPoint += (int) (atkPoint * 0.5f);

            boolean hasWallBlockedAtk = Math.random() <= wall.getBlockPercent();
            if (hasWallBlockedAtk) {
                System.out.println("Wall successfully blocked dragon's attack!");
                System.out.println("Current Wall's HealthPoint: " + wall.getHp());
            } else {
                wall.decreaseHp(atkPoint);
                System.out.println("Dragon attacked our wall!");
                System.out.println("Wall's healthpoint minus " + atkPoint);
                System.out.println("Current Wall's HealthPoint: " + wall.getHp());
            }

            System.out.println("\n\n");

            // 2. Tower attack on Dragon
            atkPoint = tower.getAtkPoint();
            isCriticalAtk = Math.random() <= tower.getCritChance();
            if (isCriticalAtk)
                atkPoint += (int) (atkPoint * 0.5f);

            dragon.decreaseHp(atkPoint);
            if (isCriticalAtk)
                System.out.println("Tower attacked dragon with critical attack!");
            else
                System.out.println("Tower attacked dragon!");
            System.out.println("Dragon's healthpoint minus " + atkPoint);
            System.out.println("Current Dragon's HealthPoint: " + dragon.getHp());

            // Check if won/lose
            if (dragon.getHp() <= 0) {
                System.out.println("You killed the dragon! You protected the city!");
                System.exit(0);
            } else if (wall.getHp() <= 0) {
                System.out.println("You failed to protect your city!");
                System.exit(0);
            }

            System.out.println("\n");

            // sleep for 1 second
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Perform random tax from citizens
     */
    public int ExecuteTax(){

        int [] randomgold = {200, 250, 300, 350, 400};
        Random random = new Random();
        int randomgoldIndex = random.nextInt(5);
        //System.out.println(randomgoldIndex);
        switch (randomgoldIndex){
            case 0:RandomGold = randomgold[0];
                break;
            case 1:RandomGold = randomgold[1];
                break;
            case 2:RandomGold = randomgold[2];
                break;
            case 3:RandomGold = randomgold[3];
                break;
            case 4:RandomGold = randomgold[4];
                break;
        }
        System.out.println(RandomGold);
        return RandomGold;
    }

    /**
     * MAIN METHOD
     */
    public static void main(String args[]) {

        Scanner scan = new Scanner(System.in);
        System.out.println("Welcome to Till The End - A Tower Defense Game!");
        System.out.println("Would you like to play: ");
        System.out.println("1. Last Saved Game");
        System.out.println("2. New Game");
        int option = scan.nextInt();

        switch (option) {
            case 1:
                // loaded game
                try {
                    ObjectInputStream is = new ObjectInputStream(new FileInputStream(new File("game.sav")));
                    dragonGame game = (dragonGame) is.readObject();
                    is.close();
                    game.run(true);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                break;
            case 2:
                // new game
                new dragonGame().run(false);
                break;
        }
        scan.close();
    }
}
