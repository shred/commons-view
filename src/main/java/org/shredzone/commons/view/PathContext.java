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

import java.util.Map;

/**
 * Context that holds all data required for building a path.
 *
 * @author Richard "Shred" Körber
 */
public interface PathContext {

    /**
     * Gets a {@link Signature} for this {@link PathContext}.
     *
     * @return {@link Signature} object
     */
    Signature getSignature();

    /**
     * Gets a map of variables to be used.
     *
     * @return map of variables
     */
    Map<String, Object> getVariables();

}
