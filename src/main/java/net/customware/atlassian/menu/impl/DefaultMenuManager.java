package net.customware.atlassian.menu.impl;

import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.PluginController;
import com.atlassian.plugin.predicate.ModuleDescriptorPredicate;
import com.atlassian.plugin.web.descriptors.WebItemModuleDescriptor;
import com.atlassian.plugin.web.descriptors.WebSectionModuleDescriptor;
import com.atlassian.sal.api.message.I18nResolver;
import net.customware.atlassian.menu.MenuManager;
import net.customware.atlassian.menu.model.Fragment;
import net.customware.atlassian.menu.model.Item;
import net.customware.atlassian.menu.model.Location;
import net.customware.atlassian.menu.model.Section;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class DefaultMenuManager implements MenuManager {

    private final PluginAccessor pluginAccessor;

    private final PluginController pluginController;

    private final I18nResolver i18nResolver;

    public DefaultMenuManager( PluginAccessor pluginAccessor, PluginController pluginController, I18nResolver i18nResolver ) {
        this.pluginAccessor = pluginAccessor;
        this.pluginController = pluginController;
        this.i18nResolver = i18nResolver;
    }

    public Collection<Location> getLocations() {
        // Reset 'locationMap'.
        Map<String, Location> locationMap = new java.util.HashMap<String, Location>();
        Map<String, Section> sectionMap = new java.util.HashMap<String, Section>();

        // Process 'sectionMap' first.
        Collection<ModuleDescriptor<Object>> sectionDescriptors = pluginAccessor
                .getModuleDescriptors( new ModuleDescriptorPredicate<Object>() {
                    public boolean matches( ModuleDescriptor<?> descriptor ) {
                        return descriptor instanceof WebSectionModuleDescriptor;
                    }
                } );

        for ( ModuleDescriptor<?> descriptor : sectionDescriptors ) {
            WebSectionModuleDescriptor sectionDescriptor = ( WebSectionModuleDescriptor ) descriptor;
            Section section = new Section( sectionDescriptor, i18nResolver );

            Location location = findLocation( locationMap, section.getLocation() );
            location.addFragment( section );
            sectionMap.put( section.getLocationKey(), section );
        }

        // Next, do 'items'.
        Collection<ModuleDescriptor<Object>> itemDescriptors = pluginAccessor
                .getModuleDescriptors( new ModuleDescriptorPredicate<Object>() {
                    public boolean matches( ModuleDescriptor<?> descriptor ) {
                        return descriptor instanceof WebItemModuleDescriptor;
                    }
                } );

        for ( ModuleDescriptor<?> descriptor : itemDescriptors ) {
            WebItemModuleDescriptor itemDescriptor = ( WebItemModuleDescriptor ) descriptor;
            Item item = new Item( itemDescriptor, i18nResolver );

            if ( item.isInSection() ) {
                Section section = sectionMap.get( item.getTarget() );
                if ( section != null ) {
                    section.addItem( item );
                }
            } else {
                Location location = findLocation( locationMap, item.getLocation() );
                location.addFragment( item );
            }
        }

        // Add and sort the locations.
        List<Location> locationList = new java.util.ArrayList<Location>();
        locationList.addAll( locationMap.values() );
        Collections.sort( locationList );
        return locationList;
    }

    private Location findLocation( Map<String, Location> locationMap, String locationName ) {
        locationName = locationName == null ? Location.UNSPECIFIED : locationName;
        Location location = locationMap.get( locationName );
        if ( location == null ) {
            location = new Location( locationName, i18nResolver );
            locationMap.put( locationName, location );
        }
        return location;
    }

    public boolean isEnabled( Fragment fragment ) {
        return pluginAccessor.isPluginModuleEnabled( fragment.getModuleKey() );
    }

    public boolean isPluginEnabled( Fragment fragment ) {
        return pluginAccessor.isPluginEnabled( fragment.getDescriptor().getPluginKey() );
    }

    public void setEnabled( Fragment fragment, boolean enabled ) {
        if ( enabled )
            pluginController.enablePluginModule( fragment.getModuleKey() );
        else
            pluginController.disablePluginModule( fragment.getModuleKey() );
    }

}
