package ddnet.useragent.util;

import java.security.Permission;

public class SecurityManager extends java.lang.SecurityManager {
	@Override
	public void checkPermission(Permission permission) {
		return;
	}
}