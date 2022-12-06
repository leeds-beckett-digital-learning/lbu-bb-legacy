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

import blackboard.platform.intl.BbLocale;
import blackboard.platform.plugin.PlugInUtil;
import com.xythos.common.api.VirtualServer;
import com.xythos.security.api.UserBase;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Properties;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import javax.servlet.annotation.WebListener;


/**
 * This is the central object in this web application. It is instantiated once 
 * when the application starts because it is annotated as a WebListener. After
 * the servlet context is created the contextInitialized method of this class
 * is called. This object puts a reference to itself in an attribute of the
 * servlet context so that servlets can find it and interact with it.
 * 
 * @author jon
 */
@WebListener
public class WebAppCore implements ServletContextListener
{
  public final static String ATTRIBUTE_CONTEXTBBMONITOR = WebAppCore.class.getCanonicalName();
  private static StringBuilder bootstraplog = new StringBuilder();
  
  public static SimpleDateFormat dateformatforfilenames = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
  public static SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

  
  
  private final Properties defaultproperties             = new Properties();
  private BbLocale locale = new BbLocale();
  String instanceid;
  String contextpath;
  String buildingblockhandle;
  String buildingblockvid;
  String pluginid;
  boolean monitoringxythos=false;
  String serverid;  
  int filesize=100;  // in mega bytes
  String action = "none";
  File propsfile;

  VirtualServer xythosvserver;
  String primarylockfilepath;
  String secondarylockfilepath;
  String xythosprincipalid;
  UserBase xythosadminuser;  
  
  
  public Path virtualserverbase=null;
  public Path pluginsbase=null;
  public Path pluginbase=null;
  public Path logbase=null;
  public Path configbase=null;
  
  
  /**
   * The constructor just checks to see how many times it has been called.
   * This constructor is called by the servlet container.
   */
  public WebAppCore()
  {
  }

  public Path getVirtualserverbase()
  {
    return virtualserverbase;
  }


  
  
  
  /**
   * This method gets called by the servlet container after the servlet 
   * context has been set up. So, this is where BBMonitor initialises itself.
   * 
   * @param sce This servlet context event includes a reference to the servlet context.
   */
  @Override
  public void contextInitialized(ServletContextEvent sce)
  {
    WebAppCore.logToBuffer("BB plugin init");
    sce.getServletContext().setAttribute( ATTRIBUTE_CONTEXTBBMONITOR, this );
    try
    {
      serverid = InetAddress.getLocalHost().getHostName();
      WebAppCore.logToBuffer( serverid );
    }
    catch (UnknownHostException ex)
    {
      WebAppCore.logToBuffer( "Unable to find local IP address." );
      WebAppCore.logToBuffer( ex );
    }
    
    if ( !initDefaultSettings( sce ) )
      return;
    
    
    contextpath = sce.getServletContext().getContextPath();
  }


  /**
   * Default settings are built into the web application. Here they are loaded
   * and also two key entries which are used to locate folders in the BB system.
   * @param sce
   * @return 
   */
  public boolean initDefaultSettings(ServletContextEvent sce)
  {
    String strfile = sce.getServletContext().getRealPath("WEB-INF/defaultsettings.properties" );
    WebAppCore.logToBuffer("Expecting to find default properties here: " + strfile );
    File file = new File( strfile );
    if ( !file.exists() )
    {
      WebAppCore.logToBuffer("It doesn't exist - cannot start." );
      return false;
    }
    
    try ( FileReader reader = new FileReader( file ) )
    {
      defaultproperties.load(reader);
    }
    catch (Exception ex)
    {
      logToBuffer( ex );
      return false;
    }
    
    buildingblockhandle = defaultproperties.getProperty("buildingblockhandle","");
    buildingblockvid = defaultproperties.getProperty("buildingblockvendorid","");
    if ( buildingblockhandle.length() == 0 || buildingblockvid.length() == 0 )
    {
      WebAppCore.logToBuffer( "Cannot work out bb handle or vendor id so can't load configuration." );
      return false;      
    }
    pluginid = buildingblockvid + "_" + buildingblockhandle;
    
    try
    {
      configbase  = Paths.get( PlugInUtil.getConfigDirectory( buildingblockvid, buildingblockhandle ).getPath() );
      pluginbase  = configbase.getParent();      
      pluginsbase = pluginbase.getParent();      
      logbase     = pluginbase.resolve( "log" );
      Path p      = pluginbase; 
      while ( p.getNameCount() > 2 )
      {
        if ( "vi".equals( p.getParent().getFileName().toString() ) )
          break;
        p = p.getParent();
      }
      virtualserverbase = p;

      WebAppCore.logToBuffer( "virtualserverbase = " + virtualserverbase.toString() );
      WebAppCore.logToBuffer( "pluginsbase       = " + pluginsbase.toString() );
      WebAppCore.logToBuffer( "pluginbase        = " + pluginbase.toString() );
      WebAppCore.logToBuffer( "configbase        = " + configbase.toString() );
      WebAppCore.logToBuffer( "logbase           = " + logbase.toString()    );
    }
    catch ( Exception e )
    {
      WebAppCore.logToBuffer( e );      
    }
    
    return true;
  }
  
  
      
  
  /**
   * This is called when the servlet context is being shut down.
   * @param sce 
   */
  @Override
  public void contextDestroyed(ServletContextEvent sce)
  {
  }

  
  /**
   * For servlet to find out where logs are located.
   * @return Full path of this app's log folder.
   */
  public String getLogFolder()
  {
    return this.logbase.toString();
  }
  
  
  /**
   * For servlet - returns the logging text that was recorded before the
   * proper logs on file were initialised.
   * @return 
   */
  public static String getBootstrapLog()
  {
    return bootstraplog.toString();
  }


  /**
   * Logs to a string buffer while this object is initializing
   * @param s 
   */
  private static void logToBuffer( String s )
  {
    if ( bootstraplog == null )
      return;
    
    synchronized ( bootstraplog )
    {
      bootstraplog.append( s );
      bootstraplog.append( "\n" );
    }
  }

  /**
   * Logs a Throwable to the bootstrap log.
   * @param th 
   */
  private static void logToBuffer( Throwable th )
  {
    StringWriter sw = new StringWriter();
    PrintWriter pw = new PrintWriter( sw );
    th.printStackTrace( pw );
    WebAppCore.logToBuffer( sw.toString() );
    WebAppCore.logToBuffer( "\n" );
  }
  
}


