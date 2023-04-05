package com.imedia.service.cache.model;


import com.imedia.service.authenticate.model.SocialInfo;

import java.util.List;

public class CacheSocialInfo {
    private List<SocialInfo> socialInfos;

    public CacheSocialInfo(List<SocialInfo> socialInfos) {
        this.socialInfos = socialInfos;
    }

    public List<SocialInfo> getSocialInfos() {
        return socialInfos;
    }

    public void setSocialInfos(List<SocialInfo> socialInfos) {
        this.socialInfos = socialInfos;
    }


}
