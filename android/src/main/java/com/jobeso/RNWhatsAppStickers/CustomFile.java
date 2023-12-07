package com.jobeso.RNWhatsAppStickers;

public class CustomFile {

	private String fullPath;
	private String name;
	private String directory;
	private boolean isImageFile;

	public boolean isImageFile() {
		return isImageFile;
	}

	public void setImageFile(boolean isImageFile) {
		this.isImageFile = isImageFile;
	}

	public CustomFile(String fileName, String path, String fileParent,
			boolean isImageFile) {
		this.name = fileName;
		this.fullPath = path;
		this.directory = fileParent;
		this.isImageFile = isImageFile;
	}

	public String getFullPath() {
		return fullPath;
	}

	public void setFullPath(String fullPath) {
		this.fullPath = fullPath;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDirectory() {
		return directory;
	}

	public void setDirectory(String directory) {
		this.directory = directory;
	}

}