package org.roadmap.filedrive.controller;

import org.roadmap.filedrive.exception.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;

public class PathAccessHandler {

    public String validatePath(String path) {
        if (path == null || path.isEmpty()) {
            return getAccessRootPath();
        }
        String rootPath = path.substring(0, path.indexOf('/') + 1);
        if (!rootPath.equals(getAccessRootPath())) {
            throw new AccessDeniedException("Access denied");
        } else {
            return path;
        }
    }

    private String getAccessRootPath() {
        return SecurityContextHolder.getContext().getAuthentication()
                .getAuthorities()
                .iterator().next().getAuthority();
    }
}
