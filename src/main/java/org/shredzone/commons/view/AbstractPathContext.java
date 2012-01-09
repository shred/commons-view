/*
 * Shredzone Commons
 *
 * Copyright (C) 2012 Richard "Shred" Körber
 *   http://commons.shredzone.org
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Library General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Library General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.shredzone.commons.view;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * An abstract implementation of {@link PathContext}. Extending classes need to offer
 * getters and setters for their properties. If a setter is used, the property name needs
 * to be passed to {@link #addProperty(String)}. In return, this class will take care
 * for creating a {@link Signature} instance.
 *
 * @author Richard "Shred" Körber
 */
public abstract class AbstractPathContext implements PathContext {

    private final Set<String> propSet = new HashSet<String>();
    private final Map<String, Object> variables = new HashMap<String, Object>();

    @Override
    public Signature getSignature() {
        return new Signature(propSet);
    }

    @Override
    public Map<String, Object> getVariables() {
        return variables;
    }

    /**
     * Sets a variable.
     *
     * @param name
     *            Variable name
     * @param value
     *            Variable value
     */
    public void setVariable(String name, Object value) {
        addProperty('#' + name);
        variables.put(name, value);
    }

    /**
     * Adds a property name to the signature. Subclasses must pass the property name to
     * this method for every relevant property setter that was invoked. It is safe to call
     * this method with the same property name multiple times.
     *
     * @param propName
     *            name of the property that was set.
     */
    protected void addProperty(String propName) {
        propSet.add(propName);
    }

}
