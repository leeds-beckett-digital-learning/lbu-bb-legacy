/*
 * Copyright 2022 Leeds Beckett University.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.ac.leedsbeckett.lbubblegacy;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author jon
 */
public abstract class AbstractServlet extends HttpServlet
{
  WebAppCore core;
  
  
  /**
   * Get a reference to the right instance of WebAppCore from an attribute which
 that instance put in the servlet context.
  */
  @Override
  public void init() throws ServletException
  {
    super.init();
    core = (WebAppCore)getServletContext().getAttribute(WebAppCore.ATTRIBUTE_CONTEXTBBMONITOR );
  }
  
  public void sendError( HttpServletRequest req, HttpServletResponse resp, String error ) throws ServletException, IOException
  {
    resp.setContentType("text/html");
    try ( ServletOutputStream out = resp.getOutputStream(); )
    {
      out.println( "<!DOCTYPE html>\n<html>" );
      out.println( "<head>" );
      out.println( "<style type=\"text/css\">" );
      out.println( "body, p, h1, h2 { font-family: sans-serif; }" );
      out.println( "</style>" );
      out.println( "</head>" );
      out.println( "<body><p>" );
      out.println( error );
      out.println( "</p></body></html>" );
    }  
  }

}
