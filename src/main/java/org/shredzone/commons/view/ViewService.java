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

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.shredzone.commons.view.exception.PageNotFoundException;
import org.shredzone.commons.view.exception.ViewException;

/**
 * Service for rendering views, creating links to views, and handling servlet requests.
 * <p>
 * <em>Note:</em> A servlet delegates a request to
 * {@link #handleRequest(HttpServletRequest, HttpServletResponse)}. The other methods are
 * request scoped and must only be invoked from the same thread that invoked
 * {@link #handleRequest(HttpServletRequest, HttpServletResponse) handleRequest()}.
 *
 * @author Richard "Shred" Körber
 */
@ParametersAreNonnullByDefault
public interface ViewService {

    /**
     * Handles a HTTP request. This method is usually invoked from a servlet.
     *
     * @param req
     *            {@link HttpServletRequest}
     * @param resp
     *            {@link HttpServletResponse}
     * @throws ViewException
     *             if the request could not be handled
     */
    void handleRequest(HttpServletRequest req, HttpServletResponse resp) throws ViewException;

    /**
     * Gets the {@link ViewContext} of the current request.
     *
     * @return {@link ViewContext}
     */
    ViewContext getViewContext();

    /**
     * Builds an URL path to a view that is able to render the provided data.
     *
     * @param context
     *            {@link PathContext} containing all the data for the path
     * @param view
     *            name of the view to build a path to
     * @param type
     *            {@link PathType} to be built
     * @return the URL path that was built, or {@code null} if no view was found to be
     *         able to render the provided data
     */
    String buildPath(PathContext context, String view, PathType type);

    /**
     * Analyzes the given path, and invokes a view handler for processing the request.
     *
     * @param path
     *            the requested path
     * @return String returned by the view handler, usually the name of a JSP template to
     *         forward the request to. May be {@code null} if the handler already took
     *         care for the response itself.
     * @throws PageNotFoundException
     *             if no view was matching the given path
     * @throws ViewException
     *             if the view handler could not be invoked or could not handle the
     *             request
     */
    String invokeView(String path) throws ViewException;

    /**
     * Gets the path to a template resource with the given name. It is not checked if the
     * template actually exists.
     *
     * @param template
     *            Template name
     * @return Template path
     */
    @Nonnull String getTemplatePath(String template);

}
