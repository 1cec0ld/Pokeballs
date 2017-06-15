package com.gmail.ak1cec0ld.plugins.Pokeballs;

import java.io.File;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Zombie;

public class CatchableStorageManager {
    private CustomYMLStorage yml;
    private YamlConfiguration storage;
    
    
    public CatchableStorageManager(Pokeballs plugin){
        yml = new CustomYMLStorage(plugin,"PokeItems"+File.separator+"catchableMobs.yml");
        storage = yml.getYamlConfiguration();
        yml.save();
    }
    
    public EntityType getEntityFromLittlename(String littlename){
        if(storage.getKeys(false).contains(littlename)){
            try{
                return EntityType.valueOf(storage.getString(littlename+".entitytype"));
            }catch(IllegalArgumentException e){return EntityType.ZOMBIE;}
        }
        return EntityType.ZOMBIE;
    }
    
    public String getLittleName(Entity e){
        boolean powered = false;
        boolean baby = false;
        if(e instanceof Zombie){
            if(((Zombie)e).isBaby()){
                baby = true;
            }
        } else if(e instanceof Creeper){
            if(((Creeper)e).isPowered()){
                powered = true;
            }
        }
        for(String littlename : storage.getKeys(false)){
            boolean getBaby = storage.getBoolean(littlename+".baby", false);
            boolean getPowered = storage.getBoolean(littlename+".powered", false);
            String bigName = storage.getString(littlename+".entitytype","");
            if(e.getType().name().equals(bigName) && getBaby == baby && getPowered == powered){
                return littlename;
            }
        }
        return "BZombie";
    }
    
    public int getChance(Entity e){
        String littlename = getLittleName(e);
        return storage.getInt(littlename+".catchrate",-1);
    }
    
    public int getMinLevel(Entity e){
        String littlename = getLittleName(e);
        return storage.getInt(littlename+".minlevel", 1);
    }
    
    public int getMaxLevel(Entity e){
        String littlename = getLittleName(e);
        return storage.getInt(littlename+".maxlevel", 5);
    }
    
    public boolean isSizeable(String littlename){
        return storage.getBoolean(littlename+".sizable", false);
    }
    
    public boolean isBaby(String littlename){
        return storage.getBoolean(littlename+".baby", false);
    }
    
    public boolean isPowerable(String littlename){
        return storage.getBoolean(littlename+".powered", false);
    }
}
