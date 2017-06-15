package com.gmail.ak1cec0ld.plugins.Pokeballs;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.projectiles.ProjectileSource;

public class ProjectileLaunchListener implements Listener{
    Pokeballs plugin;
    
    ProjectileLaunchListener(Pokeballs plugin){
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onProjectileLaunch(ProjectileLaunchEvent event){
        ProjectileSource shooter = event.getEntity().getShooter();
        if (shooter instanceof Player){
            Player player = (Player)shooter;
            if(event.getEntity() instanceof Snowball){
                Projectile proj = event.getEntity();
                ItemStack snowball = player.getInventory().getItemInMainHand();
                if(snowball.hasItemMeta()){
                    ItemMeta snowmeta = snowball.getItemMeta();
                    if(snowmeta.hasLore()){
                        if(snowmeta.getLore().get(0).contains("LEVEL:")){
                            event.setCancelled(true);
                            if(ChatColor.stripColor(snowmeta.getLore().get(3)).equals("FNT")){
                                player.sendMessage("§cYour pokemon can't fight!");
                            } else if(ChatColor.stripColor(snowmeta.getLore().get(3)).equals("OUT")){
                                if(plugin.hasPokemonOut(player.getUniqueId().toString())){
                                    for(CaughtPokemon ownedPoke : plugin.getOutPokemon(player.getUniqueId().toString())){
                                        if(itemMatchesPokemon(snowball,ownedPoke)){
                                            final List<String> loreCopy = snowmeta.getLore();
                                            loreCopy.set(3, ownedPoke.getStatus());
                                            snowmeta.setLore(loreCopy);
                                            snowball.setItemMeta(snowmeta);
                                            ownedPoke.recall();
                                        }
                                    }
                                } else {
                                    player.sendMessage(ChatColor.DARK_RED+"No pokemon were found! This pokemon appears to be RELEASED!");
                                }
                            } else{
                                Entity ball = player.getWorld().spawnEntity(player.getEyeLocation(), EntityType.SNOWBALL);
                                ball.setVelocity(proj.getVelocity());
                                ((Projectile)ball).setShooter(player);
                                plugin.setMetadata(ball, "pokeball", "yes");
                                plugin.setMetadata(ball, "pokemon_ballname", ChatColor.stripColor(snowmeta.getDisplayName()));
                                plugin.setMetadata(ball, "pokemon_level", getStatFromString(snowmeta.getLore().get(0)));
                                plugin.setMetadata(ball, "pokemon_hp", getStatMinFromFraction(snowmeta.getLore().get(1)));
                                plugin.setMetadata(ball, "pokemon_maxhp", getStatMaxFromFraction(snowmeta.getLore().get(1)));
                                plugin.setMetadata(ball, "pokemon_exp", getStatMinFromFraction(snowmeta.getLore().get(2)));
                                plugin.setMetadata(ball, "pokemon_maxexp", getStatMaxFromFraction(snowmeta.getLore().get(2)));
                                plugin.setMetadata(ball, "pokemon_status", ChatColor.stripColor(snowmeta.getLore().get(3)));
                                plugin.setMetadata(ball, "pokemon_attack", getStatFromString(snowmeta.getLore().get(4)));
                                plugin.setMetadata(ball, "pokemon_defense", getStatFromString(snowmeta.getLore().get(5)));
                                plugin.setMetadata(ball, "pokemon_spattack", getStatFromString(snowmeta.getLore().get(6)));
                                plugin.setMetadata(ball, "pokemon_spdefense", getStatFromString(snowmeta.getLore().get(7)));
                                plugin.setMetadata(ball, "pokemon_speed", getStatFromString(snowmeta.getLore().get(8)));
                                plugin.setMetadata(ball, "pokemon_ivs", snowmeta.getLore().get(9));
                                
                                final List<String> loreCopy = snowmeta.getLore();
                                loreCopy.set(3, "§8OUT");
                                snowmeta.setLore(loreCopy);
                                snowball.setItemMeta(snowmeta);
                            }
                        }
                    }
                }
            }
        }
    }
    
    private boolean itemMatchesPokemon(ItemStack snowball, CaughtPokemon poke) {
        String dispName = snowball.getItemMeta().getDisplayName();
        if(poke.getNickname().equals(plugin.parseNick(dispName)) &&
           poke.getEntity().getType().equals(plugin.getStorage().getEntityFromLittlename(plugin.parseEntityType(dispName))) &&
           plugin.getMetadataAsString(poke.getEntity(), "pokemon_ivs").equals(snowball.getItemMeta().getLore().get(9))){
           return true;
        }
        return false;
    }

    public int getStatMinFromFraction(String input){
        try{
            int numb = Integer.parseInt(input.substring(input.indexOf(" ")+1, input.indexOf("/")));
            return numb;
        }catch(NumberFormatException e){plugin.logSevere("getStatMin failed: "+input);return 0;}
    }
    public int getStatMaxFromFraction(String input){
        try{
            int numb = Integer.parseInt(input.substring(input.indexOf("/")+1, input.length()));
            return numb;
        }catch(NumberFormatException e){plugin.logSevere("getStatMax failed: "+input);return 0;}
    }
    public int getStatFromString(String input){
        try{
            int numb =Integer.parseInt(input.substring(input.indexOf(" ")+1, input.length()));
            return numb;
        }catch(NumberFormatException e){plugin.logSevere("getStatFromString failed: "+input);return 0;}
    }
}
