package it.net.customware.confluence.plugin.menumanager.pageobjects;

import com.atlassian.confluence.webdriver.pageobjects.component.ConfluenceAbstractPageComponent;
import com.atlassian.confluence.webdriver.pageobjects.component.header.ConfluenceHeader;
import com.atlassian.pageobjects.PageBinder;
import com.atlassian.pageobjects.binder.Init;
import com.atlassian.pageobjects.elements.ElementBy;
import com.atlassian.pageobjects.elements.PageElement;
import com.atlassian.pageobjects.page.AdminHomePage;
import com.google.inject.Inject;

import org.openqa.selenium.By;

import java.util.HashMap;
import java.util.Map;

import static java.util.Collections.unmodifiableMap;

public class MenuManagerPage extends ConfluenceAbstractPageComponent implements AdminHomePage<ConfluenceHeader> {

    public static class Location {

        private PageElement root;
        private String id;
        private String title;
        private Map<String, Fragment> fragments;

        public Location(PageElement root) {

            PageElement title = root.find(By.cssSelector(".mm-header > .mm-title"));

            this.root = root;
            this.id = title.getAttribute("title");
            this.title = title.getText().trim();

            fragments = new HashMap<>();
            for (PageElement f: root.findAll(By.className("mm-fragment-0"))) {
                Fragment fragment = new Fragment(f);
                fragments.put(fragment.getId(), fragment);
            }
        }

        public String getId() {
            return this.id;
        }

        public String getTitle() {
            return this.title;
        }

        public Fragment getFragment(String fragment) {
            return fragments.get(fragment);
        }

        public Map<String, Fragment> getFragments() {
            return unmodifiableMap(fragments);
        }

        public Location expand() {
            throw new UnsupportedOperationException();
        }

        public Location collapse() {
            throw new UnsupportedOperationException();
        }
    }

    public static class Fragment {

        private PageElement root;
        private String id;
        private String title;

        public Fragment(PageElement root) {

            PageElement title = root.find(By.cssSelector(".mm-header > .mm-title"));

            this.root = root;
            this.id = title.getAttribute("title");
            this.title = title.getText().trim();
        }

        public String getId() {
            return id;
        }

        public String getTitle() {
            return title;
        }

        public Fragment disable() {
            root.find(By.cssSelector("select option[value=\"disabled\"]")).click();
            return this;
        }

        public Fragment enable() {
            root.find(By.cssSelector("select option[value=\"enabled\"]")).click();
            return this;
        }
    }

    private static final String URI = "/admin/plugins/menumanager/config.action";

    private Map<String, Location> locations = new HashMap<String, Location>();

    @Inject
    private PageBinder binder;

    @ElementBy(className = "mm-expand-all")
    private PageElement expandAllLink;

    @ElementBy(className = "mm-locations")
    private PageElement locationsDiv;

    @ElementBy(cssSelector = "#mm-form input[type='submit']")
    private PageElement submit;

    @Init public void initElements() {
        for (PageElement l: locationsDiv.findAll(By.className("mm-location"))) {
            Location location = new Location(l);
            locations.put(location.getId(), location);
        }
    }

    public MenuManagerPage expandAll() {
        expandAllLink.click();
        return this;
    }

    public Location getLocation(String name) {
        return locations.get(name);
    }

    public Map<String, Location> getLocations() {
        return unmodifiableMap(locations);
    }

    public MenuManagerPage save() {
        submit.click();
        return binder.bind(MenuManagerPage.class);
    }

    @Override
    public ConfluenceHeader getHeader() {
        return pageBinder.bind(ConfluenceHeader.class);
    }

    @Override
    public String getUrl() {
        return URI;
    }
}
