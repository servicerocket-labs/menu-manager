package it.net.customware.confluence.plugin.menumanager;


import com.atlassian.confluence.api.model.content.*;
import com.atlassian.confluence.test.api.model.person.UserWithDetails;

import java.util.List;

/**
 * @author khailoon
 */
public interface StatelessTestRunner {

    /**
     * Create page {@link Content} in the default space
     *
     * @param title                 the title of the page
     * @param content               content of the page
     * @param contentRepresentation {@link ContentRepresentation}
     * @return the created page {@link Content}
     */
    Content createPage(String title, String content, ContentRepresentation contentRepresentation);

    /**
     * Create page {@link Content}
     *
     * @param title                 the title of the page
     * @param content               content of the page
     * @param contentRepresentation {@link ContentRepresentation}
     * @param space                 {@link Space} to create the new content
     * @return the created page {@link Content}
     */
    Content createPage(String title, String content, ContentRepresentation contentRepresentation, Space space);

    Content createPageForUser(UserWithDetails user, String title, String content, ContentRepresentation contentRepresentation);

    Content createPageForUser(UserWithDetails user, String title, String content, ContentRepresentation contentRepresentation, Space space);

    Content anonymousCreatePage(String title, String content, ContentRepresentation contentRepresentation);

    Content editPageContent(Content content, String newContent, ContentRepresentation contentRepresentation);

    Content editPageContent(Content content, String newTitle, String newContent, ContentRepresentation contentRepresentation);

    Content createChildPage(String title, String content, ContentRepresentation contentRepresentation, Content parent);

    List<Label> createLabels(String labels, String separator);

    void applyLabelsToContent(Content content, String labels);

    void removeContent(Content content);

    Content getViewPage(Content page);

    Content getViewPageOfVersion(Content page, Integer version);

    Content getPageByPageTitle(String pageTitle);

    Content getViewPageAsAnonymous(Content page);

    String getRenderedContent(Content viewPage);

    String getHTMLContent(Content viewPage);

    List<Space> createSpaces(Integer noOfSpaces);

    Space createSpace(String key, String name);

    void removeSpaces(List<Space> spaces);

    void removeSpace(Space space);

    void applyLabelToSpace(Space space, String label);

    Integer getSpaceCount();

    List<AttachmentUpload> createAttachments(String filePaths, String separator);

    Content addAttachmentToPage(Content page, String filePaths);

    void waitForIndexingToComplete();

    String convertWikiToStorage(String wiki);

    void visitEditContentPage(Content content);

    void bindViewPage();
}