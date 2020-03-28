import org.rspeer.runetek.adapter.scene.Npc;
import org.rspeer.runetek.adapter.scene.SceneObject;
import org.rspeer.runetek.api.commons.StopWatch;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.commons.math.Random;
import org.rspeer.runetek.api.component.Bank;
import org.rspeer.runetek.api.component.Dialog;
import org.rspeer.runetek.api.component.tab.Equipment;
import org.rspeer.runetek.api.component.tab.EquipmentSlot;
import org.rspeer.runetek.api.component.tab.Inventory;
import org.rspeer.runetek.api.movement.Movement;
import org.rspeer.runetek.api.movement.position.Area;
import org.rspeer.runetek.api.scene.Npcs;
import org.rspeer.runetek.api.scene.Players;
import org.rspeer.runetek.api.scene.SceneObjects;
import org.rspeer.runetek.event.listeners.ItemTableListener;
import org.rspeer.runetek.event.listeners.RenderListener;
import org.rspeer.runetek.event.types.ItemTableEvent;
import org.rspeer.runetek.event.types.RenderEvent;
import org.rspeer.script.Script;
import org.rspeer.script.ScriptMeta;
import org.rspeer.script.ScriptCategory;
import org.rspeer.ui.Log;

import java.awt.*;
import java.util.Timer;

import static org.rspeer.runetek.api.component.tab.Equipment.getSlot;

@ScriptMeta(name = "Climbers",  desc = "Script description", developer = "Developer's Name", category = ScriptCategory.MONEY_MAKING)
public class climbers extends Script implements ItemTableListener, RenderListener {
    private Area clanWars = Area.rectangular(3391,3158,3350,3200,0);
    private Area insideHouse = Area.rectangular(2819,3554,2822, 3556,0);
    private Area outsideHouse = Area.rectangular(2825, 3553, 2830, 3558,0);
    private Area outsidePortal = Area.rectangular(3351, 3161,3355, 3166,0);
    private Area insidePortal = Area.rectangular(3327,4751, 3324, 4754,0);
    private static final String BASE_URL = "http://services.runescape.com/m=itemdb_oldschool/api/catalogue/detail.json?item=";
    private static final int MILLION = 1000000;
    private static final int THOUSAND = 1000;
    private int bootsBought = 0;
    private String RING_OF_DUELING = "Ring of dueling(8)";
    private String GAMES_NECK = "Games necklace(8)";
    private StopWatch timer;

    @Override
    public void onStart()
    {
        bootsBought = 0;
        timer = StopWatch.start();
    }

