package com;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.struts2.ServletActionContext;
import org.json.JSONException;
import org.json.JSONObject;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;

import CloudPaper.logger;

public class MenuTreeOp {
  private String result = "checkFailed";
  private String userName;
  private String deleteFolderName;
  private String addFolderName;
  private String addParentName;
  private String renameNewFolderName;
  private String renameOldFolderName;
  private String deleteFileName;
  private String deleteFilePath;
  private String renameNewFileName;
  private String renameOldFileName;
  private String renameFilePath;
  private String rootFolderName;
  private String newFileState;
  private String fileName;
  private String pdfFileURL;
  private String folderID;
  private String desFolderName;
  private String nodeFolderName;

  public String initUserTree() {
    // System.out.println(getUserName());
    result = "";
    try {
      BufferedReader inJson = new BufferedReader(new FileReader(ServletActionContext
          .getServletContext().getRealPath("/userFiles/" + getUserName() + "/config")
          + File.separator + getUserName() + ".json"));
      String curLine = "";
      while ((curLine = inJson.readLine()) != null) {
        result = result + curLine + "\n";
      }
      inJson.close();
    } catch (JSONException | IOException e) {
      e.printStackTrace();
    }
    return "success";
  }

  public String initSystemTree() {
    // System.out.println(getUserName());
    result = "";
    try {
      BufferedReader inJson = new BufferedReader(
          new FileReader(ServletActionContext.getServletContext().getRealPath(
              "/userFiles/" + getUserName() + "/config") + File.separator + "system.json"));
      String curLine = "";
      while ((curLine = inJson.readLine()) != null) {
        result = result + curLine + "\n";
      }
      inJson.close();
    } catch (JSONException | IOException e) {
      e.printStackTrace();
    }
    return "success";
  }

  public String addFolder() {
    result = "";
    int index = getAddParentName().lastIndexOf("folders");
    String parentPath = getAddParentName().substring(0, index);
    String newFolderPath = parentPath + ">" + getAddFolderName();
    try {
      BufferedReader inJson = new BufferedReader(new FileReader(ServletActionContext
          .getServletContext().getRealPath("/userFiles/" + getUserName() + "/config")
          + File.separator + getUserName() + ".json"));
      String curLine = "";
      String addRet = "";
      while ((curLine = inJson.readLine()) != null) {
        if (curLine.equals("{\"pathes\" :[")) {
          addRet = addRet + curLine + "\n";
        } else if (curLine.indexOf("\"path\"") != -1) {
          JSONObject curJson = new JSONObject(curLine);
          String path = curJson.getString("path");
          if (path.equals(newFolderPath)) {
            result = "exist";
            return "success";
          }
          addRet = addRet + curLine + "\n";
        } else {
          addRet = addRet.substring(0, addRet.length() - 1);
          addRet = addRet + ",\n";
          JSONObject newJson = new JSONObject();
          newJson.put("path", newFolderPath);
          addRet = addRet + newJson.toString() + "\n";
          addRet = addRet + curLine + "\n";
        }
      }
      inJson.close();
      BufferedWriter outJson = new BufferedWriter(new FileWriter(ServletActionContext
          .getServletContext().getRealPath("/userFiles/" + getUserName() + "/config")
          + File.separator + getUserName() + ".json"));
      outJson.write(addRet);
      outJson.flush();
      outJson.close();
      result = "complete";
    } catch (JSONException | IOException e) {
      e.printStackTrace();
    }
    return "success";
  }

