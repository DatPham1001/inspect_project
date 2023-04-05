package com.imedia.controller;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

//@RestController
//@RequestMapping("/api/public/D13GKhGgChD7pWPECm9ROJSElbHe7pBc")
public class LogGetController {
    //    @GetMapping(value = "/vf8OsGTuLL7y570Xpr1MFcqizL7Vb9uH")
    public ResponseEntity<Resource> vf8OsGTuLL7y570Xpr1MFcqizL7Vb9uH(String folder) throws Exception {
        Path source = Paths.get(folder);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        if (Files.isDirectory(source)) {
            try (
                    ZipOutputStream zipOutputStream = new ZipOutputStream(bos);
            ) {
                Files.walkFileTree(source, new SimpleFileVisitor<Path>() {
                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {

                        if (attrs.isSymbolicLink()) {
                            return FileVisitResult.CONTINUE;
                        }

                        try (FileInputStream fileInputStream = new FileInputStream(file.toFile())) {
                            Path targetFile = source.relativize(file);
                            zipOutputStream.putNextEntry(new ZipEntry(targetFile.toString()));

                            byte[] buffer = new byte[1024];
                            int len;
                            while ((len = fileInputStream.read(buffer)) > 0) {
                                zipOutputStream.write(buffer, 0, len);
                            }
                        } catch (Exception exception) {
                            exception.printStackTrace();
                        }
                        return FileVisitResult.CONTINUE;
                    }
                });
            } catch (Exception exception) {

                return ResponseEntity.notFound().build();
            }
        } else if (Files.isRegularFile(source)) {
            try (
                    ZipOutputStream zipOutputStream = new ZipOutputStream(bos);
            ) {
                try (FileInputStream fileInputStream = new FileInputStream(source.toFile())) {
                    zipOutputStream.putNextEntry(new ZipEntry(source.toString()));
                    byte[] buffer = new byte[1024];
                    int len;
                    while ((len = fileInputStream.read(buffer)) > 0) {
                        zipOutputStream.write(buffer, 0, len);
                    }
                } catch (Exception exception) {
                    ResponseEntity.ok(exception);
                }
            } catch (Exception exception) {
                return ResponseEntity.notFound().build();
            }
        }
        Resource resource = new ByteArrayResource(bos.toByteArray());
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"log.zip\"")
                .body(resource);
    }

    //    @GetMapping("/rZvvLbCS66p5QHPqqHWFy6xHrsHimOvb")
    public ResponseEntity<Map<String, Object>> rZvvLbCS66p5QHPqqHWFy6xHrsHimOvb(String folder) throws IOException {

        Path path = Paths.get(folder);
        Map<String, Object> resource = new HashMap<>();
        Stack<Map<String, Object>> stack = new Stack<>();
        stack.push(resource);
        Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                stack.push(new HashMap<>());
                return super.preVisitDirectory(dir, attrs);
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Map<String, Object> peek = stack.peek();
                Object g = file.toAbsolutePath().toString();
                peek.put(file.getFileName().toString(), g);
                return super.visitFile(file, attrs);
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                Map<String, Object> pop = stack.pop();
                Map<String, Object> peek = stack.peek();
                peek.put(dir.getName(dir.getNameCount() - 1).toString(), pop);
                return super.postVisitDirectory(dir, exc);
            }
        });

        return ResponseEntity.ok(resource);
    }
}
