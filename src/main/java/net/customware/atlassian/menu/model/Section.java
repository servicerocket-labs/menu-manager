package net.customware.atlassian.menu.model;

import com.atlassian.confluence.velocity.htmlsafe.HtmlSafe;
import com.atlassian.plugin.web.descriptors.WebSectionModuleDescriptor;
import com.atlassian.sal.api.message.I18nResolver;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class Section extends AbstractFragment<WebSectionModuleDescriptor> implements Iterable<Item>,
        FragmentContainer {

    private List<Item> items;

    public Section( WebSectionModuleDescriptor descriptor, I18nResolver i18nResolver ) {
        super( descriptor, i18nResolver );
        items = new java.util.ArrayList<Item>();
    }

    @HtmlSafe
    public String getTarget() {
        return getDescriptor().getLocation();
    }

    public void addItem( Item item ) {
        items.add( item );
        Collections.sort( items );
    }

    public Collection<Item> getItems() {
        return items;
    }

    public Iterator<Item> iterator() {
        return items.iterator();
    }

    @Override
    @HtmlSafe
    public String getLocation() {
        return getDescriptor().getLocation();
    }

    public Collection<? extends Fragment> getFragments() {
        return getItems();
    }
}