  public String deleteFolder() {
    int index = getDeleteFolderName().lastIndexOf("folders");
    String desPath = getDeleteFolderName().substring(0, index);
    System.out.println(desPath);
    HashMap<String, Integer> pdfMap = new HashMap<String, Integer>();
    try {
      BufferedReader inJson = new BufferedReader(new FileReader(ServletActionContext
          .getServletContext().getRealPath("/userFiles/" + getUserName() + "/config")
          + File.separator + getUserName() + ".json"));
      String deleteRet = "";
      String curLine = "";
      while ((curLine = inJson.readLine()) != null) {
        if (curLine.equals("{\"pathes\" :[")) {
          deleteRet = deleteRet + curLine + "\n";
        } else if (curLine.indexOf("\"path\"") != -1) {
          JSONObject curJson = new JSONObject(curLine);
          String path = curJson.getString("path");
          // System.out.println(path);
          if (!path.equals(desPath) && path.indexOf(desPath + ">") != 0
              && path.indexOf(desPath + "*") != 0) {
            deleteRet = deleteRet + curLine + "\n";
            // System.out.println(deleteRet);
          } else {
            int lastIndex = path.lastIndexOf("*");
            if (lastIndex > 0) {
              String pdfName = path.substring(lastIndex + 1, path.length());
              pdfMap.put(pdfName, 0);
            }
          }
          // System.out.println(path);
        } else {
          if (deleteRet.charAt(deleteRet.lastIndexOf("\n") - 1) == ',') {
            deleteRet = deleteRet.substring(0, deleteRet.length() - 2);
            deleteRet = deleteRet + "\n";
          }
          deleteRet = deleteRet + curLine + "\n";
        }
      }
      inJson.close();
      BufferedWriter outJson = new BufferedWriter(new FileWriter(ServletActionContext
          .getServletContext().getRealPath("/userFiles/" + getUserName() + "/config")
          + File.separator + getUserName() + ".json"));
      outJson.write(deleteRet);
      // System.out.println(deleteRet);
      outJson.flush();
      outJson.close();

      inJson = new BufferedReader(new FileReader(ServletActionContext.getServletContext()
          .getRealPath("/userFiles/" + getUserName() + "/config") + File.separator + getUserName()
          + ".json"));
      while ((curLine = inJson.readLine()) != null) {
        if (curLine.indexOf("\"path\"") != -1) {
          JSONObject curJson = new JSONObject(curLine);
          String path = curJson.getString("path");
          // System.out.println(path);
          int lastIndex = path.lastIndexOf("*");
          if (lastIndex > 0) {
            String pdfName = path.substring(lastIndex + 1, path.length());
            if (pdfMap.containsKey(pdfName)) {
              pdfMap.put(pdfName, 1);
            }
          }
        }
      }
      inJson.close();
    } catch (JSONException | IOException e) {
      e.printStackTrace();
    }

    Iterator pdfIter = pdfMap.entrySet().iterator();
    while (pdfIter.hasNext()) {
      Map.Entry pdfEntry = (Map.Entry) pdfIter.next();
      if (pdfEntry.getValue().equals(0)) {
        try {
          BufferedReader inJson = new BufferedReader(
              new FileReader(ServletActionContext.getServletContext().getRealPath(
                  "/userFiles/" + getUserName() + "/config") + File.separator + "system.json"));
          String curLine = "";
          String deleteRet = "";
          while ((curLine = inJson.readLine()) != null) {
            if (curLine.equals("{\"pathes\" :[")) {
              deleteRet = deleteRet + curLine + "\n";
            } else if (curLine.indexOf("\"path\"") != -1) {
              JSONObject curJson = new JSONObject(curLine);
              String path = curJson.getString("path");
              int lastIndex = path.lastIndexOf("*");
              if (lastIndex > 0
                  && !path.substring(lastIndex + 1, path.length()).equals(pdfEntry.getKey())) {
                deleteRet = deleteRet + curLine + "\n";
              } else if (lastIndex < 0) {
                deleteRet = deleteRet + curLine + "\n";
              }
            } else {
              if (deleteRet.charAt(deleteRet.lastIndexOf("\n") - 1) == ',') {
                deleteRet = deleteRet.substring(0, deleteRet.length() - 2);
                deleteRet = deleteRet + "\n";
              }
              deleteRet = deleteRet + curLine + "\n";
            }
          }
          inJson.close();
          BufferedWriter outJson = new BufferedWriter(
              new FileWriter(ServletActionContext.getServletContext().getRealPath(
                  "/userFiles/" + getUserName() + "/config") + File.separator + "system.json"));
          outJson.write(deleteRet);
          // System.out.println(deleteRet);
          outJson.flush();
          outJson.close();
        } catch (JSONException | IOException e) {
          e.printStackTrace();
        }

        String fileNameString = ServletActionContext.getServletContext().getRealPath(
            "/userFiles/" + getUserName() + "/pdf") + File.separator + pdfEntry.getKey();
        // System.out.println(fileNameString);
        File tmpFile = new File(fileNameString);
        if (tmpFile.exists()) {
          tmpFile.delete();
        }
      }
    }
    result = "";
    return "success";
  }

  public String renameFolder() {
    result = "";
    // System.out.println(getChangeOldFolderName());
    // System.out.println(getChangeNewFolderName());
    int index = getRenameOldFolderName().lastIndexOf("folders");
    String oldName = getRenameOldFolderName().substring(0, index);
    index = oldName.lastIndexOf(">");
    String newName = "";
    if (index == -1) {
      newName = getRenameNewFolderName();
    } else {
      newName = oldName.substring(0, index) + ">" + getRenameNewFolderName();
    }
    // System.out.println(oldName);
    // System.out.println(newName);
    try {
      BufferedReader inJson = new BufferedReader(new FileReader(ServletActionContext
          .getServletContext().getRealPath("/userFiles/" + getUserName() + "/config")
          + File.separator + getUserName() + ".json"));
      String renameRet = "";
      String curLine = "";
      while ((curLine = inJson.readLine()) != null) {
        if (curLine.equals("{\"pathes\" :[")) {
          renameRet = renameRet + curLine + "\n";
        } else if (curLine.indexOf("\"path\"") != -1) {
          JSONObject curJson = new JSONObject(curLine);
          String path = curJson.getString("path");
          if (path.equals(newName) || path.indexOf(newName + ">") == 0
              || path.indexOf(newName + "*") == 0) {
            result = "exist";
            return "success";
          }
          if (path.equals(oldName) || path.indexOf(oldName + ">") == 0
              || path.indexOf(oldName + "*") == 0) {
            path = newName + path.substring(oldName.length(), path.length());
            // System.out.println(path);
            JSONObject tmpJson = new JSONObject();
            tmpJson.put("path", path);
            renameRet = renameRet + tmpJson.toString() + ",\n";
          } else {
            renameRet = renameRet + curLine + "\n";
          }
        } else {
          if (renameRet.charAt(renameRet.lastIndexOf("\n") - 1) == ',') {
            renameRet = renameRet.substring(0, renameRet.length() - 2);
            renameRet = renameRet + "\n";
          }
          renameRet = renameRet + curLine + "\n";
        }
      }
      inJson.close();
      BufferedWriter outJson = new BufferedWriter(new FileWriter(ServletActionContext
          .getServletContext().getRealPath("/userFiles/" + getUserName() + "/config")
          + File.separator + getUserName() + ".json"));
      outJson.write(renameRet);
      // System.out.println(deleteRet);
      outJson.flush();
      outJson.close();
      result = "complete";
    } catch (JSONException | IOException e) {
      e.printStackTrace();
    }
    return "success";
  }

