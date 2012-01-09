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

import org.shredzone.commons.view.exception.ViewContextException;

/**
 * Provides a context of the View that is to be rendered in this request.
 * <p>
 * A {@link ViewContext} is request scoped and only valid in threads that ran
 * {@link ViewService#handleRequest(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)}.
 *
 * @author Richard "Shred" Körber
 */
public interface ViewContext {

    /**
     * Returns the server URL the request was sent to. The URL contains the protocol, the
     * domain and (optionally) the port.
     *
     * @return the server URL (e.g. "http://www.shredzone.de")
     */
    String getRequestServerUrl();

    /**
     * Returns the name of the servlet that processed the request.
     *
     * @return the servlet name (e.g. "/cilla")
     */
    String getRequestServletName();

    /**
     * Adds a typed argument to this context. Typed arguments are available to the view
     * handler's parameter list.
     *
     * @param type
     *            Type this argument is to be registered with (may be the value's type or
     *            a supertype)
     * @param value
     *            the value to register, may be {@code null}
     */
    <T> void putTypedArgument(Class<T> type, T value);

    /**
     * Gets a value that matches the requested type.
     *
     * @param <T>
     *            the requested type
     * @param type
     *            type to get a value for
     * @return a value for that type, may be {@code null} if the type's value was set to
     *         {@code null}.
     * @throws ViewContextException
     *             if there was no data satisfying that type
     */
    <T> T getValueOfType(Class<T> type) throws ViewContextException;

    /**
     * Sets the path parts from resolving the view URL. Should only be invoked from the
     * {@link ViewService}.
     *
     * @param pathParts
     *            Map of path parts
     */
    void setPathParts(Map<String, String> pathParts);

    /**
     * Gets a map of all path parts. This map is immutable.
     *
     * @return Map of path parts
     */
    Map<String, String> getPathParts();

    /**
     * Gets a parameter from the request URL's search part.
     *
     * @param name
     *            the parameter name
     * @return parameter value, or {@code null} if there is no such parameter
     */
    String getParameter(String name);

}
