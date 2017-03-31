package it.net.customware.confluence.plugin.menumanager;

import com.atlassian.confluence.test.api.model.person.UserWithDetails;


import it.net.customware.confluence.plugin.menumanager.pageobjects.MenuManagerPage;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class MenuManagerTest extends DefaultStatelessTestRunner {

    MenuManagerPage page;

    @Before
    public void setUp() throws Exception {
        product.gotoLoginPage();
        page = product.login(UserWithDetails.ADMIN, MenuManagerPage.class);
    }

    @Test
    public void hideMarketPlaceSection() {
        page.expandAll().getLocation("system.admin").getFragment("system.admin/marketplace_confluence").disable();
        page.save();
        assertFalse(product.getTester().getDriver().getPageSource().contains("section-menuheading-marketplace_confluence"));
    }

    @Test
    public void showMarketPlaceSection() {
        page.expandAll().getLocation("system.admin").getFragment("system.admin/marketplace_confluence").enable();
        page.save();
        assertTrue(product.getTester().getDriver().getPageSource().contains("section-menuheading-marketplace_confluence"));
    }
}
