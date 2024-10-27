package com.mysite.core.servlets;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.servlet.Servlet;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component(service = Servlet.class, property = {
        "sling.servlet.methods=GET", // Servlet will respond to GET requests
        "sling.servlet.paths=/bin/pageviews" // Access servlet at this path
})
public class PageViewsServlet extends SlingAllMethodsServlet {

    // Inject ResourceResolverFactory
    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    private static final String SERVICE_USER = "system_user1"; // The system user mapped

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
        String pathparameter = request.getParameter("path");
        String nodePath = pathparameter + "/jcr:content"; // The path of the node to read
        nodePath = nodePath.strip();

        // Get the service ResourceResolver using system user
        Map<String, Object> param = new HashMap<>();
        param.put(ResourceResolverFactory.SUBSERVICE, SERVICE_USER);

        ResourceResolver resourceResolver = null;
        try {
            // Get a ResourceResolver tied to the service user
            resourceResolver = resourceResolverFactory.getServiceResourceResolver(param);

            // Get the resource (node) from the repository
            Resource resource = resourceResolver.getResource(nodePath);
            response.getWriter().write(nodePath);

            if (resource != null) {
                Node node = resource.adaptTo(Node.class);
                if (node != null) {
                    // Read the property value

                    if (node.hasProperty("pageViews")) {
                        long propertyValue = node.getProperty("pageViews").getLong();
                        long inccount = propertyValue + 1;
                        node.setProperty("pageViews", inccount);
                        try {
                            node.getSession().save();
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        } // Save changes to JCR
                        response.getWriter().write("node getpath: " + node.getPath());
                        response.getWriter().write("Property Updated to: " + inccount);
                        // Output the property value in the response
                        response.getWriter().write("Property Value: " + propertyValue + "increased valvue=" + inccount);
                    } else {
                        node.setProperty("pageViews", 1);
                        try {
                            node.getSession().save();
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        response.getWriter().write("Property not found.");
                    }

                } else {
                    response.getWriter().write("Node not found.");
                }
            } else {
                response.getWriter().write("Resource not found.");
            }
        } catch (Exception e) {
            response.getWriter().write("Error: " + e.getMessage());
        } finally {
            // Always close the ResourceResolver to avoid memory leaks
            if (resourceResolver != null && resourceResolver.isLive()) {
                resourceResolver.close();
            }
        }
    }
}
