package net.customware.atlassian.menu.model;

import java.util.Collection;

public interface FragmentContainer {
    Collection<? extends Fragment> getFragments();
}