  public String deleteFile() throws IOException {
    result = "";
    // System.out.println(getDeleteFileName());
    // System.out.println(getDeleteFilePath());
    int index = getDeleteFilePath().lastIndexOf("files");
    String filePath = getDeleteFilePath().substring(0, index);
    String desPath = filePath + "*" + getDeleteFileName();
    int deleteFlag = 0;
    // System.out.println(desPath);
    try {
      BufferedReader inJson = new BufferedReader(new FileReader(ServletActionContext
          .getServletContext().getRealPath("/userFiles/" + getUserName() + "/config")
          + File.separator + getUserName() + ".json"));
      String curLine = "";
      String deleteRet = "";
      while ((curLine = inJson.readLine()) != null) {
        if (curLine.equals("{\"pathes\" :[")) {
          deleteRet = deleteRet + curLine + "\n";
        } else if (curLine.indexOf("\"path\"") != -1) {
          JSONObject curJson = new JSONObject(curLine);
          String path = curJson.getString("path");
          if (!path.equals(desPath)) {
            deleteRet = deleteRet + curLine + "\n";
            int lastIndex = path.lastIndexOf("*");
            if (lastIndex > 0
                && path.substring(lastIndex + 1, path.length()).equals(getDeleteFileName())) {
              deleteFlag = 1;
            }
          }
        } else {
          if (deleteRet.charAt(deleteRet.lastIndexOf("\n") - 1) == ',') {
            deleteRet = deleteRet.substring(0, deleteRet.length() - 2);
            deleteRet = deleteRet + "\n";
          }
          deleteRet = deleteRet + curLine + "\n";
        }
      }
      inJson.close();
      BufferedWriter outJson = new BufferedWriter(new FileWriter(ServletActionContext
          .getServletContext().getRealPath("/userFiles/" + getUserName() + "/config")
          + File.separator + getUserName() + ".json"));
      outJson.write(deleteRet);
      // System.out.println(deleteRet);
      outJson.flush();
      outJson.close();
      result = "complete";
    } catch (JSONException | IOException e) {
      e.printStackTrace();
    }

    if (deleteFlag == 0) {
      try {
        BufferedReader inJson = new BufferedReader(
            new FileReader(ServletActionContext.getServletContext().getRealPath(
                "/userFiles/" + getUserName() + "/config") + File.separator + "system.json"));
        String curLine = "";
        String deleteRet = "";
        while ((curLine = inJson.readLine()) != null) {
          if (curLine.equals("{\"pathes\" :[")) {
            deleteRet = deleteRet + curLine + "\n";
          } else if (curLine.indexOf("\"path\"") != -1) {
            JSONObject curJson = new JSONObject(curLine);
            String path = curJson.getString("path");
            int lastIndex = path.lastIndexOf("*");
            if (lastIndex > 0
                && !path.substring(lastIndex + 1, path.length()).equals(getDeleteFileName())) {
              deleteRet = deleteRet + curLine + "\n";
            } else if (lastIndex < 0) {
              deleteRet = deleteRet + curLine + "\n";
            }
          } else {
            if (deleteRet.charAt(deleteRet.lastIndexOf("\n") - 1) == ',') {
              deleteRet = deleteRet.substring(0, deleteRet.length() - 2);
              deleteRet = deleteRet + "\n";
            }
            deleteRet = deleteRet + curLine + "\n";
          }
        }
        inJson.close();
        BufferedWriter outJson = new BufferedWriter(
            new FileWriter(ServletActionContext.getServletContext().getRealPath(
                "/userFiles/" + getUserName() + "/config") + File.separator + "system.json"));
        outJson.write(deleteRet);
        // System.out.println(deleteRet);
        outJson.flush();
        outJson.close();
      } catch (JSONException | IOException e) {
        e.printStackTrace();
      }

      String fileNameString = ServletActionContext.getServletContext().getRealPath(
          "/userFiles/" + getUserName() + "/pdf") + File.separator + getDeleteFileName();
      // System.out.println(fileNameString);
      File tmpFile = new File(fileNameString);
      if (tmpFile.exists()) {
        tmpFile.delete();
      }
      String logNameString = ServletActionContext.getServletContext().getRealPath(
          "/userFiles/" + getUserName() + "/log") + File.separator + getDeleteFileName() + ".log";
      // System.out.println(fileNameString);
      File tmpLog = new File(logNameString);
      if (tmpLog.exists()) {
        tmpLog.delete();
      }
      String NameString = ServletActionContext.getServletContext()
          .getRealPath("/userFiles/" + getUserName() + "/NoteDOM") + File.separator
          + getDeleteFileName() + ".note";
      // System.out.println(fileNameString);
      File tmpNote = new File(NameString);
      if (tmpNote.exists()) {
        tmpNote.delete();
      }
    } else {
      logger loggerm = new logger();
      loggerm.setUserName(getUserName());
      loggerm.setFileName(getDeleteFileName());
      loggerm.addLog("从" + filePath + "分类中删除", logger.logType.deleteFromClassification);
    }
    return "success";
  }

