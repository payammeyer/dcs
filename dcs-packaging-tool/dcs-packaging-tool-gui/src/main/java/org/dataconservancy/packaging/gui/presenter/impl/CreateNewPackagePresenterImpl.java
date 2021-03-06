/*
 * Copyright 2014 Johns Hopkins University
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.dataconservancy.packaging.gui.presenter.impl;

import java.io.File;
import java.io.InputStream;

import java.util.Map;

import javafx.concurrent.Service;
import javafx.concurrent.Task;

import org.dataconservancy.packaging.gui.Errors.ErrorKey;
import org.dataconservancy.packaging.gui.presenter.CreateNewPackagePresenter;
import org.dataconservancy.packaging.gui.util.ProgressDialogPopup;
import org.dataconservancy.packaging.gui.view.CreateNewPackageView;
import org.dataconservancy.packaging.tool.api.PackageDescriptionCreator;
import org.dataconservancy.packaging.tool.api.PackageDescriptionCreatorException;
import org.dataconservancy.packaging.tool.api.support.RulePropertiesManager;
import org.dataconservancy.packaging.tool.impl.GeneralPackageDescriptionCreator;
import org.dataconservancy.packaging.tool.impl.support.SystemPropertyPreferencesRulePropertiesManager;
import org.dataconservancy.packaging.tool.model.DcsPackageDescriptionSpec;
import org.dataconservancy.packaging.tool.model.PackageDescription;
import org.dataconservancy.packaging.tool.model.PackageDescriptionBuilder;
import org.dataconservancy.packaging.tool.model.PackageDescriptionRulesBuilder;
import org.dataconservancy.packaging.tool.model.builder.xstream.JaxbPackageDescriptionRulesBuilder;
import org.dataconservancy.packaging.tool.model.description.RulesSpec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.stage.FileChooser;
import javafx.scene.Node;
import javafx.stage.DirectoryChooser;

/**
 * The implementation for the presenter that will handle the creation of a new package either from a content directory,
 * or an existing package description file.
 */
