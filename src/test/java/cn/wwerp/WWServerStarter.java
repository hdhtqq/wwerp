package cn.wwerp;

import java.io.FileInputStream;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;
import org.eclipse.jetty.xml.XmlConfiguration;

public class WWServerStarter {
	
	public static void main(String[] args)throws Exception {
		try {
			String file = WWServerStarter.class.getResource("").getFile();
			int pos = file.indexOf("/target/");
			String baseDir = file.substring(0, pos);
			String classesDir = baseDir + "/target/classes/";
			String appDir = baseDir + "/src/main/webapp/";
            System.out.println("baseDir:" + baseDir + ", appPath:" + appDir + ", classesDir:" + classesDir);

            XmlConfiguration configuration = new XmlConfiguration(new FileInputStream(classesDir + "jetty.xml"));
    	    Server server = (Server)configuration.configure();
     
            WebAppContext context = new WebAppContext();
//            context.setDescriptor(webXml);
            context.setResourceBase(appDir);
            context.setContextPath("/");
            context.setParentLoaderPriority(true);
     
            server.setHandler(context);
        	server.start();	
        	System.out.println("JettyServer ready!");
        	server.join();
		} catch (Exception e) {
			System.out.println("JettyServer start error!");
			e.printStackTrace();
		}
	}

}
