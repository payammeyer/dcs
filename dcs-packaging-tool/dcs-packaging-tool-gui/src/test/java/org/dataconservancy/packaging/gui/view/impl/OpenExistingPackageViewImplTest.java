/*
 * Copyright 2015 Johns Hopkins University
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
package org.dataconservancy.packaging.gui.view.impl;

import org.dataconservancy.packaging.gui.BaseGuiTest;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

/**
 * Tests that open existing package view impl initializes all fields correctly.
 */
public class OpenExistingPackageViewImplTest extends BaseGuiTest {

    private OpenExistingPackageViewImpl view;

    @Before
    public void setup() {
        view = new OpenExistingPackageViewImpl(labels);
    }

    /**
     * Make sure controls can be retrieved.
     */
    @Test
    public void testComponentsNotNull() {
        assertNotNull(view.getChooseInProgressPackageFileTextField());
        assertNotNull(view.getChooseInProgressPackageFileButton());
        assertNotNull(view.getChoosePackageFileTextField());
        assertNotNull(view.getChoosePackageFileButton());
        assertNotNull(view.getChoosePackageDirectoryTextField());
        assertNotNull(view.getChoosePackageDirectoryButton());
        assertNotNull(view.getContinueButton());
        assertNotNull(view.getErrorMessage());
    }


}
