package net.customware.atlassian.menu;

import java.util.Collection;

import net.customware.atlassian.menu.model.Fragment;
import net.customware.atlassian.menu.model.Location;

public interface MenuManager {
    Collection<Location> getLocations();

    boolean isEnabled( Fragment fragment );
    
    boolean isPluginEnabled( Fragment fragment );
    
    void setEnabled( Fragment fragment, boolean enabled );
}
