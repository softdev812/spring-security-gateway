package com.ptc.gateway.security.auth;

import com.google.common.io.CharStreams;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.util.AntPathMatcher;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.security.core.Authentication;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;


@Slf4j
public class WxAuthManager {

    private static WxAuthManager ourInstance = new WxAuthManager();

    private static final String ANONYMOUS_PATH_KEY = "anonymous";

    private static final String AUTH_PATH_KEY = "auth";

    private AntPathMatcher antPathMatcher = new AntPathMatcher();

    public static WxAuthManager getInstance() {
        return ourInstance;
    }

    private WxAuthManager() {
        initAccessList();
    }

    private Set<String> anonymousPath = new HashSet<>();

    private Set<String> authPath = new HashSet<>();

    private void initAccessList() {
        try {
            Resource resource = new ClassPathResource("/auth/wx.xml");
            String str = CharStreams.toString(new InputStreamReader(resource.getInputStream()));
            Document doc = Jsoup.parse(str);
            Elements elems = doc.select(ANONYMOUS_PATH_KEY).select("path");
            anonymousPath.addAll(elems.stream().map(e -> e.attr("url")).collect(Collectors.toList()));

            elems = doc.select(AUTH_PATH_KEY).select("path");
            authPath.addAll(elems.stream().map(e -> e.attr("url")).collect(Collectors.toList()));
        } catch (IOException e) {
            log.error("initAccessList fail", e);
        }
    }

    public boolean checkAuth(String path, Authentication authentication) {

        if (anonymousPath.stream().anyMatch(i -> antPathMatcher.match(i, path))) {
            return true;
        }

        for (String s : authPath) {
            if (antPathMatcher.match(s, path)) {
                if (authentication.isAuthenticated()) {
                    return true;
                }
            }
        }

        return false;

    }

}
