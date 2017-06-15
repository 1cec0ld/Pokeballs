package com.gmail.ak1cec0ld.plugins.Pokeballs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.logging.Level;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.attribute.Attributable;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.Metadatable;
import org.bukkit.plugin.java.JavaPlugin;

public class Pokeballs extends JavaPlugin{
    private HashMap<String,Set<CaughtPokemon>> outpokemon;
    private CatchableStorageManager catchableStorage;

    private Random r = new Random();
    
    public void onEnable(){
        this.outpokemon = new HashMap<String,Set<CaughtPokemon>>();
        catchableStorage = new CatchableStorageManager(this);
        this.getServer().getPluginManager().registerEvents(new ProjectileLaunchListener(this), this);
        this.getServer().getPluginManager().registerEvents(new ProjectileHitListener(this), this);
        this.getServer().getPluginManager().registerEvents(new EntityDamageListener(this), this);
    }
    
    public int getMetadataAsInt(Metadatable entity, String key){
        if(entity.hasMetadata(key)){
            return entity.getMetadata(key).get(0).asInt();
        }
        return 0;
    }
    public String getMetadataAsString(Metadatable entity, String key){
        if(entity.hasMetadata(key)){
            return entity.getMetadata(key).get(0).asString();
        }
        return "";
    }
    
    public void setMetadata(Metadatable entity, String key, int value){
        entity.setMetadata(key, new FixedMetadataValue(this,value));
    }

    public void setMetadata(Entity entity, String key, String value) {
        entity.setMetadata(key, new FixedMetadataValue(this,value));
        
    }

    public void addPokemon(String uuid, CaughtPokemon poke){
        if(hasPokemonOut(uuid)){
            outpokemon.get(uuid).add(poke);
        } else {
            outpokemon.put(uuid, new HashSet<CaughtPokemon>());
            outpokemon.get(uuid).add(poke);
        }
    }
    
    public void removePokemon(CaughtPokemon poke){
        if(isOut(poke)){
            for(String uuid : outpokemon.keySet()){
                outpokemon.get(uuid).remove(poke);
                if(outpokemon.get(uuid).size()==0){
                    outpokemon.remove(uuid);
                }
            }
        }
    }
    
    public boolean isOut(CaughtPokemon p){
        for(String uuid : outpokemon.keySet()){
            if(outpokemon.get(uuid).contains(p)){
                return true;
            }
        }
        return false;
    }
    
    public boolean hasPokemonOut(String uuid){
        return outpokemon.containsKey(uuid);
    }
    
    public Set<CaughtPokemon> getOutPokemon(String uuid){
        return outpokemon.get(uuid);
    }

    public void logSevere(String string) {
        this.getLogger().log(Level.SEVERE, string);
    }
    
    public void logInfo(String string) {
        this.getLogger().log(Level.INFO, string);
    }

    public String parseEntityType(String ballname) {
        return ballname.substring(ballname.indexOf("(")+1, ballname.length()-1);
    }

    public String parseNick(String ballname) {
        String str = ballname.substring(0,ballname.indexOf("("));
        return (str.length()>=1?str.substring(0, str.length()-1):parseEntityType(ballname));
    }
    
    public ItemMeta getItemMetaFromEntity(Entity e){
        ItemStack stack = new ItemStack(Material.SNOW_BALL,1);
        ItemMeta imeta = stack.getItemMeta();
        Attributable ae = (Attributable)e;

        imeta.setDisplayName("("+getStorage().getLittleName(e)+")");
        List<String> ilore = new ArrayList<String>();
        int level = randomLevel(e);
        ilore.add(0,ChatColor.GOLD+"LEVEL: "+level);
        ilore.add(1,ChatColor.GREEN+"Health: "+(int)((Damageable)e).getHealth()+"/"+(int)ae.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue()+"");
        ilore.add(2,ChatColor.BLUE+"EXP: 0/5");
        ilore.add(3,ChatColor.DARK_GREEN+"HEALTHY");
        ilore.add(4,ChatColor.BLUE+"Attack: ??");
        ilore.add(5,ChatColor.DARK_GRAY+"Defence: ??");
        ilore.add(6,ChatColor.YELLOW+"Speed: "+(int)((ae.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).getBaseValue()-.9)/.0079));
        ilore.add(7,ChatColor.DARK_PURPLE+"SpAttack: ??");
        ilore.add(8,ChatColor.GRAY+"SpDefence: ??");
        ilore.add(9,randomIVs());
        imeta.setLore(ilore);
        return imeta;
    }
    
    private int randomLevel(Entity e){
        int maxLevel = getStorage().getMaxLevel(e);
        int minLevel = getStorage().getMinLevel(e);
        return r.nextInt(maxLevel-minLevel)+minLevel;
    }

    private String randomIVs(){
        String ivs = "";
        for(int i = 0; i < 6; i++){
            int temp = r.nextInt(32);
            ivs+="§";
            ivs+=temp/10;
            ivs+="§";
            ivs+=temp%10;
        }
        return ivs;
    }
    
    public CatchableStorageManager getStorage(){
        return this.catchableStorage;
    }
}
