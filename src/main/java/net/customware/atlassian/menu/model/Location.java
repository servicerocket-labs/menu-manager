package net.customware.atlassian.menu.model;

import com.atlassian.confluence.velocity.htmlsafe.HtmlSafe;
import com.atlassian.sal.api.message.I18nResolver;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class Location implements Iterable<Fragment>, Comparable<Location>, FragmentContainer {
    public static final String UNSPECIFIED = "[unspecified]";

    private String name;

    private String label;

    private List<Fragment> fragments;

    private String details;

    private final I18nResolver i18nResolver;

    public Location( String name, I18nResolver i18nResolver ) {
        this.i18nResolver = i18nResolver;
        this.name = name == null ? UNSPECIFIED : name;
        this.fragments = new java.util.ArrayList<Fragment>();
        this.label = getText( "menumanager.location." + name, name );
        this.details = getText( "menumanager.location." + name + ".details", "" );
    }

    @HtmlSafe
    public String getName() {
        return name;
    }

    public void addFragment( Fragment fragment ) {
        fragments.add( fragment );
        Collections.sort( fragments );
    }

    public Collection<Fragment> getFragments() {
        return fragments;
    }

    public Iterator<Fragment> iterator() {
        return fragments.iterator();
    }

    public int compareTo( Location o ) {
        return label == null ? -1 : label.compareTo( o.label );
    }

    @HtmlSafe
    public String getLabel() {
        return label;
    }

    @HtmlSafe
    public String getDetails() {
        return details;
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
}