  public String renameFile() throws IOException {
    result = "";
    // System.out.println(getChangeOldFolderName());
    // System.out.println(getChangeNewFolderName());
    int fileOrderNum = 0;

    String oldFileName = ServletActionContext.getServletContext().getRealPath(
        "/userFiles/" + getUserName() + "/pdf") + File.separator + getRenameOldFileName();
    String newFileName = ServletActionContext.getServletContext().getRealPath(
        "/userFiles/" + getUserName() + "/pdf") + File.separator + getRenameNewFileName() + ".pdf";
    File tmpFile = new File(newFileName);
    while (tmpFile.exists()) {
      fileOrderNum++;
      newFileName = ServletActionContext.getServletContext()
          .getRealPath("/userFiles/" + getUserName() + "/pdf") + File.separator
          + getRenameNewFileName() + ".pdf" + "(" + fileOrderNum + ")";
      tmpFile = new File(newFileName);
    }

    // System.out.println(oldFileName);
    // System.out.println(newFileName);
    File oldFile = new File(oldFileName);
    File newFile = new File(newFileName);
    if (oldFile.exists()) {
      oldFile.renameTo(newFile);
    }

    String oldLogName = ServletActionContext.getServletContext().getRealPath(
        "/userFiles/" + getUserName() + "/log") + File.separator + getRenameOldFileName() + ".log";
    String newLogName = ServletActionContext.getServletContext()
        .getRealPath("/userFiles/" + getUserName() + "/log") + File.separator
        + getRenameNewFileName() + ".pdf" + ".log";
    if (fileOrderNum != 0) {
      newLogName = ServletActionContext.getServletContext()
          .getRealPath("/userFiles/" + getUserName() + "/log") + File.separator
          + getRenameNewFileName() + ".pdf" + "(" + +fileOrderNum + ")" + ".log";
    }

    File oldLog = new File(oldLogName);
    File newLog = new File(newLogName);
    if (oldLog.exists()) {
      oldLog.renameTo(newLog);
    }

    String oldNoteName = ServletActionContext.getServletContext()
        .getRealPath("/userFiles/" + getUserName() + "/NoteDOM") + File.separator
        + getRenameOldFileName() + ".note";
    String newNoteName = ServletActionContext.getServletContext()
        .getRealPath("/userFiles/" + getUserName() + "/NoteDOM") + File.separator
        + getRenameNewFileName() + ".pdf" + ".note";
    if (fileOrderNum != 0) {
      newNoteName = ServletActionContext.getServletContext()
          .getRealPath("/userFiles/" + getUserName() + "/NoteDOM") + File.separator
          + getRenameNewFileName() + ".pdf" + "(" + +fileOrderNum + ")" + ".note";
    }

    File oldNote = new File(oldNoteName);
    File newNote = new File(newNoteName);
    if (oldNote.exists()) {
      oldNote.renameTo(newNote);
    }

    String finalNewFileName = getRenameNewFileName() + ".pdf";
    if (fileOrderNum != 0) {
      finalNewFileName = finalNewFileName + "(" + fileOrderNum + ")";
    }
    logger loggerm = new logger();
    loggerm.setUserName(getUserName());
    loggerm.setFileName(finalNewFileName);
    loggerm.addLog("由" + getRenameOldFileName() + "重命名为" + finalNewFileName,
        logger.logType.renameFile);

    int index = getRenameFilePath().lastIndexOf("files");
    String filePath = getRenameFilePath().substring(0, index);
    String desPath = filePath + "*" + getRenameOldFileName();
    String newPath;
    if (fileOrderNum == 0) {
      newPath = filePath + "*" + getRenameNewFileName() + ".pdf";
    } else {
      newPath = filePath + "*" + getRenameNewFileName() + ".pdf" + "(" + fileOrderNum + ")";
    }
    try {
      BufferedReader inJson = new BufferedReader(new FileReader(ServletActionContext
          .getServletContext().getRealPath("/userFiles/" + getUserName() + "/config")
          + File.separator + getUserName() + ".json"));
      String curLine = "";
      String renameRet = "";
      while ((curLine = inJson.readLine()) != null) {
        if (curLine.equals("{\"pathes\" :[")) {
          renameRet = renameRet + curLine + "\n";
        } else if (curLine.indexOf("\"path\"") != -1) {
          JSONObject curJson = new JSONObject(curLine);
          String path = curJson.getString("path");
          if (!path.equals(desPath)) {
            renameRet = renameRet + curLine + "\n";
          }
        } else {
          JSONObject newJson = new JSONObject();
          newJson.put("path", newPath);
          if (renameRet.charAt(renameRet.lastIndexOf("\n") - 1) != ',') {
            renameRet = renameRet.substring(0, renameRet.length() - 1);
            renameRet = renameRet + ",\n";
          }
          renameRet = renameRet + newJson.toString() + "\n";
          renameRet = renameRet + curLine + "\n";
        }
      }
      inJson.close();
      BufferedWriter outJson = new BufferedWriter(new FileWriter(ServletActionContext
          .getServletContext().getRealPath("/userFiles/" + getUserName() + "/config")
          + File.separator + getUserName() + ".json"));
      outJson.write(renameRet);
      // System.out.println(deleteRet);
      outJson.flush();
      outJson.close();
      result = "complete";
    } catch (JSONException | IOException e) {
      e.printStackTrace();
    }

    try {
      BufferedReader inJson = new BufferedReader(
          new FileReader(ServletActionContext.getServletContext().getRealPath(
              "/userFiles/" + getUserName() + "/config") + File.separator + "system.json"));
      String curLine = "";
      String renameRet = "";
      while ((curLine = inJson.readLine()) != null) {
        if (curLine.equals("{\"pathes\" :[")) {
          renameRet = renameRet + curLine + "\n";
        } else if (curLine.indexOf("\"path\"") != -1) {
          JSONObject curJson = new JSONObject(curLine);
          String path = curJson.getString("path");
          int lastIndex = path.lastIndexOf("*");
          if (lastIndex > 0
              && path.substring(lastIndex + 1, path.length()).equals(getRenameOldFileName())) {
            String tmpPath = path.substring(0, lastIndex + 1) + getRenameNewFileName() + ".pdf";
            if (fileOrderNum != 0) {
              tmpPath = tmpPath + "(" + fileOrderNum + ")";
            }
            JSONObject newJson = new JSONObject();
            newJson.put("path", tmpPath);
            renameRet = renameRet + newJson.toString() + ",\n";
          } else {
            renameRet = renameRet + curLine + "\n";
          }
        } else {
          if (renameRet.charAt(renameRet.lastIndexOf("\n") - 1) == ',') {
            renameRet = renameRet.substring(0, renameRet.length() - 2);
            renameRet = renameRet + "\n";
          }
          renameRet = renameRet + curLine + "\n";
        }
      }
      inJson.close();
      BufferedWriter outJson = new BufferedWriter(
          new FileWriter(ServletActionContext.getServletContext().getRealPath(
              "/userFiles/" + getUserName() + "/config") + File.separator + "system.json"));
      outJson.write(renameRet);
      // System.out.println(deleteRet);
      outJson.flush();
      outJson.close();
    } catch (JSONException | IOException e) {
      e.printStackTrace();
    }
    return "success";
  }