    @Override
    public int loop() {
        if(Inventory.isFull())
        {
            if(clanWars.contains(Players.getLocal()))
            {
                if(!Bank.isOpen())
                {
                    SceneObjects.getNearest("Bank chest").interact("Use");
                    Time.sleepUntil(Bank::isOpen, Random.low(1111,5555));
                }
                else
                {
                   Bank.depositAllExcept("Coins");
                   Time.sleepUntil(()-> Inventory.getCount("Climbing boots") == 0, Random.low(1111,5555));
                }

            }
            else
            {
                getSlot(a-> a != null && a.getName().contains("Ring")).interact("Clan Wars");
                Time.sleepUntil(()-> clanWars.contains(Players.getLocal()), Random.low(2222,6666));
            }
        }
        else if(Dialog.isOpen())
        {
            Dialog.processContinue();
            Dialog.process(0);
        }
        else {
            if (clanWars.contains(Players.getLocal())) {
                if ((!Equipment.isOccupied(EquipmentSlot.NECK) && Inventory.getCount(GAMES_NECK) == 0) || (!Equipment.isOccupied(EquipmentSlot.RING) && Inventory.getCount(RING_OF_DUELING) == 0)) {
                    if (!Bank.isOpen()) {
                        SceneObjects.getNearest("Bank chest").interact("Use");
                        Time.sleepUntil(()-> Bank.isOpen() || !Players.getLocal().isMoving(), Random.low(1111,5555));
                    }
                    else if (Bank.isOpen())
                    {
                        if (!Equipment.isOccupied(EquipmentSlot.NECK) && Inventory.getCount(GAMES_NECK) == 0 && Bank.getCount(GAMES_NECK) > 0) {
                            Bank.withdraw(GAMES_NECK,1);
                            Time.sleepUntil(() -> Inventory.getCount(GAMES_NECK) > 0, Random.low(888, 1222));
                        }
                        else if(!Equipment.isOccupied(EquipmentSlot.RING) && Inventory.getCount(RING_OF_DUELING) == 0 && Bank.getCount(RING_OF_DUELING) > 0)
                        {
                            Bank.withdraw(RING_OF_DUELING,1);
                            Time.sleepUntil(() -> Inventory.getCount(RING_OF_DUELING) > 0, Random.low(1555, 2222));
                        }
                        else
                            return -1;
                    }

                }
                else
                {

                    if(Inventory.getCount(RING_OF_DUELING)  > 0)
                    {
                        Inventory.getFirst(RING_OF_DUELING).interact("Wear");
                        Time.sleepUntil(() -> Inventory.getCount(RING_OF_DUELING) == 0, Random.low(888, 1222));
                    }
                    else if(Inventory.getCount(GAMES_NECK)  > 0)
                    {
                        Inventory.getFirst(GAMES_NECK).interact("Wear");
                        Time.sleepUntil(() -> Inventory.getCount(GAMES_NECK) == 0, Random.low(888, 1222));
                    }
                    else
                    {
                        if(Movement.getRunEnergy() > 40)
                        {
                            EquipmentSlot amuletSlot = getSlot(a-> a.getName().contains("Games"));
                            if(amuletSlot != null)
                            {
                                amuletSlot.interact("Burthorpe");
                            }
                            else
                                Log.info("Cannot find games neck");

                            Time.sleepUntil(()-> !clanWars.contains(Players.getLocal()), Random.high(2222,3333));
                        }
                        else
                        {
                            SceneObject ffaPortal = SceneObjects.getNearest("Free-for-all portal");
                            if (ffaPortal != null) {
                                ffaPortal.interact("Enter");
                                //Time.sleepUntil(() -> insidePortal.contains(Players.getLocal()), 8888);
                            } else
                                Movement.walkToRandomized(outsidePortal.getCenter());
                                Time.sleepUntil(() -> SceneObjects.getNearest("Free-for-all portal") != null, Random.mid(4444, 6666));
                        }

                    }

                }

            }
            else if(insidePortal.contains(Players.getLocal()))
            {
                EquipmentSlot amuletSlot = getSlot(a-> a.getName().contains("Games"));
                if(amuletSlot != null)
                {
                    amuletSlot.interact("Burthorpe");
                    Time.sleepUntil(()-> !insidePortal.contains(Players.getLocal()), 3000);
                }
                else
                    Log.info("We do not have a games necklace");
            }
            else if(insideHouse.contains(Players.getLocal()))
            {
                if(Dialog.isOpen())
                {
                    if(Dialog.canContinue())
                    {
                        Dialog.processContinue();
                    }
                    else
                    {
                        Dialog.process(0);
                    }
                }
                else
                {
                    Npc bootDude = Npcs.getNearest("Tenzing");
                    if(bootDude != null)
                    {
                        bootDude.interact("Talk-to");
                        Time.sleepUntil(Dialog::isOpen, Random.high(555,1444));
                    }
                }

            }
            else if(outsideHouse.contains(Players.getLocal()))
            {
                SceneObject theGate = SceneObjects.getNearest("Gate");
                if(theGate.containsAction("Open"))
                {
                    theGate.interact("Open");
                    Time.sleepUntil(()-> theGate.containsAction("Close"), Random.high(1111,2222));
                }
                else
                {
                    SceneObject theDoor = SceneObjects.getNearest(a-> a != null && a.getName().equals("Door") && a.getY() == 3555);
                    if(theDoor != null)
                    {
                        theDoor.interact("Open");
                        Time.sleepUntil(()-> insideHouse.contains(Players.getLocal()), Random.high(2222,4444));
                    }
                }
            }
            else
            {
                if(Movement.getRunEnergy() > 60 && !Movement.isRunEnabled())
                {
                    Movement.toggleRun(true);
                }
                Movement.walkToRandomized(outsideHouse.getCenter());
                Time.sleepUntil(()-> !Players.getLocal().isMoving() || outsideHouse.contains(Players.getLocal()), Random.high(666, 2222));
            }

        }
        return 555;
    }

    @Override
    public void notify(ItemTableEvent e) {
        if(Dialog.isOpen() && e.getChangeType() == ItemTableEvent.ChangeType.ITEM_ADDED)
        {
            bootsBought++;
        }

    }

    @Override
    public void notify(RenderEvent renderEvent) {
        Graphics g = renderEvent.getSource();
        g.setColor(new Color(0,0,0,150));
        g.fillRoundRect(5,30,150,130,10,10);
        g.setFont(new Font("Arial", Font.BOLD, 14));
        g.setColor(Color.white);
        g.drawString("Runtime: " + timer.toElapsedString(), 10,50);
        g.drawString("Boots bought: " + bootsBought, 10,70);
        g.drawString("Boots/hr: " + Math.floor(timer.getHourlyRate(bootsBought)), 10,90);

    }
}