
package net.customware.confluence.plugin.menumanager;

import java.util.ArrayList;
import java.util.Collection;

import javax.servlet.http.HttpServletRequest;

import net.customware.atlassian.menu.MenuManager;
import net.customware.atlassian.menu.model.Fragment;
import net.customware.atlassian.menu.model.Location;
import net.customware.atlassian.menu.model.Section;

import org.apache.log4j.Logger;

import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.core.ConfluenceSystemProperties;
import com.atlassian.confluence.user.UserAccessor;
import com.opensymphony.webwork.ServletActionContext;



/**
 * Configures menu item settings.
 */
public class MenuManagerAction extends ConfluenceActionSupport {

    /**
     * The serial version UID.
     */
    private static final long serialVersionUID = 6747917384931076873L;
    private static final Logger LOG = Logger.getLogger(MenuManagerAction.class);
    private String update;

    private Collection<Location> locations;

    private MenuManager menuManager;


    public MenuManagerAction() {
    }

    @Override
    public boolean isPermitted() {
        return permissionManager.isConfluenceAdministrator( getRemoteUser() );
    }

    @Override
    public String execute() throws Exception {
    	locations = new ArrayList<Location>();


            locations = menuManager.getLocations();

 
        if ( update != null ) {
            HttpServletRequest req = ServletActionContext.getRequest();

            for ( Location location : locations ) {
                updateFragments( location.getFragments(), req );
            }
        }

        return SUCCESS;
    }

    private void updateFragments(Collection<? extends Fragment> fragments, HttpServletRequest req) {
        for (Fragment fragment : fragments) {
            if (isPluginEnabled(fragment)) {
                String value = req.getParameter(fragment.getModuleKey());
                // Only adjust the enabled status if either "enabled" or
                // "disabled" is set explicitly.
                if ("enabled".equals(value)) {
                    if (! pluginAccessor.isPluginModuleEnabled(fragment.getModuleKey())) {
                        menuManager.setEnabled(fragment, true);
                    }
                } else if ("disabled".equals(value)) {
                    if (pluginAccessor.isPluginModuleEnabled(fragment.getModuleKey())) {
                        menuManager.setEnabled(fragment, false);
                    }
                }
            }

            if (fragment instanceof Section) {
                updateFragments(((Section) fragment).getItems(), req);
            }
        }
    }

    public String getUpdate() {
        return update;
    }

    public void setUpdate( String update ) {
        this.update = update;
    }

    public Collection<Location> getLocations() {
        return locations;
    }

    public boolean isSection( Fragment fragment ) {
        return fragment instanceof Section;
    }

    public boolean isEnabled( Fragment fragment ) {
        return menuManager.isEnabled( fragment );
    }

    /**
     * Returns <code>true</code> if the plugin the fragment belongs to is
     * enabled.
     * 
     * @param fragment The fragment to check.
     * @return true if the plugin fragment is enabled.
     */
    public boolean isPluginEnabled( Fragment fragment ) {
        return menuManager.isPluginEnabled( fragment );
    }

    public void setMenuManager( MenuManager menuManager ) {
        this.menuManager = menuManager;
    }

    public void setUserAccessor( UserAccessor userAccessor ) {
        this.userAccessor = userAccessor;
    }
    

}
