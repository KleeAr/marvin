package ar.com.klee.marvin.configuration;


import java.util.ArrayList;
import java.util.List;

import ar.com.klee.marvin.client.model.UserSetting;
import ar.com.klee.marvin.gps.Site;

public class UserSites {

    private static UserSites instance;

    List<Site> sites = new ArrayList<Site>();

    public UserSites(){

        instance = this;

    }

    public static UserSites getInstance() {
        if (instance == null) {
            throw new IllegalStateException("Instance not initialized. Call initializeInstance before calling getInstance");
        }
        return instance;
    }

    public void add(Site site){
        sites.add(site);
    }

    public static boolean isInstanceInitialized() {
        return instance != null;
    }

    public static void destroyInstance() {
        instance = null;
    }

    public List<Site> getSites() {
        return sites;
    }
}
