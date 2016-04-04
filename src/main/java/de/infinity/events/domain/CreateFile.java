package de.infinity.events.domain;

public class CreateFile {

    private final String id;
    private final String content;
    private final String path;
    private final String md5;
    private final String encoding;

    public CreateFile(final String id, final String content, final String path, final String md5, final String encoding) {
        this.id = id;
        this.content = content;
        this.path = path;
        this.md5 = md5;
        this.encoding = encoding;
    }

    public String getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public String getPath() {
        return path;
    }

    public String getMd5() {
        return md5;
    }

    public String getEncoding() {
        return encoding;
    }
}
