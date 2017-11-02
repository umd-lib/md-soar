/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.xmlui.aspect.discovery.json;

import org.apache.avalon.excalibur.pool.Recyclable;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.cocoon.ProcessingException;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Request;
import org.apache.cocoon.environment.Response;
import org.apache.cocoon.environment.SourceResolver;
import org.apache.cocoon.reading.AbstractReader;
import org.apache.log4j.Logger;
import org.dspace.app.xmlui.utils.ContextUtil;
import org.dspace.app.xmlui.utils.HandleUtil;
import org.dspace.content.DSpaceObject;
import org.dspace.core.Context;
import org.dspace.discovery.*;
import org.dspace.discovery.configuration.DiscoveryConfigurationParameters;
import org.dspace.handle.factory.HandleServiceFactory;
import org.dspace.handle.service.HandleService;
import org.dspace.services.factory.DSpaceServicesFactory;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Map;

/**
 * Class used to search in the discovery backend and return a json formatted string
 *
 * @author Kevin Van de Velde (kevin at atmire dot com)
 * @author Mark Diggory (markd at atmire dot com)
 * @author Ben Bosman (ben at atmire dot com)
 */
public class JSONDiscoverySuggester extends AbstractReader implements Recyclable {

    private static Logger log = Logger.getLogger(JSONDiscoverySuggester.class);
    private InputStream JSONStream;
    protected HandleService handleService = HandleServiceFactory.getInstance().getHandleService();


    /** The Cocoon response */
    protected Response response;

    protected SearchService getSearchService()
    {
        return DSpaceServicesFactory.getInstance().getServiceManager().getServiceByName(SearchService.class.getName(),SearchService.class);
    }


    @Override
    public void setup(SourceResolver resolver, Map objectModel, String src, Parameters par) throws ProcessingException, SAXException, IOException {
        //Retrieve all the given parameters
        Request request = ObjectModelHelper.getRequest(objectModel);
        this.response = ObjectModelHelper.getResponse(objectModel);

        //Customization for LIBCIR-147 
        String query = request.getParameter("query");
        String field = request.getParameter("field");

        String jsonWrf = request.getParameter("json.wrf");

        try {
            Context context = ContextUtil.obtainContext(objectModel);
        
         //End Customization
            JSONStream = getSearchService().suggestJSON(context, query, field, jsonWrf);
        } catch (Exception e) {
            log.error("Error while retrieving JSON string for Discovery auto complete", e);
        }

    }

    public void generate() throws IOException, SAXException, ProcessingException {
        if(JSONStream != null){
            byte[] buffer = new byte[8192];

            response.setHeader("Content-Length", String.valueOf(JSONStream.available()));
            int length;
            while ((length = JSONStream.read(buffer)) > -1)
            {
                out.write(buffer, 0, length);
            }
        }
        out.flush();
        
    }

    /**
     * Determine the current scope. This may be derived from the current url
     * handle if present or the scope parameter is given. If no scope is
     * specified then null is returned.
     *
     * @param context the dspace context
     * @return The current scope.
     */
    private DSpaceObject getScope(Context context, Map objectModel) throws SQLException {
        Request request = ObjectModelHelper.getRequest(objectModel);
        String scopeString = request.getParameter("scope");

        // Are we in a community or collection?
        DSpaceObject dso;
        if (scopeString == null || "".equals(scopeString))
        {
            // get the search scope from the url handle
            dso = HandleUtil.obtainHandle(objectModel);
        }
        else
        {
            // Get the search scope from the location parameter
            dso = handleService.resolveToObject(context, scopeString);
        }

        return dso;
    }

    @Override
    public void recycle() {
        response = null;
        JSONStream = null;
        super.recycle();
    }

}
