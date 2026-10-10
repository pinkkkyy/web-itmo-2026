package ru.itmo.wp.servlet;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class StaticServlet extends HttpServlet {
    private static final String SRC_STATIC = "src/main/webapp/static";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String uri = request.getRequestURI();

        String[] names = uri.split("\\+");
        List<File> files = new ArrayList<>();
        for (String name : names) {
            File file = findFile(name);
            if (file == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            files.add(file);
        }

        response.setContentType(getServletContext().getMimeType(files.get(0).getName()));

        try (OutputStream outputStream = response.getOutputStream()) {
            for (File file : files) {
                Files.copy(file.toPath(), outputStream);
            }
        }
    }

    private File findFile(String uri) throws IOException {
        File srcStatic = findSrcStatic();
        if (srcStatic != null) {
            File file = resolve(srcStatic, uri);
            if (file != null) {
                return file;
            }
        }

        String realStatic = getServletContext().getRealPath("/static");
        if (realStatic != null) {
            return resolve(new File(realStatic), uri);
        }
        return null;
    }

    private File resolve(File root, String uri) throws IOException {
        File base = root.getCanonicalFile();
        File file = new File(base, uri).getCanonicalFile();
        if (file.isFile() && file.getPath().startsWith(base.getPath() + File.separator)) {
            return file;
        }
        return null;
    }

    private File findSrcStatic() {
        List<File> starts = new ArrayList<>();
        starts.add(new File(System.getProperty("user.dir")));
        String realRoot = getServletContext().getRealPath("/");
        if (realRoot != null) {
            starts.add(new File(realRoot));
        }
        for (File dir : starts) {
            for (File cur = dir; cur != null; cur = cur.getParentFile()) {
                File candidate = new File(cur, SRC_STATIC);
                if (candidate.isDirectory()) {
                    return candidate;
                }
            }
        }
        return null;
    }
}