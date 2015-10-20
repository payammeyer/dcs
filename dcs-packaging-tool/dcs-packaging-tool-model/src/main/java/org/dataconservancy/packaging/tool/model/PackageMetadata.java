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
package org.dataconservancy.packaging.tool.model;

/**
 * This class captures information necessary to the package tool to know what and how prompt user for specific package
 * metadata.
 */
public class PackageMetadata {

    public enum ValidationType {
        NONE,
        PHONE,
        EMAIL,
        URL,
        DATE,
        FILENAME,
    };

    private String name;
    private ValidationType validationType;
    private String helpText;
    private int minOccurrence;
    private int maxOccurrence;
    private boolean isEditable;

    /**
     * Indicates whether the field is editable.
     * @return {@code true} if the field is editable by user.
     * @return {@code false} if the field is not editable by user.
     */
    public boolean isEditable() {
        return isEditable;
    }

    public void setEditable(boolean isEditable) {
        this.isEditable = isEditable;
    }

    /**
     * Returns the type of validation ({@link org.dataconservancy.packaging.tool.model.PackageMetadata.ValidationType})
     * that should be performed on the field's value. These types include: {@code NONE}, {@code EMAIL}, {@code PHONE},
     * {@code DATE}, {@code URL}, {@code FILENAME}.
     */
    public ValidationType getValidationType() {
        return validationType;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setValidationType(ValidationType validationType) {
        this.validationType = validationType;
    }

    /**
     * Returns the name of the metadata field.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the help text associated with the metadata field.
     */
    public String getHelpText() {
        return helpText;
    }

    public void setHelpText(String helpText) {
        this.helpText = helpText;
    }

    /**
     * Returns the minimum number of times the field should occur on the GUI form.
     */
    public int getMinOccurrence() {
        return minOccurrence;
    }

    public void setMinOccurrence(int minOccurrence) {
        this.minOccurrence = minOccurrence;
    }

    /**
     * Returns maximum number of times the fields should occur on the GUI form.
     * @return
     */
    public int getMaxOccurrence() {
        return maxOccurrence;
    }

    public void setMaxOccurrence(int maxOccurrence) {
        this.maxOccurrence = maxOccurrence;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null) return false;
        if (!(o instanceof PackageMetadata)) return false;

        PackageMetadata that = (PackageMetadata) o;

        if (isEditable != that.isEditable) return false;
        if (maxOccurrence != that.maxOccurrence) return false;
        if (minOccurrence != that.minOccurrence) return false;
        if (helpText != null ? !helpText.equals(that.helpText) : that.helpText != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (validationType != that.validationType) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (validationType != null ? validationType.hashCode() : 0);
        result = 31 * result + (helpText != null ? helpText.hashCode() : 0);
        result = 31 * result + minOccurrence;
        result = 31 * result + maxOccurrence;
        result = 31 * result + (isEditable ? 1 : 0);
        return result;
    }
}