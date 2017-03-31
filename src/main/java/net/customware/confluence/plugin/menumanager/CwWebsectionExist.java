package net.customware.confluence.plugin.menumanager;

import java.util.Map;

import com.atlassian.bandana.BandanaContext;
import com.atlassian.bandana.BandanaManager;
import com.atlassian.confluence.setup.bandana.ConfluenceBandanaContext;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.PluginParseException;

public class CwWebsectionExist implements com.atlassian.plugin.web.Condition{

	protected  PluginAccessor pluginAccessor; 
	protected  BandanaContext bandanaContext; 
	protected  BandanaManager bandanaManager; 
	
	protected final String CW_WEB_SECTION = "2JJTO7VWVUZLRG7OCRYIBNXXQXG60Z3Z-section"; 	
	protected final String PLUGIN_KEY =  "net.customware.confluence.plugin.menumanager";
	protected String pluginkey = null; 
	
	 
	
	public CwWebsectionExist(PluginAccessor pluginAccessor,   BandanaManager bandanaManager ){ 
		this.pluginAccessor= pluginAccessor; 
		this.bandanaContext = new ConfluenceBandanaContext(); 
		this.bandanaManager = bandanaManager; 
	}
	
	@Override
	public void init(Map<String, String> arg0) throws PluginParseException { } 

	@Override
	public boolean shouldDisplay(Map<String, Object> arg0) {
		String ownerplugin = (String) bandanaManager.getValue(bandanaContext, CW_WEB_SECTION); 
		
		if (pluginAccessor.getEnabledPlugin("net.customware.confluence.plugin.composition") != null ){
		 	return false;  // composition is installed  
		}
	 		
 		if(ownerplugin!=null && pluginAccessor.getEnabledPlugin(ownerplugin)!=null){
			if(ownerplugin.equals(PLUGIN_KEY))
				return true;
			else 
				return false; 
		}
		
		// if we are here, we have an owner which is not installed or we don't have an owner, no composition, but owner is not installed -> remove owner, register self and return true
 		bandanaManager.removeValue(bandanaContext, CW_WEB_SECTION); 
 		bandanaManager.setValue(bandanaContext, CW_WEB_SECTION, PLUGIN_KEY); 
		return true; 
 	}  
}
