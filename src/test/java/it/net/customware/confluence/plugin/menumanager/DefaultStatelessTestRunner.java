package it.net.customware.confluence.plugin.menumanager;

import com.atlassian.confluence.api.model.content.*;
import com.atlassian.confluence.api.model.locator.ContentLocatorBuilder;
import com.atlassian.confluence.api.model.pagination.SimplePageRequest;
import com.atlassian.confluence.api.service.content.ContentBodyConversionService;
import com.atlassian.confluence.labels.DefaultLabelManager;
import com.atlassian.confluence.rest.api.model.ExpansionsParser;
import com.atlassian.confluence.test.api.model.person.UserWithDetails;
import com.atlassian.confluence.test.rest.api.ConfluenceRestClient;
import com.atlassian.confluence.test.rest.api.ConfluenceRestSession;
import com.atlassian.confluence.test.rpc.api.ConfluenceRpcClient;
import com.atlassian.confluence.test.rpc.api.ConfluenceRpcSession;
import com.atlassian.confluence.test.rpc.api.permissions.PermissionsRpc;
import com.atlassian.confluence.test.stateless.ConfluenceStatelessTestRunner;
import com.atlassian.confluence.test.stateless.fixtures.Fixture;
import com.atlassian.confluence.test.stateless.fixtures.GroupFixture;
import com.atlassian.confluence.test.stateless.fixtures.SpaceFixture;
import com.atlassian.confluence.test.stateless.fixtures.UserFixture;
import com.atlassian.confluence.webdriver.pageobjects.ConfluenceTestedProduct;
import com.atlassian.confluence.webdriver.pageobjects.page.NoOpPage;
import com.atlassian.confluence.webdriver.pageobjects.page.content.EditContentPage;
import com.atlassian.confluence.webdriver.pageobjects.page.content.ViewPage;
import com.atlassian.pageobjects.PageBinder;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Whitelist;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;

import javax.inject.Inject;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.atlassian.confluence.test.rpc.api.permissions.GlobalPermission.PERSONAL_SPACE;
import static com.atlassian.confluence.test.rpc.api.permissions.SpacePermission.*;
import static com.atlassian.confluence.test.stateless.fixtures.GroupFixture.groupFixture;
import static com.atlassian.confluence.test.stateless.fixtures.SpaceFixture.spaceFixture;
import static com.atlassian.confluence.test.stateless.fixtures.UserFixture.userFixture;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.SPACE;

/**
 * @author khailoon
 */
@Ignore
@RunWith(ConfluenceStatelessTestRunner.class)
public abstract class DefaultStatelessTestRunner implements StatelessTestRunner {

    private final int SPACE_COUNT_START_INDEX = 0;
    private final int SPACE_COUNT_LIMIT = 500;
    private final String COMMA_SEPARATOR = ",";
    private final String VIEW_EXTENSION = ".view";

    @Inject
    protected static ConfluenceTestedProduct product;
    @Inject
    protected static ConfluenceRestClient restClient;
    @Inject
    protected static ConfluenceRpcClient rpcClient;
    @Inject
    protected static PageBinder pageBinder;
    @Inject
    protected static WebDriver webDriver;
    @Inject
    protected static DefaultLabelManager defaultLabelManager;
    @Inject
    protected static ContentBodyConversionService contentBodyConversionService;
    @Inject
    protected static ContentLocatorBuilder contentLocatorBuilder;

    @Fixture
    public static GroupFixture defaultGroup = groupFixture()
            .globalPermission(PERSONAL_SPACE)
            .build();

    @Fixture
    public static UserFixture defaultUser = userFixture()
            .globalPermission(PERSONAL_SPACE)
            .group(defaultGroup)
            .build();

    @Fixture
    public static SpaceFixture defaultSpace = spaceFixture()
            .permission(defaultUser, VIEW, PAGE_EDIT, COMMENT, ATTACHMENT_CREATE)
            .build();

    protected final ConfluenceRestSession confluenceRestSession;
    protected final ConfluenceRestSession confluenceAnonymousRestSession;
    protected final ConfluenceRpcSession confluenceRpcSession;
    protected static PermissionsRpc permissionsRpc;

    protected EditContentPage editContentPage;
    protected ViewPage viewPage;

