package webctclient.test;

import java.io.IOException;
import java.io.InputStream;

public class PasswordUtils {
	/** password properties file */
	static final String PASSWORD_RESOURCE = "/users.properties";
	static final String PASSWORD_SUFFIX = ".password";
	/** get user password 
	 * @throws IOException */
	public static String getPassword(String username) throws IOException  {
		java.util.Properties props = new java.util.Properties();
		InputStream is = PasswordUtils.class.getResourceAsStream(PASSWORD_RESOURCE);
		if (is==null)
			throw new IOException("Could not find resource "+PASSWORD_RESOURCE);
		props.load(is);
		String password = props.getProperty(username+PASSWORD_SUFFIX);
		if (password==null)
			throw new IOException("Password not defined for "+username+" in "+PASSWORD_RESOURCE);
		return password;
	}

}