public class CreateNewPackagePresenterImpl extends BasePresenterImpl
        implements CreateNewPackagePresenter {

    private CreateNewPackageView view;

    private File content_dir;
    private File root_artifact_dir; //has content_dir as parent

    private PackageDescriptionCreator creator;
    private PackageDescriptionBuilder packageDescriptionBuilder;
    private DirectoryChooser directoryChooser;
    private FileChooser fileChooser;
    private RulePropertiesManager ruleProperties;
    
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    public CreateNewPackagePresenterImpl(CreateNewPackageView view) {
        super(view);
        this.view = view;
        this.content_dir = null;
        directoryChooser = new DirectoryChooser();
        fileChooser = new FileChooser();

        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Package Description (*.json)", "*.json"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("All files (*.*)", "*.*"));
        
        InputStream defaultRulesStream =
                Thread.currentThread().getContextClassLoader()
                        .getResourceAsStream("rules/default-rules.xml");

        PackageDescriptionRulesBuilder builder =
                new JaxbPackageDescriptionRulesBuilder();

        RulesSpec packageDescriptionPrefs = builder.buildPackageDescriptionRules(defaultRulesStream);
        
        ruleProperties = new SystemPropertyPreferencesRulePropertiesManager();
        ruleProperties.init(packageDescriptionPrefs);

        creator = new GeneralPackageDescriptionCreator(packageDescriptionPrefs);

        view.setPresenter(this);
        view.promptForUndefinedProperties(ruleProperties);
        bind();
    }

    private void bind() {
       
        //Handles the continue button in the footer being pressed. Validates that all required fields are present, sets the state on the controller,
        //and instructs the controller to move to the next page.
        view.getContinueButton().setOnAction(arg0 -> {
            try {
                if (root_artifact_dir != null && root_artifact_dir.exists() &&
                    root_artifact_dir.canRead()) {

                    /* Insert properties, if any */
                    for (Map.Entry<String, String> property : view.getPropertyValues().entrySet()) {
                        ruleProperties.setProperty(property.getKey(), property.getValue());
                    }
                    //TODO: when we support multiple ontologies we will need to adjust the handling of the user's
                    // choice of ontology identifier instead of hardcoded value here

                    final PackageDescriptionServiceWorker packageDescriptionService = new PackageDescriptionServiceWorker(DcsPackageDescriptionSpec.SPECIFICATION_ID, root_artifact_dir);

                    view.showProgressIndicatorPopUp();

                    ((ProgressDialogPopup)view.getProgressIndicatorPopUp()).setCancelEventHandler(event -> packageDescriptionService.cancel());

                    controller.setCrossPageProgressIndicatorPopUp(view.getProgressIndicatorPopUp());
                    controller.setContentRoot(content_dir);
                    controller.setRootArtifactDir(root_artifact_dir);

                    packageDescriptionService.setOnCancelled(event -> {
                        packageDescriptionService.reset();
                        controller.getCrossPageProgressIndicatorPopUp().hide();
                    });

                    packageDescriptionService.setOnFailed(workerStateEvent -> {
                        displayExceptionMessage(workerStateEvent.getSource().getException());
                        view.getErrorMessage().setVisible(true);
                        packageDescriptionService.reset();
                        controller.getCrossPageProgressIndicatorPopUp().hide();
                    });

                    packageDescriptionService.setOnSucceeded(workerStateEvent -> {
                        PackageDescription packageDescription = (PackageDescription) workerStateEvent.getSource().getValue();
                        controller.setPackageDescription(packageDescription);
                        controller.setPackageDescriptionFile(null);
                        packageDescriptionService.reset();
                        controller.goToNextPage();
                    });

                    packageDescriptionService.start();

                } else if (root_artifact_dir != null &&
                        (!root_artifact_dir.exists() ||
                             !root_artifact_dir.canRead())) {
                    view.getErrorMessage().setText(errors.get(ErrorKey.INACCESSIBLE_CONTENT_DIR));
                    view.getErrorMessage().setVisible(true);
                } else if (controller.getPackageDescription() != null) {
                    controller.goToNextPage();
                } else {
                    view.getErrorMessage().setText(errors.get(ErrorKey.BASE_DIRECTORY_OR_DESCRIPTION_NOT_SELECTED));
                    view.getErrorMessage().setVisible(true);
                }
            } catch (Exception e) {
                view.getErrorMessage().setText(messages.formatErrorCreatingNewPackage(e.getMessage()));
                view.getErrorMessage().setVisible(true);
                log.error(e.getMessage());
            }
        });

        //Handles the user pressing the button to choose a base directory to create a package from.
        view.getChooseContentDirectoryButton()
                .setOnAction(event -> {
                    if (directoryChooser.getInitialDirectory() != null &&
                        !directoryChooser.getInitialDirectory().exists()) {
                        directoryChooser.setInitialDirectory(null);
                    }

                    File dir = controller.showOpenDirectoryDialog(directoryChooser);

                    if (dir != null) {
                        root_artifact_dir = dir;
                        content_dir = root_artifact_dir.getParentFile();
                        view.getChooseContentDirectoryTextField().setText(root_artifact_dir.getPath());
                        //view.getSelectedPackageDescriptionTextField().setText("");
                        //If the error message happens to be visible erase it.
                        view.getErrorMessage().setVisible(false);
                        directoryChooser.setInitialDirectory(dir);
                    }
                });

    }

    @Override
    public void clear() {
        view.getChooseContentDirectoryTextField().setText("");
        view.getErrorMessage().setText("");

        content_dir = null;
        root_artifact_dir = null;
    }

    public Node display() {
        //Setup help content and then rebind the base class to this view.
        view.setupHelp();
        setView(view);
        super.bindBaseElements();
        
        return view.asNode();
    }

    @Override
    public void setPackageDescriptionBuilder(PackageDescriptionBuilder packageDescriptionBuilder) {
        this.packageDescriptionBuilder = packageDescriptionBuilder;
    }

    /*
     * TODO Currently the UI Doesn't allow for setting the preferences file
    private void loadPreferences() {

        try {
            InputStream rulesStream = new FileInputStream(preferences_file);

            PackageDescriptionRulesBuilder packageDescriptionBuilder =
                    new XstreamPackageDescriptionRulesBuilder(XstreamPackageDescriptionRulesBuilderFactory
                            .newInstance());

            packageDescriptionPrefs =
                    packageDescriptionBuilder.buildPackageDescriptionRules(rulesStream);

            creator =
                    new GeneralPackageDescriptionCreator(packageDescriptionPrefs);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error reading preferences file");
        }
    }*/

    protected void displayExceptionMessage(Throwable throwable) {
        String errorMessage = throwable.getMessage();
        if (throwable instanceof PackageDescriptionCreatorException
                && ((PackageDescriptionCreatorException) throwable).hasDetail()) {
            errorMessage = errorMessage + "\n" + ((PackageDescriptionCreatorException) throwable).getDetail();
        }
        view.getErrorMessage().setText(errorMessage);
        view.getErrorMessage().setVisible(true);
    }

    /**
     * A {@link javafx.concurrent.Service} which executes the {@link javafx.concurrent.Task} of obtaining a
     * {@link org.dataconservancy.packaging.tool.model.PackageDescription} given a package ontology identifier and
     * a content directory
     */
    private class PackageDescriptionServiceWorker extends Service<PackageDescription> {

        private File root_artifact_dir;
        private String packageOntologyIdentifier;

        public PackageDescriptionServiceWorker(String packageOntologyIdentifier, File root_artifact_dir) {
            this.root_artifact_dir = root_artifact_dir;
            this.packageOntologyIdentifier = packageOntologyIdentifier;
        }

        @Override
        protected Task<PackageDescription> createTask() {
            return new Task<PackageDescription>() {
                @Override
                protected PackageDescription call() throws Exception {
                    return creator.createPackageDescription(packageOntologyIdentifier, root_artifact_dir);
                }
            };
        }
    }
}
