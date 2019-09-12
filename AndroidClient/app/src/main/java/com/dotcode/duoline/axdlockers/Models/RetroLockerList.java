package com.dotcode.duoline.axdlockers.Models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RetroLockerList {
    @SerializedName("items")
    private List<RetroLocker> lockers;
    @SerializedName("_links")
    private Links links;
    @SerializedName("_meta")
    private Meta meta;

    public RetroLocker getLocker() {
        if (lockers.size() > 0) {
            return lockers.get(0);
        } else
            return null;
    }

    public List<RetroLocker> getLockers() {
        return lockers;
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
