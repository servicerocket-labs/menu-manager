package net.customware.atlassian.menu.model;

import com.atlassian.confluence.velocity.htmlsafe.HtmlSafe;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.web.descriptors.WebFragmentModuleDescriptor;
import com.atlassian.plugin.web.model.WebLabel;
import com.atlassian.sal.api.message.I18nResolver;
import org.apache.commons.lang.StringUtils;

public abstract class AbstractFragment<T extends WebFragmentModuleDescriptor> implements Fragment {

    private T descriptor;

    private String description;

    private String label;

    private final I18nResolver i18nResolver;

    public AbstractFragment( T descriptor, I18nResolver i18nResolver ) {
        this.i18nResolver = i18nResolver;
        this.descriptor = descriptor;

        // Initialise the label
        WebLabel webLabel = getDescriptor().getWebLabel();
        if ( webLabel != null )
            label = getText( webLabel.getKey(), webLabel.getNoKeyValue() );

        if ( StringUtils.isBlank( label ) || ( label !=null && label.equals("{0}") ))
            label = getText( descriptor.getI18nNameKey(), descriptor.getName() );

        label = cleanText( label );
         
        // Initialise the description
        description = getText( descriptor.getDescriptionKey(), descriptor.getDescription() );
    }

    public T getDescriptor() {
        return descriptor;
    }

    public String getKey() {
        return descriptor.getKey();
    }

    public String getLocationKey() {
        String target = getTarget();
        return ( target == null ? Location.UNSPECIFIED : target ) + "/" + getKey();
    }

    public String getModuleKey() {
        return descriptor.getCompleteKey();
    }

    public int compareTo( Fragment o ) {
        return descriptor.getWeight() - o.getDescriptor().getWeight();
    }

    @HtmlSafe
    public String getLabel() {
        return label;
    }

    @HtmlSafe
    public String getDescription() {
        return description;
    }

    public String getPluginName() {
        Plugin plugin = getDescriptor().getPlugin();
        if ( plugin != null )
            return getText( plugin.getI18nNameKey(), plugin.getName() );
        else
            return getText( "menumanager.fragment.plugin.unknown", "Unknown" );
    }

    public String getPluginKey() {
        return getDescriptor().getPluginKey();
    }

    private String cleanText( String text ) {
        if ( text != null ) {
            text = text.replaceAll( "\\{[0-9]\\}([\\S])\\{[0-9]\\}", "$1" );
        }
        return text;
    }

    private String getText( String key, String alternate ) {
        String text = alternate;
        if ( key != null ) {
            text = i18nResolver.getText( key );
            if ( key.equals( text ) ) {
                text = alternate;
            }
        }
        return text;
    }

    public int getWeight() {
        return getDescriptor().getWeight();
    }
}
