package com.dotcode.duoline.axdlockers.Models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RetroBuildingList {
    @SerializedName("items")
    private List<RetroBuilding> buildings;
    @SerializedName("_links")
    private Links links;
    @SerializedName("_meta")
    private Meta meta;


    public RetroBuilding getFirstBuilding() {
        if (buildings.size() > 0) {
            return buildings.get(0);
        } else
            return null;
    }

    public List<RetroBuilding> getBuildings() {
        return buildings;
    }

    public int getCurrentPage() {
        return meta.currentPage;
    }
    public boolean isPastPage() {
        return links.isLastPage();
    }

    class Links {
        @SerializedName("self")
        private Href self;
        @SerializedName("first")
        private Href first;
        @SerializedName("prev")
        private Href prev;
        @SerializedName("next")
        private Href next;
        @SerializedName("last")
        private Href last;

        public boolean isLastPage() {
           return (next == null);
        }
    }
    class Href {
        @SerializedName("href")
        private String href;

        public String getHref() {
            return href;
        }

        public void setHref(String href) {
            this.href = href;
        }
    }

    class Meta {
        @SerializedName("totalCount")
        private int totalCount;
        @SerializedName("pageCount")
        private int pageCount;
        @SerializedName("currentPage")
        private int currentPage;
        @SerializedName("perPage")
        private int perPage;
    }

}
