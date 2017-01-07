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

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.shredzone.commons.view.ViewService;
import org.shredzone.commons.view.exception.ViewException;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.web.servlet.FrameworkServlet;

/**
 * Main servlet for handling all kind of view requests.
 *
 * @author Richard "Shred" Körber
 */
public class ViewServlet extends FrameworkServlet {
    private static final long serialVersionUID = 6193053466721043404L;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        getServletContext().setAttribute("jspPath", getJspPath(config));
    }

    @Override
    protected void doService(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        try {
            ViewService service = getWebApplicationContext().getBean(ViewService.class);
            service.handleRequest(req, resp);
        } catch (ViewException | BeansException ex) {
            LoggerFactory.getLogger(ViewServlet.class).error("Failed to handle request", ex);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ex.getMessage()); //NOSONAR
        }
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
