package com.fast.fastrpc.common.utils;

import com.fast.fastrpc.common.Constants;
import com.fast.fastrpc.common.URL;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author yiji
 * @version : UrlUtils.java, v 0.1 2020-09-18
 */
public class UrlUtils {

    public static boolean isMatch(URL consumerUrl, URL providerUrl) {
        // todo
        return true;
    }

    public static List<URL> parseURLs(String address, Map<String, String> defaults) {
        if (address == null || address.length() == 0) {
            return null;
        }
        String[] addresses = Constants.REGISTRY_SPLIT_PATTERN.split(address);
        if (addresses == null || addresses.length == 0) {
            return null;
        }
        List<URL> registries = new ArrayList<>();
        for (String addr : addresses) {
            registries.add(parseURL(addr, defaults));
        }
        return registries;
    }

    public static URL parseURL(String address, Map<String, String> defaults) {
        if (address == null || address.length() == 0) {
            return null;
        }
        String url;
        if (address.indexOf("://") >= 0) {
            url = address;
        } else {
            String[] addresses = Constants.COMMA_SPLIT_PATTERN.split(address);
            url = addresses[0];
            if (addresses.length > 1) {
                StringBuilder backup = new StringBuilder();
                for (int i = 1; i < addresses.length; i++) {
                    if (i > 1) {
                        backup.append(",");
                    }
                    backup.append(addresses[i]);
                }
                url += "?" + Constants.BACKUP_KEY + "=" + backup.toString();
            }
        }
        String defaultProtocol = defaults == null ? null : defaults.get("protocol");
        if (StringUtils.isEmpty(defaultProtocol)) {
            defaultProtocol = "fast";
        }
        String defaultUsername = defaults == null ? null : defaults.get("username");
        String defaultPassword = defaults == null ? null : defaults.get("password");
        int defaultPort = StringUtils.parseInteger(defaults == null ? null : defaults.get("port"));
        String defaultPath = defaults == null ? null : defaults.get("path");
        Map<String, String> defaultParameters = defaults == null ? null : new HashMap<>(defaults);
        if (defaultParameters != null) {
            defaultParameters.remove("protocol");
            defaultParameters.remove("username");
            defaultParameters.remove("password");
            defaultParameters.remove("host");
            defaultParameters.remove("port");
            defaultParameters.remove("path");
        }
        URL u = URL.valueOf(url);
        boolean changed = false;
        String protocol = u.getProtocol();
        String username = u.getUsername();
        String password = u.getPassword();
        String host = u.getHost();
        int port = u.getPort();
        String path = u.getPath();
        Map<String, String> parameters = new HashMap<>(u.getParameters());
        if (StringUtils.isEmpty(protocol) && StringUtils.isNotEmpty(defaultProtocol)) {
            changed = true;
            protocol = defaultProtocol;
        }
        if (StringUtils.isEmpty(username) && StringUtils.isNotEmpty(defaultUsername)) {
            changed = true;
            username = defaultUsername;
        }
        if (StringUtils.isEmpty(password) && StringUtils.isNotEmpty(defaultPassword)) {
            changed = true;
            password = defaultPassword;
        }
        if (port <= 0 && defaultPort > 0) {
            changed = true;
            port = defaultPort;
        }
        if (StringUtils.isEmpty(path) && (StringUtils.isNotEmpty(defaultPath))) {
            changed = true;
            path = defaultPath;
        }
        if (defaultParameters != null && !defaultParameters.isEmpty()) {
            for (Map.Entry<String, String> entry : defaultParameters.entrySet()) {
                String key = entry.getKey();
                String defaultValue = entry.getValue();
                if (defaultValue != null && defaultValue.length() > 0) {
                    String value = parameters.get(key);
                    if (value == null || value.length() == 0) {
                        changed = true;
                        parameters.put(key, defaultValue);
                    }
                }
            }
        }
        if (changed) {
            u = new URL(protocol, username, password, host, port, path, parameters);
        }
        return u;
    }

}
