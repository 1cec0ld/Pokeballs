package com.gmail.ak1cec0ld.plugins.Pokeballs;


import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.projectiles.ProjectileSource;

public class EntityDamageListener implements Listener{
    private Pokeballs plugin;
    private Random r = new Random();

    EntityDamageListener(Pokeballs plugin){
        this.plugin = plugin;
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event){
        //plugin.logInfo(((Attributable)event.getEntity()).getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).getValue()+" is speed");
        //plugin.logInfo(((Attributable)event.getEntity()).getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).getDefaultValue()+" is speed");

        if(event.getDamager() instanceof Snowball){
            Snowball ball = (Snowball)event.getDamager();
            ProjectileSource shooter = ball.getShooter();
            if(shooter instanceof Player){
                int chance = plugin.getStorage().getChance(event.getEntity());
                if(chance > 0){
                    event.setCancelled(true);
                    int roll = r.nextInt(100);
                    if(roll<chance){
                        Player trainer = (Player)shooter;
                        catchPokemon(trainer,event.getEntity());
                        trainer.sendMessage(ChatColor.YELLOW+"Gotcha! "+plugin.getStorage().getLittleName(event.getEntity())+" was caught!");
                    } else {
                        ((Player)shooter).sendMessage(getFailMessage(roll,chance));
                    }
                }
            }
        }
    }
    
    private String getFailMessage(int roll, int chance){
        if(roll-chance > chance*3/4){
            return ChatColor.DARK_RED+"Aww! It appeared to be caught!";
        } else if(roll-chance > chance/2){
            return ChatColor.RED+"Aargh! Almost had it!";
        } else if(roll-chance > chance/4){
            return ChatColor.GOLD+"Shoot! It was so close, too!";
        }
        return ChatColor.DARK_RED+""+ChatColor.BOLD+"Oh, no! The Pokémon broke free!";
    }
    
    private void catchPokemon(Player trainer, Entity entity) {
        ItemStack pokeball = new ItemStack(Material.SNOW_BALL,1);
        pokeball.setItemMeta(plugin.getItemMetaFromEntity(entity));
        trainer.getWorld().dropItem(trainer.getEyeLocation(),pokeball);
        entity.remove();
    }
}
