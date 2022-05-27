package com.ptc.gateway.security.auth;

import com.ptc.common.vo.LoginUserVo;
import com.google.common.io.CharStreams;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 *
 * @author peter
 */
@Slf4j
public class AppAuthManager {

    private static AppAuthManager ourInstance = new AppAuthManager();

    private static final String ANONYMOUS_PATH_KEY = "anonymous";

    private static final String AUTH_PATH_KEY = "auth";

    public static AppAuthManager getInstance() {
        return ourInstance;
    }

    private AppAuthManager() {
        initAccessList();
    }

    private Set<String> anonymousPath = new HashSet<>();

    private Set<String> authPath = new HashSet<>();

    private void initAccessList() {
        try {
            Resource resource = new ClassPathResource("/auth/app.xml");
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

    public boolean checkAuth(String path, LoginUserVo userVo) {
        log.info("path:"+path);
        log.info("anonymousPath:"+anonymousPath);
        if(anonymousPath.contains(path)) {
            return true;
        } else if (authPath.contains(path)) {
            if (userVo != null) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

}
