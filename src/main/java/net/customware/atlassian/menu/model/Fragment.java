package net.customware.atlassian.menu.model;

import com.atlassian.plugin.web.descriptors.WebFragmentModuleDescriptor;

public interface Fragment extends Comparable<Fragment> {
    /**
     * Returns the descriptor for the fragment.
     * 
     * @return The fragment descriptor.
     */
    WebFragmentModuleDescriptor getDescriptor();

    /**
     * The name of this fragment.
     * 
     * @return
     */
    String getKey();

    /**
     * The full name of the fragment, including location.
     * 
     * @return
     */
    String getLocationKey();

    /**
     * Returns the full key of the module descriptor.
     * 
     * @return
     */
    String getModuleKey();

    /**
     * Returns the location name this fragment is in.
     * 
     * @return
     */
    String getLocation();

    /**
     * Returns the full 'location' or 'location/section' target of the fragment.
     * 
     * @return
     */
    String getTarget();

    /**
     * The human-readable label for the fragment.
     * 
     * @return
     */
    String getLabel();

    /**
     * The module description.
     * 
     * @return The description.
     */
    String getDescription();

    /**
     * Returnes the name of the plugin the fragment belongs to.
     * 
     * @return
     */
    String getPluginName();

    String getPluginKey();
    
    int getWeight();
}