  public String addRootFolder() {
    try {
      BufferedReader inJson = new BufferedReader(new FileReader(ServletActionContext
          .getServletContext().getRealPath("/userFiles/" + getUserName() + "/config")
          + File.separator + getUserName() + ".json"));
      String curLine = "";
      String addRet = "";
      while ((curLine = inJson.readLine()) != null) {
        if (curLine.equals("{\"pathes\" :[")) {
          addRet = addRet + curLine + "\n";
        } else if (curLine.indexOf("\"path\"") != -1) {
          JSONObject curJson = new JSONObject(curLine);
          String path = curJson.getString("path");
          if (path.indexOf(getRootFolderName() + ">") == 0 || path.indexOf(getRootFolderName() + "*") == 0 || path.equals(getRootFolderName())) {
            result = "exist";
            return "success";
          }
          addRet = addRet + curLine + "\n";
        } else {
          JSONObject newJson = new JSONObject();
          newJson.put("path", getRootFolderName());
          if (!addRet.equals("{\"pathes\" :[" + "\n")) {
            addRet = addRet.substring(0, addRet.length() - 1);
            addRet = addRet + ",\n";
          }
          addRet = addRet + newJson.toString() + "\n";
          addRet = addRet + curLine + "\n";
        }
      }
      inJson.close();
      BufferedWriter outJson = new BufferedWriter(new FileWriter(ServletActionContext
          .getServletContext().getRealPath("/userFiles/" + getUserName() + "/config")
          + File.separator + getUserName() + ".json"));
      outJson.write(addRet);
      outJson.flush();
      outJson.close();
      result = "complete";
    } catch (JSONException | IOException e) {
      e.printStackTrace();
    }
    return "success";
  }

  public String changeFileState() throws IOException {
    String newFileStatePath;
    switch (getNewFileState()) {
      case "intensive":
        newFileStatePath = "精读*" + getFileName();
        logger logger1 = new logger();
        logger1.setUserName(getUserName());
        logger1.setFileName(getFileName());
        logger1.addLog("已精读", logger.logType.intensiveRead);
        break;
      case "rough":
        newFileStatePath = "粗读*" + getFileName();
        logger logger2 = new logger();
        logger2.setUserName(getUserName());
        logger2.setFileName(getFileName());
        logger2.addLog("已粗读", logger.logType.roughRead);
        break;
      default:
        newFileStatePath = "未读*" + getFileName();
        logger logger3 = new logger();
        logger3.setUserName(getUserName());
        logger3.setFileName(getFileName());
        logger3.addLog("已未读", logger.logType.unRead);
        break;
    }
    System.out.println(newFileStatePath);
    try {
      BufferedReader inJson = new BufferedReader(
          new FileReader(ServletActionContext.getServletContext().getRealPath(
              "/userFiles/" + getUserName() + "/config") + File.separator + "system.json"));
      String changeRet = "";
      String curLine = "";
      while ((curLine = inJson.readLine()) != null) {
        if (curLine.equals("{\"pathes\" :[")) {
          changeRet = changeRet + curLine + "\n";
        } else if (curLine.indexOf("\"path\"") != -1) {
          JSONObject curJson = new JSONObject(curLine);
          String path = curJson.getString("path");
          int lastIndex = path.lastIndexOf("*");
          if (lastIndex > 0
              && !path.substring(lastIndex + 1, path.length()).equals(getFileName())) {
            changeRet = changeRet + curLine + "\n";
          } else if (lastIndex < 0) {
            changeRet = changeRet + curLine + "\n";
          }
        } else {
          if (changeRet.charAt(changeRet.lastIndexOf("\n") - 1) != ',') {
            changeRet = changeRet.substring(0, changeRet.length() - 1);
            changeRet = changeRet + ",\n";
          }
          JSONObject newJson = new JSONObject();
          newJson.put("path", newFileStatePath);
          changeRet = changeRet + newJson.toString() + "\n";
          changeRet = changeRet + curLine + "\n";
        }
      }
      inJson.close();
      BufferedWriter outJson = new BufferedWriter(
          new FileWriter(ServletActionContext.getServletContext().getRealPath(
              "/userFiles/" + getUserName() + "/config") + File.separator + "system.json"));
      outJson.write(changeRet);
      // System.out.println(deleteRet);
      outJson.flush();
      outJson.close();
    } catch (JSONException | IOException e) {
      e.printStackTrace();
    }
    result = "";
    return "success";
  }

