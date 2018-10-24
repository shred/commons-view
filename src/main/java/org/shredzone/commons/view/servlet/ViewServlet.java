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

package org.shredzone.commons.view.servlet;

import java.util.concurrent.atomic.AtomicReference;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.shredzone.commons.view.ViewService;
import org.shredzone.commons.view.exception.ViewException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.FrameworkServlet;

/**
 * Main servlet for handling all kind of view requests.
 *
 * @author Richard "Shred" Körber
 */
public class ViewServlet extends FrameworkServlet {
    private static final long serialVersionUID = 6193053466721043404L;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final AtomicReference<ViewService> viewService = new AtomicReference<>();

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        getServletContext().setAttribute("jspPath", getJspPath(config));
    }

    @Override
    protected void doService(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        try {
            getViewService().handleRequest(req, resp);
        } catch (ViewException ex) {
            logger.error("Failed to handle request", ex);
            if (!resp.isCommitted()) {
                resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ex.getMessage()); //NOSONAR
            }
        }
    }

    /**
     * Returns the {@link ViewService} singleton.
     */
    private ViewService getViewService() {
        ViewService result = viewService.get();
        if (result == null) {
            result = getWebApplicationContext().getBean(ViewService.class);
            viewService.compareAndSet(null, result);
        }
        return result;
    }

    /**
     * Gets the JSP path from the servlet configuration. The default implementation
     * fetches the value from the servlet's "jspPath" init parameter. Extending classes
     * may override this method to fetch the configuration from somewhere else.
     *
     * @param config
     *            {@link ServletConfig}
     * @return JSP path, must end with a trailing '/'
     */
    protected String getJspPath(ServletConfig config) {
        String jspPath = "/";
        String path = config.getInitParameter("jspPath");
        if (path != null) {
            if (!path.endsWith("/")) {
                path += "/";
            }
            jspPath = path;
        }
        return jspPath;
    }

}
