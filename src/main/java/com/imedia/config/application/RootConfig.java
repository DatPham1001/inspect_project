package com.imedia.config.application;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

public class RootConfig {
//    static final Logger logger = Logger.getLogger(RootConfig.class);

    public RootConfig() {
        super();
        root = "./";
        try {
            this.root = this.getClass().getClassLoader().getResource("/").toURI().getPath();
            root = root.replaceAll("classes/", "");
            // System.out.println("Root:" + root);
        } catch (URISyntaxException e) {
            this.root = "./";
            // Debug.Error(e);
        } catch (Exception e) {
            // Debug.Error(e);
            this.root = "./";
        }
        instance_ = this;
    }

    private static RootConfig instance_;

    public static RootConfig getInstance() {
        if (instance_ == null) {
            new RootConfig();
        }
        return instance_;
    }

//	public String getFilePath(String fileName) throws IOException
//	{
//		String pathFile = new RootConfig().getRoot() + fileName;
//		
//		if(checkFile(pathFile))
//		{
//			return pathFile;
//		}
//		else
//		{
//			pathFile = "resource/" + fileName;
//			logger.info("File path not found try find in folder resouce {}", pathFile);
//			if(checkFile(pathFile))
//			{
//				return pathFile;
//			}
//			else
//			{
//				throw new IOException("File not found in path " + pathFile);
//			}
//		}
//	}

    public String getFile(String fileName) throws IOException {
        URL url = this.getClass().getClassLoader().getResource(fileName);

        File file = null;
        try {
            file = new File(url.toURI());
        } catch (URISyntaxException | IllegalArgumentException e) {
            file = new File(url.getPath());
            fileName = "resource/" + fileName;
//            logger.info("File path not found try find in folder resouce {}" + fileName);
            if (new File(fileName).exists()) {
//                logger.info("File path found in resouce {}" + fileName);
                return fileName;
            } else {
                throw new IOException("File not found in path " + fileName);
            }
        }

        if (file.exists()) {
            return file.getPath();
        } else {
//            logger.info("File path not found try find in folder resouce {}" + fileName);
            fileName = "resource/" + fileName;
            if (new File(fileName).exists()) {
                return fileName;
            } else {
                throw new IOException("File not found in path " + fileName);
            }
        }
    }

    public String getRealPathFile(String fileName) throws IOException {
        URL url = this.getClass().getClassLoader().getResource(fileName);

        File file = null;
        try {
            file = new File(url.toURI());
        } catch (URISyntaxException | IllegalArgumentException e) {
            file = new File(url.getPath());
//            logger.info("File path not found try find in folder resouce {}" + fileName);
            if (new File(fileName).exists()) {
                return fileName;
            } else {
                throw new IOException("File not found in path " + fileName);
            }
        }

        if (file.exists()) {
            return file.getPath();
        } else {
//            logger.info("File path not found try find in folder resouce {}" + fileName);
            if (new File(fileName).exists()) {
                return fileName;
            } else {
                throw new IOException("File not found in path " + fileName);
            }
        }
    }

    public String getRoot() {
        return root;
    }

    private String root;

}