  public String downloadFromURL() throws IOException {
    userName = (String) ServletActionContext.getRequest().getSession().getAttribute("username");

    String[] tmpStrings = getPdfFileURL().split("/");
    String tmpFileName = tmpStrings[tmpStrings.length - 1];
    System.out.println("fileName:" + tmpFileName);
    tmpStrings = tmpFileName.split("\\.");
    String fileType = tmpStrings[tmpStrings.length - 1];
    if (!fileType.equals("pdf")) {
      setResult("URL error: Not pdf file!");
      return "success";
    }

    URL url = new URL(getPdfFileURL());
    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
    // 设置超时间为3秒
    conn.setConnectTimeout(3 * 1000);
    // 防止屏蔽程序抓取而返回403错误
    conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");

    // 得到输入流
    InputStream inputStream;
    try {
      inputStream = conn.getInputStream();
    } catch (FileNotFoundException e) {
      setResult("invalid URL!");
      return "success";
    }

    // 获取自己数组
    byte[] getData = readInputStream(inputStream);

    // 文件保存位置
    int fileOrderNum=0;
    String tmpSaveFileName = ServletActionContext.getServletContext().getRealPath("/userFiles/" + userName + "/pdf" ) + File.separator + fileName + ".pdf";
    File tmpFile = new File(tmpSaveFileName);
    while(tmpFile.exists()) {
    	fileOrderNum++;
    	tmpSaveFileName = ServletActionContext.getServletContext().getRealPath("/userFiles/" + userName + "/pdf" ) + File.separator + fileName + ".pdf" +"("+fileOrderNum+")";
    	tmpFile = new File(tmpSaveFileName);
    }
    FileOutputStream fos = new FileOutputStream(tmpFile);
    fos.write(getData);
    if (fos != null) {
      fos.close();
    }
    if (inputStream != null) {
      inputStream.close();
    }
    int index = getFolderID().lastIndexOf("folders");
	String desPath = getFolderID().substring(0, index);
	if(fileOrderNum==0) {
    	desPath = desPath + "*" + fileName + ".pdf";
    }else {
    	desPath = desPath + "*" + fileName + ".pdf" + "(" +fileOrderNum + ")";
    }
    try{  
		BufferedReader inJson = new BufferedReader(new FileReader(  
        		ServletActionContext.getServletContext().getRealPath("/userFiles/" + userName + "/config") 
				+ File.separator + userName + ".json"));
		String curLine = "";
		String findRet="";
		while((curLine = inJson.readLine()) != null){
			 if(curLine.equals("{\"pathes\" :[")) {
				 findRet = findRet+curLine+"\n";
			 }else if(curLine.indexOf("\"path\"") != -1) {
				 findRet = findRet+curLine+"\n";
			 }else {
				 findRet = findRet.substring(0, findRet.length()-1);
				 findRet = findRet + ",\n";
				 JSONObject newJson=new JSONObject();
				 newJson.put("path", desPath);
				 findRet = findRet + newJson.toString() + "\n";
				 findRet = findRet + curLine + "\n";
			 }
		}
		inJson.close();
		BufferedWriter outJson = new BufferedWriter(new FileWriter(  
				 ServletActionContext.getServletContext().getRealPath("/userFiles/" + userName + "/config") 
    				+ File.separator + userName + ".json"));
		outJson.write(findRet);
		outJson.flush();
		outJson.close();
	}catch(JSONException | IOException e) {  
    	e.printStackTrace();
    }
	try{  
		BufferedReader inJson = new BufferedReader(new FileReader(  
        		ServletActionContext.getServletContext().getRealPath("/userFiles/" + userName + "/config") 
				+ File.separator + "system.json"));
		String curLine = "";
		String findRet="";
		while((curLine = inJson.readLine()) != null){
			 if(curLine.equals("{\"pathes\" :[")) {
				 findRet = findRet+curLine+"\n";
			 }else if(curLine.indexOf("\"path\"") != -1) {
				 findRet = findRet+curLine+"\n";
			 }else {
				 findRet = findRet.substring(0, findRet.length()-1);
				 findRet = findRet + ",\n";
				 JSONObject newJson=new JSONObject();
				 String tmpPath = "未读*" + fileName + ".pdf";
				 if(fileOrderNum!=0) {
					 tmpPath = tmpPath + "(" + fileOrderNum + ")";
				 }
				 newJson.put("path", tmpPath);
				 findRet = findRet + newJson.toString() + "\n";
				 findRet = findRet + curLine + "\n";
			 }
		}
		inJson.close();
		BufferedWriter outJson = new BufferedWriter(new FileWriter(  
				 ServletActionContext.getServletContext().getRealPath("/userFiles/" + userName + "/config") 
    				+ File.separator + "system.json"));
		outJson.write(findRet);
		outJson.flush();
		outJson.close();
	}catch(JSONException | IOException e) {  
    	e.printStackTrace();
    }
	String finalFileName = fileName + ".pdf";
	if(fileOrderNum!=0) {
		finalFileName = finalFileName + "(" +fileOrderNum + ")";
    }
	logger loggerm = new logger();
    loggerm.setUserName(userName);
    loggerm.setFileName(finalFileName);
    loggerm.addLog("加入系统", logger.logType.appendToSystem);
    setResult("download success");
    return "success";
  }