    public DefaultStatelessTestRunner() {
        confluenceRestSession = restClient.createSession(defaultUser.get());
        confluenceAnonymousRestSession = restClient.getAnonymousSession();
        confluenceRpcSession = rpcClient.getAdminSession();
        permissionsRpc = rpcClient.getAdminSession().getPermissionsComponent();
    }

    @BeforeClass
    public static void loginOnce() throws Exception {
        product.login(defaultUser.get(), NoOpPage.class);
    }

    @AfterClass
    public static void tearDown() throws Exception {
        product.logOutFast();
        defaultSpace.destroy();
    }

    @Override
    public Content createPage(String title, String content, ContentRepresentation contentRepresentation) {
        return createPage(title, content, contentRepresentation, defaultSpace.get());
    }

    @Override
    public Content createPage(String title, String content, ContentRepresentation contentRepresentation, Space space) {
        return confluenceRestSession.contentService().create(
                Content.builder(ContentType.PAGE)
                        .space(space)
                        .title(title)
                        .body(content, contentRepresentation)
                        .build()
        );
    }

    @Override
    public Content createPageForUser(UserWithDetails user, String title, String content, ContentRepresentation contentRepresentation) {
        return createPageForUser(user, title, content, contentRepresentation, defaultSpace.get());
    }

    @Override
    public Content createPageForUser(UserWithDetails user, String title, String content, ContentRepresentation contentRepresentation, Space space) {
        return restClient.createSession(user).contentService().create(
                Content.builder(ContentType.PAGE)
                        .space(space)
                        .title(title)
                        .body(content, contentRepresentation)
                        .build()
        );
    }

    @Override
    public Content anonymousCreatePage(String title, String content, ContentRepresentation contentRepresentation) {
        return restClient.getAnonymousSession().contentService().create(
                Content.builder(ContentType.PAGE)
                        .space(defaultSpace.get())
                        .title(title)
                        .body(content, contentRepresentation)
                        .build()
        );
    }

    @Override
    public Content editPageContent(Content content, String newContent, ContentRepresentation contentRepresentation) {
        Version version = content.getVersion().nextBuilder().build();
        Content editedContent = Content.builder(content).body(newContent, contentRepresentation).version(version).build();
        return confluenceRestSession.contentService().update(editedContent);
    }

    @Override
    public Content editPageContent(Content content, String newTitle, String newContent, ContentRepresentation contentRepresentation) {
        Version version = content.getVersion().nextBuilder().build();
        Content editedContent = Content.builder(content).title(newTitle).body(newContent, contentRepresentation).version(version).build();
        return confluenceRestSession.contentService().update(editedContent);
    }

    @Override
    public Content createChildPage(String title, String content, ContentRepresentation contentRepresentation, Content parent) {
        return confluenceRestSession.contentService().create(
                Content.builder(ContentType.PAGE)
                        .space(parent.getSpace())
                        .parent(parent)
                        .title(title)
                        .body(content, contentRepresentation)
                        .build()
        );
    }

    @Override
    public List<Label> createLabels(String labels, String separator) {
        List<Label> listLabels = new ArrayList<>();

        for (String label : labels.split(separator)) {
            listLabels.add(Label.builder(label).build());
        }
        return listLabels;
    }

    @Override
    public void applyLabelsToContent(Content content, String labels) {
        List<Label> listLabels = createLabels(labels, SPACE);
        confluenceRestSession.contentLabelService().addLabels(content.getId(), listLabels);
    }

    @Override
    public void removeContent(Content content) {
        restClient.getAdminSession().contentService().delete(content);
    }

    @Override
    public Content getViewPage(Content page) {
        waitForIndexingToComplete();
        return confluenceRestSession.contentService()
                .find(ExpansionsParser.parse(Content.Expansions.BODY + VIEW_EXTENSION + "," + Content.Expansions.VERSION))
                .withStatus(ContentStatus.CURRENT)
                .withId(page.getId())
                .fetchOneOrNull();
    }

    @Override
    public Content getViewPageOfVersion(Content page, Integer version) {
        waitForIndexingToComplete();
        return confluenceRestSession.contentService()
                .find(ExpansionsParser.parse(Content.Expansions.BODY + VIEW_EXTENSION))
                .withStatus(ContentStatus.HISTORICAL)
                .withIdAndVersion(page.getId(), version)
                .fetchOneOrNull();
    }

