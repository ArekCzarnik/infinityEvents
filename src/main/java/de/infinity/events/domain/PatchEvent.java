package de.infinity.events.domain;

import java.io.Serializable;

public class PatchEvent implements Serializable {

    private final String id;
    private final String clientId;
    private final String name;
    private final String md5;
    private final String patchString;

    public PatchEvent(final String id, final String clientId, final String name, final String md5, final String patchString) {
        this.id = id;
        this.clientId = clientId;
        this.name = name;
        this.md5 = md5;
        this.patchString = patchString;
    }

    public String getId() {
        return id;
    }

    public String getClientId() {
        return clientId;
    }

    public String getName() {
        return name;
    }

    public String getMd5() {
        return md5;
    }

    public String getPatchString() {
        return patchString;
    }

    @Override
    public String toString() {
        return "PatchEvent{" +
                "id='" + id + '\'' +
                ", clientId='" + clientId + '\'' +
                ", name='" + name + '\'' +
                ", md5='" + md5 + '\'' +
                ", patchString='" + patchString + '\'' +
                '}';
    }
}