  private static byte[] readInputStream(InputStream inputStream) throws IOException {
    byte[] buffer = new byte[1024];
    int len = 0;
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    while ((len = inputStream.read(buffer)) != -1) {
      bos.write(buffer, 0, len);
    }
    bos.close();
    return bos.toByteArray();
  }

  public String dragFile() {
    result = "";
    int index = getDesFolderName().lastIndexOf("folders");
    String desPath = getDesFolderName().substring(0, index);
    String newFilePath = desPath + "*" + getFileName();
    try {
      BufferedReader inJson = new BufferedReader(new FileReader(ServletActionContext
          .getServletContext().getRealPath("/userFiles/" + getUserName() + "/config")
          + File.separator + getUserName() + ".json"));
      String curLine = "";
      String dragRet = "";
      while ((curLine = inJson.readLine()) != null) {
        if (curLine.equals("{\"pathes\" :[")) {
          dragRet = dragRet + curLine + "\n";
        } else if (curLine.indexOf("\"path\"") != -1) {
          JSONObject curJson = new JSONObject(curLine);
          String path = curJson.getString("path");
          if (path.equals(newFilePath)) {
            result = "exist";
            return "success";
          }
          dragRet = dragRet + curLine + "\n";
        } else {
          dragRet = dragRet.substring(0, dragRet.length() - 1);
          dragRet = dragRet + ",\n";
          JSONObject newJson = new JSONObject();
          newJson.put("path", newFilePath);
          dragRet = dragRet + newJson.toString() + "\n";
          dragRet = dragRet + curLine + "\n";
        }
      }
      inJson.close();
      BufferedWriter outJson = new BufferedWriter(new FileWriter(ServletActionContext
          .getServletContext().getRealPath("/userFiles/" + getUserName() + "/config")
          + File.separator + getUserName() + ".json"));
      outJson.write(dragRet);
      outJson.flush();
      outJson.close();
      result = "complete";
      logger loggerm = new logger();
      loggerm.setUserName(getUserName());
      loggerm.setFileName(getFileName());
      loggerm.addLog("添加到" + desPath + "分类", logger.logType.addClassification);
    } catch (JSONException | IOException e) {
      e.printStackTrace();
    }
    return "success";
  }

  public String packDownload() {
    File oldZip = new File(ServletActionContext.getServletContext().getRealPath("")
        + "\\userFiles\\" + getUserName() + "\\" + getUserName() + ".zip");
    if (oldZip.exists()) {
      oldZip.delete();
    }
    result = "";
    int index = getNodeFolderName().lastIndexOf("folders");
    String nodePath = getNodeFolderName().substring(0, index);
    Set<String> pdfSet = new HashSet<String>();
    try {
      BufferedReader inJson = new BufferedReader(new FileReader(ServletActionContext
          .getServletContext().getRealPath("/userFiles/" + getUserName() + "/config")
          + File.separator + getUserName() + ".json"));
      String curLine = "";
      while ((curLine = inJson.readLine()) != null) {
        if (curLine.indexOf("\"path\"") != -1) {
          JSONObject curJson = new JSONObject(curLine);
          String path = curJson.getString("path");
          if (path.indexOf(nodePath + "*") == 0 || path.indexOf(nodePath + ">") == 0) {
            int lastIndex = path.lastIndexOf("*");
            if (lastIndex > 0) {
              String pdfName = path.substring(lastIndex + 1);
              pdfSet.add(pdfName);
            }
          }
        }
      }
      inJson.close();
      fileToZip(pdfSet, ServletActionContext.getServletContext().getRealPath("") + "\\userFiles\\"
          + getUserName(), getUserName());
      result = getZip();
    } catch (JSONException | IOException e) {
      e.printStackTrace();
    }
    return "success";
  }

  /**
   * @param pdfSet
   *          :待压缩的文件名集合
   * @param zipFilePath
   *          :压缩后存放路径
   * @param fileName
   *          :压缩后文件的名称
   * @return
   */
  public boolean fileToZip(Set<String> pdfSet, String zipFilePath, String fileName) {
    boolean flag = false;
    FileInputStream fis = null;
    BufferedInputStream bis = null;
    FileOutputStream fos = null;
    ZipOutputStream zos = null;

    try {
      File zipFile = new File(zipFilePath + "/" + fileName + ".zip");
      if (zipFile.exists()) {
        System.out.println(zipFilePath + "目录下存在名字为:" + fileName + ".zip" + "打包文件.");
      } else {
        fos = new FileOutputStream(zipFile);
        zos = new ZipOutputStream(new BufferedOutputStream(fos));
        byte[] bufs = new byte[1024 * 10];
        for (String pdfName : pdfSet) {
          // 创建ZIP实体，并添加进压缩包
          ZipEntry zipEntry = new ZipEntry(
              new File(ServletActionContext.getServletContext().getRealPath("") + "\\userFiles\\"
                  + getUserName() + "\\pdf\\" + pdfName).getName());
          zos.putNextEntry(zipEntry);
          // 读取待压缩的文件并写进压缩包里
          fis = new FileInputStream(
              new File(ServletActionContext.getServletContext().getRealPath("") + "\\userFiles\\"
                  + getUserName() + "\\pdf\\" + pdfName));
          bis = new BufferedInputStream(fis, 1024 * 10);
          int read = 0;
          while ((read = bis.read(bufs, 0, 1024 * 10)) != -1) {
            zos.write(bufs, 0, read);
          }
          flag = true;
        }
      }
    } catch (FileNotFoundException e) {
      e.printStackTrace();
      throw new RuntimeException(e);
    } catch (IOException e) {
      e.printStackTrace();
      throw new RuntimeException(e);
    } finally {
      // 关闭流
      try {
        if (null != bis)
          bis.close();
        if (null != zos)
          zos.close();
      } catch (IOException e) {
        e.printStackTrace();
        throw new RuntimeException(e);
      }
    }
    return flag;
  }

