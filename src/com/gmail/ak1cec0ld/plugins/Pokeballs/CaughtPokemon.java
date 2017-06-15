package com.gmail.ak1cec0ld.plugins.Pokeballs;

import org.bukkit.Location;
import org.bukkit.attribute.Attributable;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Slime;
import org.bukkit.entity.Zombie;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class CaughtPokemon {
    
    Pokeballs plugin;
    private Entity entity;
    private String nickname;
    //private String IVs;
    //private boolean shiny;
    //private int level;
    private String status;
    //private Player owner;
    
    public CaughtPokemon(Pokeballs pl, Player owner, Location loc, String ivList, boolean isShiny, int level, String status, String entityType){
        CatchableStorageManager storage = pl.getStorage();
        this.plugin = pl;
        //this.shiny = isShiny;
        this.entity = loc.getWorld().spawnEntity(loc, plugin.getStorage().getEntityFromLittlename(entityType));
        if(storage.isBaby(entityType)){
            ((Zombie)entity).setBaby(true);
        } else if(storage.isPowerable(entityType)){
            ((Creeper)entity).setPowered(true);
        } else if(storage.isSizeable(entityType)){
            ((Slime)entity).setSize(2);
        } else {
            if(entity instanceof Zombie){
                ((Zombie)entity).setBaby(false);
            } else if(entity instanceof Creeper){
                ((Creeper)entity).setPowered(false);
            } else if(entity instanceof Slime){
                ((Slime)entity).setSize(1);
            }
        }
        //this.IVs = ivList;
        //this.level = level;
        //this.status = status;
        //this.owner = owner;
        plugin.addPokemon(owner.getUniqueId().toString(), this);
        plugin.setMetadata(entity, "pokeball", owner.getName());
        plugin.setMetadata(entity, "pokemon_owner", owner.getName());
        plugin.setMetadata(entity, "pokemon_ivs", ivList);
    }

    public void levelUp(){
        
    }
    
    public int getIV(String stat){
        switch(stat){
            case "hp": try{
                return (Integer.parseInt(plugin.getMetadataAsString(entity, "pokemon_ivs").replace("§", "").substring(0, 2)));
            }catch(NumberFormatException e){return -1;}
            case "attack": try{
                return (Integer.parseInt(plugin.getMetadataAsString(entity, "pokemon_ivs").replace("§", "").substring(2, 4)));
            }catch(NumberFormatException e){return -1;}
            case "defense": try{
                return (Integer.parseInt(plugin.getMetadataAsString(entity, "pokemon_ivs").replace("§", "").substring(4, 6)));
            }catch(NumberFormatException e){return -1;}
            case "spattack": try{
                return (Integer.parseInt(plugin.getMetadataAsString(entity, "pokemon_ivs").replace("§", "").substring(6, 8)));
            }catch(NumberFormatException e){return -1;}
            case "spdefense": try{
                return (Integer.parseInt(plugin.getMetadataAsString(entity, "pokemon_ivs").replace("§", "").substring(8, 10)));
            }catch(NumberFormatException e){return -1;}
            case "speed": try{
                return (Integer.parseInt(plugin.getMetadataAsString(entity, "pokemon_ivs").replace("§", "").substring(10, 12)));
            }catch(NumberFormatException e){return -1;}
            
            default:
                return 0;
        }
    }
    
    public Entity getEntity(){
        return this.entity;
    }
    
    public void recall(){
        entity.remove();
        plugin.removePokemon(this);
    }
    
    /*public boolean isShiny(){
        return plugin.getMetaDataAsString(entity, "pokemon_ivs").contains("f");
    }*/
    
    public void setStats(String nick, double HP, double maxHP, int exp, int maxExp, String status, 
            double att, double def, double spd, double spatt, double spdef){
        this.nickname = nick;
        entity.setCustomNameVisible(true);
        entity.setCustomName(nick);
        
        ((Attributable)entity).getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(maxHP);
        ((Damageable)entity).setHealth(HP/*+((HPIV-16)/4)*/);
        ((Attributable)entity).getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(((Attributable)entity).getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).getBaseValue()*(0.9+(0.0079*spd))+getIV("speed")/100);
        setStatus(status);
    }
    
    private void setStatus(String newStatus) {
        status = newStatus;
        switch(newStatus){
            case "§cBRN":
                entity.setFireTicks(60);
                break;
            case "§bFRZ":
                ((LivingEntity)entity).addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 60, 10), true);
                break;
            case "§6PRZ":
                ((LivingEntity)entity).addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 60, 10), true);
                break;
            case "§5PSN":
                ((LivingEntity)entity).addPotionEffect(new PotionEffect(PotionEffectType.POISON, 60, 1), true);
                break;
            case "§3SLP":
                ((LivingEntity)entity).addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 60, 10), true);
                break;
            case "§7CFS":
                ((LivingEntity)entity).addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 60, 10), true);
                break;
            case "§dLOVE":
                ((LivingEntity)entity).addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 60, 10), true);
                break;
            case "§5§lBPSN":
                ((LivingEntity)entity).addPotionEffect(new PotionEffect(PotionEffectType.POISON, 60, 3), true);
                break;
        }
        plugin.getServer().getScheduler().runTaskLater(plugin, new Runnable(){
            @Override
            public void run() {
                status = "§2HEALTHY";
            }}, 60L);
    }
    
    public String getStatus(){
        return this.status;
    }
    
    public String getNickname(){
        return this.nickname;
    }
    
    public String getOwner(){
        return plugin.getMetadataAsString(entity, "pokemon_owner");
    }
    
    /*public void evolve(){
     * get the location of this.entity
     * despawn this.entity then
     * spawn a new pokemon at that same location and reassign this.entity
        switch(entity.getType().name()){
            case "ZOMBIE":
                if(((Zombie)entity).isBaby()){
                    //return EntityType.ZOMBIE;
                } else {
                    //return EntityType.HUSK;
                }
            case "WITHER_SKELETON":
                //return EntityType.STRAY;
            case "CREEPER":
                //return EntityType.CREEPER;
            case "SPIDER":
                //return EntityType.CAVE_SPIDER;
            case "SLIME":
                //return EntityType.SLIME;
            case "MAGMA_CUBE":
                //return EntityType.MAGMA_CUBE;
                
            default:
                //return null;
        }
    }
    
    private replaceEntity(EntityType newE){
        Location loc = this.entity.getLocation();
        this.entity.despawn();
        this.entity = loc.getWorld().spawnEntity(loc,newE);
    }
    */
    
    /*public String getExpCurve(){
        return "";
    }*/
}
