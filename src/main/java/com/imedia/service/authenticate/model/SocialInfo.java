package com.imedia.service.authenticate.model;

public class SocialInfo {
    private String socialId;
    private String username;

    public SocialInfo(String socialId, String username) {
        this.socialId = socialId;
        this.username = username;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + 1;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        System.out.println("Data equals method");
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        SocialInfo other = (SocialInfo) obj;
        return username.equals(other.username);
    }

    public String getSocialId() {
        return socialId;
    }

    public void setSocialId(String socialId) {
        this.socialId = socialId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}