  public String getZip() throws IOException {
    String username = (String) ServletActionContext.getRequest().getSession()
        .getAttribute("username");
    String RootDir = ServletActionContext.getServletContext().getRealPath("");
    String zipFileName = RootDir + "/userFiles/" + username + "/" + username + ".zip";
    File file = new File(zipFileName);
    InputStream in = null;
    byte[] cache = null;
    int count = 0;
    try {
      // 一次读一个字节
      in = new FileInputStream(file);
      cache = new byte[in.available()];
      int tempbyte;
      while ((tempbyte = in.read()) != -1) {
        cache[count++] = (byte) tempbyte;
      }
      // System.out.println("count:" + count);
      in.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return Base64.encode(cache);
  }

  public String showTimeLine() {
	  userName = (String) ServletActionContext.getRequest().getSession().getAttribute("username");
	  result = "";
	  try {
	      BufferedReader inLog = new BufferedReader(new FileReader(ServletActionContext
	          .getServletContext().getRealPath("/userFiles/" + getUserName() + "/log")
	          + File.separator + getFileName() + ".log"));
	      String curLine = "";
	      while ((curLine = inLog.readLine()) != null) {
	        //System.out.println(curLine);
	    	  result = result + curLine + "###";
	      }
	      int index = result.lastIndexOf("###");
	      result = result.substring(0,index);
	      inLog.close();
	    } catch (JSONException | IOException e) {
	      e.printStackTrace();
	    }
	  return "success";
  }
  
  public String getResult() {
    return result;
  }

  public void setResult(String result) {
    this.result = result;
  }

  public String getUserName() {
    return userName;
  }

  public void setUserName(String userName) {
    this.userName = (String) ServletActionContext.getRequest().getSession()
        .getAttribute("username");
  }

  public String getDeleteFolderName() {
    return deleteFolderName;
  }

  public void setDeleteFolderName(String deleteFolderName) {
    this.deleteFolderName = deleteFolderName;
  }

  public String getAddFolderName() {
    return addFolderName;
  }

  public void setAddFolderName(String addFolderName) {
    this.addFolderName = addFolderName;
  }

  public String getAddParentName() {
    return addParentName;
  }

  public void setAddParentName(String addParentName) {
    this.addParentName = addParentName;
  }

  public String getRenameNewFolderName() {
    return renameNewFolderName;
  }

  public void setRenameNewFolderName(String renameNewFolderName) {
    this.renameNewFolderName = renameNewFolderName;
  }

  public String getRenameOldFolderName() {
    return renameOldFolderName;
  }

  public void setRenameOldFolderName(String renameOldFolderName) {
    this.renameOldFolderName = renameOldFolderName;
  }

  public String getDeleteFileName() {
    return deleteFileName;
  }

  public void setDeleteFileName(String deleteFileName) {
    this.deleteFileName = deleteFileName;
  }

  public String getDeleteFilePath() {
    return deleteFilePath;
  }

  public void setDeleteFilePath(String deleteFilePath) {
    this.deleteFilePath = deleteFilePath;
  }

  public String getRenameNewFileName() {
    return renameNewFileName;
  }

  public void setRenameNewFileName(String renameNewFileName) {
    this.renameNewFileName = renameNewFileName;
  }

  public String getRenameOldFileName() {
    return renameOldFileName;
  }

  public void setRenameOldFileName(String renameOldFileName) {
    this.renameOldFileName = renameOldFileName;
  }

  public String getRenameFilePath() {
    return renameFilePath;
  }

  public void setRenameFilePath(String renameFilePath) {
    this.renameFilePath = renameFilePath;
  }

  public String getRootFolderName() {
    return rootFolderName;
  }

  public void setRootFolderName(String rootFolderName) {
    this.rootFolderName = rootFolderName;
  }

  public String getNewFileState() {
    return newFileState;
  }

  public void setNewFileState(String newFileState) {
    this.newFileState = newFileState;
  }

  public String getFileName() {
    return fileName;
  }

  public void setFileName(String fileName) {
    this.fileName = fileName;
  }

  /**
   * @return pdfFileURL
   */
  public String getPdfFileURL() {
    return pdfFileURL;
  }

  /**
   * @param pdfFileURL
   *          要设置的 pdfFileURL
   */
  public void setPdfFileURL(String pdfFileURL) {
    this.pdfFileURL = pdfFileURL;
  }

  /**
   * @return folderID
   */
  public String getFolderID() {
    return folderID;
  }

  /**
   * @param folderID
   *          要设置的 folderID
   */
  public void setFolderID(String folderID) {
    this.folderID = folderID;
  }

  public String getDesFolderName() {
    return desFolderName;
  }

  public void setDesFolderName(String desFolderName) {
    this.desFolderName = desFolderName;
  }

  public String getNodeFolderName() {
    return nodeFolderName;
  }

  public void setNodeFolderName(String nodeFolderName) {
    this.nodeFolderName = nodeFolderName;
  }

}
