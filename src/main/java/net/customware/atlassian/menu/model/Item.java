package net.customware.atlassian.menu.model;

import com.atlassian.confluence.velocity.htmlsafe.HtmlSafe;
import com.atlassian.plugin.web.descriptors.WebItemModuleDescriptor;
import com.atlassian.plugin.web.model.DefaultWebLink;
import com.atlassian.plugin.web.model.WebLink;
import com.atlassian.sal.api.message.I18nResolver;

public class Item extends AbstractFragment<WebItemModuleDescriptor> {

    private String location;

    private String section;

    public Item( WebItemModuleDescriptor descriptor, I18nResolver i18nResolver ) {
        super( descriptor, i18nResolver );
        String[] split = getDescriptor().getSection().split( "\\/" );

        switch ( split.length ) {
            case 2:
                section = split[1];
            case 1:
                location = split[0];
        }
    }

    public boolean isInSection() {
        return section != null;
    }

    @HtmlSafe
    public String getTarget() {
        return getDescriptor().getSection();
    }

    @HtmlSafe
    public String getLocation() {
        return location;
    }

    /**
     * Returns the section name this item is in.
     *
     * @return
     */
    @HtmlSafe
    public String getSection() {
        return section;
    }

    public String getLink() {
        WebLink link = getDescriptor().getLink();
        DefaultWebLink dLink;
        String linkText = null;

        if ( link != null )
            linkText = link.toString();

        return linkText;
    }

}
