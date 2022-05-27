package com.ptc.gateway.security.auth;

import com.google.common.io.CharStreams;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.util.AntPathMatcher;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author cxsz
 */
@Slf4j
public class PcAuthManager {

    private static PcAuthManager ourInstance = new PcAuthManager();

    private static final String ANONYMOUS_PATH_KEY = "anonymous";

    private static final String AUTH_PATH_KEY = "auth";

    private AntPathMatcher antPathMatcher = new AntPathMatcher();

    public static PcAuthManager getInstance() {
        return ourInstance;
    }

    private PcAuthManager() {
        initAccessList();
    }

    private Set<String> anonymousPath = new HashSet<>();

    private Map<String, List<String>> authPath = new HashMap<>();

    private void initAccessList() {
        try {
            Resource resource = new ClassPathResource("/auth/pc.xml");
            String str = CharStreams.toString(new InputStreamReader(resource.getInputStream()));
            Document doc = Jsoup.parse(str);
            Elements elems = doc.select(ANONYMOUS_PATH_KEY).select("path");
            anonymousPath.addAll(elems.stream().map(e -> e.attr("url")).collect(Collectors.toList()));

            doc.select(AUTH_PATH_KEY).select("path").forEach((element) -> {
                String url = element.attr("url");
                List<String> rights = element.select("right").stream().map(Element::text).collect(Collectors.toList());
                authPath.put(url, rights);
            });
        } catch (IOException e) {
            log.error("initAccessList fail", e);
        }
    }
    public boolean checkAuth(String path, List<String> rights) {

        List<String> needPermission = new ArrayList<>();

        Set<Map.Entry<String, List<String>>> entries = authPath.entrySet();
        for (Map.Entry<String, List<String>> entry : entries) {
            String key = entry.getKey();
            if (antPathMatcher.match(key, path)) {
                needPermission = entry.getValue();
                break;
            }
        }

        if (anonymousPath.stream().anyMatch(i -> antPathMatcher.match(i, path))) {
            return true;
        }

        if (needPermission == null || needPermission.isEmpty()) {
            log.error("path not config permission: {}", path);
            return false;
        } else {
            for (String p : needPermission) {
                if (p.equals("queryHorizontalMenu") || p.equals("getUserMenus") || p.equals("queryDict")) {
                    return true;
                }
                if (rights.contains(p)) {
                    log.debug("checkAuth pass, path: {}, permission: {}", path, p);
                    return true;
                }
            }
            log.debug("checkAuth denied, path: {}", path);
            return false;
        }
    }
}
