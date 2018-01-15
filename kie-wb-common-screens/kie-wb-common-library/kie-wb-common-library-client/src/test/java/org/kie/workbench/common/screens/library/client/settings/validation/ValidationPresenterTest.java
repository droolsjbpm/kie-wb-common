package org.kie.workbench.common.screens.library.client.settings.validation;

import javax.enterprise.event.Event;

import elemental2.promise.Promise;
import org.guvnor.common.services.project.model.ProjectRepositories;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.library.client.settings.Promises;
import org.kie.workbench.common.screens.library.client.settings.SettingsPresenter;
import org.kie.workbench.common.screens.library.client.settings.SettingsSectionChange;
import org.kie.workbench.common.screens.library.client.settings.SyncPromises;
import org.kie.workbench.common.screens.projecteditor.model.ProjectScreenModel;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ValidationPresenterTest {

    private ValidationPresenter validationPresenter;

    @Mock
    private ValidationView view;

    @Mock
    private SettingsPresenter.MenuItem menuItem;

    @Mock
    private Event<SettingsSectionChange> settingsSectionChangeEvent;

    @Mock
    private ValidationPresenter.ValidationListPresenter validationItemPresenters;

    private final Promises promises = new SyncPromises();

    @Before
    public void before() {

        validationPresenter = spy(new ValidationPresenter(view,
                                                          promises,
                                                          menuItem,
                                                          settingsSectionChangeEvent,
                                                          validationItemPresenters));
    }

    @Test
    public void testSetup() {

        final Promise<Void> result = validationPresenter.setup(mock(ProjectScreenModel.class));

        assertPromiseStatusEquals(result, SyncPromises.Status.RESOLVED);

        verify(view).init(eq(validationPresenter));
        verify(validationItemPresenters).setup(any(), any(), any());
    }

    @Test
    public void testCurrentHashCode() {

        final ProjectScreenModel model = mock(ProjectScreenModel.class);
        final ProjectRepositories repositories = new ProjectRepositories();
        doReturn(repositories).when(model).getRepositories();

        validationPresenter.setup(model);

        int currentHashCode = validationPresenter.currentHashCode();
        Assert.assertEquals(repositories.getRepositories().hashCode(), currentHashCode);

        repositories.getRepositories().add(mock(ProjectRepositories.ProjectRepository.class));
        int updatedHashCode = validationPresenter.currentHashCode();
        Assert.assertEquals(repositories.getRepositories().hashCode(), updatedHashCode);
    }

    private static void assertPromiseStatusEquals(final Promise<Void> promise,
                                                  final SyncPromises.Status status) {

        assertTrue(promise instanceof SyncPromises.SyncPromise);
        assertEquals(status, ((SyncPromises.SyncPromise<?>) promise).status);
    }
}