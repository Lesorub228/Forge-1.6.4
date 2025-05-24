// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraftforge.common;

import java.util.HashSet;
import java.util.Set;
import java.util.Iterator;
import java.util.List;
import java.util.Collection;
import java.util.Arrays;
import java.util.LinkedList;

public class AchievementPage
{
    private String name;
    private LinkedList<ko> achievements;
    private static LinkedList<AchievementPage> achievementPages;
    
    public AchievementPage(final String name, final ko... achievements) {
        this.name = name;
        this.achievements = new LinkedList<ko>(Arrays.asList(achievements));
    }
    
    public String getName() {
        return this.name;
    }
    
    public List<ko> getAchievements() {
        return this.achievements;
    }
    
    public static void registerAchievementPage(final AchievementPage page) {
        if (getAchievementPage(page.getName()) != null) {
            throw new RuntimeException("Duplicate achievement page name \"" + page.getName() + "\"!");
        }
        AchievementPage.achievementPages.add(page);
    }
    
    public static AchievementPage getAchievementPage(final int index) {
        return AchievementPage.achievementPages.get(index);
    }
    
    public static AchievementPage getAchievementPage(final String name) {
        for (final AchievementPage page : AchievementPage.achievementPages) {
            if (page.getName().equals(name)) {
                return page;
            }
        }
        return null;
    }
    
    public static Set<AchievementPage> getAchievementPages() {
        return new HashSet<AchievementPage>(AchievementPage.achievementPages);
    }
    
    public static boolean isAchievementInPages(final ko achievement) {
        for (final AchievementPage page : AchievementPage.achievementPages) {
            if (page.getAchievements().contains(achievement)) {
                return true;
            }
        }
        return false;
    }
    
    public static String getTitle(final int index) {
        return (index == -1) ? "Minecraft" : getAchievementPage(index).getName();
    }
    
    static {
        AchievementPage.achievementPages = new LinkedList<AchievementPage>();
    }
}
