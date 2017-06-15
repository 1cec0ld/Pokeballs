package com.gmail.ak1cec0ld.plugins.Pokeballs;


import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;

public class ProjectileHitListener implements Listener{
    Pokeballs plugin;
    
    ProjectileHitListener(Pokeballs plugin){
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event){
        if(event.getEntity() instanceof Snowball){
            Snowball ball = (Snowball)event.getEntity();
            if(ball.hasMetadata("pokeball")){
                String ballname = plugin.getMetadataAsString(ball, "pokemon_ballname");
                String nickname = plugin.parseNick(ballname);
                String entityType = plugin.parseEntityType(ballname);
                int level = plugin.getMetadataAsInt(ball, "pokemon_level");
                double HP = plugin.getMetadataAsInt(ball, "pokemon_hp");
                double MaxHP = plugin.getMetadataAsInt(ball, "pokemon_maxhp");
                int EXP = plugin.getMetadataAsInt(ball, "pokemon_exp");
                int MaxEXP = plugin.getMetadataAsInt(ball, "pokemon_maxexp");
                String status = plugin.getMetadataAsString(ball, "pokemon_status");
                int attack = plugin.getMetadataAsInt(ball, "pokemon_attack");
                int defense = plugin.getMetadataAsInt(ball, "pokemon_defense");
                int speed = plugin.getMetadataAsInt(ball, "pokemon_speed");
                int spatt = plugin.getMetadataAsInt(ball, "pokemon_spattack");
                int spdef = plugin.getMetadataAsInt(ball, "pokemon_spdefense");
                String IVs = plugin.getMetadataAsString(ball, "pokemon_ivs");
                                
                CaughtPokemon poke = new CaughtPokemon(plugin, (Player)ball.getShooter(), ball.getLocation(), IVs, IVs.contains("f"), level, status, entityType);
                poke.setStats(nickname, HP, MaxHP, EXP, MaxEXP, status, attack, defense, speed, spatt, spdef);
                ((Player)ball.getShooter()).sendMessage("§3You sent out "+nickname+"!");
            }
        }
    }
}
