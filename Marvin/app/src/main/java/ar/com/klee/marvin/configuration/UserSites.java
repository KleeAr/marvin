package ar.com.klee.marvin.configuration;


import java.util.ArrayList;
import java.util.List;

import ar.com.klee.marvin.client.model.UserSetting;
import ar.com.klee.marvin.gps.Site;

public class UserSites {

    private static UserSites instance;

    List<Site> sites = new ArrayList<Site>();

    private UserSites() {

    }

    public void setSites(List<Site> sites) {
        this.sites = sites;
    }

    public static UserSites getInstance() {
        if (instance == null) {
            instance = new UserSites();
        }
        return instance;

    }

    public void add(Site site){
        sites.add(site);
    }

    public static void destroyInstance() {
        instance = null;
    }

    public List<Site> getSites() {
        return sites;
    }
}