    @Override
    public Content getPageByPageTitle(String pageTitle) {
        waitForIndexingToComplete();
        return confluenceRestSession.contentService()
                .find(ExpansionsParser.parse(Content.Expansions.BODY + VIEW_EXTENSION))
                .withLocator(contentLocatorBuilder.forPage().bySpaceKeyAndTitle(defaultSpace.get().getKey(), pageTitle))
                .fetchOneOrNull();
    }

    @Override
    public Content getViewPageAsAnonymous(Content page) {
        waitForIndexingToComplete();
        return confluenceAnonymousRestSession.contentService()
                .find(ExpansionsParser.parse(Content.Expansions.BODY + VIEW_EXTENSION))
                .withStatus(ContentStatus.CURRENT)
                .withId(page.getId())
                .fetchOneOrNull();
    }

    @Override
    public String getRenderedContent(Content viewPage) {
        return Jsoup.clean(getHTMLContent(viewPage), "", Whitelist.none(), new Document.OutputSettings().prettyPrint(false));
    }

    @Override
    public String getHTMLContent(Content viewPage) {
        return viewPage.getBody().get(ContentRepresentation.VIEW).getValue();
    }

    @Override
    public List<Space> createSpaces(Integer noOfSpaces) {
        String spaceKey;
        String spaceTitle;
        List<Space> spaces = new ArrayList<>();

        for (int i = 0; i < noOfSpaces; i++) {
            spaceKey = randomAlphanumeric(5);
            spaceTitle = randomAlphanumeric(10);

            spaces.add(createSpace(spaceKey, spaceTitle));
        }

        return spaces;
    }

    @Override
    public Space createSpace(String key, String name) {
        return confluenceRestSession.spaceService().create(
                Space.builder()
                        .type(SpaceType.PERSONAL)
                        .key(key)
                        .name(name)
                        .build()
                , false);
    }

    @Override
    public void removeSpaces(List<Space> spaces) {
        for (Space space : spaces) {
            removeSpace(space);
        }
    }

    @Override
    public void removeSpace(Space space) {
        confluenceRestSession.spaceService().delete(space);
    }

    @Override
    public void applyLabelToSpace(Space space, String label) {
        rpcClient.getAdminSession().addLabelToSpace(label, space);
    }

    @Override
    public Integer getSpaceCount() {
        return confluenceRestSession.spaceService()
                .find(ExpansionsParser.parse(Content.Expansions.SPACE))
                .fetchMany(new SimplePageRequest(SPACE_COUNT_START_INDEX, SPACE_COUNT_LIMIT))
                .size();
    }

    @Override
    public List<AttachmentUpload> createAttachments(String filePaths, String separator) {
        List<AttachmentUpload> listAttachments = new ArrayList<>();
        AttachmentUpload attachmentUpload;

        for (String filePath : filePaths.split(separator)) {
            File file = new File(filePath);
            attachmentUpload = new AttachmentUpload(file, file.getName(), EMPTY, EMPTY, false);

            listAttachments.add(attachmentUpload);
        }
        return listAttachments;
    }

    @Override
    public Content addAttachmentToPage(Content page, String filePaths) {
        List<AttachmentUpload> listAttachments = createAttachments(filePaths, COMMA_SEPARATOR);
        return confluenceRestSession.attachmentService().addAttachments(page.getId(), listAttachments).getResults().get(0);
    }

    @Override
    public void waitForIndexingToComplete() {
        rpcClient.getAdminSession().getSystemComponent().flushIndexQueue();
    }

    @Override
    public String convertWikiToStorage(String wiki) {
        return contentBodyConversionService.convert(
                new ContentBody(ContentRepresentation.WIKI, wiki), ContentRepresentation.STORAGE
        ).getValue();
    }

    @Override
    public void visitEditContentPage(Content content) {
        waitForIndexingToComplete();
        editContentPage = product.visit(EditContentPage.class, content);
    }

    @Override
    public void bindViewPage() {
        waitForIndexingToComplete();
        viewPage = pageBinder.bind(ViewPage.class);
    }
}